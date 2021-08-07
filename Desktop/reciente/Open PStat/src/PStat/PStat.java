/**
 * 
 * Copyright (C) 2015 UHU.ES/OSHW
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * See http://rxtx.qbang.org/wiki/index.php/Installation_for_Windows to install
 * DLL needed for appropriate function of Arduino.jar library in Windows
 *
 * Arduino.jar library was released by Antony Garcia Gonzalez (PanamaHitek)
 * under Creative Commons Attribution - Non Commertial 4.0 International License
 * (CC BY-NC 4.0)
 *
 * JChart2D.jar library was released by Achim Westermann under
 * GNU Lesser General Public License (GNU - LGPL)
 *
 *
 */
package PStat;

import Arduino.Arduino;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyManualTicks;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.util.Range;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author JDaniel Mozo for the Open Source Hardware Group of the 
 * University of Huelva(UHU.ES/OSHW)
 * 
 */
public abstract class PStat implements ChangeListener{
  static Chart2D chart;
  static ITrace2D trace;
  static Timer timer = new Timer(true);
  static TimerTask task1;
  static boolean copied = true;
  static Clipboard clipboard = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
  static ArrayList<Double> dataT = new ArrayList<>();
  static ArrayList<Double> dataV = new ArrayList<>();
  static ArrayList<Double> dataC = new ArrayList<>();
  static DecimalFormat formatter = new DecimalFormat("#####0.00");
  static Arduino arduino = new Arduino();
  static boolean demo = false;
  static double m_starttime;
  static int index = 0;
  static int timeinterval = 50;         // time interval ms (50/5000) each 50 ms
  static int gain = 1;                  // gain factor (1/500)
  static int initvolt = 0;              // initial sweep voltage mV (-4000/4000) each 10 mV
  static int volt2 = -500;              // final sweep voltage / first cycle voltage mV (-4000/4000) each 10 mV
  static int volt3 = 100;               // ending voltage mV (-4000/4000) each 10 mV
  static int deltavolt = 5;             // voltage increase mV (1/100) each 1 mV
  static int cycles = 1;                // number of cycles (0/50) each 1
  static int pulsewidth = 50;           // pulse duration to time interval ratio (10/50)
  static int pulsevolt = 20;            // pulse amplitude mV (10/100) each 5 mV
  static boolean cellstatus = false;    
          
  //static int flt;
  static JLabel lblTime = new JLabel("t (s): ");
  static JLabel lblPot = new JLabel("E (mV): ");
  static float LowXMax;
  static float HighXMax;
  static final int PointNr = 181;       // poits to be showed in chart
  static final int Interval = 333;      // time in msec between data acq
  static int m=0;
  static IAxis axisX;
  static IAxis axisY;
  @SuppressWarnings("static-access")

