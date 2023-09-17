/**
 *
 * SENG Iteration 3 P3-3 | AskingBagPanel.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class AskingBagPanel extends JPanel {
	private BagOptionPopup bagOptionPopup;
	public static JTextArea inputArea;
	private PinPad pinPad;
	private GradientButton cancel;
	private static GradientButton ok;
	
	
	public AskingBagPanel(BagOptionPopup bagOptionPopup) {
		
		this.bagOptionPopup = bagOptionPopup;
		setLayout(null);
		this.setPreferredSize(new Dimension(400,500));
		setBackground(new Color(205, 219, 210));

		
		JLabel label = new JLabel("How many bags do you want to purchase?");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        inputArea = new JTextArea();
        inputArea.setEditable(false);
        inputArea.setBounds(85,48,217,50);
        add(inputArea, BorderLayout.CENTER);

        pinPad = new PinPad(inputArea);
        pinPad.setBounds(42,118,300,269);
        add(pinPad, BorderLayout.SOUTH);
        
        cancel = new GradientButton("Cancel",new Color(55, 196, 67), Color.GREEN );
        cancel.setBounds(42, 395, 96, 56);
        add(cancel);
        cancel.addActionListener(e ->{
        	bagOptionPopup.setVisible(false);
		});
        
        ok = new GradientButton("Okay", new Color(55, 196, 67), Color.GREEN);
        ok.setBounds(246, 395, 96, 56);
        add(ok);
        ok.addActionListener(e ->{
        	System.out.println(inputArea.getText());
        	if(inputArea.getText() == null) {
        		//Error handling for no input
        		System.out.println("No bags entered");
        		bagOptionPopup.numOfBag = "0";
        	}else {
        		//text updated to reflect number of bags entered
        		bagOptionPopup.numOfBag = inputArea.getText();
        		System.out.println("Bags are" + bagOptionPopup.numOfBag);

        	}
        	//switch to confirm bag panel
        	bagOptionPopup.change("confirmBagPanel");
		});	
	}
	
	public static JTextArea getBagsWanted() {
		return inputArea;
	}
	
	public static JButton getOkayButton() {
		return ok; 
	}

}
