/**
 *
 * SENG Iteration 3 P3-3 | PaymentPanel.java
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

import com.autovend.Card;
import com.autovend.CreditCard;
import com.autovend.DebitCard;
import com.autovend.MethodOfPayment;
import com.autovend.Order;
import com.autovend.PaymentMethodException;
import com.autovend.ReusableBag;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.SellableUnit;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;

/**
 * @wbp.parser.entryPoint
 */
public class PaymentPanel extends JPanel {
	
	private CustomerScreen customerScreen;
	private static Card cardList[] = new Card[3];
	private static JTextArea textCartInfo;
	private GradientButton addItemButton;
	private JLabel TestPaymentLabel;
	private static GradientButton btnGiftCard;
	private static GradientButton btnCash;
	private static GradientButton btnDebit;
	private static GradientButton btnCredit;
	private static GradientButton purchaseBag;
	private static JComboBox<String> cardComboBox;
	private JLabel totalDueLabel;
	
	private JLabel notifLabel;
	private MethodOfPayment paymentMethod;
	
	private BagOptionPopup popup;
	
	SelfCheckoutStationLogic stationLogic;
	private JComboBox comboBox;
	private static GradientButton tapButton;
	private static GradientButton swipeButton;
	private static GradientButton insertButton;
	private static CardIssuer bank;
	
	public PaymentPanel(CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		setForeground(new Color(0, 0, 0));
		this.stationLogic = stationLogic;
		this.customerScreen = customerScreen;
		setLayout(null);
		this.setPreferredSize(new Dimension(800,600));
		
		this.setBackground(new Color(205, 219, 210));
		this.setLayout(null);
		
		textCartInfo = new JTextArea();
		textCartInfo.setBounds(447, 32, 326, 437);
		add(textCartInfo);
		
		addItemButton = new GradientButton("Add More Items", Color.GRAY, Color.LIGHT_GRAY);
		addItemButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		addItemButton.setBounds(438, 500, 336, 50);
		add(addItemButton);
		addItemButton.addActionListener(e ->{
			
			//change screen to shoppingCartPanel
			customerScreen.change("shoppingCartPanel");
		});
		
		TestPaymentLabel = new JLabel("Test Payment Method");
		TestPaymentLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
		TestPaymentLabel.setForeground(new Color(81, 97, 228));
		TestPaymentLabel.setBounds(10, 476, 194, 22);
		add(TestPaymentLabel);
		
		btnGiftCard = new GradientButton("Gift Card", new Color(55, 196, 67), Color.GREEN);
		btnGiftCard.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnGiftCard.setBounds(221, 49, 207, 96);
		add(btnGiftCard);
		
		//Button for GiftCard payment
		btnGiftCard.addActionListener(e ->{
			
			//if cart is empty:
			if(stationLogic.getOrder().getTotal().toString().equals("0")) {
				stationLogic.notifyCustomerIO("Your cart is empty!");
			}else {
				//change method of payment to giftcard
				paymentMethod = MethodOfPayment.GIFTCARD;
				try {
					//notify bank of method of payment
					stationLogic.pay(paymentMethod, bank);
				} catch (PaymentMethodException e1) {
					e1.printStackTrace();
				}
			}	

		});
		
		btnCash = new GradientButton("Cash", new Color(55, 196, 67), Color.GREEN);
		btnCash.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnCash.setBounds(10, 49, 207, 96);
		add(btnCash);
		
		//Button for cash payment
		btnCash.addActionListener(e ->{
			//If cart is empty:
			if(stationLogic.getOrder().getTotal().toString().equals("0")) {
				stationLogic.notifyCustomerIO("Your cart is empty!");
			}else {
				//switch to cash payment panel
				customerScreen.change("payWithCashPanel");
				try {
					//set method of payment to cash
					stationLogic.pay(MethodOfPayment.CASH);
				} catch (PaymentMethodException e1) {
					e1.printStackTrace();
				}

			}	
		});
		
		btnDebit = new GradientButton("Debit", new Color(55, 196, 67), Color.GREEN);
		btnDebit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnDebit.setBounds(10, 156, 207, 96);
		add(btnDebit);
		
		//Button for debit payment
		btnDebit.addActionListener(e ->{
			//If cart is empty:
			if(stationLogic.getOrder().getTotal().toString().equals("0")) {
				stationLogic.notifyCustomerIO("Your cart is empty!");
			}else {
				//set method of payment to debit
				paymentMethod = MethodOfPayment.DEBIT;
				try {
					//notify bank of method of payment
					stationLogic.pay(paymentMethod, bank);
				} catch (PaymentMethodException e1) {
					e1.printStackTrace();
				}
			}	

		});
		
		btnCredit = new GradientButton("Credit", new Color(55, 196, 67), Color.GREEN);
		btnCredit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnCredit.setBounds(221, 156, 207, 96);
		add(btnCredit);
		
		//Button for credit payment
		btnCredit.addActionListener(e ->{
			
			//If cart is empty:
			if(stationLogic.getOrder().getTotal().toString().equals("0")) {
				stationLogic.notifyCustomerIO("Your cart is empty!");
				}else {
				//set method of payment to credit
				paymentMethod = MethodOfPayment.CREDIT;
				try {
					//notify bank of method of payment
					stationLogic.pay(paymentMethod, bank);
				} catch (PaymentMethodException e1) {
					e1.printStackTrace();
				}
			}	
		});
		
		purchaseBag = new GradientButton("Purchase bags", new Color(55, 196, 67), Color.GREEN);
		purchaseBag.setFont(new Font("Tahoma", Font.PLAIN, 18));
		purchaseBag.setBounds(10, 263, 418, 179);
		add(purchaseBag);
		
		cardComboBox = new JComboBox<String>();
		cardComboBox.setBounds(10, 500, 184, 22);
		add(cardComboBox);
		
		tapButton = new GradientButton("Tap", new Color(55, 196, 67), Color.GREEN);
		tapButton.setBounds(198, 500, 89, 23);
		add(tapButton);
		
		tapButton.addActionListener(e ->{
			
			//create Card object based on combo box selection
			Card currentCard = cardList[cardComboBox.getSelectedIndex()];
			
			//Make sure that selected method of payment aligns with the card type 
			if (paymentMethod == MethodOfPayment.DEBIT || paymentMethod == MethodOfPayment.CREDIT) {
				
				//check if card is an instance of debit or credit
				if (currentCard instanceof DebitCard || currentCard instanceof CreditCard) {
					
					//make sure tap is enabled
					if (currentCard.isTapEnabled) {
						try {
							//attempt to tap card
							stationLogic.getSelfCheckoutStation().cardReader.tap(currentCard);
							updateText();
						} catch (IOException e1) {
							stationLogic.notifyCustomerIO("Card not read");
						}
					}
				}
			
				
			}
		});
		
		swipeButton = new GradientButton("Swipe", new Color(55, 196, 67), Color.GREEN);
		swipeButton.setBounds(287, 500, 89, 23);
		add(swipeButton);
		swipeButton.addActionListener(e ->{

			//create Card object based on combo box selection
			Card currentCard = cardList[cardComboBox.getSelectedIndex()];
			
			//Make sure that selected method of payment aligns with the card type 
			if (paymentMethod == MethodOfPayment.DEBIT || paymentMethod == MethodOfPayment.CREDIT) {
				
				//check if card is an instance of debit or credit
				if (currentCard instanceof DebitCard || currentCard instanceof CreditCard) {
					try {
						
						//Test signature for swiping
						BufferedImage signature = new BufferedImage(1, 1, 1);
						//Attempt to swipe card
						stationLogic.getSelfCheckoutStation().cardReader.swipe(currentCard, signature);
						updateText();
					} catch (IOException e1) {
						stationLogic.notifyCustomerIO("Card not read");
					}
				}		
			}
		});
		
		insertButton = new GradientButton("Insert", new Color(55, 196, 67), Color.GREEN);
		insertButton.setBounds(245, 523, 89, 23);
		add(insertButton);
		insertButton.addActionListener(e ->{
			
			//create Card object based on combo box selection
			Card currentCard = cardList[cardComboBox.getSelectedIndex()];
			
			//Make sure that selected method of payment aligns with the method of payment attempted
			if (paymentMethod != MethodOfPayment.CASH) {
				try {
					
					//Insert card simulation, PIN already entered
					stationLogic.getSelfCheckoutStation().cardReader.insert(currentCard, "1234");
					stationLogic.getSelfCheckoutStation().cardReader.remove();
					System.out.println(stationLogic.getOrder().getTotal().toString());
					updateText();
				} catch (IOException e1) {
					stationLogic.notifyCustomerIO("Card not read");
				}		
			}
		});
		
		purchaseBag.addActionListener(e ->{
			popup.setVisible(true);
		});
		
		
		
		popup = new BagOptionPopup(this, customerScreen, stationLogic);
		
		notifLabel = new JLabel("Please Select Payment Option");
		notifLabel.setBackground(new Color(237, 255, 230));
		notifLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
		notifLabel.setForeground(new Color(81, 97, 228));
		notifLabel.setBounds(10, 16, 418, 22);
		add(notifLabel);
		
		JLabel cartLabel = new JLabel("Cart:");
		cartLabel.setBounds(447, 16, 123, 13);
		add(cartLabel);
		
		totalDueLabel = new JLabel();
		totalDueLabel.setBounds(660, 479, 113, 11);
		
		add(totalDueLabel);
		popup.change("askingBagPanel");
		
		
		
	}
	
