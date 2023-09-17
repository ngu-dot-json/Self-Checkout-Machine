/**
 *
 * SENG Iteration 3 P3-3 | PayWithGiftCardTest.java
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
import com.autovend.MethodOfPayment;
import com.autovend.Numeral;
import com.autovend.Order;
import com.autovend.PaymentMethodException;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.TapFailureException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;


/**
 * Tests for the payWithGiftCard class
 */
public class PayWithGiftCardTest {

	private int capacity;
    private int value;
    private Bill bill;
    private Currency curr;
    private int[] denom;
    BigDecimal gcAmount;
    
    private SelfCheckoutStation coStation;
    private SelfCheckoutStationLogic coStationLogic;
    
    //products
    BigDecimal applePrice;
    BigDecimal bananaPrice;
    private BarcodedProduct apple;
    private BarcodedProduct banana;
    
    //orders
    private Order orderApple;
    private Order orderAllFruits;
    private Order orderNothing;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    private PayWithGiftCard payWithGC;
    private CardIssuer cardIssuer;
    
//	creating a gift card
    private class GiftCard extends Card{

		public GiftCard(String number, String pin, Currency currency, BigDecimal amount) {
			super("GIFTCARD", number, "BEARER", "0", pin, false, true, false);
		}
    	
    }
    private GiftCard giftCard;
    private GiftCard blockedGC;
   
    
    /**
     * This will run before every test
     */
    @Before
	public void setup() {
		System.setOut(new PrintStream(outContent));
		
		gcAmount = BigDecimal.valueOf(25.0);
		curr = Currency.getInstance(Locale.CANADA);
    	int[] billDenom = {1,5,10,20,50,100};
    	int maxWeight = 50;
        int scaleSensitivity = 1;
        BigDecimal[] coinDenom = { BigDecimal.ONE };
    	
    	coStation = new SelfCheckoutStation(curr, billDenom, coinDenom, maxWeight, scaleSensitivity);
    	coStationLogic = new SelfCheckoutStationLogic(coStation);
    	
    	//creating an apple barcode product
    	String appleStr = "apple";
        applePrice = BigDecimal.valueOf(5.0);
        double appleWeight = 5.0;        
        Barcode bar1 = new Barcode(Numeral.one);
        apple = new BarcodedProduct(bar1, appleStr, applePrice, appleWeight);
        
        //creating an banana barcode product
        String bananaStr = "banana";
        bananaPrice = BigDecimal.valueOf(2.0);
        double bananaWeight = 2.0;
        Barcode bar2 = new Barcode(Numeral.two);
        banana = new BarcodedProduct(bar2, bananaStr, bananaPrice, bananaWeight);
        
        //creating Orders
        orderApple = new Order(coStationLogic);
        orderApple.add(apple);
        
        orderAllFruits = new Order(coStationLogic);
        orderAllFruits.add(apple);
        orderAllFruits.add(banana);
        
    	orderNothing = new Order(coStationLogic);
    	
    	//creating expiry date of card
    	Calendar exp = Calendar.getInstance();
    	exp.set(Calendar.YEAR, 2030);
    	exp.set(Calendar.MONTH, 11);
    	exp.set(Calendar.DATE, 18);
    	
    	//creating the PayCard object
    	cardIssuer = new CardIssuer("GIFT CARD COMPANY");
    	payWithGC = new PayWithGiftCard(coStationLogic);
    	
    	giftCard = new GiftCard("111111111111111", "1234", curr, gcAmount);
    	cardIssuer.addCardData("111111111111111", "BEARER", exp, "123", gcAmount);
    	
    	blockedGC = new GiftCard("222222222222222", "4321", curr, gcAmount);
    	cardIssuer.addCardData("222222222222222", "BEARER", exp, "123", gcAmount);
    	cardIssuer.block("222222222222222");
	    							
	}
	
    
    /**
     * This will run after every test
     */
	@After
	public void teardown() {
		System.setOut(originalOut);

		coStation = null;
		coStationLogic = null;
		payWithGC = null;
	}
	
	
	/**
	 * Test paying with gift card when nothing is added to the order yet
	 */
	@Test
	public void testNoPayGC() {
		String expected = String.format("Customer I/O: Amount due: 5.00");
		String expected1 = "Customer I/O: Insert Gift Card";
		
		payWithGC.pay(orderApple, MethodOfPayment.GIFTCARD, cardIssuer);
		assertTrue(outContent.toString().contains(expected));
		assertTrue(outContent.toString().contains(expected1));
	}
    
	
	/**
	 * Test paying with gift card when one item is added to the order
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test
	public void testPayGC1() throws IOException, PaymentMethodException {
		
		coStationLogic.startOrder();
		coStationLogic.addToOrder(apple);

		coStationLogic.pay(MethodOfPayment.GIFTCARD, cardIssuer);
		
		for(int i = 0; i < 10; i++) {
			try {
				coStation.cardReader.insert(giftCard, "1234");
				break;
			}catch(TapFailureException e) {
				
			}
		}
		
		String expected = String.format("Customer I/O: Transaction Complete");
		assertTrue(outContent.toString().contains(expected));
	}
	
	
	/**
	 * Test paying with gift card when two different items are added to the order
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test
	public void testPayGC2() throws IOException, PaymentMethodException {
		
		coStationLogic.startOrder();
		coStationLogic.addToOrder(apple);
		coStationLogic.addToOrder(banana);		

		coStationLogic.pay(MethodOfPayment.GIFTCARD, cardIssuer);
		
		for(int i = 0; i < 10; i++) {
			try {
				coStation.cardReader.insert(giftCard, "1234");
				break;
			}catch(TapFailureException e) {
				
			}
		}
		
		String expected = String.format("Customer I/O: Transaction Complete");
		assertTrue(outContent.toString().contains(expected));
	}
	
	
	/**
	 * Test paying with gift card when two of one item and another item is added to the order
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test
	public void testPayGC3() throws IOException, PaymentMethodException {
		
		coStationLogic.startOrder();
		coStationLogic.addToOrder(apple);
		coStationLogic.addToOrder(apple);
		coStationLogic.addToOrder(banana);		

		coStationLogic.pay(MethodOfPayment.GIFTCARD, cardIssuer);
		
		for(int i = 0; i < 10; i++) {
			try {
				coStation.cardReader.insert(giftCard, "1234");
				break;
			}catch(TapFailureException e) {
				
			}
		}
		
		String expected = String.format("Customer I/O: Transaction Complete");
		assertTrue(outContent.toString().contains(expected));
	}
	
	
	/**
	 * Test paying with gift card when the order total exceeds the amount on gift card
	 * Should prompt for a gift card once again
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test
	public void testPayGC4() throws IOException, PaymentMethodException {
		
		coStationLogic.startOrder();
		coStationLogic.addToOrder(apple);
		coStationLogic.addToOrder(apple);
		coStationLogic.addToOrder(apple);
		coStationLogic.addToOrder(apple);
		coStationLogic.addToOrder(apple);
		coStationLogic.addToOrder(banana);		

		coStationLogic.pay(MethodOfPayment.GIFTCARD, cardIssuer);
		
		for(int i = 0; i < 10; i++) {
			try {
				coStation.cardReader.insert(giftCard, "1234");
				break;
			}catch(TapFailureException e) {
				
			}
		}
		
		String expected = String.format("Customer I/O: Insert Gift Card");
		assertTrue(outContent.toString().contains(expected));
	}
	
	
	/**
	 * testing paying with a blocked credit card
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test
	public void testPayBlockGC() throws IOException, PaymentMethodException {

		coStationLogic.startOrder();
		coStationLogic.addToOrder(apple);
		coStationLogic.pay(MethodOfPayment.GIFTCARD, cardIssuer);

		for(int i = 0; i < 10; i++) {
			try {
				coStation.cardReader.insert(blockedGC, "4321");
				break;
			}catch(TapFailureException e) {

			}
		}

		String expected = String.format("Customer I/O: Card declined.",applePrice);

		assertTrue(outContent.toString().contains(expected));
	}


	/**
	 * Test paying with a gift card when a wrong pin is inserted
	 * @throws IOException
	 * @throws PaymentMethodException
	 */
	@Test (expected = InvalidPINException.class)
	public void testPayGcWrongPin() throws IOException, PaymentMethodException {

		coStationLogic.startOrder();
		coStationLogic.addToOrder(apple);
		coStationLogic.pay(MethodOfPayment.GIFTCARD, cardIssuer);

		for(int i = 0; i < 10; i++) {
			try {
				coStation.cardReader.insert(giftCard, "1111");
				break;
			}catch(ChipFailureException e) {

			}
		}

	}
	
	
	/**
	 * This class is used by the following test case to make a mock card issuer that will time out the connection.
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
	 * This test case will check that the code actually catches time out connections
	 * THIS TEST WILL TAKE 100 SECONDS TO RUN
	 */
	@Test
	public void testAuthorizationFailed()  {
		CardIssuerMock fakeIssuer = new CardIssuerMock("mock");
		fakeIssuer.setWait(20000);
		coStationLogic.startOrder();
		coStationLogic.addToOrder(apple);
		payWithGC.pay(coStationLogic.getOrder(), MethodOfPayment.GIFTCARD, fakeIssuer);
		CardData data = null;
		for(int i = 0; i < 10; i++) {
			try {
				data = giftCard.insert("1234");
				break;
			}catch(IOException e) {

			}
		}
		payWithGC.reactToCardDataReadEvent(coStation.cardReader, data);
	}
	
	
	/**
     * This test checks that the unimplemented methods do not have errors in them
     */
    @Test
    public void testUnimplemented() {
    	payWithGC.reactToCardRemovedEvent(coStation.cardReader);
    	payWithGC.reactToEnabledEvent(coStation.cardReader);
    	payWithGC.reactToDisabledEvent(coStation.cardReader);
    	payWithGC.reactToCardTappedEvent(coStation.cardReader);
    	payWithGC.reactToCardSwipedEvent(coStation.cardReader);
    }	
    
}
