/**
 *
 * SENG Iteration 3 P3-3 | CustomerScreen.java
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.payments.UnpaidTotalException;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

// class to control Customer GUI
public class CustomerScreen {
	
	public JFrame screen;
	public WelcomePanel welcomePanel = null;
	public LanguageSelectPanel languageSelectPanel = null;
	public ChooseMembershipPanel chooseMembershipPanel = null;
	public EnterMembershipPanel enterMembershipPanel = null;
	public ShoppingCartPanel shoppingCartPanel = null;
	public AddItemPanel addItemPanel = null;
	public PaymentPanel paymentPanel = null;
	public AddOwnBagsPanel ownBagsPanel = null;
	public PayWithCashPanel payWithCashPanel = null;
	public PLUPanel pluPanel;
	public ThankYouPanel thankYouPanel = null;
	private SelfCheckoutStationLogic stationLogic;
		
	// constructor
	public CustomerScreen(SelfCheckoutStationLogic stationLogic) {
		screen = stationLogic.getSelfCheckoutStation().screen.getFrame();
		this.welcomePanel = new WelcomePanel(this, stationLogic);
		this.languageSelectPanel = new LanguageSelectPanel(this, stationLogic);
		this.chooseMembershipPanel = new ChooseMembershipPanel(this, stationLogic);
		this.enterMembershipPanel = new EnterMembershipPanel(this, stationLogic);
		this.shoppingCartPanel = new ShoppingCartPanel(this, stationLogic);
		this.paymentPanel = new PaymentPanel(this, stationLogic);
		this.ownBagsPanel = new AddOwnBagsPanel(this, stationLogic);
		this.payWithCashPanel = new PayWithCashPanel(this, stationLogic);
		this.thankYouPanel = new ThankYouPanel(this,stationLogic);
		this.stationLogic = stationLogic;
		
		screen.setTitle("Touch Screen");		
		screen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		screen.setUndecorated(false);
		screen.setExtendedState(JFrame.NORMAL);
		screen.setSize(800,600);
		screen.setLocationRelativeTo(null);
		screen.setVisible(true);
		
		
	}
	
	/**
	 *  A method to change the current panel being displayed on the Customer GUI.
	 *  Whenever the screen gets changed to welcome panel, the system resets.
	 *  
	 *  @param panelName 
	 *  			panelName the name of the panel to switch to
	 * */
	public void change(String panelName) {
		String language = stationLogic.getLanguage();
	    switch (panelName) {
	        case "welcomePanel":
	        	reset();
	        	this.welcomePanel.updateLanguage(language);
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(welcomePanel);
	            break;
	        case "languageSelectPanel":
	        	this.languageSelectPanel.updateLanguage(language);
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(languageSelectPanel);
	            break;    
	        case "chooseMembershipPanel":
	        	this.chooseMembershipPanel.updateLanguage(language);
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(chooseMembershipPanel);
	            break;
	        case "enterMembershipPanel":
	    		stationLogic.getSelfCheckoutStation().cardReader.enable();
	    		stationLogic.getSelfCheckoutStation().mainScanner.enable();
	    		stationLogic.getSelfCheckoutStation().handheldScanner.enable();
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(enterMembershipPanel);
	            break;
	        case "shoppingCartPanel":
	        	this.shoppingCartPanel.updateText();
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(shoppingCartPanel);
	        	break;
	        case "addItemPanel":
	        	screen.getContentPane().removeAll();
	        	this.addItemPanel = new AddItemPanel(this, stationLogic);
	        	screen.getContentPane().add(addItemPanel);
	        	break;
	        case "paymentPanel":
	        	this.paymentPanel.updateText();
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(paymentPanel);
	        	break;
	        case "ownBagsPanel":
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(ownBagsPanel);
	        	break;
	        case "pluPanel":
	        	screen.getContentPane().removeAll();
	        	this.pluPanel = new PLUPanel(this, addItemPanel, stationLogic);
	        	screen.getContentPane().add(pluPanel);
	        	break;
	        case "payWithCashPanel":
	        	payWithCashPanel.update();
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(payWithCashPanel);
	        	break;
	        case "thankYouPanel":
				try {
					thankYouPanel.update();
				} catch (UnpaidTotalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	screen.getContentPane().removeAll();
	        	screen.getContentPane().add(thankYouPanel);
	        	break;		
	            
	        default:
	            throw new IllegalArgumentException("Unknown panel name: " + panelName);
	    }
	    screen.revalidate();
	    screen.repaint();
	}
	
	/**
	 * This method resets the language, membership, card payment checker, and order.
	 * **/
	public void reset() {
		if(stationLogic.getMembershipInstance().GetId() != null) {
			stationLogic.getMembershipInstance().setID(null);
		}
		
		stationLogic.getPayCard().cardPaymentChecker = true;
		stationLogic.startOrder();
	}

}
