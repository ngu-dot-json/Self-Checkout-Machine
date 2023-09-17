/**
 *
 * SENG Iteration 3 P3-3 | AddItemTextSearchController.java
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

import java.util.ArrayList;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>AddItemTextSearchController Class</h1> The AddItemTextSearchController
 * Class is an extension of the AbstractAddItemController Class. This class
 * contains methods that allow an attendant at a SelfCheckoutStation to add a
 * product to a customer's order via text search.
 * <p>
 * 
 * @author Brett Lyle
 * @since 2023-03-27
 *
 */
public class AddItemTextSearchController extends AbstractAddItemController {
	// Private fields relevant to the addItemTextSearchController
	private boolean isEnabled = false;
	private boolean satisfaction = false;
	private Product currentProduct;
	private ArrayList<Product> results;

	// Can be used for future iterations, where the index of the desired item
	// from the successful search results can be stored (after Attendant I/O)
	private int index = 0;

	/**
	 * Creates an AddItemScanController.
	 * 
	 * @param stationLogic The SelfCheckoutStationLogic for the station to which the
	 *                     scanners belongs.
	 * @param scale        The BaggingAreaScaleController for the bagging area of
	 *                     this station.
	 */
	public AddItemTextSearchController(SelfCheckoutStationLogic stationLogic, BaggingAreaScaleController scale,
			ScanningScale sc) {
		super(stationLogic, scale, sc);
	}

	/**
	 * Executes the text search for products containing the keyword specified by the
	 * SelfCheckoutStation attendant
	 * <p>
	 * 
	 * @param keyword The keyword to look for in the database
	 * @param items   The INVENTORY Product database to search through
	 * @return an ArrayList of relevant product results that contain the desired
	 *         keyword
	 */
	public ArrayList<Product> ItemTextSearch(String keyword, Map<Product, Integer> items) {
		String prodName = "";

		// Convert keyword to lowercase to ensure no case-sensitivity when searching
		String keywordLower = keyword.toLowerCase();

		this.results = new ArrayList<Product>();

		// Iterate through the INVENTORY Product database (hash map) and search for
		// products that contain the keyword
		for (Map.Entry<Product, Integer> entry : items.entrySet()) {
			currentProduct = entry.getKey();

			// If the product is a PLUCodedProduct, get the product's description and check
			// whether it contains the specified keyword
			if (currentProduct instanceof PLUCodedProduct) {
				prodName = ((PLUCodedProduct) currentProduct).getDescription().toLowerCase();
				if (prodName.contains(keywordLower)) {
					results.add(currentProduct);
					// add product if it contains the keyword
				}
			}

			// Otherwise, the product is a BarcodedProduct and do the same process as above!
			else {
				prodName = ((BarcodedProduct) currentProduct).getDescription().toLowerCase();
				if (prodName.contains(keywordLower)) {
					results.add(currentProduct); // add product if it contains the keyword

				}
			}
		}

		// Assuming that a non-empty arraylist (the search results yielded at least one
		// product)
		// is satisfactory for the attendant
		if (!results.isEmpty()) {
			satisfaction = true;
		}

		else {
			satisfaction = false;
		}

		// Return the arraylist of the search results (products that containing the
		// keyword)
		// The arraylist would be displayed on the front-end (GUI) for attendant to
		// analyze
		return results;
	}

	/**
	 * A method that gets the satisfaction level for the text search results
	 * 
	 * @return The boolean of whether the results were satisfactory (true) or not
	 *         (false)
	 */
	public boolean getResultsSastisfaction() {
		return satisfaction;
	}

	/**
	 * A reactionary event notifying the system that the attendant has selected an
	 * product and would like to add it to the current customer's order after a
	 * successful search has been executed.
	 * <p>
	 * 
	 * @param relevant The results list which has products containing the keyword
	 * @param index    The index number of the product to be added to the customer's
	 *                 order
	 */
	public void reactToTextSearchEvent(ArrayList<Product> relevant, int index) {
		product = null;
		productPLU = null;

		// Currently, assuming that the index number would be passed in from the
		// front-end (GUI)
		// after the attendant has selected which item to add into the customer's order
		currentProduct = relevant.get(index);

		scanScale.setAddItemController(this);
		scaleController.setAddItemController(this);

		// Check if the product is a PLUCodedProduct and set the corresponding field in
		// the logic class
		if (currentProduct instanceof PLUCodedProduct) {
			productPLU = (PLUCodedProduct) currentProduct;
		}

		// Otherwise, product is a BarcodedProduct and set the corresponding field in
		// the logic class
		else {
			product = (BarcodedProduct) currentProduct;
			expectedWeight = product.getExpectedWeight();
			stationLogic.addToOrder(product);
		}
	}

	/**
	 * A method to formally add the product to the customer's order.
	 * 
	 * @param weight The exact weight of the item in grams
	 */
	@Override
	public void addToOrder(double weight) {
		// Check if the product is a PLUCodedProduct and add it to the customer's order
		if (currentProduct instanceof PLUCodedProduct) {
			expectedWeight = weight;
			stationLogic.addToOrderPLU(productPLU, weight);
		}

		// Otherwise, product is a BarcodedProduct and add it to the customer's order
	}

	@Override
	public void addToOrderNoBaggingArea() {
		productPLU = null;
		product = null;
		stationLogic.unsuspend();

	}

	@Override
	public void checkForDiscrepancy(double weight) {
		// Suspend the system, so the customer cannot interact with it
		stationLogic.suspend();
		if (expectedWeight == weight) {
			expectedWeight = 0;
			if (currentProduct instanceof PLUCodedProduct) {
				stationLogic.getOrder().updateWeightHashMap(productPLU, weight);
				productPLU = null;
			} else {
				stationLogic.getOrder().updateWeightArray(product);
				product = null;
			}
			stationLogic.unsuspend();

		} else {
			stationLogic.notifyAttendantIO("Weight discrepancy in bagging area.\n");
			stationLogic.notifyCustomerIO("Weight discrepancy in bagging area.\n");
		}

	}
}