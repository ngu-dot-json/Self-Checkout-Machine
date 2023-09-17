/**
 *
 * SENG Iteration 3 P3-3 | TestCustomerGUI.java
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

package com.autovend.gui;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;

import javax.swing.*;

import org.junit.*;

import com.autovend.Barcode;
import com.autovend.Card;
import com.autovend.CreditCard;
import com.autovend.DebitCard;
import com.autovend.GiftCard;
import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.MembershipCard;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.ReusableBag;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.SupervisionStationLogic;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.gui.AttendantScreen;
import com.autovend.software.gui.*;
import com.autovend.software.gui.CustomerScreenController;
import com.autovend.software.gui.EnterMembershipPanel;

import com.autovend.software.gui.PaymentPanel;

import com.autovend.software.gui.PayWithCashPanel;
import com.autovend.software.gui.ShoppingCartPanel;

public class TestCustomerGUI {
	SelfCheckoutStation station;
	SelfCheckoutStationLogic logic;
	Currency cad = Currency.getInstance("CAD");
	int cash[] = { 5, 10, 20, 50, 100 };
    BigDecimal penny = new BigDecimal(0.01);
    BigDecimal nickel = new BigDecimal(0.05);
    BigDecimal dime = new BigDecimal(0.10);
    BigDecimal quarter = new BigDecimal(0.25);
    BigDecimal loonie = new BigDecimal(1.00);
    BigDecimal toonie = new BigDecimal(2.00);
    BigDecimal[] coins = { penny, nickel, dime, quarter, loonie, toonie };
	int weight = 1000;
	int sens = 1;
	CustomerScreenStub obs;

	MembershipCard membershipCard = new MembershipCard("membership", "123456789123", "holder", false);
	MembershipCard cardList[] = { membershipCard };

	@Before
	public void setup() {

		station = new SelfCheckoutStation(cad, cash, coins, weight, sens);
		logic = new SelfCheckoutStationLogic(station);
		obs = new CustomerScreenStub();
		station.screen.register(obs);
		station.screen.disable();
		station.screen.enable();

	}

	public static void setVisible(SelfCheckoutStation station) {
		station.screen.setVisible(true);
	}

	@Test
	public void testEnabled() {
		station.screen.enable();
	}

	public static void start() throws OverloadException {
		Barcode barcode1 = new Barcode(Numeral.one);
		Barcode barcode2 = new Barcode(Numeral.one, Numeral.one);
		Barcode barcode3 = new Barcode(Numeral.one, Numeral.one, Numeral.one);
		Barcode barcode4 = new Barcode(Numeral.two, Numeral.two, Numeral.two);
		Barcode barcode5 = new Barcode(Numeral.one, Numeral.two, Numeral.three);
		Barcode barcode6 = new Barcode(Numeral.three, Numeral.three, Numeral.three);
		Barcode barcode7 = new Barcode(Numeral.four, Numeral.four, Numeral.four, Numeral.four);
		Barcode barcode8 = new Barcode(Numeral.five, Numeral.four, Numeral.three, Numeral.two, Numeral.one);
		Barcode barcode9 = new Barcode(Numeral.one, Numeral.two, Numeral.three, Numeral.four, Numeral.five);
		Barcode barcode10 = new Barcode(Numeral.one, Numeral.one, Numeral.one, Numeral.one, Numeral.one, Numeral.one,
				Numeral.one, Numeral.one, Numeral.one);

		BarcodedProduct product1 = new BarcodedProduct(barcode1, "Chips", BigDecimal.valueOf(4.99), 5.00);
		BarcodedProduct product2 = new BarcodedProduct(barcode2, "Cereal", BigDecimal.valueOf(2.99), 3.00);
		BarcodedProduct product3 = new BarcodedProduct(barcode3, "Chicken", BigDecimal.valueOf(12.99), 7.00);
		BarcodedProduct product4 = new BarcodedProduct(barcode4, "Hamburger Patties", BigDecimal.valueOf(13.99), 15.00);
		BarcodedProduct product5 = new BarcodedProduct(barcode5, "2% Milk", BigDecimal.valueOf(6.99), 7.00);
		BarcodedProduct product6 = new BarcodedProduct(barcode6, "6 Water Bottles", BigDecimal.valueOf(5.99), 9.00);
		BarcodedProduct product7 = new BarcodedProduct(barcode7, "Pork Chops", BigDecimal.valueOf(12.99), 14.00);
		BarcodedProduct product8 = new BarcodedProduct(barcode8, "Mixed Deli Meat", BigDecimal.valueOf(15.99), 10.00);
		BarcodedProduct product9 = new BarcodedProduct(barcode9, "Rice Crackers", BigDecimal.valueOf(5.99), 7.00);
		BarcodedProduct product10 = new BarcodedProduct(barcode10, "Basmati Rice - 8Lbs", BigDecimal.valueOf(9.99),
				8.00);

		PriceLookUpCode PLUCode1 = new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.one, Numeral.one);
		PriceLookUpCode PLUCode2 = new PriceLookUpCode(Numeral.two, Numeral.two, Numeral.two, Numeral.two);
		PriceLookUpCode PLUCode3 = new PriceLookUpCode(Numeral.three, Numeral.three, Numeral.three, Numeral.three);
		PriceLookUpCode PLUCode4 = new PriceLookUpCode(Numeral.four, Numeral.four, Numeral.four, Numeral.four);
		PriceLookUpCode PLUCode5 = new PriceLookUpCode(Numeral.five, Numeral.five, Numeral.five, Numeral.five,
				Numeral.five);
		PriceLookUpCode PLUCode6 = new PriceLookUpCode(Numeral.six, Numeral.six, Numeral.six, Numeral.six);
		PriceLookUpCode PLUCode7 = new PriceLookUpCode(Numeral.seven, Numeral.seven, Numeral.seven, Numeral.seven);
		PriceLookUpCode PLUCode8 = new PriceLookUpCode(Numeral.eight, Numeral.eight, Numeral.eight, Numeral.eight);
		PriceLookUpCode PLUCode9 = new PriceLookUpCode(Numeral.nine, Numeral.nine, Numeral.nine, Numeral.nine);
		PriceLookUpCode PLUCode10 = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four,
				Numeral.five);

		PLUCodedProduct pluProduct1 = new PLUCodedProduct(PLUCode1, "Mackeral Nigiri", BigDecimal.valueOf(3.99));
		PLUCodedProduct pluProduct2 = new PLUCodedProduct(PLUCode2, "Medium-Rare Rib-Eye Steak (Pre-Cooked)",
				BigDecimal.valueOf(30.99));
		PLUCodedProduct pluProduct3 = new PLUCodedProduct(PLUCode3, "Cucumber", BigDecimal.valueOf(1.59));
		PLUCodedProduct pluProduct4 = new PLUCodedProduct(PLUCode4, "Gala Apples", BigDecimal.valueOf(2.99));
		PLUCodedProduct pluProduct5 = new PLUCodedProduct(PLUCode5, "Cheddar Cheese", BigDecimal.valueOf(6.59));
		PLUCodedProduct pluProduct6 = new PLUCodedProduct(PLUCode6, "Roma Tomatoes", BigDecimal.valueOf(10.99));
		PLUCodedProduct pluProduct7 = new PLUCodedProduct(PLUCode7, "Yukon Gold Potatoes", BigDecimal.valueOf(8.59));
		PLUCodedProduct pluProduct8 = new PLUCodedProduct(PLUCode8, "Parmesan Cheese", BigDecimal.valueOf(24.99));
		PLUCodedProduct pluProduct9 = new PLUCodedProduct(PLUCode9, "Avocados", BigDecimal.valueOf(4.99));
		PLUCodedProduct pluProduct10 = new PLUCodedProduct(PLUCode10, "Solid Gold Italian Bread",
				BigDecimal.valueOf(1430.99));

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, product1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, product3);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode4, product4);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode5, product5);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode6, product6);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode7, product7);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode8, product8);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode9, product9);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode10, product10);

		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode1, pluProduct1);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode2, pluProduct2);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode3, pluProduct3);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode4, pluProduct4);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode5, pluProduct5);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode6, pluProduct6);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode7, pluProduct7);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode8, pluProduct8);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode9, pluProduct9);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode10, pluProduct10);

		ProductDatabases.INVENTORY.put(product1, 10);
		ProductDatabases.INVENTORY.put(product2, 10);
		ProductDatabases.INVENTORY.put(product3, 10);
		ProductDatabases.INVENTORY.put(product4, 10);
		ProductDatabases.INVENTORY.put(product5, 10);
		ProductDatabases.INVENTORY.put(product6, 10);
		ProductDatabases.INVENTORY.put(product7, 10);
		ProductDatabases.INVENTORY.put(product8, 10);
		ProductDatabases.INVENTORY.put(product9, 10);
		ProductDatabases.INVENTORY.put(product10, 10);

		ProductDatabases.INVENTORY.put(pluProduct1, 10);
		ProductDatabases.INVENTORY.put(pluProduct2, 10);
		ProductDatabases.INVENTORY.put(pluProduct3, 10);
		ProductDatabases.INVENTORY.put(pluProduct4, 10);
		ProductDatabases.INVENTORY.put(pluProduct5, 10);
		ProductDatabases.INVENTORY.put(pluProduct6, 10);
		ProductDatabases.INVENTORY.put(pluProduct7, 10);
		ProductDatabases.INVENTORY.put(pluProduct8, 10);
		ProductDatabases.INVENTORY.put(pluProduct9, 10);
		ProductDatabases.INVENTORY.put(pluProduct10, 10);
		
		ArrayList<Card> paycards = new ArrayList<Card>();

		DebitCard debit = new DebitCard("debit", "123123123", "Person", "123", "1234", true, true);
		CreditCard credit = new CreditCard("credit", "111111111", "Person", "123", "1234", true, true);
		GiftCard gift = new GiftCard("GIFTCARD", "222222222", "1234", Currency.getInstance("CAD"), BigDecimal.valueOf(1500));
		
		Card cardInputs[] = {debit, credit, gift};
		String cardLabels[] = {"Debit", "Credit", "Giftcard"};
		
		Calendar expiry = Calendar.getInstance();
		expiry.set(2023, 12, 30);
		
		CardIssuer bank = new CardIssuer("bank");
		bank.addCardData("123123123", "Person", expiry, "123", BigDecimal.valueOf(1500));
		bank.addCardData("111111111", "Person", expiry, "123", BigDecimal.valueOf(1500));
		bank.addCardData("222222222", "Person", expiry, "123", BigDecimal.valueOf(1500));
		
		SelfCheckoutStation station;
		SelfCheckoutStationLogic logic;
		Currency cad = Currency.getInstance("CAD");
		int cash[] = { 5, 10, 20, 50, 100 };

        BigDecimal penny = new BigDecimal("0.01");
        BigDecimal nickel = new BigDecimal("0.05");
        BigDecimal dime = new BigDecimal("0.10");
        BigDecimal quarter = new BigDecimal("0.25");
        BigDecimal loonie = new BigDecimal("1.00");
        BigDecimal toonie = new BigDecimal("2.00");
        BigDecimal[] coins = { penny, nickel, dime, quarter, loonie, toonie };

		int weight = 100000;
		int sens = 1;
		CustomerScreenStub obs;
		MembershipCard membershipCard1 = new MembershipCard("membership", "123456789123", "holder", false);
		MembershipCard membershipCard2 = new MembershipCard("membership", "222222222222", "holder", false);
		MembershipCard cardList[] = { membershipCard1, membershipCard2 };
		String[] labels = { "Valid Card", "Invalid Card" };

		station = new SelfCheckoutStation(cad, cash, coins, weight, sens);
		logic = new SelfCheckoutStationLogic(station);
		logic.addMember("123456789012");
		obs = new CustomerScreenStub();
		station.screen.register(obs);
		station.screen.disable();
		station.screen.enable();
		logic.addMember("123456789123");
		logic.addMember("123456789012");
		
		EnterMembershipPanel.addTestCards(cardList);
		EnterMembershipPanel.addComboBoxLabels(labels);

		SupervisionStation astation = new SupervisionStation();
		SupervisionStationLogic alogic = new SupervisionStationLogic(astation);
		ReusableBag bag = new ReusableBag();
		try {
			logic.getSelfCheckoutStation().bagDispenser.load(bag, bag, bag, bag, bag, bag, bag, bag, bag, bag, bag, bag);
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BarcodedProduct barcodedProducts[] = { product1, product2, product3 };
		String barcodelabels[] = { product1.getDescription(), product2.getDescription(), product3.getDescription()};

		ShoppingCartPanel.addBarcodeLabels(barcodelabels);
		ShoppingCartPanel.addTestBarcodedItems(barcodedProducts);
		
		PaymentPanel.addLabelsToComboBox(cardLabels);
		PaymentPanel.addCards(cardInputs);
		PaymentPanel.registerCardIssuer(bank);
		logic.setCardIssuer(bank);
		

		logic.startupStation();
		
		Bill five = new Bill(5,cad);
		Bill ten = new Bill(10,cad);
		Bill twenty = new Bill(20,cad);
		Bill fifty = new Bill(50,cad);
		Bill hundred = new Bill(100,cad);
		Bill bill[] = {five,ten,twenty,fifty,hundred};
		

		Coin oneCent = new Coin(penny, cad);
		Coin fiveCents = new Coin(nickel, cad);
		Coin tenCents = new Coin(dime, cad);
		Coin twentyFiveCents = new Coin(quarter, cad);
		Coin oneDollar = new Coin(loonie, cad);
		Coin twoDollar = new Coin(toonie, cad);
		Coin coin[] = {oneCent, fiveCents, tenCents, twentyFiveCents, oneDollar, twoDollar};
		
		PayWithCashPanel.addTestBillList(bill);
		PayWithCashPanel.addTestBills(bill);
		PayWithCashPanel.addTestCoinList(coin);
		PayWithCashPanel.addTestCoins(coin);
		
		
		setupMachine(station, logic, bill, coin);
		

	}
	
	@Test
	public void membershipValid() {	
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton enterMember = ChooseMembershipPanel.getEnterMembershipButton();
		
		enterMember.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownMenu = EnterMembershipPanel.getComboBox();
		
		// We select a valid card
		dropDownMenu.setSelectedIndex(0);
				
		//We then scan
		JButton scan = EnterMembershipPanel.scanButton();
		
		scan.doClick();
		
		JButton confirm = EnterMembershipPanel.confirmButton();
		confirm.doClick();
				
		Assert.assertTrue(EnterMembershipPanel.panelChanged());
	}
	
	/**
	 * This test will determine if the correct exception is thrown if user gives invalid membership
	 *
	 * @param args
	 */
	@Test
	public void invalidMembership() {
		// Move on to important pages to begin tests
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		// First, we get the combo box
		JComboBox<String> dropDownMenu = EnterMembershipPanel.getComboBox();
		
		// We select an invalid card
		dropDownMenu.setSelectedIndex(1);
		
		//We then swipe
		JButton swipe = EnterMembershipPanel.swipeButton();
		swipe.doClick();
		
		Assert.assertEquals("Could not find swiped Membership Card in database.", EnterMembershipPanel.getUnsuccessfulLabel());
	}
	
	/**
	 * This test will determine if the language is correctly changed to French
	 *
	 * @param args
	 */
	@Test
	public void updateToFrench() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton languageSwitch = WelcomePanel.getLanguageSwitch();
		languageSwitch.doClick();
		
		JButton frenchButton = LanguageSelectPanel.getFrench();
		frenchButton.doClick();
	
		Assert.assertEquals("Choisir la langue", languageSwitch.getText());
	}
	
	/**
	 * This test will determine if the total price of items is correct after adding 2 items
	 *
	 * @param args
	 */
	@Test
	public void totalPrice() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownMenu = ShoppingCartPanel.getMenu();
		// We select an invalid card
		dropDownMenu.setSelectedIndex(0);
		
		JButton scanButton = ShoppingCartPanel.getScanButton();
		scanButton.doClick();
		
		// We select an invalid card
		dropDownMenu.setSelectedIndex(1);		
		scanButton.doClick();
		
		Assert.assertEquals(ShoppingCartPanel.getTotalPrice(), "Total Price: $7.98");
	}
	
	/**
	 * This test will determine if the correct item is being added
	 *
	 * @param args
	 */
	@Test
	public void addCorrectItem() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();	
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownMenu = ShoppingCartPanel.getMenu();
		// We select an invalid card
		dropDownMenu.setSelectedIndex(0);
		
		JButton scanButton = ShoppingCartPanel.getScanButton();	
		scanButton.doClick();
		
		Assert.assertEquals(ShoppingCartPanel.getItemName(),"Chips                                                  $4.99\n");
	}
	
	/**
	 * This test will determine if the manually added item will appear in the current order text
	 *
	 */
	@Test
	public void addItemManually() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		JButton addManually = ShoppingCartPanel.getAddItemButton();
		addManually.doClick();
		
		JList<String> browseList = AddItemPanel.getMenuList();
		browseList.setSelectedIndex(0);
				
		JLabel enterWeight = AddItemPanel.getWeight();
		enterWeight.setText("10");
	}
	
	/**
	 *
	 *
	 */
	@Test
	public void addItemInvalidWight() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();	
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownMenu = ShoppingCartPanel.getMenu();
		// We select an invalid card
		dropDownMenu.setSelectedIndex(0);
		
		// First, we get the combo box
		JComboBox<String> weightMenu = ShoppingCartPanel.getWeightMenu();
		// We select an invalid card
		weightMenu.setSelectedIndex(1);
		
		JButton scanButton = ShoppingCartPanel.getScanButton();	
		scanButton.doClick();		
	}
	
	/**
	 * This test will determine if bags are correctly added
	 *
	 * @param args
	 */
	@Test
	public void correctNumOfBags() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		
		JButton payButton = ShoppingCartPanel.getPayButton();
		payButton.doClick();
		
		JButton bagsButton = PaymentPanel.getPurchaseBagsButton();
		bagsButton.doClick();
		
		JTextArea numOfBags = AskingBagPanel.getBagsWanted();
		numOfBags.setText("3");
		
		JButton okayButton = AskingBagPanel.getOkayButton();
		okayButton.doClick();
						
		JButton confirm = ConfirmBagPanel.getConfirmation();
		confirm.doClick();
		
		JTextArea order = PaymentPanel.getOrderInfo();
		
		Assert.assertEquals(order.getText(),"Reusable bag                                           $0.50\n"
				+ "Reusable bag                                           $0.50\n"
				+ "Reusable bag                                           $0.50\n");
	}
	
	
	/**
	 * This test will see if the correct action is taken if the bag dispenser is empty
	 *
	 * @param args
	 */
	@Test
	public void bagDispenserEmpty() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		
		JButton payButton = ShoppingCartPanel.getPayButton();
		payButton.doClick();
		
		JButton bagsButton = PaymentPanel.getPurchaseBagsButton();
		bagsButton.doClick();
		
		JTextArea numOfBags = AskingBagPanel.getBagsWanted();
		numOfBags.setText("101");
		
		JButton okayButton = AskingBagPanel.getOkayButton();
		okayButton.doClick();
						
		JButton confirm = ConfirmBagPanel.getConfirmation();
		confirm.doClick();
		
		Assert.assertEquals("Bag Dispenser is empty.", ConfirmBagPanel.getLabel());
	}
	
	
	/**
	 * This test will determine if paying with cash works
	 *
	 * @param args
	 */
	@Test
	public void payWithCash() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();	
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownMenu = ShoppingCartPanel.getMenu();
		// We select an invalid card
		dropDownMenu.setSelectedIndex(0);
		
		JButton scanButton = ShoppingCartPanel.getScanButton();	
		scanButton.doClick();
		
		JButton payButton = ShoppingCartPanel.getPayButton();	
		payButton.doClick();
		
		JButton cash = PaymentPanel.getCashButton();
		cash.doClick();
		
		
	}
	
	/**
	 * This test will determine if credit card payment works + testing tap
	 *
	 * @param args
	 */
	@Test
	public void creditCardPayTap() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();	
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownMenu = ShoppingCartPanel.getMenu();
		// We select an invalid card
		dropDownMenu.setSelectedIndex(0);
		
		JButton scanButton = ShoppingCartPanel.getScanButton();	
		scanButton.doClick();
		
		JButton payButton = ShoppingCartPanel.getPayButton();	
		payButton.doClick();
		
		JButton creditCard = PaymentPanel.getCreditButton();
		creditCard.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownCards = PaymentPanel.getCardCombo();
		// We select an invalid card
		dropDownCards.setSelectedIndex(1);
		
		JButton tap = PaymentPanel.getTapButton();
		tap.doClick();
	}
	
	/**
	 * This test will determine if the debit card works + testing swipe
	 *
	 * @param args
	 */
	@Test
	public void debitCardPaySwipe() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();	
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownMenu = ShoppingCartPanel.getMenu();
		// We select an invalid card
		dropDownMenu.setSelectedIndex(0);
		
		JButton scanButton = ShoppingCartPanel.getScanButton();	
		scanButton.doClick();
		
		JButton payButton = ShoppingCartPanel.getPayButton();	
		payButton.doClick();
		
		JButton debitCard = PaymentPanel.getDebitButton();
		debitCard.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownCards = PaymentPanel.getCardCombo();
		// We select an invalid card
		dropDownCards.setSelectedIndex(0);
		
		JButton swipe = PaymentPanel.getSwipeButton();
		swipe.doClick();
	}
	
	/**
	 * This test will determine if the gift card works + testing insert
	 *
	 * @param args
	 */
	@Test
	public void giftCardPayInsert() {
		try {
			start();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton skip = ChooseMembershipPanel.getSkipButton();	
		skip.doClick();
		
		JButton noButton = AddOwnBagsPanel.getNoButton();
		noButton.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownMenu = ShoppingCartPanel.getMenu();
		// We select an invalid card
		dropDownMenu.setSelectedIndex(0);
		
		JButton scanButton = ShoppingCartPanel.getScanButton();	
		scanButton.doClick();
		
		JButton payButton = ShoppingCartPanel.getPayButton();	
		payButton.doClick();
		
		JButton giftCard = PaymentPanel.getGiftCardButton();
		giftCard.doClick();
		
		// First, we get the combo box
		JComboBox<String> dropDownCards = PaymentPanel.getCardCombo();
		// We select an invalid card
		dropDownCards.setSelectedIndex(2);
		
		JButton insert = PaymentPanel.getInsertButton();
		insert.doClick();
		
	}
	
	@Test
	public void addPLU() {
		
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}



	public static void setupMachine(SelfCheckoutStation station,
			SelfCheckoutStationLogic logic,
			Bill bill[],
			Coin coin[]
			) throws  OverloadException{
		
		logic.getReceiptPrinterController().addInk(1000000);
		logic.getReceiptPrinterController().addPaper(1024);
		
		station.billStorage.load(bill);
		station.coinStorage.load(coin);		
	}
	
	public static void main(String[] args) throws OverloadException {
		start();
		
	}
}
