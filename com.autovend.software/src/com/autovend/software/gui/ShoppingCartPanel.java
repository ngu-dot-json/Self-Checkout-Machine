/**
 *
 * SENG Iteration 3 P3-3 | ShoppingCartPanel.java
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import com.autovend.BarcodedUnit;
import com.autovend.IBarcoded;
import com.autovend.MembershipCard;
import com.autovend.Numeral;
import com.autovend.Order;
import com.autovend.ReusableBag;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.SellableUnit;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.items.Unit;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

public class ShoppingCartPanel extends JPanel implements ActionListener{
	private SelfCheckoutStationLogic stationLogic;
	private CustomerScreen customerScreen;
	
	private JPanel northContainer;
	private JTextArea notificationsTextArea;
	private JLabel instructionLabel;
	
    private JPanel cartPanel;
    private JLabel cartLabel;
    private static JTextArea itemTextDisplay;
    private JScrollPane cartItemsPane;
    private static JLabel totalPriceLabel;
    private GradientButton cancelButton;
    
    private JPanel buttonsPanel;
    private static GradientButton addItemButton;
    private GradientButton removeItemButton;
    private static GradientButton payButton;

    private JPanel simulationPanel;
    
    private JPanel addCBPanel;
    private JLabel simulateAddItemLabel;
    private static JComboBox<String> itemsComboBox;
    
    private JPanel weighCBPanel;
    private static JLabel simulateWeighItemLabel;
    private static JComboBox<String> weightComboBox;
    
    private JPanel simButtonPanel;
    private static GradientButton simulateScan;
    
    
    private static BarcodedProduct products[] = new BarcodedProduct[3];
  		
	public ShoppingCartPanel(CustomerScreen customerScreen2, SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic;
		this.customerScreen = customerScreen2;

		this.setPreferredSize(new Dimension(800,600));
		this.setLayout(new BorderLayout());
		
		//Creating Overhead Label/ TextArea for CustomerIO notifications;
		northContainer = new JPanel(new BorderLayout());
		
        notificationsTextArea = new JTextArea();
        notificationsTextArea.setEditable(false);
        notificationsTextArea.setForeground(Color.RED);
        notificationsTextArea.setFont(notificationsTextArea.getFont().deriveFont(Font.BOLD));
        notificationsTextArea.setOpaque(false);
        notificationsTextArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        
        northContainer.add(notificationsTextArea, BorderLayout.NORTH);
        northContainer.setBackground(new Color(205, 219, 210));
        
		instructionLabel = new JLabel("Start Scanning to Add Items", SwingConstants.CENTER);
		Font font = instructionLabel.getFont();
		Font boldFont = font.deriveFont(Font.BOLD, 16f);
		instructionLabel.setFont(new Font("Khmer Sangam MN", Font.BOLD, 22));
		instructionLabel.setForeground(new Color(81, 97, 228));
		
		northContainer.add(instructionLabel, BorderLayout.SOUTH);		
		add(northContainer, BorderLayout.NORTH);
		
		
		//Creating central panel with label, scroll area for cart contents, total price and space at bottom for simulation
		cartPanel = new JPanel(new BorderLayout());
		
		cartLabel = new JLabel("Items in Cart:", SwingConstants.LEFT);
		cartLabel.setBackground(new Color(205, 219, 210));
		cartPanel.add(cartLabel, BorderLayout.NORTH);
		
		itemTextDisplay = new JTextArea(50, 1); //Arbitrarily high number of rows, 2 columns for (item:price)
		itemTextDisplay.setEditable(false);
		itemTextDisplay.setLineWrap(true);
		itemTextDisplay.setWrapStyleWord(true);
		cartItemsPane = new JScrollPane(itemTextDisplay);
		
		cartPanel.add(cartItemsPane, BorderLayout.CENTER);
		
		JPanel totalPricePanel = new JPanel(new FlowLayout());
		totalPricePanel.setBackground(new Color(205, 219, 210));
		totalPriceLabel = new JLabel("Total Price: $0.00");

		totalPricePanel.add(totalPriceLabel);
		totalPricePanel.setAlignmentX(RIGHT_ALIGNMENT);
		
        cartPanel.add(totalPricePanel, BorderLayout.SOUTH);
		add(cartPanel, BorderLayout.CENTER);
        
	
		
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		
		
		JPanel addPanel = new JPanel();
		addPanel.setBackground(new Color(205, 219, 210));
		addItemButton = new GradientButton("Add Item Manually", new Color(55, 196, 67), Color.GREEN);
		addItemButton.addActionListener(this);
		addItemButton.setPreferredSize(new Dimension(150, 25));
		addPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		addPanel.add(addItemButton);
		
		buttonsPanel.add(addPanel);
		
		JPanel removePanel = new JPanel();
		removePanel.setBackground(new Color(205, 219, 210));
		removeItemButton = new GradientButton("Remove an Item", new Color(0xB22222), new Color(0xFF4136));
		removeItemButton.addActionListener(this);
		removeItemButton.setPreferredSize(new Dimension(150, 25));
		removePanel.add(removeItemButton);
		
		buttonsPanel.add(removePanel);
		
		
		JPanel payPanel = new JPanel();
		payPanel.setBackground(new Color(205, 219, 210));
		payButton = new GradientButton("Pay", new Color(55, 196, 67), Color.GREEN);
		payButton.addActionListener(this);
		payButton.setPreferredSize(new Dimension(150, 25));
		payPanel.add(payButton);
		
		buttonsPanel.add(payPanel);
		
	
		add(buttonsPanel, BorderLayout.EAST);
		
		JPanel cancelPanel = new JPanel(new BorderLayout());
		cancelPanel.setBackground(new Color(205, 219, 210));
		cancelButton = new GradientButton("Cancel",new Color(0xB22222), new Color(0xFF4136));
		cancelButton.setAlignmentY(BOTTOM_ALIGNMENT);
		cancelButton.addActionListener(this);
		cancelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		
		cancelPanel.add(cancelButton, BorderLayout.SOUTH);
		
		add(cancelPanel, BorderLayout.WEST);
		
		
		//Simulation Panel - Combo boxes and Buttons to simulate scanning and weighing of items.
		simulationPanel = new JPanel(new FlowLayout());
		simulationPanel.setBackground(new Color(205, 219, 210));

		
		addCBPanel = new JPanel(new BorderLayout());
		addCBPanel.setBackground(new Color(205, 219, 210));
		itemsComboBox = new JComboBox<String>();
		simulateAddItemLabel = new JLabel("Simulate Add Item:");
		simulateAddItemLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		addCBPanel.add(simulateAddItemLabel, BorderLayout.NORTH);
		addCBPanel.add(itemsComboBox, BorderLayout.CENTER);
		
		weighCBPanel = new JPanel(new BorderLayout());
		weighCBPanel.setBackground(new Color(205, 219, 210));
		weightComboBox = new JComboBox<String>();
		weightComboBox.addItem("Valid Weight");
		weightComboBox.addItem("Invalid Weight");
		simulateWeighItemLabel = new JLabel("Simulate Weigh Item:");
		simulateWeighItemLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		weighCBPanel.add(simulateWeighItemLabel, BorderLayout.NORTH);
		weighCBPanel.add(weightComboBox, BorderLayout.CENTER);
		
		simButtonPanel = new JPanel(new BorderLayout());
		simButtonPanel.setBackground(new Color(205, 219, 210));

		simulateScan = new GradientButton("Scan", new Color(55, 196, 67), Color.GREEN);
		simulateScan.addActionListener(this);
		
		simButtonPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

		simButtonPanel.add(simulateScan, BorderLayout.EAST);
		
		simulationPanel.add(addCBPanel);
		simulationPanel.add(weighCBPanel);
		simulationPanel.add(simButtonPanel);
		
		add(simulationPanel, BorderLayout.SOUTH);
		
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cancelButton) {
			customerScreen.change("welcomePanel");
			//Probably want a prompt "Are you sure you want to cancel transaction?"
		}
		if(e.getSource() == addItemButton) {
			customerScreen.change("addItemPanel");
		}
		if(e.getSource() == removeItemButton) {
			stationLogic.notifyCustomerIO("Please wait for attendant assistance.");
			stationLogic.notifyAttendantIO("Customer wishes to remove an item.");
			stationLogic.suspend();
		}
		if(e.getSource() == payButton) {
			stationLogic.customerScreen.paymentPanel.updateText();
			customerScreen.change("paymentPanel");
		}		
		if(e.getSource() == simulateScan) {
			//product = whatever String is selected in the combo box
			BarcodedProduct product = products[itemsComboBox.getSelectedIndex()];
			
			//create a unit from the product for scale and bagging area purposes
			BarcodedUnit productUnit = new BarcodedUnit(product.getBarcode(), product.getExpectedWeight());
			
			//check if mainScanner is disabled
			if (!stationLogic.getSelfCheckoutStation().mainScanner.isDisabled()) {
				stationLogic.getSelfCheckoutStation().mainScanner.scan(productUnit);
				
		    	String popupText = "Would you like to bag this item?";
		    	String popupTitle = "Customer Notification";
		    	int optionType = JOptionPane.YES_NO_OPTION;
		    	
		    	//Valid weight will simulate the expected weight of the selected item, system will allow item to be added to cart
				if (weightComboBox.getSelectedItem() == "Valid Weight") {
					Unit tempUnit = new Unit(productUnit.getWeight());
					
					//Popup window to select to bag item or not
				    int result = JOptionPane.showConfirmDialog(null, popupText, popupTitle, optionType);
				    if (result == JOptionPane.YES_OPTION) {
				    	stationLogic.notifyCustomerIO("Place Item in Bagging Area");
				    	
				    	//Add item to cart, update bagging area total weight
				    	stationLogic.getSelfCheckoutStation().baggingArea.add(tempUnit);
				    } else {
				    	
				    	//Add item to cart, do not update bagging area total weight.
				    	stationLogic.getAddItemScanController().addToOrderNoBaggingArea();
				    }
					
				    //Invalid unit selected
				} else {
					
					//Unit weight will be expected weight to be weight+100
					Unit tempUnit = new Unit(productUnit.getWeight()+100);
					
					//Prompt to add to cart (this is before the weight discrepancy check is done)
				    int result = JOptionPane.showConfirmDialog(null, popupText, popupTitle, optionType);
				    
				    if (result == JOptionPane.YES_OPTION) {
				    	stationLogic.notifyCustomerIO("Place Item in Bagging Area");
				    	//Attempt to add to cart, weight discrepancy occurs
				    	stationLogic.getSelfCheckoutStation().baggingArea.add(tempUnit);
				    } else {
				    	//Item added to cart, weight not an issue.
				    	stationLogic.getAddItemScanController().addToOrderNoBaggingArea();
				    }
				    
				}
				//Update text displayed on screen showing items in cart and prices
				updateText();
			}
		}
	}
	
	public void updateText() {
		//Will replace all text on cart screen with updated text correlating to current Order
		itemTextDisplay.setText(orderToString(stationLogic.getOrder()));
		//Update total price of cart to reflect total price of Order
		totalPriceLabel.setText("Total Price: $" + ((stationLogic.getOrder().getTotal())).setScale(2, RoundingMode.HALF_UP).toString());
	
	}
	
	public String orderToString(Order order) {
		StringBuilder sb = new StringBuilder();
		List<Product> products = order.getProducts();
		List<SellableUnit> sellables = order.getSellable();


		String description = null;
		String priceString = null;
		int whitespaceCount;
		// Loop over the products in the order and add them to the string
		for (Product product : products) {
			if (product.getClass() == BarcodedProduct.class) {
				// If its a BarcodedProduct, add its description, empty spaces until
				// the end of the line, then its unit price
				description = ((BarcodedProduct) product).getDescription();
				priceString = NumberFormat.getCurrencyInstance(Locale.CANADA).format(product.getPrice());

			} else {
				description = ((PLUCodedProduct) product).getDescription();
				double pluWeight;
				pluWeight = stationLogic.getOrder().getPLUWeightOfItem(((PLUCodedProduct) product));
				priceString = NumberFormat.getCurrencyInstance(Locale.CANADA).format(product.getPrice().multiply(new BigDecimal(pluWeight / 1000)));

			}

			// trim description if too long
			if (description.length() + priceString.length() + 1 > ReceiptPrinter.CHARACTERS_PER_LINE) {
				description = description.substring(0,
						(ReceiptPrinter.CHARACTERS_PER_LINE - priceString.length() - 1));
			}

			// Finds the number of spaces needed to push the price to the end of the line
			whitespaceCount = ReceiptPrinter.CHARACTERS_PER_LINE
					- description.length() - priceString.length();

			// Add the entry for this product to the string
			sb.append(description + " ".repeat(whitespaceCount) + priceString + "\n");
		}
		
		for(SellableUnit sellable: sellables) {
			if(sellable.getClass() == ReusableBag.class) {
				description = "Reusable bag";
				priceString = NumberFormat.getCurrencyInstance(Locale.CANADA).format(stationLogic.priceOfBag);
			}
			
			if (description.length() + priceString.length() + 1 > ReceiptPrinter.CHARACTERS_PER_LINE) {
				description = description.substring(0,
						(ReceiptPrinter.CHARACTERS_PER_LINE - priceString.length() - 1));
			}

			// Finds the number of spaces needed to push the price to the end of the line
			whitespaceCount = ReceiptPrinter.CHARACTERS_PER_LINE
					- description.length() - priceString.length();

			// Add the entry for this product to the string
			sb.append(description + " ".repeat(whitespaceCount) + priceString + "\n");
		}
		return sb.toString();
	}
	
	//method to add testable barcoded items that combo box refers to
	public static void addTestBarcodedItems(BarcodedProduct items[]) {
		for (int i = 0; i < items.length; i++) {
			products[i] = items[i];
		}
	}
	
	//method to add barcodeLabels to combo box
	public static void addBarcodeLabels(String labels[]) {
		for (int i = 0; i < labels.length; i++) {
			itemsComboBox.addItem(labels[i]);
		}
	}
	
	public static JButton getAddItemButton() {
		return addItemButton;
	}
	
	public static JButton getPayButton() {
		return payButton;
	}
	
	public static JComboBox<String> getMenu() {
		return itemsComboBox;
	}
	
	public static JButton getScanButton() {
		return simulateScan;
	}
	
	public static String getItemName() {
		return itemTextDisplay.getText();
	}
	
	public static String getTotalPrice() {
		return totalPriceLabel.getText();
	}
	
	public static JComboBox<String> getWeightMenu(){
		return weightComboBox;
	}
	
	
}
