/**
 *
 * SENG Iteration 3 P3-3 | TestAddItemTextSearch.java
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
import java.util.ArrayList;
import java.util.Currency;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.MethodOfAdd;
import com.autovend.Numeral;
import com.autovend.Order;
import com.autovend.PriceLookUpCode;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.SellableUnit;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

public class TestAddItemTextSearch {
	// Private fields relevant to test the addItemTextSearchController class
	private SelfCheckoutStation station;
	private SelfCheckoutStationLogic selfCheckLogic;
	private Currency currency;

	private BaggingAreaScaleController scaleController;
	private ScanningScale scanscale;
	private ElectronicScale elecScale;

	private int[] billDenoms;
	private BigDecimal[] coinDenoms;
	private int maxWeight;
	private int sensitivity;

	private AddItemTextSearchController textSearchController;
	private Order order;

	// Initializing map instance which will point to the ProductDatabases.INVENTORY
	private Map<Product, Integer> storeItems;

	// Initializing PLUCodedProduct instances and BarcodedProduct instances
	// ProductDatabases.INVENTORY can have both PLUCoded and Barcoded products
	private PriceLookUpCode plCode1;
	private PriceLookUpCode plCode2;

	private Barcode baCode1;
	private Barcode baCode2;

	private PLUCodedProduct PLUItem1;
	private PLUCodedProduct PLUItem2;

	private BarcodedProduct BarcodeItem1;
	private BarcodedProduct BarcodeItem2;

	/**
	 * Sets up the test suite. Runs before every method. General initialization of
	 * ReceiptPrinter and other corresponding objects.
	 */
	@Before
	public void setUp() {
		currency = Currency.getInstance("CAD");
		billDenoms = new int[] { 5, 10, 20, 50, 100 };
		coinDenoms = new BigDecimal[] { new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"),
				new BigDecimal("1.00"), new BigDecimal("2.00") };
		maxWeight = 100;
		sensitivity = 1;

		station = new SelfCheckoutStation(currency, billDenoms, coinDenoms, maxWeight, sensitivity);
		selfCheckLogic = new SelfCheckoutStationLogic(station);
		scaleController = new BaggingAreaScaleController(null);
		textSearchController = new AddItemTextSearchController(selfCheckLogic, scaleController, scanscale);

		// Setting up the PLUCodedProducts and BarcodedProducts
		plCode1 = new PriceLookUpCode(Numeral.zero, Numeral.zero, Numeral.zero, Numeral.zero);
		plCode2 = new PriceLookUpCode(Numeral.five, Numeral.two, Numeral.eight, Numeral.one);

		baCode1 = new Barcode(Numeral.three, Numeral.nine);
		baCode2 = new Barcode(Numeral.seven, Numeral.six);

		PLUItem1 = new PLUCodedProduct(plCode1, "Organic Bananas, imported", new BigDecimal("1.79"));
		PLUItem2 = new PLUCodedProduct(plCode2, "Driscoll's organic raspberries", new BigDecimal("6.99"));

		BarcodeItem1 = new BarcodedProduct(baCode1, "Tide Sport Laundry Detergent", new BigDecimal("28.91"), 9);
		BarcodeItem2 = new BarcodedProduct(baCode2, "Pasta sauce made with Real Basil and MoZzerella",
				new BigDecimal("7.49"), 3);

		// Adding the products to the INVENTORY database, the integers reflect an
		// arbitrarily chosen product quantity
		ProductDatabases.INVENTORY.put(PLUItem1, 3);
		ProductDatabases.INVENTORY.put(BarcodeItem1, 20);
		ProductDatabases.INVENTORY.put(PLUItem2, 9);
		ProductDatabases.INVENTORY.put(BarcodeItem2, 14);

		// storeItems now contains the INVENTORY database
		storeItems = ProductDatabases.INVENTORY;

		order = new Order(selfCheckLogic);

		elecScale = new ElectronicScale(1000, 1);
		elecScale.register(scaleController);

		// Initializing which method to add items to order
		selfCheckLogic.addItems(MethodOfAdd.TEXT);

		selfCheckLogic.startOrder();
	}

	/**
	 * Cleans up the test suite. Runs after every test method.
	 */
	@After
	public void tearDown() {
		this.currency = null;
		this.billDenoms = null;
		this.coinDenoms = null;
		this.maxWeight = 0;
		this.sensitivity = 0;
		this.station = null;
		this.selfCheckLogic = null;
		this.scaleController = null;
		this.textSearchController = null;
		this.elecScale = null;
		this.order = null;
		this.storeItems.clear();
	}

	/**
	 * Tests attendant entering a keyword and using the text search. Resulting in a
	 * successful search result, where a single PLUCodedProduct was found containing
	 * the keyword
	 */
	@Test
	public void testSuccessfulSearchPLUProduct() {
		boolean expected = true;
		sellableForScale rasperies = new sellableForScale(5);

		ArrayList<Product> searchReturn = textSearchController.ItemTextSearch("raspberries", storeItems);
		boolean actual = textSearchController.getResultsSastisfaction();

		selfCheckLogic.getItemTextSearchController().reactToTextSearchEvent(searchReturn, 0);
		selfCheckLogic.getSelfCheckoutStation().baggingArea.add(rasperies);
		// Assuming the attendant has selected the desired item via arraylist index
		// (returned from the front-end/GUI)
		
		// The text search was successful, so the boolean returned from
		// getResultsSatisfaction should be true
		assertEquals(expected, actual);
	}

	/**
	 * Tests attendant entering a keyword and using the text search. Resulting in a
	 * successful search result, where a single BarcodedProduct was found containing
	 * the keyword
	 */
	@Test
	public void testSuccessfulSearchBarcodedProduct() {
		boolean expected = true;

		ArrayList<Product> searchReturn = selfCheckLogic.getItemTextSearchController().ItemTextSearch("basil", storeItems);
		boolean actual = selfCheckLogic.getItemTextSearchController().getResultsSastisfaction();

		// Assuming the attendant has selected the desired item via arraylist index
		// (returned from the front-end/GUI)
		selfCheckLogic.getItemTextSearchController().reactToTextSearchEvent(searchReturn, 0);

		// The text search was successful, so the boolean returned from
		// getResultsSatisfaction should be true
		assertEquals(expected, actual);
	}

	/**
	 * Tests attendant entering a keyword and using the text search. Resulting in an
	 * unsuccessful search result, where no product was found containing the keyword
	 */
	@Test
	public void testUnsuccessfulSearchForAProduct() {
		boolean expected = false;
		int expectedSize = 0;
		
		ArrayList<Product> searchReturn = textSearchController.ItemTextSearch("Milk", storeItems);
		boolean actual = textSearchController.getResultsSastisfaction();

		int actualSize = searchReturn.size();

		// The text search was unsuccessful, so the boolean returned from
		// getResultsSatisfaction should be false
		assertEquals(expected, actual);

		// The text search was unsuccessful, so the size of the searchReturn array
		// should be zero (empty)
		assertEquals(expectedSize, actualSize);
	}

	/**
	 * Tests attendant entering a keyword and using the text search. Resulting in
	 * successful search results, where two products were found containing the same
	 * keyword
	 */
	@Test
	public void testSuccessfulSearchProductsWithSameKeyWord() {
		boolean expected = true;
		int expectedSize = 2;

		// The storeItems array contains two products that have the keyword organic in
		// their description
		ArrayList<Product> searchReturn = selfCheckLogic.getItemTextSearchController().ItemTextSearch("organic", storeItems);
		boolean actual = selfCheckLogic.getItemTextSearchController().getResultsSastisfaction();

		int actualSize = searchReturn.size();

		// Assuming the attendant has selected the desired item via arraylist index
		// (returned from the front-end/GUI)
		// This test assumes the attendant wants to add bananas to the customer's order
		

		// The text search was successful, so the boolean returned from the
		// getResultsSatisfaction should be true
		assertEquals(expected, actual);

		// The text search was successful, so the size of the searchReturn array should
		// be 2
		assertEquals(expectedSize, actualSize);
	}

	/**
	 * Tests attendant entering a keyword and using the text search. Resulting in a
	 * successful search result, where a product was found containing the keyword.
	 * The product gets added to the customer's order
	 */
	@Test
	public void testAddingOneItemToOrderAfterSuccessfulSearch() {
		boolean expected = true;
		int expectedSize = 1;

		ArrayList<Product> searchReturn = selfCheckLogic.getItemTextSearchController().ItemTextSearch("Tide", storeItems);

		boolean actual = selfCheckLogic.getItemTextSearchController().getResultsSastisfaction();

		// Assuming the attendant has selected the desired item via arraylist index
		// (returned from the front-end/GUI)
		selfCheckLogic.getItemTextSearchController().reactToTextSearchEvent(searchReturn, 0);

		BarcodedProduct productToAdd = (BarcodedProduct) searchReturn.get(0);
		selfCheckLogic.getItemTextSearchController().addToOrder(productToAdd.getExpectedWeight());

		// Getting size of the stationLogic's order product list
		int actualSize = selfCheckLogic.getOrder().getProducts().size();

		// The text search was successful, so the boolean returned from the
		// getResultsSatisfaction should be true
		assertEquals(expected, actual);

		// The add item to order was successful, so the size of the selfCheckLogic
		// product order list should be 1
		assertEquals(expectedSize, actualSize);
	}

	/**
	 * Tests attendant entering a keyword and using the text search, two separate
	 * times. Both times resulting in a successful search result, where a product
	 * was found containing the keyword. Both products get added to the customer's
	 * order
	 */
	@Test
	public void testAddingTwoItemsToOrderAfterSuccessfulSearch() {
		boolean expected1 = true;
		boolean expected2 = true;
		int expectedSize = 2;

		ArrayList<Product> searchReturn = selfCheckLogic.getItemTextSearchController().ItemTextSearch("Bananas", storeItems);
		boolean actual1 = selfCheckLogic.getItemTextSearchController().getResultsSastisfaction();

		// Assuming the attendant has selected the first desired item via arraylist
		// index (returned from the front-end/GUI)
		// and add the item to the order
		selfCheckLogic.getItemTextSearchController().reactToTextSearchEvent(searchReturn, 0);
		PLUCodedProduct productToAdd1 = (PLUCodedProduct) searchReturn.get(0);
		selfCheckLogic.getItemTextSearchController().addToOrder(1);


		ArrayList<Product> searchReturn2 = selfCheckLogic.getItemTextSearchController().ItemTextSearch("mozzerella", storeItems);
		boolean actual2 = selfCheckLogic.getItemTextSearchController().getResultsSastisfaction();

		// Assuming the attendant has selected the second desired item via arraylist
		// index (returned from the front-end/GUI)
		// and add the item to the order
		selfCheckLogic.getItemTextSearchController().reactToTextSearchEvent(searchReturn2, 0);
		BarcodedProduct productToAdd2 = (BarcodedProduct) searchReturn2.get(0);
		selfCheckLogic.getItemTextSearchController().addToOrder(productToAdd2.getExpectedWeight());

		// Getting size of the stationLogic's order product list
		int actualSize = selfCheckLogic.getOrder().getProducts().size();
		// The text search was successful, so the boolean returned from the
		// getResultsSatisfaction should be true for both products
		assertEquals(expected1, actual1);
		assertEquals(expected2, actual2);

		// The add item to order was successful, so the size of the selfCheckLogic
		// product order list should be 2
		assertEquals(expectedSize, actualSize);
	}

	/**
	 * Tests attendant entering a keyword and using the text search, two separate
	 * times. The first time should result in a successful search result, where the
	 * product was found containing the keyword. This product gets added to the
	 * order.
	 * 
	 * The second time should result in an unsuccessful search result, where no
	 * product was found containing the keyword
	 */
	@Test
	public void testMixedSuccessSearchesAndAddItemToOrderVersion1() {
		boolean expected1 = true;
		boolean expected2 = false;

		ArrayList<Product> searchReturn1 = selfCheckLogic.getItemTextSearchController().ItemTextSearch("Sport", storeItems);

		boolean actual1 = selfCheckLogic.getItemTextSearchController().getResultsSastisfaction();

		// Assuming the attendant has selected the second desired item via arraylist
		// index (returned from the front-end/GUI)
		// and add the item to the order
		selfCheckLogic.getItemTextSearchController().reactToTextSearchEvent(searchReturn1, 0);
		BarcodedProduct productToAdd1 = (BarcodedProduct) searchReturn1.get(0);
		selfCheckLogic.getItemTextSearchController().addToOrder(productToAdd1.getExpectedWeight());

		// Searching for a non-existent product within the INVENTORY
		ArrayList<Product> searchReturn2 = textSearchController.ItemTextSearch("Eggs", storeItems);

		boolean actual2 = textSearchController.getResultsSastisfaction();

		// The text search was successful, so the boolean returned from the
		// getResultsSatisfaction should be true
		assertEquals(expected1, actual1);

		// The text search was unsuccessful, so the boolean returned from the
		// getResultsSatisfaction should be false
		assertEquals(expected2, actual2);
	}

	/**
	 * Variation of the previous test! Tests attendant entering a keyword and using
	 * the text search, two separate times.
	 * 
	 * The first time should result in an unsuccessful search result, where no
	 * product was found containing the keyword.
	 * 
	 * The second time should result in a successful search result, where one
	 * product was found containing the keyword. This product gets added to the
	 * order.
	 */
	@Test
	public void testMixedSuccessSearchesAndAddItemToOrderVersion2() {
		boolean expected1 = false;
		boolean expected2 = true;

		// Searching for a non-existent product within the INVENTORY
		ArrayList<Product> searchReturn1 = selfCheckLogic.getItemTextSearchController().ItemTextSearch("spinach", storeItems);

		boolean actual1 = selfCheckLogic.getItemTextSearchController().getResultsSastisfaction();

		ArrayList<Product> searchReturn2 = selfCheckLogic.getItemTextSearchController().ItemTextSearch("raspberries", storeItems);

		// Assuming the attendant has selected the second desired item via arraylist
		// index (returned from the front-end/GUI)
		// and add the item to the order
		selfCheckLogic.getItemTextSearchController().reactToTextSearchEvent(searchReturn2, 0);
		PLUCodedProduct productToAdd2 = (PLUCodedProduct) searchReturn2.get(0);
		selfCheckLogic.getItemTextSearchController().addToOrder(1);

		boolean actual2 = selfCheckLogic.getItemTextSearchController().getResultsSastisfaction();

		// The text search was unsuccessful, so the boolean returned from the
		// getResultsSatisfaction should be false
		assertEquals(expected1, actual1);

		// The text search was successful, so the boolean returned from the
		// getResultsSatisfaction should be true
		assertEquals(expected2, actual2);
	}
	public class sellableForScale extends SellableUnit{

		protected sellableForScale(double weightInGrams) {
			super(weightInGrams);
			// TODO Auto-generated constructor stub
		}
		
	}
}