	public void updateText() {
		textCartInfo.setText(orderToString(stationLogic.getOrder()));
		totalDueLabel.setText("Total Price: $" + ((stationLogic.getOrder().getTotalDue())).setScale(2, RoundingMode.HALF_UP).toString());

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
	
	public static void addCards(Card cards[]) {
		for (int i = 0; i < cards.length; i++) {
			cardList[i] = cards[i];
		}
	}
	
	public static void addLabelsToComboBox(String labels[]) {
		for (int i = 0; i < labels.length; i++) {
			cardComboBox.addItem(labels[i]);
		}
	}
	
	public  void sendCustomerMessagePayment(String message) {
		notifLabel.setText(message);
	}
	
	public static void registerCardIssuer(CardIssuer cardIssuer) {
		bank = cardIssuer;
	}
	
	/**
	 * 
	 * Getter methods for GUI testing purposes
	 * 
	 */
	public static JButton getPurchaseBagsButton() {
		return purchaseBag;
	}
	
	public static JTextArea getOrderInfo() {
		return textCartInfo;
	}
	
	public static JButton getCashButton() {
		return btnCash;
	}
	
	public static JButton getDebitButton() {
		return btnDebit;
	}
	
	public static JButton getCreditButton() {
		return btnCredit;
	}
	
	public static JButton getGiftCardButton() {
		return btnGiftCard;
	}
	
	public static JComboBox<String> getCardCombo() {
		return cardComboBox;
	}
	
	public static JButton getTapButton() {
		return tapButton;
	}
	
	public static JButton getSwipeButton() {
		return swipeButton;
	}
	
	public static JButton getInsertButton() {
		return insertButton;
	}
}
