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
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class CPPopUpMenu
{
    private JPanel contentPane;
    private CPPanel panel1;
    JFrame CPframe = new JFrame("CP Settings");
    
    public void displayGUI()
    {   
        CPframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("./src/resources/UHU-OSHW.jpg");
        CPframe.setIconImage(icon);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new CardLayout());
        panel1 = new CPPanel(contentPane);
        contentPane.add(panel1, "Panel 1"); 
        CPframe.setContentPane(contentPane);
        CPframe.pack();   
        CPframe.setLocationByPlatform(true);
        CPframe.setVisible(true);
    }

    public static void main(String... args)
           
    {
        
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new CPPopUpMenu().displayGUI();
            }
        });
    }


class CPPanel extends JPanel {

    public int timeinterval;
    private JSpinner Spinner1;
    private final JLabel jcomp2;
    private final JButton jcomp4;
    private final JButton jcomp5;
    private final JPanel contentPane;
    private int choseninterval;

    public CPPanel(JPanel panel) {

        contentPane = panel;
        //construct components
        Spinner1 = new JSpinner();
        Spinner1.setValue(50);
        jcomp2 = new JLabel ("Time interval (ms)");
        jcomp4 = new JButton ("OK");
        jcomp5 = new JButton ("Cancel");

        //adjust size and set layout
        setPreferredSize (new Dimension (365, 150));
        setLayout (null);

        //set component bounds (only needed by Absolute Positioning)
        Spinner1.setBounds (125, 13, 60, 25);
        jcomp2.setBounds (15, 0, 120, 50);
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
            
        //OK Button Actions
        jcomp4.addActionListener((ActionEvent e) -> {
            timeinterval=choseninterval;
            //system msg to check the value is correct
            System.out.println("timeinterval is " + timeinterval);
            CPframe.dispose();
        });        

        //Cancel Button Actions
        jcomp5.addActionListener((ActionEvent e) -> {
            CPframe.dispose();
        });
        

        //add components
        add (Spinner1);
        add (jcomp2);
        add (jcomp4); 
        add (jcomp5);
    }
}
}