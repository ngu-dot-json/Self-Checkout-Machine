/**
 *
 * SENG Iteration 3 P3-3 | AddItemPanel.java
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

import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;

import com.autovend.Barcode;
import com.autovend.PriceLookUpCode;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.external.ProductDatabases;
import com.autovend.items.AddItemByBrowsingController;
import com.autovend.items.AddItemPLUController;
import com.autovend.items.BaggingAreaScaleController;
import com.autovend.items.Unit;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;

public class AddItemPanel extends JPanel implements ActionListener {
	private CustomerScreen customerScreen;
	private SelfCheckoutStationLogic stationLogic;

	private JPanel flexPanel;

	private JPanel pluSearchPanel;
	private JButton pluButton;

	private JPanel searchPanel;
	private JButton searchButton;

	private JPanel browseItemsPanel;
	private JLabel browseLabel;
	private static JList<String> browseList;

	private JPanel browseWeightPanel;
	private static JLabel browseWeightLabel;
	private JPanel fieldContainer;
	private JTextField browseWeightField;
	private JButton browseWeightButton;

	private JPanel backPanel;
	private JButton backButton;

	private String[] listOfItems;
	private PriceLookUpCode PLUCode;

	private AddItemByBrowsingController addByBrowseController;
	private BaggingAreaScaleController scaleController;

	public AddItemPanel(CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic;
		this.customerScreen = customerScreen;
		this.setPreferredSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout());

		flexPanel = new JPanel(new BorderLayout());
		flexPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));

		pluSearchPanel = new JPanel();

		pluButton = new JButton("Enter PLU Code:");
		pluButton.addActionListener(this);
		pluButton.setPreferredSize(new Dimension(150, 25));

		pluSearchPanel.add(pluButton);

		flexPanel.add(pluSearchPanel, BorderLayout.WEST);

		add(flexPanel, BorderLayout.NORTH);

		browseItemsPanel = new JPanel(new BorderLayout());
		browseLabel = new JLabel("Browse Catalog");
		browseLabel.setHorizontalAlignment(SwingConstants.CENTER);
		browseItemsPanel.add(browseLabel, BorderLayout.NORTH);

		browseList = new JList();
		browseList.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 0));

		// Creating a list that captures the String description of each item in the
		// database to display in the JList.

		String[] listOfItems = new String[ProductDatabases.BARCODED_PRODUCT_DATABASE.size()
				+ ProductDatabases.PLU_PRODUCT_DATABASE.size()];
		int counter = 0;

		// Iterate through PLU database first, add each String description to
		// listOfItems

		Iterator<Map.Entry<PriceLookUpCode, PLUCodedProduct>> it = ProductDatabases.PLU_PRODUCT_DATABASE.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<PriceLookUpCode, PLUCodedProduct> entry = it.next();
			PLUCodedProduct value = entry.getValue();
			listOfItems[counter] = value.getDescription();
			counter++;
		}

		// Then iterate through BARCODE database to get item descriptions for display.
		Iterator<Entry<Barcode, BarcodedProduct>> it2 = ProductDatabases.BARCODED_PRODUCT_DATABASE.entrySet()
				.iterator();
		while (it2.hasNext()) {
			Entry<Barcode, BarcodedProduct> entry = it2.next();
			BarcodedProduct value = entry.getValue();
			listOfItems[counter] = value.getDescription();
			counter++;
		}

		browseList.setListData(listOfItems);

		// This panel will be invisible until the customer selects a product and presses
		// "Add Item To Cart"
		// This panel will simulate weighing the PLU item, by entering the weight into
		// the text box
		browseWeightPanel = new JPanel(new BorderLayout());
		browseWeightPanel.setVisible(false);

		browseWeightLabel = new JLabel("(Sim) Enter Weight of Product:");
		browseWeightLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

		browseWeightButton = new JButton("Add Selected Item");
		browseWeightButton.setPreferredSize(new Dimension(175, 25));
		browseWeightButton.addActionListener(this);

		fieldContainer = new JPanel(new BorderLayout());
		browseWeightField = new JTextField();
		browseWeightField.setEditable(true);
		browseWeightField.setText("");
		browseWeightField.setPreferredSize(new Dimension(175, 25));

		fieldContainer.add(browseWeightLabel, BorderLayout.NORTH);
		fieldContainer.add(browseWeightField, BorderLayout.CENTER);
		fieldContainer.add(browseWeightButton, BorderLayout.SOUTH);

		browseWeightPanel.add(fieldContainer, BorderLayout.NORTH);

		browseItemsPanel.add(browseWeightPanel, BorderLayout.EAST);
		browseItemsPanel.add(browseList, BorderLayout.CENTER);

		add(browseItemsPanel, BorderLayout.CENTER);

		searchPanel = new JPanel();
		searchButton = new JButton("Add Item to Cart");
		searchButton.setPreferredSize(new Dimension(150, 25));
		searchButton.addActionListener(this);

		searchPanel.add(searchButton);

		add(searchPanel, BorderLayout.EAST);

		backPanel = new JPanel(new BorderLayout());
		backButton = new JButton("Back");
		backButton.setVerticalAlignment(SwingConstants.BOTTOM);
		backButton.addActionListener(this);

		backPanel.add(backButton, BorderLayout.WEST);

		add(backPanel, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// Back button will move UI to main shopping cart interface
		if (e.getSource() == backButton) {
			customerScreen.change("shoppingCartPanel");
		}

		// pressing "Enter PLU Code" will shift the frame to the PLU Panel
		if (e.getSource() == pluButton) {
			customerScreen.change("pluPanel");
		}
		// Pressing "Add Item to Cart"
		if (e.getSource() == searchButton) {

			// Save selected item string to compare to databases
			String selectedItem = browseList.getSelectedValue();

			// Save the index of the selected item
			int indexItem = browseList.getSelectedIndex();

			// index <= PLU database size = search PLU database for matching item to fetch
			// item data.
			if (indexItem <= ProductDatabases.PLU_PRODUCT_DATABASE.size()) {

				// Iterate through PLU database.
				Iterator<Map.Entry<PriceLookUpCode, PLUCodedProduct>> it = ProductDatabases.PLU_PRODUCT_DATABASE
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<PriceLookUpCode, PLUCodedProduct> entry = it.next();
					PriceLookUpCode code = entry.getKey();
					PLUCodedProduct value = entry.getValue();

					// if the description of the iterated pluProduct = the item name selected in the
					// JList:
					if (value.getDescription().equals(selectedItem)) {

						PLUCode = code;

						// React to browsing selected event -> addItemByBrowseController
						stationLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(PLUCode);

						// disable browseList to prevent selecting during add item process
						browseList.setEnabled(false);

						// Switch "Add Item to Cart" button for PLU weight simulation panel
						browseWeightPanel.setVisible(true);
						searchButton.setVisible(false);
						break;
					}
				}
			}
			// If index is larger than PLU database size, then it is a barcoded product.
			else {
				// Iterate through Barcoded database.
				Iterator<Entry<Barcode, BarcodedProduct>> it2 = ProductDatabases.BARCODED_PRODUCT_DATABASE.entrySet()
						.iterator();
				while (it2.hasNext()) {
					Entry<Barcode, BarcodedProduct> entry = it2.next();
					Barcode code = entry.getKey();
					BarcodedProduct value = entry.getValue();

					// if the description of the iterated barcodedProduct = the item name selected
					// in the JList:
					if (value.getDescription().equals(selectedItem)) {

						stationLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(code);

						// get expected weight of selected item
						Unit tempUnit = new Unit(value.getExpectedWeight());

						// Popup text to determine if bagging area weight will change
						String popupText = "Would you like to bag this item?";
						String popupTitle = "Customer Notification";
						int optionType = JOptionPane.YES_NO_OPTION;
						int result = JOptionPane.showConfirmDialog(null, popupText, popupTitle, optionType);
						if (result == JOptionPane.YES_OPTION) {
							stationLogic.notifyCustomerIO("Place Item in Bagging Area");
							stationLogic.getSelfCheckoutStation().baggingArea.add(tempUnit);
						} else {
							stationLogic.getAddByBrowseController().addToOrderNoBaggingArea();
						}
						// update text in main textField and total price.
						customerScreen.shoppingCartPanel.updateText();
						// switch frame to main shopping cart panel
						customerScreen.change("shoppingCartPanel");
						break;
					}
				}

			}

		}
		// browseWeightButton is the button pressed after the simulated weight for a PLU
		// item is entered.
		if (e.getSource() == browseWeightButton) {

			int weight = Integer.parseInt(browseWeightField.getText());
			Unit tempUnit = new Unit(weight);

			//add temp unit to scale to prepare for change in weight in bagging area
			stationLogic.getSelfCheckoutStation().scale.add(tempUnit);

			String popupText = "Would you like to bag this item?";
			String popupTitle = "Customer Notification";
			int optionType = JOptionPane.YES_NO_OPTION;
			int result = JOptionPane.showConfirmDialog(null, popupText, popupTitle, optionType);
			if (result == JOptionPane.YES_OPTION) {
				stationLogic.notifyCustomerIO("Place Item in Bagging Area");
				
				//add to cart, update total weight in bagging area
				stationLogic.getSelfCheckoutStation().baggingArea.add(tempUnit);
			} else {
				
				//add to cart, do not update weight in bagging area.
				stationLogic.getAddByBrowseController().addToOrderNoBaggingArea();
			}
			
			//reset addItemPanel
			browseList.setEnabled(true);
			browseWeightPanel.setVisible(false);
			browseWeightField.setText("");
			searchButton.setVisible(true);
			
			//update the text in the main shopping cart panel
			customerScreen.shoppingCartPanel.updateText();
			
			//switch frame to main shopping cart panel.
			customerScreen.change("shoppingCartPanel");
		}
	}

	public static JList<String> getMenuList() {
		return browseList;
	}

	public static JLabel getWeight() {
		return browseWeightLabel;
	}
}
