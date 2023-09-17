/**
 *
 * SENG Iteration 3 P3-3 | BagOptionPopup.java
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.autovend.SelfCheckoutStationLogic;

public class BagOptionPopup extends JFrame {
	private AskingBagPanel askingBagPanel;
	private ConfirmBagPanel confirmBagPanel;
	private SelfCheckoutStationLogic stationLogic;
	
	public String numOfBag;
	
	
    public BagOptionPopup(PaymentPanel paymentPanel, CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
    	this.stationLogic = stationLogic;
    	this.askingBagPanel = new AskingBagPanel(this);
    	this.confirmBagPanel = new ConfirmBagPanel(this, stationLogic, askingBagPanel, paymentPanel);
		setBackground(new Color(205, 219, 210));

    	
    	numOfBag = "";
    	
    	
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("Bag Option Popup");
        setSize(400, 500);
        setLocationRelativeTo(null);
        
        //Visibility changes as required
        setVisible(false);
    }
    
    // method to change the screen of BagOptionPopup 
	public void change(String panelName) {
	    switch (panelName) {
	        case "askingBagPanel":
	            this.getContentPane().removeAll();
	            this.getContentPane().add(askingBagPanel);
	            break;
	        case "confirmBagPanel":
                confirmBagPanel.updateValues();
	            this.getContentPane().removeAll();
	            this.getContentPane().add(confirmBagPanel);
	            break;   
	        default:
	            throw new IllegalArgumentException("Unknown panel name: " + panelName);
	    }
	    this.revalidate();
	    this.repaint();
	}
  
}
