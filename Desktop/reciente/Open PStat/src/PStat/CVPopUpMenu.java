/*
 * Copyright (C) 2021 Javier
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
 */
package PStat;

/**
 *
 * @author Javier
 */

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CVPopUpMenu
{
    private JPanel contentPane;
    private CVPanel panel1;
    JFrame CVframe = new JFrame("CV Settings");
    
    private void displayGUI()
    {
        CVframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("./src/resources/UHU-OSHW.jpg");
        CVframe.setIconImage(icon);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new CardLayout());
        panel1 = new CVPanel(contentPane);
        contentPane.add(panel1, "Panel 1"); 
        CVframe.setContentPane(contentPane);
        CVframe.pack();   
        CVframe.setLocationByPlatform(true);
        CVframe.setVisible(true);
    }

    public static void main(String... args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new CVPopUpMenu().displayGUI();
            }
        });
    }


class CVPanel extends JPanel {

    private JSpinner timeintervalspinner;
    private JSpinner initvoltspinner;
    private JSpinner volt2spinner;
    private JSpinner volt3spinner;
    private JSpinner deltavoltspinner;
    private JSpinner cyclesspinner;
    private final JLabel timeintervallabel;
    private final JLabel initvoltlabel;
    private final JLabel volt2label;
    private final JLabel volt3label;
    private final JLabel deltavoltlabel;
    private final JLabel cycleslabel;
    private final JButton okbutton;
    private final JButton cancelbutton;
    private JPanel contentPane;
    public float initvolt = 0;
    public float timeinterval = 50;
    public float volt2 = -500;
    public float volt3 = 100;
    public float deltavolt = 5;
    public float cycles = 1;
    public JLabel sweepratelabel;
    private float chosenvolt = 0;
    private float choseninterval = 50;
    private float chosenvolt2 = -500;
    private float chosenvolt3 = 100;
    private float chosendeltavolt = 5;
    private float chosencycles = 1;
    private float chosensweeprate = chosendeltavolt/choseninterval;
    
