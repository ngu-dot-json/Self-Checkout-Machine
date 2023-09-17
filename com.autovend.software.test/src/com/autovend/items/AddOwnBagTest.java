/**
 *
 * SENG Iteration 3 P3-3 | AddOwnBagTest.java
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

package com.autovend.items;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.SupervisionStationLogic;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;


public class AddOwnBagTest {

	public BigDecimal[] total;
	private ElectronicScale es;
	private BarcodeScanner bs;
	public BagFromHome bag;
	public AddOwnBag ownBag;
	public SelfCheckoutStationLogic stationLogic;
	private BaggingAreaScaleController bagz;
	public Currency currency;
	public int[] denominations;
	public boolean accept;
	public RemoveItemController remove;
	private SupervisionStation attend;
	private SupervisionStationLogic supervise;


	@Before
	public void setUp(){
		es = new ElectronicScale(5000, 1);
		bs = new BarcodeScanner();
		bs.enable();
		currency = Currency.getInstance("CAD");
		denominations = new int[2];
		denominations[0]= 5;
		denominations[1] = 10;
		total = new BigDecimal[1];
		total[0] = BigDecimal.valueOf(0.05);

		SelfCheckoutStation station = new SelfCheckoutStation(currency, denominations, total, 5000, 1 );
		stationLogic = new SelfCheckoutStationLogic(station);
		remove = new RemoveItemController(stationLogic);
		bagz = new BaggingAreaScaleController(remove);
		ownBag = new AddOwnBag(stationLogic, bagz, new ScanningScale());
		attend = new SupervisionStation();
		supervise = new SupervisionStationLogic(attend);
		supervise.getSupervisionStation().screen.disable();
		supervise.getSupervisionStation().screen.enable();
		stationLogic.setSupervisor(supervise);
		supervise.supervise(stationLogic);
	}


	@After
	public void tearDown() {
		bag = null;
	}


	/*
	 * Tries to Add a bag with no weight , should throw an exception
	 */
	@Test
	public void testnoweightBag() {
		boolean found = false;

		try {
			bag = new BagFromHome(0.0);
			fail();
		}
		catch(Exception e) {
			assertTrue(true);
		}

	}

	/*
	 * Tries to Add a bag with negative weight , should throw an exception
	 */
	@Test
	public void testnegativeweightBag() {

		try {
			bag = new BagFromHome(-1.0);
			fail();
		}
		catch(Exception e) {
			assertTrue(true);
		}

	}

	/*
	 * Tries to Add a bag with a valid weight , should return True as a sucessful add
	 */
	@Test
	public void testValidBagTest() {
		ownBag.setAccept(true);
		bag = new BagFromHome(2.0);
		ownBag.AddBag(bag);
		ownBag.addToOrder(bag.getWeight());
	//	assertTrue(found);
	}


	/*
	 * Tries to Add a bag that is beyond the limit of the scale , should return false as an unsucessful add.
	 */
	@Test
	public void testHeavyBagTest() {
		bag = new BagFromHome(5002.0);
		ownBag.AddBag(bag);
	}


	/*
	 * Tries to Add a bag with a valid weight , should return True as a sucessful add
	 */
	@Test
	public void declinedBagTest() {
		ownBag.setAccept(false);
		bag = new BagFromHome(2.0);
		ownBag.AddBag(bag);
	}
	@Test
	public void failweight() {
		ownBag.setAccept(true);
		bag = new BagFromHome(6.0);
		ownBag.AddBag(bag);
		ownBag.addToOrder(4);
	}
	@Test
	public void nobag() {
		ownBag.setAccept(true);
		ownBag.addToOrderNoBaggingArea();
	}
}
