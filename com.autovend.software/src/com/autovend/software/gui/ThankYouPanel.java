/**
 *
 * SENG Iteration 3 P3-3 | ThankYouPanel.java
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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.autovend.MethodOfPayment;
import com.autovend.Order;
import com.autovend.PaymentMethodException;
import com.autovend.ReusableBag;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.SellableUnit;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.payments.UnpaidTotalException;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

/**
 * This panel is designed to display the 'Thank You' screen to the customer.
 * */
public class ThankYouPanel extends JPanel {

	private SelfCheckoutStationLogic stationLogic;
	private CustomerScreen customerScreen;
	private JButton takeReceipt;
	private JTextArea receipt;
	private JLabel totalPrice;

	public ThankYouPanel(CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic;
		this.customerScreen = customerScreen;
		setLayout(null);
		this.setPreferredSize(new Dimension(800,600));
		
		JLabel thank = new JLabel("Thank you for shopping with us!");
		thank.setFont(new Font("Tahoma", Font.PLAIN, 15));
		thank.setBounds(95, 78, 280, 22);
		add(thank);
		
		JLabel lblNewLabel = new JLabel("Please take your receipt.");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(95, 141, 185, 39);
		add(lblNewLabel);
		
		receipt = new JTextArea();
		receipt.setBounds(438, 11, 335, 487);
		add(receipt);
		
		totalPrice = new JLabel();
		totalPrice.setFont(new Font("Tahoma", Font.PLAIN, 14));
		totalPrice.setBounds(448, 508, 158, 39);
		add(totalPrice);
		
		takeReceipt = new JButton("Take Receipt");
		takeReceipt.setFont(new Font("Tahoma", Font.PLAIN, 18));
		takeReceipt.setBounds(41, 325, 207, 96);
		add(takeReceipt);
		// take receipt button takes you to the welcome screen and reset the logic
		takeReceipt.addActionListener(e ->{
			customerScreen.ownBagsPanel.reset();
			customerScreen.reset();
			customerScreen.change("welcomePanel");
		});
	}
	
	/**
	 * This method is to update the texts in this panel
	 * */		
	public void update() throws UnpaidTotalException {
		stationLogic.getSelfCheckoutStation().printer.cutPaper();
		receipt.setText(orderToString(stationLogic.getOrder()));
		totalPrice.setText("Your Total: " + stationLogic.getOrder().getTotal().toString());
		
	}
	
	/**
	 * This method is to convert order to String value
	 * 
	 * @param order
	 * 			current customer's order
	 * */
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
}
