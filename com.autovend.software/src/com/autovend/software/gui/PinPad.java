/**
 *
 * SENG Iteration 3 P3-3 | PinPad.java
 *
 * @author Brian Vo - 10188952
 * @author Jason Ngu - 30145770
 * @author Andy Tran - 30125341
 * @author Brett Lyle -30103268
 * @author Caio Araujo 30148726
 * @author Nishan Soni 30147280
 * @author Brian Tran - 30064686
 * @author Duong Tran - 30113765
 * @author Imran Haji - 30141571
 * @author Sahil Brar - 30021440
 * @author Eugene Lee - 30137489
 * @author Maira Khan - 30047942
 * @author Zainab Bari - 30154224
 * @author Eungyo Song - 30079379
 * @author Anthony Krim - 30142199
 * @author Michelle Loi - 30019557
 * @author Alisha Nasir - 30140704
 * @author Alina Mansuri - 30008370
 * @author Adam Mogensen - 30071819
 * @author Nemanja Grujic - 30116614
 * @author Ali Savab Pour - 30154744
 * @author Rheanna Fielder - 30092060
 * @author Justin Clibbett - 30128271
 * @author Aminreza Tavakoli - 30127594
 * @author Amasil Rahim Zihad - 30164830
 */

package com.autovend.software.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
/**
 * This is a panel that displays the pin pad.
 * */
public class PinPad extends JPanel{
	private JTextArea textArea;
	private String[] numbers = {
			"1","2","3",
			"4","5","6",
			"7","8","9",
			"Clear","0","<-"
	};
	
	// gets JTextArea textArea
	// matching number is presented in textArea, if any of button is pressed
	public PinPad(JTextArea textArea) {
		this.textArea = textArea;
		setLayout(new GridLayout(5, 10, 5, 5));
		setPreferredSize(new Dimension(300,400));
		for(int i = 0; i < numbers.length; i++) {
			JButton button = new JButton(numbers[i]);
			button.setBackground(new Color(240, 255, 240));
			button.setOpaque(true);
	        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			button.setBorderPainted(true);
			button.addActionListener(new KeyActionListener());
			add(button);
		}
	}
	
	// ActionListener implementation for the keypad buttons in the GUI
	// Detects which button is pressed and updates the textArea accordingly
	// Implements functionality for the backspace, clear, and enter buttons
	private class KeyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String key = ((JButton) e.getSource()).getText();
            
            if (key.equals("<-")) {
                if (textArea.getDocument().getLength() > 0) {
                    textArea.replaceRange("", textArea.getDocument().getLength() - 1, textArea.getDocument().getLength());
                }
            } else if (key.equals("Clear")) {
                textArea.setText("");
            } else if (key.equals("Enter")) {
                // do something when Enter is pressed
            } else {
                textArea.append(key);
            }
        }
    }
}
