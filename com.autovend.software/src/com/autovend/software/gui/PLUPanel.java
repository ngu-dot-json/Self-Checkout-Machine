/**
 *
 * SENG Iteration 3 P3-3 | PLUPanel.java
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

import javax.swing.*;

import com.autovend.items.AddItemPLUController;
import com.autovend.items.Unit;
import com.autovend.products.PLUCodedProduct;
import com.autovend.*;
import com.autovend.external.ProductDatabases;

public class PLUPanel extends JPanel implements ActionListener{
	private static boolean validPLU;
	private PLUCodedProduct pluProduct;
	private CustomerScreen customerScreen;
	
	private SelfCheckoutStationLogic stationLogic;
	
	private JPanel topContainer;
	private JTextArea notificationsTextArea;
	private JLabel instructionLabel;
	private AddItemPanel addItemPanel;
	
	private JPanel padPanel;
	private JTextArea padArea;
	private PinPad pinPad;
	private JButton search;
	
	private JPanel enterWeightPanel;
	private JLabel enterWeightLabel;
	private JTextArea enterWeightArea;
	private JButton addItemButton;
	
	private JPanel buttonPanel;
	private JButton backButton;
	
	//private SelfCheckoutStationLogic logic;
	
	public PLUPanel(CustomerScreen customerScreen, AddItemPanel addItemPanel, SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic;
		this.customerScreen = customerScreen;
		
		this.setPreferredSize(new Dimension(800,600));
		this.setLayout(new BorderLayout());
        
        topContainer = new JPanel();
        topContainer.setLayout(new BorderLayout());

        notificationsTextArea = new JTextArea();
        notificationsTextArea.setForeground(Color.RED);
        notificationsTextArea.setFont(notificationsTextArea.getFont().deriveFont(Font.BOLD));
        notificationsTextArea.setEditable(false);
        notificationsTextArea.setOpaque(false);
        
        instructionLabel = new JLabel("Enter PLU Code:");
		instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 50, 35, 0));

        topContainer.add(instructionLabel, BorderLayout.CENTER);
           	

        padArea = new JTextArea();
        padArea.setPreferredSize(new Dimension(150,25));
        padArea.setAlignmentX(CENTER_ALIGNMENT);
		padArea.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
		
        topContainer.add(notificationsTextArea, BorderLayout.NORTH);
        topContainer.add(padArea, BorderLayout.SOUTH);
        
        add(topContainer, BorderLayout.NORTH);
        
        padPanel = new JPanel();
        pinPad = new PinPad(padArea);
        pinPad.setAlignmentY(BOTTOM_ALIGNMENT);
        padPanel.add(pinPad);
        add(padPanel, BorderLayout.WEST);
        
        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setPreferredSize(new Dimension(150,25));
        
        search = new JButton ("Search");
        search.addActionListener(this);
    	search.setVisible(true);
    	search.setAlignmentY(TOP_ALIGNMENT);
    	search.setPreferredSize(new Dimension(150,25));
    	
        
    	enterWeightPanel = new JPanel(new BorderLayout());
    	enterWeightPanel.setVisible(false);
    	enterWeightPanel.setAlignmentX(CENTER_ALIGNMENT);
    	
    	enterWeightLabel = new JLabel();
    		
    	enterWeightArea = new JTextArea();
    	enterWeightArea.setEditable(false);
    	
    	addItemButton = new JButton("Add Item");
    	addItemButton.setVisible(false);
    	addItemButton.addActionListener(this);
    	addItemButton.setPreferredSize(new Dimension(150,25));
    	addItemButton.setAlignmentY(TOP_ALIGNMENT);
    	
    	enterWeightPanel.add(enterWeightLabel, BorderLayout.NORTH);
    	enterWeightPanel.add(enterWeightArea, BorderLayout.CENTER);
	


    	backButton = new JButton("Back");
    	backButton.addActionListener(this);
    	backButton.setHorizontalAlignment(SwingConstants.LEFT);
    	
    	buttonPanel.add(backButton);
    	buttonPanel.add(addItemButton);
        buttonPanel.add(search);
    	
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

    	add(buttonPanel, BorderLayout.SOUTH);
    	add(enterWeightPanel, BorderLayout.CENTER);
 	}
	
	//This method is called in the reactToPLUEnteredEvent in response to PLU found in database
	public static void PLUValid() {
		validPLU = true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Search button after entering PLU code
		if(e.getSource() == search) {
			
			//set validPLU to false
        	validPLU = false;
        	String pluText = padArea.getText();
        	
        	//Check to make sure input text is correct length (4-5 characters)
        	if (pluText.length() < 4 || pluText.length() > 5) {
        		//reset padArea
        		padArea.setText("");
        		
        		//error handling message
        		notificationsTextArea.setText("Please Enter a Valid PLU Code (4-5 digits)");
        	}
        	
        	else {
            	
        		//Have to convert input text to bytes because we need to end up with Numerals (which are made from bytes)
	        	byte[] bytes = new byte[pluText.length()];
        		for(int i = 0; i < pluText.length(); i++) {
        			//make a list of bytes based on the input
        			bytes[i] = (byte)Integer.parseInt(String.valueOf(pluText.charAt(i)));
        		}
        		//make new numeralList
	        	Numeral[] numeralList = new Numeral[bytes.length];
	        	
	        	//convert byte list into numeralList.
	        	for(int i = 0; i < bytes.length; i++) {
	        		numeralList[i] = Numeral.valueOf(bytes[i]);
	        	}
	        	
	        	//make new PLUCode from numeralList
	        	PriceLookUpCode PLUCode = new PriceLookUpCode(numeralList);
	        	
	        	//Use PLUCode to reactTOPLUEntereredEvent
	        	stationLogic.getPLUController().reactToPLUEnteredEvent(PLUCode);

	        	if(validPLU) {
	        		//Find plu product in database using plucode.
	        		PLUCodedProduct pluProduct = ProductDatabases.PLU_PRODUCT_DATABASE.get(PLUCode);
	        		
	        		//Next phase of frame: simulate weighing item;
	        		enterWeightPanel.setVisible(true);
	            	enterWeightArea.setEditable(true);
	        		enterWeightLabel.setText("The item you found is " + pluProduct.getDescription());
	        		
	        		//reset text area 
	        		padArea.setText("");
	        		
	        		//update instructions
	        		instructionLabel.setText("Enter the Weight of Item in Grams:");
	        		
	            	
	        		//change search button to an "add item button"
	            	search.setVisible(false);
	            	addItemButton.setVisible(true);
		        }
	        	else {
	        		//Reset text area and provide error handling instructions
	        		padArea.setText("");
	        		notificationsTextArea.setText("Could not find PLU Code. Please Try Again.");
	        	}
	        }
		}
		
		if(e.getSource() == addItemButton) {
			//When addItemButton is pressed, make sure number is valid
			if(padArea.getText().equals("") ||Integer.parseInt(padArea.getText()) == 0) {
				notificationsTextArea.setText("Please Enter a Valid Weight.");
				padArea.setText("");
			}
			else {
				//if valid, convert string to double
				double weight = Double.parseDouble(padArea.getText());
				
				//create temporary Unit to add to scale
				Unit tempUnit = new Unit(weight);
				
				//Unit added to scale, total weight noted.
				stationLogic.getSelfCheckoutStation().scale.add(tempUnit);
				String popupText = "Would you like to bag this item?";
			    String popupTitle = "Customer Notification";
			    int optionType = JOptionPane.YES_NO_OPTION;
			    int result = JOptionPane.showConfirmDialog(null, popupText, popupTitle, optionType);
			    if (result == JOptionPane.YES_OPTION) {
			    	stationLogic.notifyCustomerIO("Place Item in Bagging Area");
			    	//Item added to cart and placed in bagging area, total weight updated.
			    	stationLogic.getSelfCheckoutStation().baggingArea.add(tempUnit);
			    } else {
			    	//added to cart, no update in bagging area.
			    	stationLogic.getPLUController().addToOrderNoBaggingArea();
			    }
				
			    //Update cart on main shopping cart screen then goto main shopping screen
				customerScreen.shoppingCartPanel.updateText();
				customerScreen.change("shoppingCartPanel");
			}
		}
		if(e.getSource() == backButton) {
			
			//Go back to additemPanel
			customerScreen.change("addItemPanel");
		}
	}		
}
