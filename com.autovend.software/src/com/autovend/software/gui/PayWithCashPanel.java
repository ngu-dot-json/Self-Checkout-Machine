/**
 *
 * SENG Iteration 3 P3-3 | PayWithCashPanel.java
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
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.DisabledException;
import com.autovend.devices.OverloadException;


/**
 * This is a panel that display the pay with cash screen
 * */
public class PayWithCashPanel extends JPanel {
	
	private CustomerScreen customerScreen;
	private SelfCheckoutStationLogic stationLogic;
	
	private JLabel label;
	private JLabel totalDue;
	private JLabel inserted;
	private JButton cancel;
	
	// For test
	private JButton insertBill;
	private JButton insertCoin;
	private static Bill billList[] = new Bill[5];
	private static Coin coinList[] = new Coin[6];
	private static JComboBox<Bill> billComboBox;
	private static JComboBox<Coin> coinComboBox;

	public PayWithCashPanel(CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		this.customerScreen = customerScreen;
		this.stationLogic = stationLogic;
		setLayout(null);
		this.setPreferredSize(new Dimension(800,600));
		
		label = new JLabel("Please Insert coin or bill");
		label.setBounds(204, 126, 160, 58);
		add(label);
		
		totalDue = new JLabel("Your Total:");
		totalDue.setBounds(233, 319, 335, 58);
		add(totalDue);
		
		inserted = new JLabel("Inserted:");
		inserted.setBounds(233, 387, 335, 58);
		add(inserted);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(53, 476, 171, 81);
		add(cancel);
		// go back to the previous screen (payment panel)
		cancel.addActionListener(e ->{
			customerScreen.change("paymentPanel");
		});
		
		// insertBill and insertCoin for testing purpose
		insertBill = new JButton("Insert Bill");
		insertBill.setBounds(518,282, 171,34);
		add(insertBill);
		// click on insertBill simulates inserting bill to the machine
		insertBill.addActionListener(e ->{
			try {
				stationLogic.getSelfCheckoutStation().billInput.accept(billList[billComboBox.getSelectedIndex()]);
			}catch (DisabledException de){
				System.out.println("Disabled!");
			}catch (OverloadException oe) {
				System.out.println("Overloaded!");
			}
			update();
		});
		
		// click on insertCoin simulates inserting coin to the machine
		insertCoin = new JButton("Insert Coin");
		insertCoin.setBounds(518,238, 171,34);
		add(insertCoin);
		insertCoin.addActionListener(e ->{
			try {
				stationLogic.getSelfCheckoutStation().coinSlot.accept(coinList[coinComboBox.getSelectedIndex()]);
			}catch (DisabledException de){
				System.out.println("Disabled!");
			}
			update();
		});
		
		billComboBox = new JComboBox<Bill>();
		billComboBox.setBounds(356, 287, 150, 22);
		add(billComboBox);
		
		coinComboBox = new JComboBox<Coin>();
		coinComboBox.setBounds(356, 244, 150, 22);
		add(coinComboBox);	
	}
	
	// updates the text in PayWithCashPanel
	public void update() {
		String priceString = NumberFormat.getCurrencyInstance(Locale.CANADA).format(stationLogic.getOrder().getTotal());
		totalDue.setText("Your Total: " + priceString);
		priceString = NumberFormat.getCurrencyInstance(Locale.CANADA).format(stationLogic.getOrder().getTotal().subtract(stationLogic.getOrder().getTotalDue()));
		inserted.setText("You Paid: " + priceString);
	}
	
	
	// below are for testing
	public static void addTestBills(Bill bill[]) {
		for(int i = 0; i < bill.length; i++ ) {
			billComboBox.addItem(bill[i]);
		}
	}
	
	public static void addTestBillList(Bill bill[]) {
		for(int i = 0; i < bill.length; i++ ) {
			billList[i] = (bill[i]);
		}
	}
	
	public static void addTestCoins(Coin coin[]) {
		for(int i = 0; i < coin.length; i++ ) {
			coinComboBox.addItem(coin[i]);
		}
	}
	
	public static void addTestCoinList(Coin coin[]) {
		for(int i = 0; i < coin.length; i++ ) {
			coinList[i] = (coin[i]);
		}
	}

}