  public static void main(String[]args){
    // Create a chart:
    chart = new Chart2D();
    // Create an ITrace:
    // Note that dynamic charts need limited amount of values!!!
    trace = new Trace2DSimple();    //Ltd(PointNr);
    trace.setColor(Color.RED);
    // Add the trace to the chart. This has to be done before adding points (deadlock prevention):
    chart.addTrace(trace);
    // Configure Chart:
    chart.setPaintLabels(false);
    chart.setUseAntialiasing(true);
    axisX = chart.getAxisX();
    axisY = chart.getAxisY();
    //Define default axis Title
    axisX.setAxisTitle(new AxisTitle("Time (s)"));
    axisY.setAxisTitle(new AxisTitle("Potential (mV)"));
    // Make it visible:
    // Create a frame.
    JFrame frame = new JFrame("Open PStat");
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    // Place frame in the middle of desktop
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    Toolkit.getDefaultToolkit().getScreenSize();
    int ancho = dim.width*3/4;
    int alto = dim.height*3/4;
    frame.setBounds(ancho/6,alto/6,ancho,alto);
    
    Image icon = Toolkit.getDefaultToolkit().getImage("./src/resources/UHU-OSHW.jpg");
    frame.setIconImage(icon);

    // Create layout on frame to distribute panels
    frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.X_AXIS));
    // Create panels
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    JPanel panelR = new JPanel();
    panelR.setLayout(new BoxLayout(panelR, BoxLayout.Y_AXIS));

    // Add panels left to right
    frame.getContentPane().add(panel);
    frame.getContentPane().add(panelR);

    // Enable the termination button [cross on the upper right edge]:
    frame.addWindowListener(
        new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                // check if data was copied before exit
                if (copied) {
                    System.exit(1);         // Terminates
                }else{
                    int n = JOptionPane.showConfirmDialog(frame,"Data was not copied, do you continue?","Exit confirmation",JOptionPane.YES_NO_OPTION);
                    // if answer is YES:
                    if (n == JOptionPane.YES_OPTION) {
                        copied = true;  // set data as copied and continue
                        System.exit(0);     // Abort exit
                    }
                }
            }
        }
    );
    frame.setVisible(true);
    
    // Configure control panel:
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    // Create controls and add them to panel:
    JButton btnStart = new JButton("Start");
    btnStart.setPreferredSize(new Dimension(100,40));
    btnStart.setMaximumSize(new Dimension(150,40));
    btnStart.setAlignmentX(panel.LEFT_ALIGNMENT);

    JButton btnCopy = new JButton("Copy");
    btnCopy.setPreferredSize(new Dimension(100,20));
    btnCopy.setMaximumSize(new Dimension(150,20));
    btnCopy.setAlignmentX(panel.LEFT_ALIGNMENT);
    
    JButton settings = new JButton("Settings");
    settings.setPreferredSize(new Dimension(100,20));
    settings.setMaximumSize(new Dimension(150,20));
    settings.setAlignmentX(panel.LEFT_ALIGNMENT);

    JLabel techniquelabel = new JLabel("Technique:");
    techniquelabel.setMinimumSize(new Dimension(100,0));
    techniquelabel.setAlignmentX(panel.LEFT_ALIGNMENT);
    JComboBox technique = new JComboBox();
    technique.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CP", "CA", "CV","DPV" }));
    technique.setPreferredSize(new Dimension(100,20));
    technique.setMaximumSize(new Dimension(100,20));
    technique.setAlignmentX(panel.LEFT_ALIGNMENT);
    technique.setSelectedIndex(0);
    
    JLabel label1 = new JLabel("Range(FE) (uA):");
    label1.setMinimumSize(new Dimension(100,0));
    label1.setAlignmentX(panel.LEFT_ALIGNMENT);
    JComboBox cmbSens = new JComboBox();
    cmbSens.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000","20" }));
    cmbSens.setPreferredSize(new Dimension(100,20));
    cmbSens.setMaximumSize(new Dimension(100,20));
    cmbSens.setAlignmentX(panel.LEFT_ALIGNMENT);

    lblTime.setBorder(BorderFactory.createEtchedBorder());
    lblTime.setPreferredSize(new Dimension(100,20));
    lblTime.setMaximumSize(new Dimension(100,20));
    lblTime.setAlignmentX(panel.LEFT_ALIGNMENT);
    lblPot.setBorder(BorderFactory.createEtchedBorder());
    lblPot.setPreferredSize(new Dimension(100,20));
    lblPot.setMaximumSize(new Dimension(100,20));
    lblPot.setAlignmentX(panel.LEFT_ALIGNMENT);
    
    JLabel commportlabel = new JLabel("Comm Port:");
    commportlabel.setMinimumSize(new Dimension(100,0));
    commportlabel.setAlignmentX(panel.LEFT_ALIGNMENT);
    //commportlabel.setAlignmentY(panel.TOP_ALIGNMENT);
    JComboBox cmbPortComm = new javax.swing.JComboBox();
    cmbPortComm.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
    cmbPortComm.setPreferredSize(new Dimension(100,20));
    cmbPortComm.setMaximumSize(new Dimension(100,20));
    cmbPortComm.setAlignmentX(panel.LEFT_ALIGNMENT);
    //cmbPortComm.setAlignmentY(panel.TOP_ALIGNMENT);
    
    arduino.ShowMessageDialogs(false);
    for (int i = 1; i <= (arduino.SerialPortsAvailable()); i++){
        // read available comm-port names and add to combo
        String PortName = arduino.NameSerialPortAt(i);
        // if there are 2 ports or plus NameSerialPortAt gives all ports concatenated
        // to isolate each port name:
        if (i > 1) {
            PortName = (String) PortName.subSequence(arduino.NameSerialPortAt(i-1).length()+1,arduino.NameSerialPortAt(i).length());
        }
        cmbPortComm.addItem(PortName);
    }
    
    // Add a demo mode comm-port
    cmbPortComm.addItem("DEMO");
    cmbPortComm.setSelectedItem("DEMO");

    // Set graphical scales
    demo = true;
    ScaleGraf (axisY, -5000, 5000, true);
    ScaleGraf (axisX, 0, 60, false);

    // Load panel controls
    panel.add(btnStart);
    panel.add(Box.createRigidArea(new Dimension(0,5)));
    panel.add(btnCopy);
    panel.add(Box.createRigidArea(new Dimension(0,5)));
    panel.add(techniquelabel);
    panel.add(technique);
    panel.add(Box.createRigidArea(new Dimension(0,5)));
    panel.add(settings);
    panel.add(Box.createRigidArea(new Dimension(0,5)));
    panel.add(label1);
    panel.add(cmbSens);
    label1.setVisible(false);
    cmbSens.setVisible(false);
    panel.add(Box.createRigidArea(new Dimension(0,5)));
    panel.add(lblTime);
    panel.add(lblPot);
    panel.add(Box.createVerticalGlue());
    panel.add(Box.createRigidArea(new Dimension(0,5)));
    panel.add(commportlabel);
    panel.add(cmbPortComm);
    
    //Load panelR content (graph and slider)
    // add chart to panelR:
    panelR.add(chart);
    // create and set an horizontal slider
    LowXMax = 0;
    HighXMax = PointNr * Interval/1000;
    JSlider slider = new JSlider((int) LowXMax,(int) HighXMax,(int) HighXMax);

    ChangeListener ch = new ChangeListener() {
        // Define listener action for slider change value
        @Override
        public void stateChanged(ChangeEvent e) {
            // set axisX range from slider value
            if (slider.getValueIsAdjusting()){      // while slider knob drags
                int XMax = slider.getValue();
                double XMin = XMax - PointNr * Interval/1000;
                ScaleGraf(axisX, XMin, (double)XMax, false);
            }
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };

    slider.addChangeListener(ch);
    panelR.add(slider);
    slider.setEnabled(false);

    frame.setVisible(true);

    btnStart.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String Text = btnStart.getText();
            // check if data was copied before acquire
            if (!copied) {
                int n = JOptionPane.showConfirmDialog(frame,"Data was not copied, do you continue?","Warning",JOptionPane.YES_NO_OPTION);
                // if answer is YES:
                if (n == JOptionPane.YES_OPTION) {
                    copied = true;  // set data as copied and continue
                }
            }
            if ("Start".equals(Text) & copied){     // start acquisition
                btnStart.setText("Stop");
                // clear chart
                trace.removeAllPoints();
                // clear data arraylist
                dataT.clear();
                dataV.clear();
                dataC.clear();
                //initialize rangepolicy for Xaxis
                // case segun tecnica
                ScaleGraf (axisX, 0, Interval * PointNr/1000, false);
                //disable slider
                slider.setEnabled(false);

                if (!demo) {
                    index = 0;
                    m_starttime = System.currentTimeMillis();

                    // Send start acquisition order to Serial comm
                    // (Data acquisition, store and graph is in SerialPortEventListener)
                    // (Pacer is Arduino, not Java)
                    try {
                        arduino.SendData("r");
                    } catch (Exception ex) {
                        Logger.getLogger(PStat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    // Now the dynamic adding of points. This is just a demo!
                    task1 = new TimerTask(){

                        private double m_y = 100;
                        private double m_starttime = System.currentTimeMillis();
                        private int index = 0;

                        @Override
                        public void run() {
                          // This is just computation of some nice looking value.
                          double rand = Math.random();
                          boolean add = (rand >= 0.5);
                          this.m_y = (add) ? this.m_y + Math.random()*10 : this.m_y - Math.random()*10;

                          // This is the important thing: Point is added from separate Thread.
                          dataT.add((double)(System.currentTimeMillis() - this.m_starttime)/1000);
                          dataV.add(this.m_y);
                          dataC.add((double)(index));

                          // Displays new data on labels and graph
                          trace.addPoint(dataT.get(index), dataV.get(index));
                          
                          if (m==0) {
                          lblTime.setText("t (s): " + formatter.format(dataT.get(index)));
                          lblPot.setText("E (mV): " + formatter.format(dataV.get(index)));
                          }
                          if (m==1){
                          lblTime.setText("t (s): " + formatter.format(dataT.get(index)));
                          lblPot.setText("C (uA): " + formatter.format(dataV.get(index)));
                          }
                          if (m==2){
                          lblTime.setText("E (mV): " + formatter.format(dataT.get(index)));
                          lblPot.setText("C (uA): " + formatter.format(dataV.get(index)));
                          }
                          if (m==3){
                          lblTime.setText("E (mV): " + formatter.format(dataT.get(index)));
                          lblPot.setText("C (uA): " + formatter.format(dataV.get(index)));
                          }
                          // check scale X and drag if necessary
                          if (dataT.get(index) > Interval * PointNr /1000 ) {
                              ScaleGraf (axisX, dataT.get(index) - PointNr * Interval/1000, dataT.get(index), false);
                          }

                          index++;
                        }
                    };

                    // Every "Interval" milliseconds a new value is collected.
                    timer.schedule(task1, 0, Interval);
                }
            } else {        //stop adquisition
                btnStart.setText("Start");
                copied = false;         // set flag not copied data

                //setup the slider and enable
                HighXMax = (float) axisX.getMax();
                LowXMax = (PointNr * Interval / 1000);
                if (HighXMax < LowXMax) {
                    LowXMax = 0;
                }
                slider.setMinimum ((int) LowXMax);
                slider.setMaximum ((int) HighXMax);
                slider.setValue ((int) HighXMax);
                slider.setEnabled(HighXMax > PointNr * Interval / 1000);

                // Halts the data acquisition
                if (!demo){
                    try {
                        // Send stop acquisition order to Serial comm
                        arduino.SendData("s");
                    } catch (Exception ex) {
                        Logger.getLogger(PStat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else { // if demo mode
                    // stop new value collection.
                    task1.cancel();         //demo mode
                }
            }
        }
    });

    btnCopy.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // copy data buffer to clipboard
            // build text string to be copied
            int i = 0;
            String textBuild = new String();
            String dataTString, dataCString, dataVString;
            do {
                dataTString = formatter.format(dataT.get(i));
                dataCString = formatter.format(dataC.get(i));
                dataVString = formatter.format(dataV.get(i));
                
                textBuild += dataTString + "\t" + dataCString + "\n" + dataVString + "\t";
                i++;
            } while (i < dataT.size());
            // put text string to clipboard
            StringSelection data2copy = new StringSelection(textBuild);
            clipboard.setContents(data2copy,data2copy);

            copied = true;      //set copied flag
        }
    });
    
 technique.addActionListener (new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
              
            switch (technique.getSelectedIndex()) {
                
            case 0: // CP
                
                m=0;
                cmbSens.setVisible(false);
                label1.setVisible(false);
                ScaleGraf (axisY, -5000, 5000, true);
                lblTime.setText("Time (s): ");
                lblPot.setText("Potential (mV): ");
                axisX.setTitle("Time (s): ");
                axisY.setTitle("Potential (mV)");
                break;
                
            case 1: // CA
                
                m=1;
                cmbSens.setVisible(true);
                label1.setVisible(true);
                ScaleGraf (axisY, -10000, 10000, true);
                lblTime.setText("Time (s): ");
                lblPot.setText("Current (uA): ");
                axisX.setTitle("Time (s): ");
                axisY.setTitle("Current (uA): ");
                
                break;
                
            case 2: // CV
                
                m=2;
                cmbSens.setVisible(true);
                label1.setVisible(true);
                ScaleGraf (axisY, -10000, 10000, true);
                lblTime.setText("Potential (mV): ");
                lblPot.setText("Current (uA): ");
                axisX.setTitle("Potential (mV): ");
                axisY.setTitle("Current (uA):" );
                
                break;
                
            case 3: // DPV
                
                m=3;
                cmbSens.setVisible(true);
                label1.setVisible(true);
                ScaleGraf (axisY, -10000, 10000, true);
                lblTime.setText("Potential (mV): ");
                lblPot.setText("Current (uA): ");
                axisX.setTitle("Potential (mV): ");
                axisY.setTitle("Current uA");
                break;
            }
        }
    });
 
 settings.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (technique.getSelectedIndex()) {
                case 0: // CP
                    CPPopUpMenu.main(args);
                    break;
                case 1: // CA
                    CAPopUpMenu.main(args);
                    break;
                case 2: // CV
                    CVPopUpMenu.main(args);
                    break;
                case 3: // DPV
                    DPVPopUpMenu.main(args);
                    break;    
            }
        }
    });    
 
    cmbSens.addActionListener (new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // Send to Arduino the Gain setting and set graph

            // Set YGraphScale depending combo selection
            switch (cmbSens.getSelectedIndex()){
            case 0: // 10000 uA
                ScaleGraf (axisY, -10000, 10000, true);
                gain = 1;
                break;
            case 1: // 20 uA
                ScaleGraf (axisY, -20, 20, true);
                gain = 500;
                break;
            }
        }
    });

    cmbPortComm.addActionListener (new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          // Set DEMO flag
          demo = cmbPortComm.getSelectedItem().equals("DEMO");

          if (!demo) {
            // Initialize Serial Comm Port for Arduino
            try {
                arduino.ShowMessageDialogs(false);
                arduino.KillArduinoConnection();
                //arduino.ArduinoRXTX((String) cmbPortComm.getSelectedItem(),2000,9600,evento);
            } catch (Exception ex) {
                Logger.getLogger(PStat.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                arduino.ShowMessageDialogs(true);
                arduino.ArduinoRXTX((String) cmbPortComm.getSelectedItem(),2000,9600,evento);
            } catch (Exception ex) {
                Logger.getLogger(PStat.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
    });

    chart.addMouseMotionListener(new MouseMotionListener () {

        @Override
        public void mouseMoved(MouseEvent e) {
            if (!trace.isEmpty()){
                //System.out.println("X: " + x + "\t" + axisX.getMin() + "\t" + axisX.getMax() + "\t" + axisX.translatePxToValue(x)  );
                double scaledX = axisX.translatePxToValue(e.getX());
                double scaledY = axisY.translatePxToValue(e.getY());

                if (m==0) {
                    lblTime.setText("t (s): " + formatter.format(scaledX));
                    lblPot.setText("E (mV): " + formatter.format(scaledY));
                    
                    axisX.setAxisTitle(new AxisTitle("Time (s)"));
                    axisY.setAxisTitle(new AxisTitle("Potential (mV)"));
                }
                if (m==1) {
                    lblTime.setText("t (s): " + formatter.format(scaledX));
                    lblPot.setText("C (mA): " + formatter.format(scaledY));
                }
                if (m==2) {
                    lblTime.setText("E (mV): " + formatter.format(scaledX));
                    lblPot.setText("C (mA): " + formatter.format(scaledY));
                }
                if (m==3) {
                    lblTime.setText("E (mV): " + formatter.format(scaledX));
                    lblPot.setText("C (mA): " + formatter.format(scaledY));
                }
                
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    });

  }

  public static void ScaleGraf (IAxis axis,double sMin,double sMax, boolean g) {
    axis.setRangePolicy(new RangePolicyFixedViewport(new Range(sMin,sMax)));
    axis.setAxisScalePolicy(new AxisScalePolicyManualTicks());
    axis.setMinorTickSpacing((sMax-sMin)/10);

    // Calculate gain from selected FE
    if (g) {
        if (!demo){         // If not DEMO send gain to Arduino
          String msg = new String();
          switch ((int) sMax) {
              case 1000: {
                  msg = "g255";
                  break; }
              case 500: {
                  msg = "g112";
                  break; }
              case 250: {
                  msg = "g52";
                  break; }
              case 100: {
                  msg = "g20";
                  break; }
              case 50: {
                  msg = "g9";
                  break; }
          }
          try {
              // Send gain setting to Serial comm
              arduino.SendData(msg);
          } catch (Exception ex) {
              Logger.getLogger(PStat.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
    }
  }

  static SerialPortEventListener evento = new SerialPortEventListener(){

        @Override
        public void serialEvent(SerialPortEvent spe) {
            if (arduino.MessageAvailable() == true){
                String msg = arduino.PrintMessage();    // read data
                
                
                
                if (msg.startsWith("v") == true){       // If message is a data:
                    // stores time in X
                    // dataT.add((double)(System.currentTimeMillis() - m_starttime)/1000);
                    
                    // extract data from msg
                    int comma = msg.indexOf(",");
                    // calculate voltage from data and gain
                    String dato = msg.substring(1, comma);
                    dataT.add(Double.parseDouble(dato)); 
                    msg = msg.substring(comma + 1);
                    comma = msg.indexOf(",");
                    dato = msg.substring(1, comma); //revisar el inicio de la cadena (1/0)
                    dataV.add(Double.parseDouble(dato));
                    dato = msg.substring(comma + 1);
                    dataC.add(Double.parseDouble(dato)/gain);
                    
                    // Add point to trace
                    trace.addPoint(dataT.get(index), dataC.get(index));
                    // Print point to labels
                    
                    if (m==0) {
                    lblTime.setText("t (s): " + formatter.format(dataT.get(index)));
                    lblPot.setText("E (mV): " + formatter.format(dataV.get(index)));
                    }
                    if (m==1) {
                    lblTime.setText("t (s): " + formatter.format(dataT.get(index)));
                    lblPot.setText("C (uA): " + formatter.format(dataV.get(index)));
                    }
                    if (m==2) {
                    lblTime.setText("E (mV): " + formatter.format(dataT.get(index)));
                    lblPot.setText("C (uA): " + formatter.format(dataV.get(index)));
                    }
                    if (m==3) {
                    lblTime.setText("E (mV): " + formatter.format(dataT.get(index)));
                    lblPot.setText("C (uA): " + formatter.format(dataV.get(index)));
                    }
                    // check scale X
                    if (dataT.get(index) > Interval * PointNr /1000 ) {
                        ScaleGraf (chart.getAxisX(), dataT.get(index) - PointNr * Interval/1000, dataT.get(index), false);
                    }

                    index++;
                } else {
                    // If message isn't a data:
                    System.out.println(msg);
                }
            }
        }
  };
}


 
    