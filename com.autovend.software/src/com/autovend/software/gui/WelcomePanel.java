/**
 *
 * SENG Iteration 3 P3-3 | WelcomePanel.java
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

import javax.swing.*;

import java.awt.*;

import com.autovend.SelfCheckoutStationLogic;

/**
 * This is a panel that displays the welcome screen.
 * */
public class WelcomePanel extends JPanel{
	
	private CustomerScreen customerScreen;
	private static GradientButton checkOutButton;
	private static GradientButton languageSelectButton;
	private SelfCheckoutStationLogic stationLogic; 
	private JLabel label;
	
	public WelcomePanel(CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic; 
		this.customerScreen = customerScreen;
		setLayout(null);
		setBackground(new Color(205, 219, 210));
		this.setPreferredSize(new Dimension(800,600));
	
		JLabel picture = new JLabel(new ImageIcon("gui.pictures/SelfCheckout.jpeg"));
		add(picture); 
		
		label = new JLabel("WELCOME!", SwingConstants.CENTER);
		label.setFont(new Font("Kannada MN", Font.BOLD, 40));
		label.setForeground(new Color(81, 97, 228));
		label.setBounds(425,55,385,50);
		add(label);
		
		checkOutButton = new GradientButton("Begin Checkout", new Color(55, 196, 67), Color.GREEN);
		checkOutButton.setFont(new Font("Kannada MN", Font.BOLD, 13));
		checkOutButton.setBounds(524, 323, 200, 50);
        add(checkOutButton);
        // click on checkOutButton starts the order and change the Customer Screen to choose membership panel
        checkOutButton.addActionListener(e ->{
        	stationLogic.startOrder();
            customerScreen.change("chooseMembershipPanel");
        });
		
		languageSelectButton = new GradientButton("Select Language", new Color(55, 196, 67), Color.GREEN);
		languageSelectButton.setFont(new Font("Kannada MN", Font.BOLD, 13));
		languageSelectButton.setBounds(541, 405, 160, 50);
		add(languageSelectButton);
		// click on languageSelectButton changes the screen to the language select panel
		languageSelectButton.addActionListener(e ->{
			customerScreen.change("languageSelectPanel");
		});
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setIcon(new ImageIcon(getClass().getResource("gui.pictures/SelfCheckout.jpeg")));
		lblNewLabel.setBounds(-17, -28, 430, 622);
		add(lblNewLabel);
		
		ImageIcon icon = new ImageIcon(getClass().getResource("gui.pictures/Fasticon-Shop-Cart-Shop-cart.512.png"));		 
		Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		ImageIcon resizedIcon = new ImageIcon(img);
		JLabel lblNewLabel_1 = new JLabel(resizedIcon);
		lblNewLabel_1.setBounds(579, 157, 100, 100);
		add(lblNewLabel_1);
		
	}
	
	/**
	 * This method updates the language of the station logic
	 * 
	 * @param language
	 * 			The supported languages for changing are English and French.
	 * */
	public void updateLanguage(String language) {
		if (language == "english") {
			checkOutButton.setText("Check-out");
			languageSelectButton.setText("Select Language");
			label.setText("WELCOME!");
		} else {
			languageSelectButton.setText("Choisir la langue");
			checkOutButton.setText("Vérifier");
			label.setText("BIENVENUE!");
		}
	}
	
	/**
	 *  Below are getter methods for testing purpose
	 * */
	public static JButton getBegin() {
		return checkOutButton;
	}

	public static JButton getLanguageSwitch() {
		return languageSelectButton;
	}

}
