/**
 *
 * SENG Iteration 3 P3-3 | EnterMembershipPanel.java
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
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.autovend.Card;
import com.autovend.IBarcoded;
import com.autovend.IllegalDigitException;
import com.autovend.MembershipCard;
import com.autovend.MembershipNotFoundException;
import com.autovend.SelfCheckoutStationLogic;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.Font;

public class EnterMembershipPanel extends JPanel {
	
	private CustomerScreen customerScreen;
	private SelfCheckoutStationLogic stationLogic;

	private static JTextArea inputArea;
	private static JComboBox<String> cardComboBox;
	private static MembershipCard cardList[] = new MembershipCard[2];
	private PinPad pinPad;
	
	private static GradientButton confirm;
	private GradientButton previousScreen;
	private GradientButton cancel;
	private static GradientButton swipeButton;
	private static GradientButton scanButton;
	public static boolean panelChanged = false;
	
	private JLabel cardComboBoxLabel;
	private static JLabel lblNewLabel;
	

	
	public EnterMembershipPanel (CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic;
		this.customerScreen = customerScreen;
		setLayout(null);
		this.setPreferredSize(new Dimension(800,600));
		setBackground(new Color(205, 219, 210));

		
		lblNewLabel = new JLabel("ENTER MEMBERSHIP NUMBER");
		lblNewLabel.setFont(new Font("Khmer Sangam MN", Font.BOLD, 17));
		lblNewLabel.setBounds(183, 8, 665, 54);
		lblNewLabel.setForeground(new Color(81, 97, 228));
		add(lblNewLabel);
		
        inputArea = new JTextArea();
        inputArea.setBounds(183,74,442,54);
        add(inputArea);
        pinPad = new PinPad(inputArea);
        pinPad.setBounds(255,138,300,300);
        add(pinPad);
		
        
        confirm = new GradientButton ("CONFIRM", new Color(55, 196, 67), Color.GREEN);
        confirm.setBounds(644,466, 150,81);
        add(confirm);
        confirm.addActionListener(e ->{ // Checks the membership number in the input field, changes panel if valid
        	try {
        		// Runs membership number into membership number logic
        		stationLogic.enterMembershipNumberTyping(inputArea.getText());
        		
        		// Changes panels on success
        		customerScreen.change("ownBagsPanel");
        		panelChanged = true;
        		panelChanged();
        		
        		// Reset labels
        		lblNewLabel.setText("Scan or Swipe Membership Card, or Type Membership Number.");
        		inputArea.setText("");
        		
        	// Invalid length of membership id
        	}catch (IllegalDigitException ie){
        		
        		// Shows error message to customer
    			lblNewLabel.setText("The membership number should be 12 digits long.");
        		inputArea.setText("");
        		repaint();
        		
        	// Membership number is not in database
        	}catch (MembershipNotFoundException me) {
        		
        		// Shows error message to customer
        		lblNewLabel.setText("Membership number could not be found. Please try another.");
        		inputArea.setText("");
        		repaint();
        	}
		});
        
		previousScreen = new GradientButton("<<BACK", Color.DARK_GRAY, Color.LIGHT_GRAY);
		previousScreen.setBounds(331, 525, 150, 45);
		add(previousScreen);
		previousScreen.addActionListener(e ->{ // Returns to the previous screen
			
			// Resets labels
			inputArea.setText("");
			lblNewLabel.setText("Scan or Swipe Membership Card, or Type Membership Number.");
			
			// Changes to choose membership screen
			customerScreen.change("chooseMembershipPanel");
		});
		
		cancel = new GradientButton("CANCEL", new Color(0xB22222), new Color(0xFF4136));
		cancel.setFont(new Font("Kannada Sangam MN", Font.BOLD, 13));
		cancel.setBounds(6, 469, 150, 81);
		add(cancel);
		cancel.addActionListener(e ->{ // Cancels the order and returns to welcome screen

			// Resets labels
			inputArea.setText("");
			lblNewLabel.setText("Scan or Swipe Membership Card, or Type Membership Number.");
			
			// Changes back to welcome panel
			customerScreen.change("welcomePanel");
		});
		
		cardComboBox = new JComboBox<String>();
		cardComboBox.setBounds(230, 481, 150, 22);
		add(cardComboBox);
		
		scanButton = new GradientButton("Scan", new Color(0x6EA100), new Color(0x436A0D));
		scanButton.setBounds(392, 480, 89, 23);
		add(scanButton);
		scanButton.addActionListener(e ->{ // Scans the selected membership card
			
			// Attempts to scan the membership card
			try {
				stationLogic.getSelfCheckoutStation().mainScanner.scan(cardList[cardComboBox.getSelectedIndex()]);
			} catch (NullPointerException e1) {
				lblNewLabel.setText("Could not find scanned Membership Card in database.");
			}
			
			// Checks if scanned membership number was found in database
			if (stationLogic.getMembershipInstance().getIsScanFoundInDatabase() == true && stationLogic.getMembershipInstance().GetId() != null) {
				
				// Update membership field with scanned id
				inputArea.setText(stationLogic.getMembershipInstance().GetId());
				
				// Shows message to customer
				lblNewLabel.setText("Membership card scanned successfully. Press Confirm to continue.");
			}
		});
		
		swipeButton = new GradientButton("Swipe", new Color(0x6EA100), new Color(0x436A0D));
		swipeButton.setBackground(new Color(251, 252, 255));
		swipeButton.setBounds(498, 480, 89, 23);
		add(swipeButton);
		cardComboBoxLabel = new JLabel("Simulate Membership Card Action");
		cardComboBoxLabel.setFont(new Font("Kannada Sangam MN", Font.BOLD, 13));
		cardComboBoxLabel.setBounds(309, 455, 223, 14);
		cardComboBoxLabel.setForeground(new Color(81, 97, 228));
		add(cardComboBoxLabel);
		swipeButton.addActionListener(e ->{ // Swipes the selected 
			BufferedImage signature = new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB); // Temp signature for testing
			
			// Try to swipe the card
			try {
				stationLogic.getSelfCheckoutStation().cardReader.swipe(cardList[cardComboBox.getSelectedIndex()], signature); // Try swiping a card
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// Check if swiped card's id is found in database
    		if (stationLogic.getMembershipInstance().getIsSwipeFoundInDatabase() == false) {
    			
    			// Shows error to customer
    			lblNewLabel.setText("Could not find swiped Membership Card in database.");
    		} else if (stationLogic.getMembershipInstance().GetId() != null) {
    			
    			// Show success message to customer
    			lblNewLabel.setText("Membership card swiped successfully. Press Confirm to continue.");

    		} else {
    			lblNewLabel.setText("Could not find swiped Membership Card in database.");
    		}
		});
		
		
		
	}
	
	
	/*
	 * Sends membership string to the input text area
	 * 
	 */
	public static void sendMembershipToGUI(String input) {
		inputArea.setText(input);
	}
	
	
	/*
	 * Checks if input area is true
	 * 
	 */
	public static boolean inputArea() {
		if (inputArea == null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/*
	 * Fills list with testable membership cards for swiping/scanning
	 * 
	 */
	public static void addTestCards(MembershipCard cards[]) {
		for (int i = 0; i < cards.length; i++) {
			cardList[i] = cards[i];
		}
	}
	
	/*
	 * Adds testable membership cards to on screen menu as labels
	 * 
	 */
	public static void addComboBoxLabels(String labels[]) {
		for (int i = 0; i < labels.length; i++) {
			cardComboBox.addItem(labels[i]);
		}
	}
	
	/*
	 * Updates the language in this panel
	 * 
	 */
	public void updateLanguage(String language) {
		
		// Checks between available languages (English and French)
		if (language == "english") {
			lblNewLabel.setText("Enter membership");
			confirm.setText("CONFIRM");
			previousScreen.setText(">>Back");
			cancel.setText("Cancel");
		} else { // French
			lblNewLabel.setText("ENTRER LE NUMÉRO DE MEMBRE");
			confirm.setText("CONFIRMER");
			previousScreen.setText("<<DOS");
			cancel.setText("Annuler");
		}
	}
	
	
	/*
	 * Getters for unit testing
	 * 
	 */
	public static JComboBox<String> getComboBox() {
		return cardComboBox;
	}
	
	public static JButton scanButton() {
		return scanButton;
	}
	
	public static JButton confirmButton() {
		return confirm;
	}
	
	public static boolean panelChanged() {
		return panelChanged;
	}
	
	public static JTextArea enterMember() {
		return inputArea;
	}
	
	public static String getUnsuccessfulLabel() {
		return lblNewLabel.getText();
	}
	
	public static JButton swipeButton() {
		return swipeButton;
	}
}
