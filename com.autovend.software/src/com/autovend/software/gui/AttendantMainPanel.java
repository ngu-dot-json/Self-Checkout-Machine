/**
 *
 * SENG Iteration 3 P3-3 | AttendantMainPanel.java
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

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Choice;
import java.awt.Color;

import javax.swing.JToggleButton;

import com.autovend.BarcodedUnit;
import com.autovend.ReusableBag;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.SupervisionStationLogic;
import com.autovend.devices.OverloadException;
import com.autovend.external.ProductDatabases;
import com.autovend.items.Unit;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

import javax.swing.JList;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JRadioButton;

public class AttendantMainPanel extends JPanel {

	public JTextArea notificationArea;
	private JComboBox<String> stationComboBox;
	private JComboBox<String> itemComboBox;
	private JComboBox<String> searchComboBox;
	private AttendantScreen attendantScreen;
	private ArrayList<SelfCheckoutStationLogic> stationList = new ArrayList<SelfCheckoutStationLogic>();
	private ArrayList<Product> productsFound;
	public JLabel lblUser;
	private JTextField inkField;
	private JTextField paperField;
	private JTextField bagField;
	private JTextArea inkArea;
	private JTextArea paperArea;
	private JTextArea bagArea;
	private JTextArea enabledArea;
	private JTextArea blockedArea;
	private JTextField searchField;
	private JTextField weightField;
	private SupervisionStationLogic logic;
	
	
	public AttendantMainPanel(AttendantScreen screen, SupervisionStationLogic logic) {
		this.attendantScreen = screen;
		this.logic = logic;
		setLayout(null);
		setBackground(new Color(222, 234, 220));
		this.setPreferredSize(new Dimension(800, 600));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 34, 263, 517);
		add(scrollPane);
		
		JRadioButton dontBagItemRButton = new JRadioButton("Do Not Bag Item");
		dontBagItemRButton.setBounds(545, 488, 133, 23);
		add(dontBagItemRButton);
		
		notificationArea = new JTextArea();
		notificationArea.setLineWrap(true);
		notificationArea.setWrapStyleWord(true);
		scrollPane.setViewportView(notificationArea);
		
		JLabel lblNotif = new JLabel("Notifications");
		lblNotif.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNotif.setBounds(10, 11, 106, 21);
		add(lblNotif);
		
		JLabel lblSelectCustomerStation = new JLabel("Select Customer Station");
		lblSelectCustomerStation.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblSelectCustomerStation.setBounds(289, 11, 169, 21);
		add(lblSelectCustomerStation);
		
		JButton logoutButton = new JButton("Logout");
		logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		logoutButton.setBounds(657, 11, 121, 23);
		add(logoutButton);
		logoutButton.addActionListener(e ->{ // Goes back to Login screen when you logout
			screen.change("loginPanel");
			screen.getStation().loginlogout.logout(); // Logouts the current attendant
		});
		
		JLabel lblStationStatus = new JLabel("Station Status");
		lblStationStatus.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblStationStatus.setBounds(289, 71, 169, 21);
		add(lblStationStatus);
		
		JButton btnRemoveItem = new JButton("Remove Item");
		btnRemoveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		btnRemoveItem.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnRemoveItem.setBounds(545, 307, 133, 22);
		add(btnRemoveItem);
		btnRemoveItem.addActionListener(e ->{ // Removes an item from the selected stations cart
			if (itemComboBox.getSelectedIndex() > -1) {
				
				// Gets the product corresponding to the selected item from the order
				Product product = stationList.get(stationComboBox.getSelectedIndex()).getOrder().getProducts().get(itemComboBox.getSelectedIndex());
				if (product.getClass() == BarcodedProduct.class) {
					
					// Remove selected product from customers order
					stationList.get(stationComboBox.getSelectedIndex()).getRemoveItemController().removeFromOrder(product);
					
					// Remove selected item from the Attendant menu
					itemComboBox.removeItem(itemComboBox.getSelectedIndex());
					
					// Update customer's shopping cart
					stationList.get(stationComboBox.getSelectedIndex()).customerScreen.shoppingCartPanel.updateText();
				} else {
					
					
					stationList.get(stationComboBox.getSelectedIndex()).getRemoveItemController().removeFromOrder(product);
					
					
					itemComboBox.removeItem(itemComboBox.getSelectedIndex());
					
					
					stationList.get(stationComboBox.getSelectedIndex()).customerScreen.shoppingCartPanel.updateText();
				}
				updateStationStatus(); // Updates the station status
			}
			
		});
		
		JLabel lblItemsInCart = new JLabel("Items in Cart");
		lblItemsInCart.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblItemsInCart.setBounds(289, 287, 169, 21);
		add(lblItemsInCart);
		
		lblUser = new JLabel("User: ");
		lblUser.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblUser.setBounds(657, 39, 121, 14);
		add(lblUser);
		
		enabledArea = new JTextArea();
		enabledArea.setText("Station Enabled: ");
		enabledArea.setBounds(289, 91, 246, 22);
		add(enabledArea);
		
		blockedArea = new JTextArea();
		blockedArea.setText("Station Blocked: ");
		blockedArea.setBounds(289, 125, 246, 22);
		add(blockedArea);
		
		inkArea = new JTextArea();
		inkArea.setText("Ink Empty: ");
		inkArea.setBounds(289, 160, 246, 22);
		add(inkArea);
		
		paperArea = new JTextArea();
		paperArea.setText("Paper Empty: ");
		paperArea.setBounds(289, 193, 246, 22);
		add(paperArea);
		
		bagArea = new JTextArea();
		bagArea.setText("Bags Empty: ");
		bagArea.setBounds(289, 226, 246, 22);
		add(bagArea);
		
		inkField = new JTextField();
		inkField.setColumns(10);
		inkField.setBounds(688, 162, 86, 20);
		add(inkField);
		
		paperField = new JTextField();
		paperField.setColumns(10);
		paperField.setBounds(688, 195, 86, 20);
		add(paperField);
		
		bagField = new JTextField();
		bagField.setColumns(10);
		bagField.setBounds(688, 228, 86, 20);
		add(bagField);
		
		JLabel lblNewLabel = new JLabel("Refill Amount");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(688, 250, 89, 14);
		add(lblNewLabel);
		
		JButton updateButton = new JButton("Update");
		updateButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		updateButton.setBounds(545, 35, 89, 23);
		add(updateButton);
		updateButton.addActionListener(e ->{
			updateStationStatus();
		});
		
		stationComboBox = new JComboBox<String>();
		stationComboBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		stationComboBox.setBounds(289, 35, 246, 22);
		add(stationComboBox);
		
		itemComboBox = new JComboBox<String>();
		itemComboBox.setBounds(289, 307, 246, 22);
		add(itemComboBox);
		
		JButton bagButton = new JButton("Refill Bags");
		bagButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		bagButton.setBounds(545, 226, 133, 22);
		add(bagButton);
		bagButton.addActionListener(e ->{ // Refills the bag dispenser
			ReusableBag bag = new ReusableBag();
			int bagCount = 0;
			try {
				bagCount = Integer.parseInt(bagField.getText()); // Gets the number of bags entered by Attendant
				
			} catch (NumberFormatException ex) {
				notifyAttendantGUI("Value entered is not of type int."); // Must be an integer
			}
			int i;
			for (i = 0; i < bagCount; i++) {
				try {
					stationList.get(stationComboBox.getSelectedIndex()).getSelfCheckoutStation().bagDispenser.load(bag); // Refills bag one at a time bagCount times
				} catch (OverloadException e1) {
					notifyAttendantGUI("Too many bags were added to dispenser"); // Catches the overload exception when dispenser is full
					break;
				}
			}
			if (i > 0) {
				notifyAttendantGUI(i+ " bags were added to "+ stationComboBox.getSelectedItem()); // Notifies attendant IO when bags are successfully added
			}
		});
		
		JButton paperButton = new JButton("Refill Paper");
		paperButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		paperButton.setBounds(545, 193, 133, 22);
		add(paperButton);
		paperButton.addActionListener(e ->{ // Refills the receipt printer's paper storage
			int paperCount = 0;
			try {
				paperCount = Integer.parseInt(paperField.getText()); // Gets number of paper units from textfield
				
			} catch (NumberFormatException ex) {
				notifyAttendantGUI("Value entered is not of type int."); // Must be int
			}
			 
			try {
				if (paperCount > 0) {
					
					// Loads paper units to selected stations receipt printer
					stationList.get(stationComboBox.getSelectedIndex()).getReceiptPrinterController().addPaper(paperCount);
					
					// Notifies number of units loaded
					notifyAttendantGUI(paperCount+ " paper units were loaded to "+ stationComboBox.getSelectedItem());
				}
			} catch (OverloadException e1) {
				notifyAttendantGUI("Too much paper was added to printer"); // Too many paper units
			}
		});
		
		JButton inkButton = new JButton("Refill Ink");
		inkButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		inkButton.setBounds(545, 160, 133, 22);
		add(inkButton);
		inkButton.addActionListener(e ->{ // Refills the receipt printer's ink storage
			int inkCount = 0;
			try {
				inkCount = Integer.parseInt(inkField.getText()); // Gets number of ink units from textfield
				
			} catch (NumberFormatException ex) {
				notifyAttendantGUI("Value entered is not of type int."); // Must be int
			}
			 
			try {
				if (inkCount > 0) {
					
					// Loads ink to selected station's receipt printer
					stationList.get(stationComboBox.getSelectedIndex()).getReceiptPrinterController().addInk(inkCount);
					
					// Notifies number of units loaded
					notifyAttendantGUI(inkCount+ " ink units were loaded to "+ stationComboBox.getSelectedItem());
				}
				
			} catch (OverloadException e1) {
				notifyAttendantGUI("Too much ink was added to printer"); // Too many ink units
			}
		});
		
		JButton enableButton = new JButton("Enable");
		enableButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		enableButton.setBounds(545, 92, 86, 22);
		add(enableButton);
		enableButton.addActionListener(e ->{ // Powers on the selected SelfCheckoutStation
			stationList.get(stationComboBox.getSelectedIndex()).startupStation();
			updateStationStatus();
		});
		
		JButton disableButton = new JButton("Disable");
		disableButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		disableButton.setBounds(635, 92, 86, 22);
		add(disableButton);
		disableButton.addActionListener(e ->{ // Powers off the selected SelfCheckoutStation
			stationList.get(stationComboBox.getSelectedIndex()).shutdownStation();
			updateStationStatus();
		});
		
		JButton blockButton = new JButton("Block");
		blockButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		blockButton.setBounds(545, 126, 86, 22);
		add(blockButton);
		blockButton.addActionListener(e ->{ // Blocks a SelfCheckoutStation from interaction
			stationList.get(stationComboBox.getSelectedIndex()).suspend();
			updateStationStatus();
		});
		
		JButton unblockButton = new JButton("Unblock");
		unblockButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		unblockButton.setBounds(635, 126, 86, 22);
		add(unblockButton);
		unblockButton.addActionListener(e ->{  // Unblocks a SelfCheckoutStation from interaction
			stationList.get(stationComboBox.getSelectedIndex()).unsuspend();
			updateStationStatus();
		});
		
		searchField = new JTextField();
		searchField.setBounds(289, 370, 246, 20);
		add(searchField);
		searchField.setColumns(10);
		
		
		JButton searchButton = new JButton("Search");
		searchButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		searchButton.setBounds(545, 369, 133, 22);
		add(searchButton);
		searchButton.addActionListener(e ->{
			searchDatabase();
		});
		
		JButton addItemButton = new JButton("Add Item ");
		addItemButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		addItemButton.setBounds(545, 402, 133, 22);
		add(addItemButton);
		addItemButton.addActionListener(e ->{ // Adds an item selected from Text Search 
			// Checks if comboBox is not empty
			if (searchComboBox.getSelectedIndex() > -1) {
				
				// Checks if stationComboBox is empty
				if (stationComboBox.getSelectedIndex() > -1) {
					
					// Gets if ordered is started at the self checkout station
					if (stationList.get(stationComboBox.getSelectedIndex()).isOrderStarted()) {
						
						// Checks if the selected product is PLU
						if (productsFound.get(searchComboBox.getSelectedIndex()) instanceof PLUCodedProduct) {
							
							
							// Try catch block for parsing to int
							try {
								int PLUweight = Integer.parseInt(weightField.getText()); // Get weight of PLU from attendant textfield
								
								// PLU weight must be greater than 0
								if (PLUweight > 0) {
									
									// Call Add Item By Search controller with the selected item's index in the search list
									stationList.get(stationComboBox.getSelectedIndex()).getItemTextSearchController().reactToTextSearchEvent(productsFound, searchComboBox.getSelectedIndex());
									
									// Create a temporary unit to add to the scale with PLU's weight
									Unit tempUnit = new Unit(PLUweight);
									
									// Weigh PLU item
									stationList.get(stationComboBox.getSelectedIndex()).getSelfCheckoutStation().scale.add(tempUnit);
									
									// Checks if customer wishes to bag item
									if (dontBagItemRButton.isSelected()) {
										
										// Adds to order without bagging
										stationList.get(stationComboBox.getSelectedIndex()).getItemTextSearchController().addToOrderNoBaggingArea();
									} else {
										
										// Tells customer to place the item in bagging area and adds the weight to the bagging area
										stationList.get(stationComboBox.getSelectedIndex()).notifyCustomerIO("Place Item in Bagging Area");
										stationList.get(stationComboBox.getSelectedIndex()).getSelfCheckoutStation().baggingArea.add(tempUnit);
									}
									
									// Updates customer's shopping cart text
									stationList.get(stationComboBox.getSelectedIndex()).customerScreen.shoppingCartPanel.updateText();
								} else {
									
									// Tells attendant to enter a valid weight
									logic.notifyAttendant("Weight of PLU item should be greater than 0.");
								}
								
							} catch (NumberFormatException ex) {
								
								// Tells attendant to enter an integer
								logic.notifyAttendant("Weight field is not of type integer.");
							}
							
						// instanceof BarcodedProduct
						} else {
							
							// Gets the selected product from search list
							Product prod = productsFound.get(searchComboBox.getSelectedIndex());
							
							// Create a temp unit with the barcoded item's weight
							Unit tempUnit = new Unit(((BarcodedProduct) prod).getExpectedWeight());
							
							// Call Add Item by Search controller with the selected item
							stationList.get(stationComboBox.getSelectedIndex()).getItemTextSearchController().reactToTextSearchEvent(productsFound, searchComboBox.getSelectedIndex());
							
							// Check if customer wants to bag this item
							if (dontBagItemRButton.isSelected()) {
								
								// Adds item without putting it in bagging area
								stationList.get(stationComboBox.getSelectedIndex()).getItemTextSearchController().addToOrderNoBaggingArea();
							} else {
								
								// Adds item to bagging area
								stationList.get(stationComboBox.getSelectedIndex()).getSelfCheckoutStation().baggingArea.add(tempUnit);
								
							}
							
							// Updates customer's shopping cart with new item
							stationList.get(stationComboBox.getSelectedIndex()).customerScreen.shoppingCartPanel.updateText();
						}
						
					}
				}
				
				// Updates the station status on attendant GUI
				updateStationStatus();
			}
		});
		
		weightField = new JTextField();
		weightField.setBounds(545, 451, 133, 20);
		add(weightField);
		weightField.setColumns(10);
		
		JLabel pluLabel = new JLabel("PLU Item Weight");
		pluLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		pluLabel.setBounds(545, 435, 133, 14);
		add(pluLabel);
		
		JLabel addItemLabel = new JLabel("Search for Item");
		addItemLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		addItemLabel.setBounds(289, 350, 246, 21);
		add(addItemLabel);
		
		searchComboBox = new JComboBox<String>();
		searchComboBox.setBounds(289, 401, 246, 22);
		add(searchComboBox);
		
		
		
	}
	
	
	
	/*
	 * Adds a new line to the notification area with the input message
	 * 
	 */
	public void notifyAttendantGUI(String message) {
		notificationArea.setText(notificationArea.getText() + message + "\n");
	}
	
	/*
	 * Registers a station to the GUI menu
	 * 
	 */
	public void addStationToGUI(SelfCheckoutStationLogic station, int stationCount) {
		stationList.add(stationCount-1, station);
		stationComboBox.addItem("Station " + String.valueOf(stationCount));
	}
	
	/*
	 * Removes a station from the GUI menu
	 * 
	 */
	public void removeStationFromGUI(SelfCheckoutStationLogic station) {
		int stationIndex = -1;
		for (int i = 0; i < stationList.size(); i++) {
			if (stationList.get(i) == station) {
				stationIndex = i;
				stationList.remove(stationIndex);
			}
		}
		
		if (stationIndex > -1) {
			stationComboBox.removeItemAt(stationIndex);
		}
	}
	
	/*
	 * Updates the lists and text areas on the 
	 * 
	 */
	private void updateStationStatus() {
		// Get the station from the station list
		SelfCheckoutStationLogic station = stationList.get(stationComboBox.getSelectedIndex());
		
		// Updates text areas by checking station's booleans
		blockedArea.setText("Station Blocked: " + station.isSuspended());
		enabledArea.setText("Station Enabled: " + station.isEnabled());
		paperArea.setText("Paper Empty: " + station.getReceiptPrinterController().isPaperLow());
		inkArea.setText("Ink Emtpy: " + station.getReceiptPrinterController().isInkLow());
		bagArea.setText("Bags Empty: " + station.rbdo.areBagsEmpty());
		
		// Removes all items from shopping cart menu to avoid dups
		itemComboBox.removeAllItems();
		
		// Checks if order is started
		if (station.isOrderStarted()) {
			
			// Gets the list of products in the customer's order
			List<Product> itemsInCart = station.getOrder().getProducts();
			
			
			// Fills the on screen menu with the items in the customer's order
			for (int i = 0; i < itemsInCart.size(); i++) {
				
				// Checks if product is Barcoded or PLU before adding
				if (itemsInCart.get(i).getClass() == BarcodedProduct.class) {
					
					itemComboBox.addItem(((BarcodedProduct) itemsInCart.get(i)).getDescription());
				} else {
					
					itemComboBox.addItem(((PLUCodedProduct) itemsInCart.get(i)).getDescription());
				}
			}
		}
	}
	
	
	/*
	 * Searches the item database with an input string, then fills the on screen menu with search results
	 * 
	 */
	private void searchDatabase() {
		
		// Gets search string from attendant input
		String searchString = searchField.getText();
		
		// Searches the list of products using Add Item By Text Search Controller
		productsFound = stationList.get(stationComboBox.getSelectedIndex()).getItemTextSearchController().ItemTextSearch(searchString, ProductDatabases.INVENTORY);
		
		// Removes all items from menu to avoid dups
		searchComboBox.removeAllItems();
		
		
		// Fills the on screen menu with the search results
		for (int i = 0; i < productsFound.size(); i++) {
			
			// Checks if there is inventory for the product
			if (ProductDatabases.INVENTORY.get(productsFound.get(i)) > 0) {
				if (productsFound.get(i) instanceof BarcodedProduct) {
					searchComboBox.addItem(((BarcodedProduct) productsFound.get(i)).getDescription());
				} else {
					searchComboBox.addItem(((PLUCodedProduct) productsFound.get(i)).getDescription());
				}
			}
		}
	}
}
