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

public class DPVPopUpMenu
{
    private JPanel contentPane;
    private DPVPanel panel1;
    JFrame DPVframe = new JFrame("DPV Settings");
    
    
private void displayGUI()
    {
        DPVframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("./src/resources/UHU-OSHW.jpg");
        DPVframe.setIconImage(icon);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new CardLayout());
        panel1 = new DPVPanel(contentPane);
        contentPane.add(panel1, "Panel 1"); 
        DPVframe.setContentPane(contentPane);
        DPVframe.pack();   
        DPVframe.setLocationByPlatform(true);
        DPVframe.setVisible(true);
    }

    public static void main(String... args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new DPVPopUpMenu().displayGUI();
            }
        });
    }


class DPVPanel extends JPanel {

    private JSpinner timeintervalspinner;
    private JSpinner initvoltspinner;
    private JSpinner finalvoltspinner;
    private JSpinner deltavoltspinner;
    private JSpinner pulseheightspinner;
    private JSpinner pulsewidthspinner;
    private final JLabel timeintervallabel;
    private final JLabel initvoltlabel;
    private final JLabel finalvoltlabel;
    private final JLabel deltavoltlabel;
    private final JLabel pulseheightlabel;
    private final JLabel pulsewidthlabel;
    private final JButton okbutton;
    private final JButton cancelbutton;
    private JPanel contentPane;
    public float timeinterval = 50;
    public float initvolt = 0;
    public float finalvolt = 500;
    public float deltavolt = 5;
    public float pulsewidth = 20;
    public float pulseheight= 50;
    public JLabel sweepratelabel;
    private float choseninterval = 50;
    private float chosenvolt = 0;
    private float chosenfinalvolt = 500;
    private float chosendeltavolt = 5;
    private float chosenpulseheight = 20;
    private float chosenpulsewidth = 50;
    private float chosensweeprate = chosendeltavolt/choseninterval;

    public DPVPanel(JPanel panel) {

        contentPane = panel;
        //construct components
        
        timeintervalspinner = new JSpinner();
        timeintervalspinner.setValue(timeinterval);
        initvoltspinner = new JSpinner();
        initvoltspinner.setValue(initvolt);
        finalvoltspinner = new JSpinner();
        finalvoltspinner.setValue(finalvolt);
        deltavoltspinner = new JSpinner();
        deltavoltspinner.setValue(deltavolt);
        pulseheightspinner = new JSpinner();
        pulseheightspinner.setValue(pulseheight);
        pulsewidthspinner = new JSpinner();
        pulsewidthspinner.setValue(pulsewidth);
        timeintervallabel = new JLabel ("Time interval (ms)");
        initvoltlabel = new JLabel ("Initial voltage (mV)");
        finalvoltlabel = new JLabel ("Final voltage (mV)");
        deltavoltlabel = new JLabel ("Voltage increment (mV)");
        pulseheightlabel = new JLabel ("Pulse height (mV)");
        pulsewidthlabel = new JLabel ("Pulse width (%)");
        okbutton = new JButton ("OK");
        cancelbutton = new JButton ("Cancel");
        sweepratelabel = new JLabel ("Sweep rate (V/s): "+chosensweeprate);

        //adjust size and set layout
        setPreferredSize (new Dimension (465, 200));
        setLayout (null);
        
        //set component bounds (only needed by Absolute Positioning)
        timeintervalspinner.setBounds (370, 13, 60, 25);
        initvoltspinner.setBounds (135, 13, 60, 25);
        finalvoltspinner.setBounds (135, 43, 60, 25);
        deltavoltspinner.setBounds (370, 43, 60, 25);
        pulseheightspinner.setBounds (135, 98, 60, 25);
        pulsewidthspinner.setBounds (135, 125, 60, 25);
        timeintervallabel.setBounds (225, 0, 120, 50);
        initvoltlabel.setBounds (15, 0, 130, 50);
        finalvoltlabel.setBounds (15, 30, 130, 50);
        deltavoltlabel.setBounds (225, 30, 200, 50);
        pulseheightlabel.setBounds (15, 86, 200, 50);
        pulsewidthlabel.setBounds (15, 111, 200, 50);
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
                    
                    timeintervalspinner = (JSpinner) e.getSource();
                        choseninterval = (int)timeintervalspinner.getValue();
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

            //Set limit volt 3
            finalvoltspinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    finalvoltspinner = (JSpinner) e.getSource();
                        chosenfinalvolt = (int)finalvoltspinner.getValue();
                        chosenfinalvolt=chosenfinalvolt/10*10;
                        if (chosenfinalvolt%10>5)
                                chosenfinalvolt=chosenfinalvolt+5;
                        if (chosenfinalvolt>4000){
                            chosenfinalvolt=4000;
                        }
                        if (chosenfinalvolt<-4000)
                        {
                            chosenfinalvolt=-4000;
                        }
                    }
                });
            
            //Set pulsevolt
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
                        chosendeltavolt=chosendeltavolt/10*10;
                        if (chosendeltavolt%10>5)
                                chosendeltavolt=chosendeltavolt+10;
                        if (chosendeltavolt>4000){
                            chosendeltavolt=4000;
                        }
                        if (chosendeltavolt<-4000)
                        {
                            chosendeltavolt=-4000;
                        }
                    }
                });
            
            //Set delta volt
            pulseheightspinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    pulseheightspinner = (JSpinner) e.getSource();
                        chosendeltavolt = (int)pulseheightspinner.getValue();
                        chosendeltavolt=chosendeltavolt;
                        if (chosendeltavolt>100){
                            chosendeltavolt=100;
                        }
                        if (chosendeltavolt<1)
                        {
                            chosendeltavolt=1;
                        }
                    }
                });
            
            //Set pulsewidth
            pulsewidthspinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    pulsewidthspinner = (JSpinner) e.getSource();
                        chosenpulsewidth = (int)pulsewidthspinner.getValue();
                        chosenpulsewidth=chosenpulsewidth;
                        if (chosenpulsewidth>50){
                            chosenpulsewidth=50;
                        }
                        if (chosenpulsewidth<10)
                        {
                            chosenpulsewidth=10;
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
            finalvolt=chosenfinalvolt;
            //system msg to check the value is correct
                        System.out.println("volt2 is " + finalvolt);
            deltavolt=chosendeltavolt;
            //system msg to check the value is correct
                        System.out.println("deltavolt is " + deltavolt);
            pulseheight=chosenpulseheight;
            //system msg to check the value is correct
                        System.out.println("pulsewidth is " + pulsewidth);            
            pulsewidth=chosenpulsewidth;
            //system msg to check the value is correct
                        System.out.println("pulsewidth is " + pulsewidth);
            DPVframe.dispose();
        });       

        //Cancel Button Actions
        cancelbutton.addActionListener((ActionEvent e) -> {
            DPVframe.dispose();
        });

        //add components
        add (timeintervallabel);
        add (initvoltlabel);
        add (finalvoltlabel);
        add (deltavoltlabel);
        add (pulseheightlabel);
        add (pulsewidthlabel);
        add (okbutton); 
        add (cancelbutton);
        add (timeintervalspinner);
        add (initvoltspinner);
        add (finalvoltspinner);
        add (deltavoltspinner);
        add (pulseheightspinner);
        add (pulsewidthspinner);
        add (sweepratelabel);
        
    }
}
}

