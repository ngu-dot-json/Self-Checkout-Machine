/**
 *
 * SENG Iteration 3 P3-3 | MembershipTest.java
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

package com.autovend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.BarcodedUnit;
import com.autovend.Card.CardData;
import com.autovend.Membership;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.items.AddOwnBag;
public class MembershipTest {
	public BigDecimal[] total;
	private ElectronicScale es;
	private BarcodeScanner bs; 
	public AddOwnBag ownBag;
	public SelfCheckoutStationLogic stationLogic;
	public Currency currency;
	public int[] denominations;
	private Membership testmember;
	private SelfCheckoutStation station;
	private MembershipCard membershipCard;
	private BufferedImage signature;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@Before
	public void setUp(){
		System.setOut(new PrintStream(outContent));
		es = new ElectronicScale(5000, 1);
		bs = new BarcodeScanner();
		bs.enable();
		currency = Currency.getInstance("CAD");
		denominations = new int[2];
		denominations[0]= 5;
		denominations[1] = 10;
		total = new BigDecimal[1];
		total[0] = BigDecimal.valueOf(0.05);
		
		station = new SelfCheckoutStation(currency, denominations, total, 5000, 1 );
		stationLogic = new SelfCheckoutStationLogic(station);
		testmember = new Membership(stationLogic);
		
		signature = new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);
		membershipCard = new MembershipCard("Membership", "123456789123", "holder", false);
		
	}
	
	@After
    public void tearDown() throws FileNotFoundException {
        testmember.clearDatabase();
    }
	
	
	//adds a specific id to the database, then checks if it is in fact in the database
	@Test
    public void AddedMember() {
		String id = "123456789123";
		testmember.AddMember(id);
		try {
			testmember.enterMembershipByTyping(id);
		} catch (MembershipNotFoundException e) {
		}
		
		boolean boolcheck = testmember.inSystem(id);
    	assertEquals(true,boolcheck);
    }
	
	//doesnt add id to database, then checks if it is in the database
	@Test
	public void notAdded() {
		stationLogic.setInput("1");
		stationLogic.setInput2("4444444888888");
		
		try {
			testmember.enterMembershipByTyping("444444888888");
		} catch (MembershipNotFoundException e) {
		}
		testmember.AddMember("111111111111");
		
		boolean boolcheck = testmember.inSystem("444444888888");
		assertEquals(false,boolcheck);
	}
	
	@Test
	public void notAdded2() {
		stationLogic.setInput("wew");
		stationLogic.setInput2("111111222222");

		try {
			testmember.enterMembershipByTyping("111111222222");
		} catch (MembershipNotFoundException e) {
		}
		testmember.AddMember("000000000000");
			
		boolean boolcheck = testmember.inSystem("111111222222");
		assertEquals(false,boolcheck);
		assertEquals(true, testmember.inSystem("000000000000"));
		
	}
	@Test
	public void notAdded3() {
		stationLogic.setInput("4");
		stationLogic.setInput2("111111222222");

		try {
			testmember.enterMembershipByTyping("111111222225");
		} catch (MembershipNotFoundException e) {
		}
		testmember.AddMember("000000000000");
		boolean boolcheck = testmember.inSystem("000000000000");
		assertEquals(true,boolcheck);
	}
	
	// check whether a member is in the system. should be false if not.
	// then add them and check whether they're in the system again.
	@Test
	public void testAddMember() {
		stationLogic.setInput("4");
		stationLogic.setInput2("111111222222");

		boolean boolcheck = testmember.inSystem("445445445445");
		assertEquals(false, boolcheck);
		testmember.AddMember("445445445445");
		boolean newboolcheck = testmember.inSystem("445445445445");
		assertEquals(true, newboolcheck);
	}
	
	// testing what happens when the same member is added twice.
	// should recognize the member is already in the system and returns false.
	@Test
	public void testSameUserAddedTwice() {
		stationLogic.setInput("4");
		stationLogic.setInput2("222222333333");
		testmember = new Membership(stationLogic);
		

		assertEquals(false, testmember.inSystem("222222333333"));
		testmember.AddMember("222222333333");	
		assertEquals(true, testmember.inSystem("222222333333"));
		testmember.AddMember("222222333333");
		assertEquals(true, testmember.inSystem("222222333333"));
	}
	
	// test when a number of length less than 12 is added
	@Test (expected = IllegalDigitException.class)
	public void testInvalidNumber() {
		stationLogic.setInput("4");
		stationLogic.setInput2("12345");
		testmember = new Membership(stationLogic);
		
		testmember.AddMember("12345");
	}
	
	// test when a null number is added
	@Test (expected = IllegalDigitException.class)
	public void testNullNumberAdded() {
		stationLogic.setInput("4");
		stationLogic.setInput2(null);
		testmember = new Membership(stationLogic);
		
		testmember.AddMember(null);
	}
	
	// test when a number of length less than 12 is entered by customer
	@Test (expected = IllegalDigitException.class)
	public void testInvalidNumberEntered() {
		stationLogic.setInput("4");
		stationLogic.setInput2("12345");
		testmember = new Membership(stationLogic);
		try {
			testmember.enterMembershipByTyping("12345");
		} catch (MembershipNotFoundException e) {
		}
		testmember.inSystem("12345");
	}
	
	// test when a number doesn't have correct digits in it
	@Test (expected = IllegalDigitException.class)
	public void testInvalidDigitEntered() {
		stationLogic.setInput("4");
		stationLogic.setInput2("12345678912h");
		testmember = new Membership(stationLogic);
		testmember.AddMember("12345678912h");
	}
	
	// test when logic station is used to enter a number
	@Test
	public void testLogicStationEnterByTyping() {
		stationLogic.setInput("4");
	    stationLogic.setInput2("123456789123");
	    testmember.AddMember("123456789123");
	    try {
			stationLogic.enterMembershipNumberTyping("123456789123");
		} catch (MembershipNotFoundException e) {
		}
	    Membership testmember = stationLogic.getMembershipInstance();
	    
	    assertEquals("123456789123", testmember.GetId());
	}
	
	/*
	 * TESTS FOR SWIPE BELOW
	 */
	
	// Test that the correct number has been returned when we swipe the card
	// test that the swipe has been registered too.
	@Test
	public void testSwipe() {
		String id = "123456789123";
		testmember.AddMember(id);
				
		boolean boolcheck = testmember.inSystem(id);
    	assertEquals(true,boolcheck);    	
    	
    	boolean swipeWorked = false;
    	
    	while (swipeWorked == false) {
	    	try {
				station.cardReader.swipe(membershipCard, signature);
				swipeWorked = true;
			} catch (IOException e) {
			}
    	}
    	assertEquals(swipeWorked, testmember.getIsSwipeFoundInDatabase());
	}
	
	// Test when the card is null
	@Test (expected = SimulationException.class)
	public void testSwipeNull() {
		String id = "123456789123";
		testmember.AddMember(id);
		
		MembershipCard nullMember = new MembershipCard(null, null, null, false);
	
		boolean swipeWorked = false;
    	
    	while (swipeWorked == false) {
	    	try {
				station.cardReader.swipe(nullMember, signature);
				swipeWorked = true;
			} catch (IOException e) {
				
			}
    	}
	}
	
	// test swipeMembershipCard method
	@Test
	public void testSwipeMembershipCardMethod() {
		String id = "123456789123";
		testmember.AddMember(id);
				
		boolean boolcheck = testmember.inSystem(id);
    	assertEquals(true,boolcheck);    	
    	
    	boolean swipeWorked = true;
    	
    	testmember.swipeMembershipCard(membershipCard, signature);
    	assertEquals(swipeWorked, testmember.getIsSwipeFoundInDatabase());
	}
	
	// test whether set id is correct when swipe occurs.
	@Test
	public void testGetIDSwipe() {
		String id = "123456789123";
		testmember.AddMember(id);
		
		boolean swipeWorked = false;
    	
    	while (swipeWorked == false) {
	    	try {
				station.cardReader.swipe(membershipCard, signature);
				swipeWorked = true;
			} catch (IOException e) {
			}
    	}
    	assertEquals(id, testmember.GetId());
	}
	
	/*
	 * TESTS FOR SCAN BELOW
	 */
	
	@Test
	public void testScan() {
		MembershipCard memCard = new MembershipCard("membership", "135792468011", "Walker", false);
		String id = "135792468011";
		testmember = new Membership(stationLogic, memCard);
		testmember.AddMember(id);
		boolean boolcheck = testmember.inSystem(id);
    	assertEquals(true,boolcheck); 				// add new member into system
    	boolean isScanWorked = false;
    	while(!isScanWorked) {
			isScanWorked = station.mainScanner.scan(memCard);
			
    	}
		assertEquals(true, testmember.getIsScanFoundInDatabase());
	}
	
	@Test
	public void testSelectInputMembershipScan() {
		String id = "987654321098";
		MembershipCard memCard = new MembershipCard("membership", "987654321098", "Robert", false);
		testmember = new Membership(stationLogic, memCard);
		testmember.AddMember(id);
		boolean boolcheck = testmember.inSystem(id);
    	assertEquals(true,boolcheck); 	
    	Membership member = new Membership(stationLogic, memCard);
    	member.selectInputMethod(MethodOfMembership.SCAN);
    	assertEquals(true, member.getIsScanFoundInDatabase());
	}
	
	// test whether the ID is correctly set when scan is called
	@Test
	public void testGetIDScan() {
		String id = "987654321098";
		MembershipCard memCard = new MembershipCard("membership", "987654321098", "Robert", false);
		testmember = new Membership(stationLogic, memCard);
		testmember.AddMember(id);
		
		boolean isScanWorked = false;
    	while(!isScanWorked) {
			isScanWorked = station.mainScanner.scan(memCard);
			
    	}
		assertEquals(id, testmember.GetId());
	}
}
	
	
	
	
	

