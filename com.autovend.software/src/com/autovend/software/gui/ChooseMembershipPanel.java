/**
 *
 * SENG Iteration 3 P3-3 | ChooseMembershipPanel.java
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

import javax.swing.JButton;
import javax.swing.JPanel;

import com.autovend.SelfCheckoutStationLogic;

import javax.swing.JLabel;
import java.awt.Font;

public class ChooseMembershipPanel extends JPanel {
	
	private CustomerScreen customerScreen;
	
	private static GradientButton enterMembershipButton;
	private static GradientButton skipButton;
	private GradientButton cancelButton;
	private JLabel lblNewLabel;

	
	public ChooseMembershipPanel(CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		this.customerScreen = customerScreen;
		setLayout(null);
		setBackground(new Color(205, 219, 210));
		this.setPreferredSize(new Dimension(800,600));
		
		enterMembershipButton = new GradientButton("ENTER MEMBERSHIP",new Color(55, 196, 67), Color.GREEN);
		enterMembershipButton.setFont(new Font("Kannada Sangam MN", Font.BOLD, 13));
		enterMembershipButton.setBounds(101,133,273,269);
		add(enterMembershipButton);
		enterMembershipButton.addActionListener(e ->{ // Changes to enter membership panel
			customerScreen.change("enterMembershipPanel");
		});
		
		skipButton = new GradientButton("SKIP", new Color(55, 196, 67), Color.GREEN);
		skipButton.setFont(new Font("Kannada Sangam MN", Font.BOLD, 13));
		skipButton.setBounds(398,133,273,269);
		add(skipButton);
		skipButton.addActionListener(e ->{ // Changes to add own bag button
			customerScreen.change("ownBagsPanel");
		});
		
		cancelButton = new GradientButton("CANCEL", new Color(0xB22222), new Color(0xFF4136));
		cancelButton.setFont(new Font("Kannada Sangam MN", Font.BOLD, 13));
		cancelButton.setBounds(311,449,171,81);
		add(cancelButton);
		cancelButton.addActionListener(e ->{ // Cancels order and returns to welcome panel
			customerScreen.change("welcomePanel");
		});
		
		lblNewLabel = new JLabel("Do you have a membership?");
		lblNewLabel.setFont(new Font("Kannada MN", Font.BOLD, 30));
		lblNewLabel.setForeground(new Color(81, 97, 228));
		lblNewLabel.setBounds(193, 55, 543, 66);
		add(lblNewLabel);	
	}
	
	/*
	 * Updates the language in this panel
	 * 
	 */
	public void updateLanguage(String language) {
		
		// Checks between the available languages (English and French)
		if (language == "english") {
			lblNewLabel.setText("Do you have a membership?");
			enterMembershipButton.setText("Enter membership");
			skipButton.setText("Skip");
			cancelButton.setText("Cancel");
		} else { // French
			lblNewLabel.setText("Avez-vous une adhésion?");
			enterMembershipButton.setText("Entrez l'adhésion");
			skipButton.setText("Sauter");
			cancelButton.setText("Annuler");
		}
	}
	
	/*
	 * Getters for testing
	 * 
	 */
	public static JButton getSkipButton() {
		return skipButton;
	}
	
	public static JButton getEnterMembershipButton() {
		return enterMembershipButton;
	}
}
