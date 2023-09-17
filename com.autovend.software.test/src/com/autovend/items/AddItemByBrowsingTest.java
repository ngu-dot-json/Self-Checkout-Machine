/**
 *
 * SENG Iteration 3 P3-3 | AddItemByBrowsingTest.java
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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.Order;
import com.autovend.PriceLookUpCode;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

public class AddItemByBrowsingTest {
	private Currency currency;
	private SelfCheckoutStation checkoutStation;
	private SelfCheckoutStationLogic checkoutLogic;
	private BaggingAreaScaleController scaleControl;
	private ScanningScale scanscale;
	int[] denom = {1, 5, 10, 20, 50, 100};
	BigDecimal[] coinDenom = {BigDecimal.ONE};
	int maxWeight = 100;
	int sens = 1;
	Order order;
	
	
	// the setup
	@Before
	public void setUp() {
		currency = Currency.getInstance(Locale.CANADA);
		checkoutStation = new SelfCheckoutStation(currency, denom, coinDenom, maxWeight, sens);
		checkoutLogic = new SelfCheckoutStationLogic(checkoutStation);
		scaleControl = new BaggingAreaScaleController(new RemoveItemController(checkoutLogic));
		checkoutStation.mainScanner.enable();
		order = new Order(checkoutLogic);
	}
	
	
	// the teardown
	@After
	public void tearDown() {
		currency = null;
		checkoutStation.mainScanner.disable();
		checkoutStation = null;
		checkoutLogic = null;
		scaleControl = null;
		order = null;
	}
	
	// Tests a non-valid type with reactToBrowsingSelectedEvent(). We expect an exception as a result.
	@Test
	public void testBadInput() {
		boolean propererror = false;
		AddItemByBrowsingController browseCon = new AddItemByBrowsingController(checkoutLogic, scaleControl, scanscale);
		int a = 1;
		try {
			browseCon.reactToBrowsingSelectedEvent(a);
			browseCon.addToOrder(a);
			System.out.println(browseCon.stationLogic.getOrder().getProducts());
		} catch (NullPointerException e) {
			propererror = true;
		}
		assertTrue(propererror);
	}
	
	
	// Tests sending a PLU-code with a corresponding product through reactToBrowsingSelectedEvent(). We expect the product to be successfully added to the Order object.
	@Test
	public void testPLUInput() {
		AddItemByBrowsingController browseCon = new AddItemByBrowsingController(checkoutLogic, scaleControl, scanscale);
		PriceLookUpCode PLUcode = new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.one, Numeral.one);
		PLUCodedProduct apples = new PLUCodedProduct(PLUcode,"apples",new BigDecimal ("1.59"));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUcode, apples);
		checkoutLogic.startOrder();
		
		checkoutLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(PLUcode);
		checkoutLogic.getAddByBrowseController().addToOrder(1.59);
		Product currentproduct = checkoutLogic.getOrder().getProducts().get(0);
		assertEquals(apples, currentproduct);
	}
	
	// Tests sending a barcode with a corresponding product through reactToBrowsingSelectedEvent(). We expect the product to be successfully added to the Order object.
	@Test
	public void testBarcodeInput() {
		double weight = 10;
		AddItemByBrowsingController browseCon = new AddItemByBrowsingController(checkoutLogic, scaleControl, scanscale);
		Barcode barcode = new Barcode(Numeral.one, Numeral.one, Numeral.one, Numeral.one);
		BarcodedProduct banana = new BarcodedProduct(barcode, "a banana", BigDecimal.TEN, weight);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, banana);
		checkoutLogic.startOrder();
		checkoutLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(barcode);
		checkoutLogic.getAddByBrowseController().addToOrder(2);
		Product currentproduct = checkoutLogic.getOrder().getProducts().get(0);
		assertEquals(banana, currentproduct);
	}
	
	// Tests sending both a PLU and barcode through reactToBrowsing event, both with their own products. We expect both to be added to the Order object, in corresponding order.
	@Test
	public void testBothInput() {
		double weight = 10;
		AddItemByBrowsingController browseCon = new AddItemByBrowsingController(checkoutLogic, scaleControl, scanscale);
		Barcode barcode = new Barcode(Numeral.one, Numeral.one, Numeral.one, Numeral.one);
		PriceLookUpCode PLUcode = new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.one, Numeral.one);
		BarcodedProduct banana = new BarcodedProduct(barcode, "a banana", BigDecimal.TEN, weight);
		PLUCodedProduct apples = new PLUCodedProduct(PLUcode,"apples",new BigDecimal ("1.59"));
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, banana);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUcode, apples);
		checkoutLogic.startOrder();
		
		checkoutLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(barcode);
		checkoutLogic.getAddByBrowseController().addToOrder(2);
		checkoutLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(PLUcode);
		checkoutLogic.getAddByBrowseController().addToOrder(2);
		Product currentproduct = checkoutLogic.getOrder().getProducts().get(0);
		assertEquals(banana, currentproduct);
		currentproduct = checkoutLogic.getOrder().getProducts().get(1);
		assertEquals(apples, currentproduct);
	}
	
}
