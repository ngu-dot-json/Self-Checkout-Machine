/**
 *
 * SENG Iteration 3 P3-3 | ConfirmBagPanel.java
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
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.EmptyException;

import javax.swing.JLabel;
import java.awt.Font;

public class ConfirmBagPanel extends JPanel {
	private SelfCheckoutStationLogic stationLogic;
	private BagOptionPopup bagOptionPopup;
	private AskingBagPanel askingBagPanel;
	private PaymentPanel paymentPanel;
	private GradientButton cancel;
	private static GradientButton ok;
	private static JLabel label;
	private JLabel newLabel;
	
	public void updateValues() {
		label.setText("You need " + bagOptionPopup.numOfBag +" bags correct?");		

    }
	
	public ConfirmBagPanel (BagOptionPopup bagOptionPopup,SelfCheckoutStationLogic stationLogic, AskingBagPanel askingBagPanel, PaymentPanel paymentPanel) {
		this.stationLogic = stationLogic;
		this.bagOptionPopup = bagOptionPopup;
		this.askingBagPanel = askingBagPanel;
		this.paymentPanel = paymentPanel;
		setLayout(null);
		this.setPreferredSize(new Dimension(400,500));
		setBackground(new Color(205, 219, 210));
		
		cancel = new GradientButton("Cancel", new Color(0xB22222), new Color(0xFF4136));
        cancel.setBounds(42, 395, 96, 56);
        add(cancel);
        cancel.addActionListener(e ->{
        	
        	//Reset bagPopup values
        	bagOptionPopup.numOfBag = "";
        	bagOptionPopup.change("askingBagPanel");
		});
        
        ok = new GradientButton("Confirm", new Color(55, 196, 67), Color.GREEN);
        ok.setBounds(249, 395, 96, 56);
        add(ok);
        ok.addActionListener(e ->{
        	//For each bag added, add a bag to the bag dispenser then add to order.
        	for(int i = 0; i < Integer.parseInt(bagOptionPopup.numOfBag); i++) {
        		try {
					stationLogic.getSelfCheckoutStation().bagDispenser.dispense();
					stationLogic.rbdo.addToOrder(stationLogic.rbdo.bag.getWeight());
				} catch (EmptyException e1) {
					update();
					stationLogic.notifyAttendantIO("Bag dispenser is empty!");
				}
        	}
        	//Update the textbox in payment panel to adjust to new bags
        	paymentPanel.updateText();
        	newLabel = new JLabel("You need " + bagOptionPopup.numOfBag +" bags correct?");
        	bagOptionPopup.numOfBag = "";
        	bagOptionPopup.change("askingBagPanel");
        	askingBagPanel.inputArea.setText("");
        	bagOptionPopup.setVisible(false);
		});
        
        label = new JLabel("You need " + bagOptionPopup.numOfBag +" bags correct?", SwingConstants.CENTER);
        label.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
        label.setBounds(67, 178, 282, 126);
        add(label);
        
        
       
	}
	
	public void update() {
		label.setText("Bag Dispenser is empty.");
	}
	
	public static JButton getConfirmation() {
		return ok;
	}
	
	public static String getLabel() {
		return label.getText();
	}
}
