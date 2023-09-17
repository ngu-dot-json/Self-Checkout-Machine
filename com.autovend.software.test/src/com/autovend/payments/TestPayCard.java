/**
 *
 * SENG Iteration 3 P3-3 | TestPayCard.java
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

package com.autovend.payments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Bill;
import com.autovend.Card;
import com.autovend.Card.CardData;
import com.autovend.ChipFailureException;
import com.autovend.InvalidPINException;
import com.autovend.MagneticStripeFailureException;
import com.autovend.MethodOfPayment;
import com.autovend.Numeral;
import com.autovend.Order;
import com.autovend.PaymentMethodException;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.TapFailureException;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.CardReader;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;

public class TestPayCard {
	private int capacity;
    private int value;
    private Bill bill;
    private Currency currency;
    private int[] denominations;

    private SelfCheckoutStation checkout_station;
    private SelfCheckoutStationLogic checkout_station_logic;

    //products
    BigDecimal price_apple;
    BigDecimal price_banana;
    private BarcodedProduct apple;
    private BarcodedProduct banana;

    //orders
    private Order order_apple;
    private Order order_all_fruits;
    private Order order_nothing;

    //
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private PayCard pay_with_card;
    private CardIssuer amex;

    //creating a credit card
    private class CreditCard extends Card{

		public CreditCard(String number, String cardholder, String cvv, String pin, boolean isTapEnabled,
				boolean hasChip) {
			super("CREDIT", number, cardholder, cvv, pin, isTapEnabled, hasChip, hasChip);
			// TODO Auto-generated constructor stub
		}

    }
    private CreditCard amex_card;
    private CreditCard blocked_amex;

  //creating a Debit card
    private class DebitCard extends Card{

		public DebitCard(String number, String cardholder, String cvv, String pin, boolean isTapEnabled,
				boolean hasChip) {
			super("DEBIT", number, cardholder, cvv, pin, isTapEnabled, hasChip, hasChip);
			// TODO Auto-generated constructor stub
		}

    }
    private DebitCard amex_debit;

    //customer fake signature
    BufferedImage signature = new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);


	@Before
	public void setup() {
		System.setOut(new PrintStream(outContent));

		currency = Currency.getInstance(Locale.CANADA);
    	int[] bill_denom = {1,5,10,20,50,100};
    	int max_weight = 50;
        int scale_sensitivity = 1;
        BigDecimal[] coin_denom = { BigDecimal.ONE };

    	checkout_station = new SelfCheckoutStation(
    			currency,
    			bill_denom,
    			coin_denom,
    			max_weight,
    			scale_sensitivity
    		);

    	checkout_station_logic = new SelfCheckoutStationLogic(
    			checkout_station
    		);


    	//creating an apple barcode product
    	String apple_str = "apple";
        price_apple = BigDecimal.valueOf(5.0);
        double weight_apple =5.0;
        Barcode bar1=new Barcode(Numeral.one);
        apple = new BarcodedProduct(
        		bar1, apple_str, price_apple, weight_apple
        	);

        //creating an banana barcode product
        String banana_str = "banana";
        price_banana = BigDecimal.valueOf(2.0);
        double weight_banana = 2.0;
        Barcode bar2 = new Barcode(Numeral.two);
        banana = new BarcodedProduct(
        		bar2, banana_str, price_banana, weight_banana
        	);

        //creating Orders
        order_apple = new Order(checkout_station_logic);
        order_apple.add(apple);

        order_all_fruits = new Order(checkout_station_logic);
        order_all_fruits.add(apple);
        order_all_fruits.add(banana);

    	order_nothing = new Order(checkout_station_logic);

    	//creating the PayCard object
    	amex = new CardIssuer("AMERICAN EXPRESS");
    	pay_with_card = new PayCard(checkout_station_logic);

    	amex_card = new CreditCard(
    			"111111111111111", "PERSON", "123", "111", true, true
    		);
    	Calendar exp = Calendar.getInstance();
    	exp.set(Calendar.YEAR, 2030);
    	exp.set(Calendar.MONTH, 11);
    	exp.set(Calendar.DATE, 18);
    	amex.addCardData(
    			"111111111111111", "PERSON", exp, "123", BigDecimal.TEN
    		);

    	amex_debit = new DebitCard(
    			"222222222222222", "PERSON", "321", "222", true, true
    		);
    	amex.addCardData(
    			"222222222222222", "PERSON", exp, "321", BigDecimal.TEN
    		);
    	blocked_amex = new CreditCard(
    			"333333333333333", "PERSON", "333", "333", true, true
    		);
    	amex.addCardData(
    			"333333333333333", "PERSON", exp, "333", BigDecimal.TEN
    		);
    	amex.block("333333333333333");
	}

	@After
	public void teardown() {
		System.setOut(originalOut);

		checkout_station = null;
		checkout_station_logic = null;
		pay_with_card = null;
	}

	@Test
	public void testNoPayCredit() {

		String expected = String.format(
				"Customer I/O: Amount due: %.2f",
				price_apple
			);

		pay_with_card.pay(order_apple, MethodOfPayment.CREDIT, amex);
		assertTrue(outContent.toString().contains(expected));
	}

	@Test
	public void testPayCredit() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);

		checkout_station_logic.pay(MethodOfPayment.CREDIT, amex);

		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.tap(amex_card);
				break;
			}catch(TapFailureException e) {

			}
		}

		String expected = String.format(
				"Customer I/O: Transaction Complete"
			);
		assertTrue(outContent.toString().contains(expected));
	}

	@Test
	public void testNoPayDebit() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.DEBIT, amex);

		String expected = String.format(
				"Customer I/O: Amount due: %.2f",
				price_apple
			);


		assertTrue(outContent.toString().contains(expected));

	}

	@Test
	public void testPayDebit() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.DEBIT,amex);

		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.tap(amex_debit);
				break;
			}catch(ChipFailureException e) {

			}
		}

		String expected = String.format(
				"Customer I/O: Transaction Complete"
			);

		assertTrue(outContent.toString().contains(expected));
	}

	@Test
	public void testFakeCredit() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.CREDIT, amex);

		CreditCard fake_card = new CreditCard(
    			"33333333", "PERSON", "123", "111", true, true
    		);

		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.tap(fake_card);
				break;
			}catch(TapFailureException e) {

			}
		}

		String expected = String.format(
				"Customer I/O: Card declined.",
				price_apple
			);

		assertTrue(outContent.toString().contains(expected));
	}

	@Test
	public void testFakeDebit() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.DEBIT, amex);

		DebitCard fake_card = new DebitCard(
    			"33333333", "PERSON", "123", "111", true, true
    		);


		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.tap(fake_card);
				break;
			}catch(TapFailureException e) {

			}
		}


		String expected = String.format(
				"Customer I/O: Card declined.",
				price_apple
			);

		assertTrue(outContent.toString().contains(expected));
	}

	@Test
	public void testPayDebitSwipe() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.DEBIT,amex);



		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.swipe(amex_debit, signature);
				break;
			}catch(MagneticStripeFailureException e) {

			}
		}


		String expected = String.format(
				"Customer I/O: Transaction Complete"
			);

		assertTrue(outContent.toString().contains(expected));
	}

	@Test
	public void testPayCreditSwipe() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.CREDIT, amex);

		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.swipe(amex_debit, signature);
				break;
			}catch(MagneticStripeFailureException e) {

			}
		}

		String expected = String.format(
				"Customer I/O: Transaction Complete"
			);

		assertTrue(outContent.toString().contains(expected));
	}

	/**
	 * test with inserted credit card.
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test
	public void testPayCreditInsert() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.CREDIT, amex);

		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.insert(amex_card, "111");
				break;
			}catch(ChipFailureException e) {

			}
		}

		String expected = String.format(
				"Customer I/O: Transaction Complete"
			);

		assertTrue(outContent.toString().contains(expected));
	}

	/**
	 * Test pay with debit insert
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test
	public void testPayDebitInsert() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.DEBIT, amex);

		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.insert(amex_debit, "222");
				break;
			}catch(ChipFailureException e) {

			}
		}

		String expected = String.format(
				"Customer I/O: Transaction Complete"
			);

		assertTrue(outContent.toString().contains(expected));
	}

	@Test( expected = InvalidPINException.class)
	public void testPayDebitInsertInvalid() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.DEBIT, amex);


		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.insert(amex_debit, "221");
				break;
			}catch(ChipFailureException e) {

			}
		}
	}

	@Test( expected = InvalidPINException.class)
	public void testPayCreditInsertInvalid() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.CREDIT, amex);

		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.insert(amex_card, "1");
				break;
			}catch(ChipFailureException e) {

			}
		}

	}

	/**
	 * testing paying with a blocked credit card
	 *
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test
	public void testPayBlockCredit() throws IOException, PaymentMethodException {

		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		checkout_station_logic.pay(MethodOfPayment.CREDIT, amex);

		for(int i = 0; i < 10; i++) {
			try {
				checkout_station.cardReader.tap(blocked_amex);
				break;
			}catch(TapFailureException e) {

			}
		}

		String expected = String.format(
				"Customer I/O: Card declined.",
				price_apple
			);

		assertTrue(outContent.toString().contains(expected));
	}

	/**
	 * This class is used by the following test case to make a mock card issuer that will time out the connection.
	 *
	 */
	private class CardIssuerMock extends CardIssuer {
		int waitTime;
		public CardIssuerMock(String name) {
			super(name);
		}
		public void setWait(int wait) {
			waitTime= wait;
		}

		public int authorizeHold(String cardNumber, BigDecimal amount) {
			return 1;
		}
		public boolean postTransaction(String cardNumber, int holdNumber, BigDecimal actualAmount) {
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}

		public boolean releaseHold(String cardNumber, int holdNumber) {
			return true;
		}

	}

	/**
	 * This test case will check that the code will actually catch time out connections.
	 * THIS TEST WILL TAKE 100 SECONDS TO RUN
	 */
	@Test
	public void testAuthorizationFailed()  {
		CardIssuerMock fakeIssuer = new CardIssuerMock("mock");
		fakeIssuer.setWait(20000);
		checkout_station_logic.startOrder();
		checkout_station_logic.addToOrder(apple);
		pay_with_card.pay(checkout_station_logic.getOrder(), MethodOfPayment.CREDIT, fakeIssuer);
		CardData data = null;
		for(int i = 0; i < 10; i++) {
			try {
				data = amex_debit.swipe();
				break;
			}catch(IOException e) {

			}
		}
		pay_with_card.reactToCardDataReadEvent(checkout_station.cardReader, data);
	}

    /**
     * This test checks that the unimplemented methods do not have errors in them.
     */
    @Test
    public void testCheckUnimplementedCodeForUnexpectedErrors() {
    	pay_with_card.reactToCardRemovedEvent(checkout_station.cardReader);
    	pay_with_card.reactToEnabledEvent(checkout_station.cardReader);
    	pay_with_card.reactToDisabledEvent(checkout_station.cardReader);
    }

}
