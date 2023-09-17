/**
 *
 * SENG Iteration 3 P3-3 | TestStartUpShutDown.java
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

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.BillDispenser;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.SelfCheckoutStation;

public class TestStartUpShutDown {

	private SelfCheckoutStation checkoutStation;
	private StartUpShutDown attendant;
	private Currency currency;
	int[] denom = { 1, 5, 10, 20, 50, 100 };
	BigDecimal[] coinDenom = { BigDecimal.ONE };
	int maxWeight = 100;
	int sens = 1;
	Order order;

	@Before
	public void setUp() {
		currency = Currency.getInstance(Locale.CANADA);
		checkoutStation = new SelfCheckoutStation(currency, denom, coinDenom, maxWeight, sens);
		attendant = new StartUpShutDown(checkoutStation);
	}

	/**
	 * A test method to test if the startup works correctly.
	 */
	@Test
	public void testStartupStation() {

		// Startup station is called.
		attendant.startupStation();

		// They all should be enabled now.
		Assert.assertFalse(checkoutStation.scale.isDisabled());
		Assert.assertFalse(checkoutStation.printer.isDisabled());
		Assert.assertFalse(checkoutStation.coinSlot.isDisabled());
		Assert.assertFalse(checkoutStation.coinTray.isDisabled());
		Assert.assertFalse(checkoutStation.billInput.isDisabled());
		Assert.assertFalse(checkoutStation.cardReader.isDisabled());
		Assert.assertFalse(checkoutStation.billOutput.isDisabled());
		Assert.assertFalse(checkoutStation.billStorage.isDisabled());
		Assert.assertFalse(checkoutStation.baggingArea.isDisabled());
		Assert.assertFalse(checkoutStation.mainScanner.isDisabled());
		Assert.assertFalse(checkoutStation.coinStorage.isDisabled());
		Assert.assertFalse(checkoutStation.billValidator.isDisabled());
		Assert.assertFalse(checkoutStation.coinValidator.isDisabled());
		Assert.assertFalse(checkoutStation.handheldScanner.isDisabled());
		Assert.assertFalse(checkoutStation.screen.isDisabled());

		for (BillDispenser bDispenser : checkoutStation.billDispensers.values()) {
			Assert.assertFalse((bDispenser.isDisabled()));
		}

		for (CoinDispenser cDispenser : checkoutStation.coinDispensers.values()) {
			Assert.assertFalse(cDispenser.isDisabled());
		}

	}

	/**
	 * A test method to test if the shutdown works correctly.
	 */
	@Test
	public void testShutdownStation() {

		// Shutdown station is called.
		attendant.shutdownStation();

		// They all should be disabled now.
		Assert.assertTrue(checkoutStation.scale.isDisabled());
		Assert.assertTrue(checkoutStation.printer.isDisabled());
		Assert.assertTrue(checkoutStation.coinSlot.isDisabled());
		Assert.assertTrue(checkoutStation.coinTray.isDisabled());
		Assert.assertTrue(checkoutStation.billInput.isDisabled());
		Assert.assertTrue(checkoutStation.cardReader.isDisabled());
		Assert.assertTrue(checkoutStation.billOutput.isDisabled());
		Assert.assertTrue(checkoutStation.billStorage.isDisabled());
		Assert.assertTrue(checkoutStation.baggingArea.isDisabled());
		Assert.assertTrue(checkoutStation.mainScanner.isDisabled());
		Assert.assertTrue(checkoutStation.coinStorage.isDisabled());
		Assert.assertTrue(checkoutStation.billValidator.isDisabled());
		Assert.assertTrue(checkoutStation.coinValidator.isDisabled());
		Assert.assertTrue(checkoutStation.handheldScanner.isDisabled());
		Assert.assertTrue(checkoutStation.screen.isDisabled());

		for (BillDispenser bDispenser : checkoutStation.billDispensers.values()) {
			Assert.assertTrue((bDispenser.isDisabled()));
		}

		for (CoinDispenser cDispenser : checkoutStation.coinDispensers.values()) {
			Assert.assertTrue(cDispenser.isDisabled());
		}
	}
}