    public CVPanel(JPanel panel) {

        contentPane = panel;
        //construct components
        timeintervalspinner = new JSpinner();
        timeintervalspinner.setValue(timeinterval);
        initvoltspinner = new JSpinner();
        initvoltspinner.setValue(initvolt);
        volt2spinner = new JSpinner();
        volt2spinner.setValue(volt2);
        volt3spinner = new JSpinner();
        volt3spinner.setValue(volt3);
        deltavoltspinner = new JSpinner();
        deltavoltspinner.setValue(deltavolt);
        cyclesspinner = new JSpinner();
        cyclesspinner.setValue(cycles);
        timeintervallabel = new JLabel ("Time interval (ms)");
        initvoltlabel = new JLabel ("Initial voltage (mV)");
        volt2label = new JLabel ("Limit voltage 1 (mV)");
        volt3label = new JLabel ("Limit voltage 2 (mV)");
        deltavoltlabel = new JLabel ("Voltage increment (mV)");
        cycleslabel = new JLabel ("Number of cycles");
        okbutton = new JButton ("OK");
        cancelbutton = new JButton ("Cancel");
        sweepratelabel = new JLabel ("Sweep rate (V/s): "+chosensweeprate);
        
       
        //adjust size and set layout
        setPreferredSize (new Dimension (465, 200));
        setLayout (null);

        //set component bounds (only needed by Absolute Positioning)
        timeintervalspinner.setBounds (370, 13, 60, 25);
        initvoltspinner.setBounds (135, 13, 60, 25);
        volt2spinner.setBounds (135, 43, 60, 25);
        volt3spinner.setBounds (135, 73, 60, 25);
        deltavoltspinner.setBounds (370, 43, 60, 25);
        cyclesspinner.setBounds (135, 125, 60, 25);
        timeintervallabel.setBounds (225, 0, 120, 50);
        initvoltlabel.setBounds (15, 0, 130, 50);
        volt2label.setBounds (15, 30, 130, 50);
        volt3label.setBounds (15, 60, 200, 50);
        deltavoltlabel.setBounds (225, 30, 200, 50);
        cycleslabel.setBounds (15, 111, 200, 50);
        sweepratelabel.setBounds (225, 111, 200, 50);
        
        okbutton.setLocation(375, 170);
        okbutton.setSize(60, 25);
        cancelbutton.setLocation(270, 170);
        cancelbutton.setSize(90, 25);
        
        //Set time interval
            timeintervalspinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    timeintervalspinner = (JSpinner) e.getSource();
                    try {
                        timeintervalspinner.commitEdit();
                    } catch (ParseException exx) {
                        System.out.println("Se ha producido un error: " + exx);
                    }
                    choseninterval = (int)timeintervalspinner.getValue();
                    chosensweeprate = chosendeltavolt/choseninterval;
                    sweepratelabel.setText("Sweep rate(V/s): " + chosensweeprate);
                        choseninterval=choseninterval/50*50;
                        if (choseninterval%50>25)
                                choseninterval=choseninterval+50;
                        if (choseninterval>5000){
                            choseninterval=5000;
                        }
                        if (choseninterval<50)
                        {
                            choseninterval=50;
                        }
                    }
                }); 
            
            //Set voltage
            initvoltspinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    
                    initvoltspinner = (JSpinner) e.getSource();
                    try {
                        initvoltspinner.commitEdit();
                    } catch (ParseException exx) {
                        System.out.println("Se ha producido un error: " + exx);
                    }
                        chosenvolt = (int)initvoltspinner.getValue();
                        chosenvolt=chosenvolt/10*10;
                        if (chosenvolt%10>5)
                                chosenvolt=chosenvolt+10;
                        if (chosenvolt>4000){
                            chosenvolt=4000;
                        }
                        if (chosenvolt<-4000)
                        {
                            chosenvolt=-4000;
                        }
                    }
                });

            //Set voltage limit 1
            volt2spinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    try {
                        volt2spinner.commitEdit();
                    } catch (ParseException exx) {
                        System.out.println("Se ha producido un error: " + exx);
                    }
                    volt2spinner = (JSpinner) e.getSource();
                        chosenvolt2 = (int)volt2spinner.getValue();
                        chosenvolt2=chosenvolt2/10*10;
                        if (chosenvolt2%10>5)
                                chosenvolt2=chosenvolt2+5;
                        if (chosenvolt2>4000){
                            chosenvolt2=4000;
                        }
                        if (chosenvolt2<4000)
                        {
                            chosenvolt2=-4000;
                        }
                    }
                });
            
            //Set voltage limit 2
            volt3spinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    try {
                        volt3spinner.commitEdit();
                    } catch (ParseException exx) {
                        System.out.println("Se ha producido un error: " + exx);
                    }
                    volt3spinner = (JSpinner) e.getSource();
                        chosenvolt3 = (int)volt3spinner.getValue();
                        chosenvolt3=chosenvolt3/10*10;
                        if (chosenvolt3%10>5)
                                chosenvolt3=chosenvolt3+10;
                        if (chosenvolt3>4000){
                            chosenvolt3=4000;
                        }
                        if (chosenvolt3<4000)
                        {
                            chosenvolt3=4000;
                        }
                    }
                });
            
            //Set delta volt
            deltavoltspinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                
                    try {
                        deltavoltspinner.commitEdit();
                    } catch (ParseException exx) {
                        System.out.println("Se ha producido un error: " + exx);
                    }
                    deltavoltspinner = (JSpinner) e.getSource();
                        chosendeltavolt = (int)deltavoltspinner.getValue();
                        chosensweeprate = chosendeltavolt/choseninterval;
                        sweepratelabel.setText("Sweep rate(V/s): " + chosensweeprate);
                        if (chosendeltavolt>100){
                            chosendeltavolt=100;
                        }
                        if (chosendeltavolt<1)
                        {
                            chosendeltavolt=1;
                        }
                    }
                });
            
            //Set number of cycles
            cyclesspinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    cyclesspinner = (JSpinner) e.getSource();
                        chosencycles = (int)cyclesspinner.getValue();
                        if (chosencycles>50){
                            chosencycles=50;
                        }
                        if (chosencycles<1)
                        {
                            chosencycles=1;
                        }
                    }
                });
            
        //OK Button Actions
        okbutton.addActionListener((ActionEvent e) -> {
            timeinterval=choseninterval;
            //system msg to check the value is correct
                        System.out.println("timeinterval is " + timeinterval);
            initvolt=chosenvolt;
            //system msg to check the value is correct
                        System.out.println("initvolt is " + initvolt);
            volt2=chosenvolt2;
            //system msg to check the value is correct
                        System.out.println("volt2 is " + volt2);
            volt3=chosenvolt3;
            //system msg to check the value is correct
                        System.out.println("volt3 is " + volt3);
            deltavolt=chosendeltavolt;
            //system msg to check the value is correct
                        System.out.println("deltavolt is " + deltavolt);
            cycles=chosencycles;
            //system msg to check the value is correct
                        System.out.println("cycles is " + cycles);
            //system msg to check the value is correct
                        System.out.println("sweeprate " + chosensweeprate);
                        System.out.println("choseninterval " + choseninterval);
                        System.out.println("chosenvolt " + chosenvolt);
                        System.out.println("chosenvolt2 " + chosenvolt2);
                        System.out.println("chosenvolt3 " + chosenvolt3);
                        System.out.println("chosendeltavolt " + chosendeltavolt);
                        System.out.println("chosencycles " + chosencycles);
            CVframe.dispose();
        });        

        //Cancel Button Actions
        cancelbutton.addActionListener((ActionEvent e) -> {
            CVframe.dispose();
        });
        
        //add components
        add (timeintervallabel);
        add (initvoltlabel);
        add (volt2label);
        add (volt3label);
        add (deltavoltlabel);
        add (cycleslabel);
        add (okbutton); 
        add (cancelbutton);
        add (timeintervalspinner);
        add (initvoltspinner);
        add (volt2spinner);
        add (volt3spinner);
        add (deltavoltspinner);
        add (cyclesspinner);
        add (sweepratelabel);
    }
}
}
