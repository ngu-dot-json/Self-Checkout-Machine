/**
 *
 * SENG Iteration 3 P3-3 | TestAddItemPLU.java
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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
// obtained from: https://docs.oracle.com/javase/7/docs/api/java/math/MathContext.html
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.MethodOfAdd;
import com.autovend.Numeral;
import com.autovend.Order;
import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.PLUCodedProduct;

/**
 * Test class for adding items by the PriceLookUpCode
 */

public class TestAddItemPLU {

	private SelfCheckoutStationLogic stationLogic;
	private SelfCheckoutStation station;
	private Currency currency;
	private BaggingAreaScaleController scaleController;
	private ScanningScale scanScale;
	private ElectronicScale scale;
	private ElectronicScale prodScale; 
	private int[] denoms = { 5, 10, 20, 50 };
	private BigDecimal[] coinDenoms = { BigDecimal.ONE };
	private int maxScaleWeight = 100;
	private int scaleSens = 1;
	private Order order;
	private PLUTouchscreenStub screen;
	private AddItemPLUController controller;
	
	private PriceLookUpCodedUnit pluUnit;
	private MathContext m = new MathContext(3); // used for rounding

	private PriceLookUpCode code0 = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.two, Numeral.one);
	private PriceLookUpCode code1 = new PriceLookUpCode(Numeral.seven, Numeral.seven, Numeral.three, Numeral.four);
	private PriceLookUpCode code2 = new PriceLookUpCode(Numeral.zero, Numeral.five, Numeral.eight, Numeral.seven);
	private PriceLookUpCode code3 = new PriceLookUpCode(Numeral.two, Numeral.three, Numeral.six, Numeral.nine);
	private PriceLookUpCode code4 = new PriceLookUpCode(Numeral.nine, Numeral.nine, Numeral.one, Numeral.one);

	/**
	 * Invoked before each test
	 */
	@Before
	public void setUp() {
		currency = Currency.getInstance(Locale.CANADA);
		station = new SelfCheckoutStation(currency, denoms, coinDenoms, maxScaleWeight, scaleSens);
		stationLogic = new SelfCheckoutStationLogic(station);
		scaleController = new BaggingAreaScaleController(new RemoveItemController(stationLogic));
		scanScale = new ScanningScale();
		controller = new AddItemPLUController(stationLogic, scaleController, scanScale);

		PriceLookUpCode[] PLUCodes = { code0, code1, code2, code3, code4 };
		PLUCodedProduct[] products = { new PLUCodedProduct(PLUCodes[0], "apples", new BigDecimal(2)),
				new PLUCodedProduct(PLUCodes[1], "grapes", new BigDecimal(1.5)),
				new PLUCodedProduct(PLUCodes[2], "walnuts", new BigDecimal(3)),
				new PLUCodedProduct(PLUCodes[3], "chocolate almonds", new BigDecimal(3.5)),
				new PLUCodedProduct(PLUCodes[4], "bananas", new BigDecimal(2.5)) };

		for (int i = 0; i < products.length; i++)
			ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCodes[i], products[i]);

		order = new Order(stationLogic);

		// init devices/observers
		screen = new PLUTouchscreenStub();
		screen.register(controller);
		screen.disable();
		screen.enable();
		prodScale = new ElectronicScale(5000, 1);
		prodScale.register(controller.scanScale);
		scale = new ElectronicScale(5000, 1);

		scale.register(scaleController);

		// needed??
		stationLogic.addItems(MethodOfAdd.PLU);

		// start order
		stationLogic.startOrder();

	}

	/**
	 * Invoked after each test, break down objects/classes as needed
	 */
	@After
	public void tearDown() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
	}

	/**
	 * Tests customer entering a non-existent PLU code
	 */
	@Test
	public void testIncorrectPLUInput() {
		BigDecimal totalBeforeTransaction = stationLogic.getOrder().getTotal();
		PriceLookUpCode badcode = new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.two, Numeral.two);
		screen.enterPLUCode(badcode);
		assertEquals(totalBeforeTransaction, order.getTotal());
	}

	/**
	 * Tests the addition of a single item to a customer's order
	 */
	@Test
	public void testSingularPLUInput() {
		// enter valid PLU code
		screen.enterPLUCode(code2);

		// prompted to move item to bagging area
		pluUnit = new PriceLookUpCodedUnit(code2, 400);
		prodScale.add(pluUnit);
		BigDecimal totalAfterTransaction = stationLogic.getOrder().getTotal();

		totalAfterTransaction = totalAfterTransaction.round(m);

		assertEquals(totalAfterTransaction, new BigDecimal(1.2).round(m));

	}

	/**
	 * Tests the addition of multiple items at once to a customer's order
	 */
	@Test
	public void testMultiplePLUInputs() {

		screen.enterPLUCode(code0);
		pluUnit = new PriceLookUpCodedUnit(code0, 750);
		prodScale.add(pluUnit);

		screen.enterPLUCode(code1);
		pluUnit = new PriceLookUpCodedUnit(code1, 1100);
		prodScale.add(pluUnit);

		screen.enterPLUCode(code4);
		pluUnit = new PriceLookUpCodedUnit(code4, 300);
		prodScale.add(pluUnit);

		screen.enterPLUCode(code3);
		pluUnit = new PriceLookUpCodedUnit(code3, 1500);
		prodScale.add(pluUnit);

		BigDecimal totalAfterTransaction = stationLogic.getOrder().getTotal();

		totalAfterTransaction = totalAfterTransaction.round(m);

		assertEquals(totalAfterTransaction, new BigDecimal(9.15).round(m));
	}

	/**
	 * Test adding item when screen is disabled
	 */
	@Test
	public void testDisabledScreenAdd() {
		screen.disable();

		BigDecimal before = stationLogic.getOrder().getTotal();

		screen.enterPLUCode(code4);
		pluUnit = new PriceLookUpCodedUnit(code4, 300);

		assertEquals(before, stationLogic.getOrder().getTotal());
	}

}
