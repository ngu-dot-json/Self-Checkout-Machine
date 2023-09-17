/**
 *
 * SENG Iteration 3 P3-3 | AddItemsByBrowsingController.java
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

import java.math.BigDecimal;

import com.autovend.Barcode;
import com.autovend.PriceLookUpCode;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.external.ProductDatabases;

public class AddItemByBrowsingController extends AbstractAddItemController {

	public boolean frontEndDisabled = false;
	private boolean isEnabled = false;
	private boolean isPLU = false;

	public AddItemByBrowsingController(SelfCheckoutStationLogic stationLogic, BaggingAreaScaleController scale,
			ScanningScale scal) {
		super(stationLogic, scale, scal);
	}

	/**
	 * Backend method intended for use with a future frontend GUI. Takes in either a
	 * Barcode or PriceLookUpCode and searches the appropriate database. Upon
	 * finding the correct product, will set the corresponding variable for further
	 * use with addToOrder(). An expected weight is also set for barcoded products.
	 * 
	 * @param inputCode The selected PriceLookUpCode or Barcode object
	 */
	public <T> void reactToBrowsingSelectedEvent(T inputCode) {
		isPLU = false;
		product = null;
		productPLU = null;
		if (inputCode instanceof PriceLookUpCode) {
			isPLU = true;
		} else if (inputCode instanceof Barcode) {
			isPLU = false;
		} else {
			stationLogic.notifyAttendantIO("Unknown item in bagging area.");
		}

		scanScale.setAddItemController(this);
		scaleController.setAddItemController(this);

		if (isPLU) {
			productPLU = ProductDatabases.PLU_PRODUCT_DATABASE.get(inputCode);
			stationLogic.notifyCustomerIO("Please weigh the PLU item.");
		} else {
			product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(inputCode);
			expectedWeight = product.getExpectedWeight();
			stationLogic.addToOrder(product);
		}

	}

	/**
	 * Adds the current product or productPLU to the order. PLU-coded products
	 * require the scale's current weight, as well.
	 */
	@Override
	public void addToOrder(double weight) {
		if (isPLU) {
			expectedWeight = weight;
			stationLogic.addToOrderPLU(productPLU, weight);
		}
	}

	@Override
	public void addToOrderNoBaggingArea() {
		isPLU = false;
		productPLU = null;
		product = null;
		stationLogic.unsuspend();

	}

	@Override
	public void checkForDiscrepancy(double weight) {
		stationLogic.suspend();
		if (expectedWeight == weight) {
			expectedWeight = 0;
			if (isPLU) {
				isPLU = false;
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
