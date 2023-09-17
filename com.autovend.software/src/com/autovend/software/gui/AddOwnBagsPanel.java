/**
 *
 * SENG Iteration 3 P3-3 | AddOwnBagsPanel.java
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
import java.awt.Font;
import java.math.BigDecimal;

import com.autovend.items.BagFromHome;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AddOwnBagsPanel extends JPanel {
	
	private CustomerScreen customerScreen;
	private GradientButton yesButton;
	private SelfCheckoutStationLogic stationLogic;
	private static GradientButton noButton;
	private JTextField weightTextField;
    private JButton enterWeightButton;
    private JLabel notificationsLabel;
	
	public AddOwnBagsPanel(CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic;
		this.customerScreen = customerScreen;
		setLayout(null);
		this.setPreferredSize(new Dimension(800,600));
		setBackground(new Color(205, 219, 210));

		
		
		noButton = new GradientButton("No", new Color(55, 196, 67), Color.GREEN);
		noButton.setFont(new Font("Kannada Sangam MN", Font.BOLD, 13));
		noButton.setBounds(398, 133, 273, 260);
		add(noButton);
		noButton.addActionListener(e ->{
			//switch to shoppingCartPanel
			customerScreen.change("shoppingCartPanel");
		});
		
		JLabel lblNewLabel = new JLabel("Would you like to use your own bags?");
		lblNewLabel.setFont(new Font("Kannada MN", Font.BOLD, 30));
		lblNewLabel.setForeground(new Color(81, 97, 228));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 37, 780, 66);
		add(lblNewLabel);
		
		GradientButton cancelButton = new GradientButton("Cancel", new Color(0xB22222), new Color(0xFF4136));
		cancelButton.setFont(new Font("Kannada Sangam MN", Font.BOLD, 13));
		cancelButton.setBounds(320, 450, 171, 81);
		add(cancelButton);
		
		cancelButton.addActionListener(e ->{
			//If ID is not null:
			if(stationLogic.getMembershipInstance().GetId() != null) {
				
				//make ID = null
				stationLogic.getMembershipInstance().setID(null);
				
	
			}
			//switch to welcomePanel
			//Reset station text
			lblNewLabel.setText("Would you like to use your own bags?");
			stationLogic.customerScreen.change("welcomePanel");
			
		});
		
		yesButton = new GradientButton("Yes",new Color(55, 196, 67), Color.GREEN);
		yesButton.setFont(new Font("Kannada Sangam MN", Font.BOLD, 13));
		yesButton.setBounds(101, 133, 273, 260);
		add(yesButton);
		
		weightTextField = new JTextField();
		weightTextField.setBounds(278, 104, 96, 19);
		weightTextField.setVisible(false);
		weightTextField.setText("");
		add(weightTextField);
		weightTextField.setColumns(10);
		
		enterWeightButton = new JButton("Enter");
		enterWeightButton.setVisible(false);
		enterWeightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Check if valid weight was entered if not:
				if(weightTextField.getText().equals("") ||Integer.parseInt(weightTextField.getText()) == 0) {
					notificationsLabel.setText("Please enter a valid weight");
					weightTextField.setText("");
				}
				//If valid weight entered:
				else {		
		
				int weight = Integer.parseInt(weightTextField.getText());
				
				//Create a BagFromHome object to pass into addOwnBagController
				BagFromHome homeBag = new BagFromHome(weight);
				
				//add bagFromHome to AddOwnBagController and to BaggingArea
				stationLogic.getAddOwnBagController().AddBag(homeBag);
				stationLogic.getSelfCheckoutStation().baggingArea.add(homeBag);
				
				//suspend station, will get unsuspended by Attendant
				stationLogic.suspend();
				
				stationLogic.notifyAttendantIO("Customer wishes to use own bags");
				stationLogic.notifyCustomerIO("Please wait for Attendant to approve bags");
				
				//switch frame to shoppingCartPanel
				customerScreen.change("shoppingCartPanel");
				lblNewLabel.setText("Would you like to use your own bag?");
				}
			}
		});
		enterWeightButton.setBounds(398, 102, 85, 21);
		add(enterWeightButton);
		
		notificationsLabel = new JLabel("");
		notificationsLabel.setBounds(122, 21, 549, 13);
        notificationsLabel.setForeground(Color.RED);
        notificationsLabel.setFont(notificationsLabel.getFont().deriveFont(Font.BOLD));
		
		add(notificationsLabel);
		
		
		yesButton.addActionListener(e ->{
			//Change text to simulation weight text
			lblNewLabel.setText("Please enter weight of bags:");
			
			//Make the textField and enterWeightButton visible
			weightTextField.setVisible(true);
			enterWeightButton.setVisible(true);
		});
	}
	
	public static JButton getNoButton() {
		return noButton;
	}
	
	public void reset() {
		//Hide label/button that appear for simulating bag weight
		weightTextField.setVisible(false);
		enterWeightButton.setVisible(false);
		customerScreen.change("welcomePanel");
	}
}
