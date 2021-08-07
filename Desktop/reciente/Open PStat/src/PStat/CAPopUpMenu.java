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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Javier
 */

public class CAPopUpMenu
{
    private JPanel contentPane;
    private CAPanel panel1;
    JFrame CAframe = new JFrame("CA Settings");
    
    private void displayGUI()
    {
        CAframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("./src/resources/UHU-OSHW.jpg");
        CAframe.setIconImage(icon);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new CardLayout());
        panel1 = new CAPanel(contentPane);
        contentPane.add(panel1, "Panel 1"); 
        CAframe.setContentPane(contentPane);
        CAframe.pack();   
        CAframe.setLocationByPlatform(true);
        CAframe.setVisible(true);
    }

    public static void main(String... args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new CAPopUpMenu().displayGUI();
            }
        });
    }


class CAPanel extends JPanel {

    private JSpinner Spinner1;
    private JSpinner Spinner2;
    private final JLabel jcomp2;
    private final JLabel jcomp3;
    private final JButton jcomp4;
    private final JButton jcomp5;
    private final JPanel contentPane;
    public int initvolt;
    public int timeinterval;
    private int chosenvolt;
    private int choseninterval;

    public CAPanel(JPanel panel) {

        contentPane = panel;
        //construct components
        Spinner1 = new JSpinner();
        Spinner1.setValue(50);
        Spinner2 = new JSpinner();
        jcomp2 = new JLabel ("Time interval (ms)");
        jcomp3 = new JLabel ("Potential (mV)");
        jcomp4 = new JButton ("OK");
        jcomp5 = new JButton ("Cancel");

        //adjust size and set layout
        setPreferredSize (new Dimension (365, 150));
        setLayout (null);

        //set component bounds (only needed by Absolute Positioning)
        
        Spinner1.setBounds (135, 13, 60, 25);
        Spinner2.setBounds (105, 43, 60, 25);
        jcomp2.setBounds (15, 0, 120, 50);
        jcomp3.setBounds (15, 30, 90, 50);
        
        jcomp4.setLocation(275, 120);
        jcomp4.setSize(60, 25);
        jcomp5.setLocation(170, 120);
        jcomp5.setSize(90, 25);
        
         //Set time interval
            Spinner1.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    Spinner1 = (JSpinner) e.getSource();
                        choseninterval = (int)Spinner1.getValue();
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
            Spinner2.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    
                    Spinner2 = (JSpinner) e.getSource();
                        chosenvolt = (int)Spinner2.getValue();
                        chosenvolt=chosenvolt/10*10;
                        if (choseninterval%10>5)
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
                
        //OK Button Actions
        jcomp4.addActionListener((ActionEvent e) -> {
            timeinterval=choseninterval;
            //system msg to check the value is correct
                        System.out.println("timeinterval is " + timeinterval);
            initvolt=chosenvolt;
            //system msg to check the value is correct
                        System.out.println("initvolt is " + initvolt);
            CAframe.dispose();
            });   
        
        //Cancel Button Actions
        jcomp5.addActionListener((ActionEvent e) -> {
            CAframe.dispose();
        });
        
        
//add components
        
        add (jcomp2);
        add (jcomp3);
        add (jcomp4); 
        add (jcomp5);
        add (Spinner1);
        add (Spinner2);
    }
}
}