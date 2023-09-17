/**
 *
 * SENG Iteration 3 P3-3 | TestAddItemScan.java
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

import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;

import com.autovend.Numeral;

import com.autovend.SelfCheckoutStationLogic;
import com.autovend.SellableUnit;
import com.autovend.devices.DisabledException;
import com.autovend.devices.SelfCheckoutStation;

import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;

public class TestAddItemScan {
	private SelfCheckoutStation check;
	private Currency currency;
	private SelfCheckoutStationLogic stationLogic;
	private BaggingAreaScaleController scale;
	private ScanningScale scanscale;

	private Barcode barcode;


	@Before
	public void setUp() {
		currency = Currency.getInstance(Locale.CANADA);
		int[] denom = { 1, 5, 10, 20, 50, 100 };
		BigDecimal[] val = { BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.25),
				BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(0.5) };
		int a = 50;
		int b = 1;
		check = new SelfCheckoutStation(currency, denom, val, a, b);
		stationLogic = new SelfCheckoutStationLogic(check);
		check.mainScanner.enable();
		barcode = new Barcode(Numeral.five);
		//appleUnit = new BarcodedProduct(barcode, "apple", new BigDecimal(5), 5.3);

	}

	// testing the constructor to see if everything gets executed
	@Test
	public void TestAbstractAddItemController() {
		RemoveItemController removeCon = new RemoveItemController(stationLogic);
		scale = new BaggingAreaScaleController(removeCon);
		AddItemScanController add = new AddItemScanController(stationLogic, scale, scanscale);
		stationLogic.startOrder();
		
		BarcodedProduct appleprod = new BarcodedProduct(barcode, "apple", new BigDecimal(4), 5.3);
		BarcodedUnit appleUnit = new BarcodedUnit(barcode, 5.3);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, appleprod);
		sellableForScale apple = new sellableForScale(5.3);
	
		boolean goodScan = stationLogic.getSelfCheckoutStation().mainScanner.scan(appleUnit);

		while(goodScan == false) {
			goodScan = stationLogic.getSelfCheckoutStation().mainScanner.scan(appleUnit);
		}
		check.scale.add(apple);
		check.baggingArea.add(apple);
		
		assertTrue(stationLogic.getOrder().getProducts().contains(appleprod));
	}

	// testing react to barcode scanned event when the scanner is disabled. use actual system logic then 
	// test it out by reacting but just goes directly to additemscancontroller and react to this thing
	@Test
	public void testReactToBarcodeScannedEventDisabledScanner(){
		check.mainScanner.disable();
		BaggingAreaScaleController scale2 = new BaggingAreaScaleController(null);
		AddItemScanController add = new AddItemScanController(stationLogic, scale2,scanscale);
		Barcode barbar = new Barcode(Numeral.one);
		stationLogic.startOrder();
		
		BarcodedProduct apple = new BarcodedProduct(barbar, "apple", BigDecimal.ONE, 5);
		BarcodedUnit appleUnit = new BarcodedUnit(barcode, 5.3);

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barbar, apple);
	
		try {
			stationLogic.getSelfCheckoutStation().mainScanner.scan(appleUnit);
		} catch (DisabledException d){
			
		}
		assertEquals(false, stationLogic.isSuspended());

	}
	
	// testing react to barcode scanned event when the scanner is enabled
	@Test
	public void TestreactToBarcodeScannedEvent3() {
		check.mainScanner.enable();
		BaggingAreaScaleController scale2 = new BaggingAreaScaleController(null);
		AddItemScanController add = new AddItemScanController(stationLogic, scale2,scanscale);
		stationLogic.startOrder();
		
		
		
		Barcode barbar = new Barcode(Numeral.one);
		BarcodedProduct apple = new BarcodedProduct(barbar, "apple", BigDecimal.ONE, 5);
		BarcodedUnit appleUnit = new BarcodedUnit(barcode, 5.3);

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barbar, apple);
	
		stationLogic.getSelfCheckoutStation().mainScanner.scan(appleUnit);
		
		
		assertEquals(false, stationLogic.isSuspended());
	}



	// testing to see if addToOrder function works well for products of different
	// weight as expected weight
	@Test
	public void weightDiscrepency() {
		RemoveItemController removeCon = new RemoveItemController(stationLogic);
		scale = new BaggingAreaScaleController(removeCon);
		AddItemScanController add = new AddItemScanController(stationLogic, scale, scanscale);
		stationLogic.startOrder();
		
		BarcodedProduct appleprod = new BarcodedProduct(barcode, "apple", new BigDecimal(4), 3);
		BarcodedUnit appleUnit = new BarcodedUnit(barcode, 5.3);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, appleprod);
		sellableForScale apple = new sellableForScale(5.3);
	
		boolean goodScan = stationLogic.getSelfCheckoutStation().mainScanner.scan(appleUnit);

		while(goodScan == false) {
			goodScan = stationLogic.getSelfCheckoutStation().mainScanner.scan(appleUnit);
		}
		check.scale.add(apple);
		check.baggingArea.add(apple);
		
		assertTrue(stationLogic.getOrder().getProducts().contains(appleprod));
		assertTrue(stationLogic.isSuspended());
	}
	
	public class sellableForScale extends SellableUnit{

		protected sellableForScale(double weightInGrams) {
			super(weightInGrams);
			// TODO Auto-generated constructor stub
		}
		
	}
}
