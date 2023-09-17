/**
 *
 * SENG Iteration 3 P3-3 | removeitemstest.java
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.*;
import java.util.*;

import org.junit.*;

import com.autovend.*;
import com.autovend.devices.*;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

public class removeitemstest {
	private SelfCheckoutStation station;
	private SelfCheckoutStationLogic selfCheckLogic;
	private SupervisionStation attend;
	private SupervisionStationLogic supervise;
	private Currency currency;
	PLUTouchscreenStub screen;
	private BaggingAreaScaleController scaleController;
	private ScanningScale scanscale;
	private ElectronicScale elecScale;
	private ElectronicScale prodscale;
	private int[] billDenoms;
	private BigDecimal[] coinDenoms;
	private int maxWeight;
	private int sensitivity;
	private RemoveItemController remove;

	public AddOwnBag ownBag;

	@Before
	public void setup() {
		currency = Currency.getInstance("CAD");
		billDenoms = new int[] {5, 10, 20, 50, 100};
		coinDenoms = new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"),
				new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};
		maxWeight = 100;
		sensitivity = 1;

		station = new SelfCheckoutStation(currency, billDenoms, coinDenoms, maxWeight, sensitivity);
		selfCheckLogic = new SelfCheckoutStationLogic(station);
		attend = new SupervisionStation();
		supervise = new SupervisionStationLogic(attend);
		supervise.getSupervisionStation().screen.disable();
		supervise.getSupervisionStation().screen.enable();
		selfCheckLogic.setSupervisor(supervise);
		supervise.supervise(selfCheckLogic);
		remove = new RemoveItemController(selfCheckLogic);
		scaleController = new BaggingAreaScaleController(remove);
		prodscale = new ElectronicScale(5000, 1);
		scanscale = new ScanningScale();
		prodscale.register(scanscale);
		elecScale = new ElectronicScale(5000, 1);
		elecScale.register(scaleController);
		ownBag = new AddOwnBag(selfCheckLogic, scaleController, scanscale);
	}
	@Test
	public void pluremove() {
		AddItemByBrowsingController browseCon = new AddItemByBrowsingController(selfCheckLogic, scaleController, scanscale);
		PriceLookUpCode PLUcode = new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.one, Numeral.one);
		PLUCodedProduct apples = new PLUCodedProduct(PLUcode,"apples",new BigDecimal ("1.59"));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUcode, apples);
		selfCheckLogic.startOrder();
		selfCheckLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(PLUcode);
		selfCheckLogic.getAddByBrowseController().addToOrder(1.59);
		Product currentproduct = selfCheckLogic.getOrder().getProducts().get(0);
		remove.removeFromOrder(currentproduct);
		remove.checkForDiscrepancy(1.59);
	}
	@Test
	public void badweight() {
		AddItemPLUController controller = new AddItemPLUController(selfCheckLogic, scaleController, scanscale);
		PriceLookUpCode PLUcode = new PriceLookUpCode(Numeral.one, Numeral.one, Numeral.one, Numeral.one);
		PLUCodedProduct apples = new PLUCodedProduct(PLUcode,"apples",new BigDecimal ("1.59"));
		screen = new PLUTouchscreenStub();
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUcode, apples);
		selfCheckLogic.startOrder();
		sellableForScale apple = new sellableForScale(10);
		sellableForScale apple2 = new sellableForScale(14);
		selfCheckLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(PLUcode);
		selfCheckLogic.getAddByBrowseController().addToOrder(1.59);
		selfCheckLogic.getSelfCheckoutStation().baggingArea.add(apple);
		selfCheckLogic.getAddByBrowseController().reactToBrowsingSelectedEvent(PLUcode);
		selfCheckLogic.getAddByBrowseController().addToOrder(1.59);
		selfCheckLogic.getSelfCheckoutStation().baggingArea.add(apple2);
		Product currentproduct = selfCheckLogic.getOrder().getProducts().get(0);
		selfCheckLogic.getRemoveItemController().removeFromOrder(currentproduct);
		selfCheckLogic.getSelfCheckoutStation().baggingArea.remove(apple2);
		assertTrue(selfCheckLogic.isSuspended());
	}
	
	@Test
	public void barcoderemove() {
		station.mainScanner.enable();
    	BaggingAreaScaleController scale2=new BaggingAreaScaleController(null);
    	AddItemScanController add=new AddItemScanController(selfCheckLogic,scale2, scanscale);
    	Barcode barbar=new Barcode(Numeral.one);
    	add.expectedWeight=5.0;
    	Double d1=5.0;
    	BarcodedProduct app= new BarcodedProduct(barbar,"apple",BigDecimal.ONE,d1);
    	ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barbar, app);
    	selfCheckLogic.startOrder();
    	BarcodedUnit appleUnit = new BarcodedUnit(barbar, d1);

        selfCheckLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if(station.mainScanner.scan(appleUnit)) {
            	break;
            }
        }
        selfCheckLogic.getSelfCheckoutStation().baggingArea.add(appleUnit);
        selfCheckLogic.getRemoveItemController().removeFromOrder(app);
        assertFalse(selfCheckLogic.getOrder().getProducts().contains(appleUnit));
	}
	
	@Test
	public void badbarcoderemove() {
		station.mainScanner.enable();
    	BaggingAreaScaleController scale2=new BaggingAreaScaleController(null);
    	AddItemScanController add=new AddItemScanController(selfCheckLogic,scale2, scanscale);
    	Barcode barbar=new Barcode(Numeral.one);
    	Barcode bar2 = new Barcode(Numeral.two);
    	add.expectedWeight=5.0;
    	Double d1=5.0;
    	BarcodedProduct app= new BarcodedProduct(barbar,"apple",BigDecimal.ONE,d1);
    	BarcodedProduct app2= new BarcodedProduct(barbar,"apple",BigDecimal.ONE, 10.0);
    	BarcodedUnit apple2 = new BarcodedUnit(bar2 , 10.0);
    	ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barbar, app);
    	ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bar2, app2);
    	selfCheckLogic.startOrder();
    	BarcodedUnit appleUnit = new BarcodedUnit(barbar, d1);
    	
        selfCheckLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if(station.mainScanner.scan(appleUnit)) {
            	break;
            }
        }
        selfCheckLogic.getSelfCheckoutStation().baggingArea.add(appleUnit);
        selfCheckLogic.getAddItemScanController();
        boolean scan = selfCheckLogic.getSelfCheckoutStation().mainScanner.scan(apple2);
        while (scan == false) {
        	scan = selfCheckLogic.getSelfCheckoutStation().mainScanner.scan(apple2);
        }
        selfCheckLogic.getSelfCheckoutStation().baggingArea.add(apple2);
        selfCheckLogic.getRemoveItemController().removeFromOrder(app);
    	selfCheckLogic.getSelfCheckoutStation().baggingArea.remove(apple2);
    	assertTrue(selfCheckLogic.isSuspended());

	}
	
	@After
	public void dead() {
		this.supervise.stopSupervising(selfCheckLogic);
		this.supervise = null;
		this.currency = null;
		this.billDenoms = null;
		this.coinDenoms = null;
		this.maxWeight = 0;
		this.sensitivity = 0;
		this.station = null;
		this.selfCheckLogic = null;
		this.scaleController = null;
		this.elecScale = null;
	}
	public class sellableForScale extends SellableUnit{

		protected sellableForScale(double weightInGrams) {
			super(weightInGrams);
			// TODO Auto-generated constructor stub
		}
		
	}
}
