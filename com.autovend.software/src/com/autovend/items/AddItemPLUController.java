/**
 *
 * SENG Iteration 3 P3-3 | AddItemPLUController.java
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

import java.util.Map;

import com.autovend.PriceLookUpCode;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.gui.PLUPanel;

public class AddItemPLUController extends AbstractAddItemController implements EnterPLUObserverStub {

	private boolean isEnabled = true;
	private Map<PriceLookUpCode, PLUCodedProduct> database = ProductDatabases.PLU_PRODUCT_DATABASE;

	/**
	 * Creates an AddItemPLUController
	 * 
	 * @param stationLogic The logic for the SelfCheckoutStation where the AddItem
	 *                     controllers belong
	 * @param scale        The BaggingAreaScaleController for this
	 *                     SelfCheckoutStation
	 */
	public AddItemPLUController(SelfCheckoutStationLogic stationLogic, BaggingAreaScaleController scale,
			ScanningScale scaleForScanner) {
		super(stationLogic, scale, scaleForScanner);

	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		isEnabled = true;
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		isEnabled = false;
	}

	/**
	 * Event notifying system that PLU has been entered, meaning that station should
	 * be suspended while item is found
	 * 
	 * @param enteredPLU the PLU code entered by the Customer
	 */
	@Override
	public void reactToPLUEnteredEvent(PriceLookUpCode enteredPLU) {
		productPLU = null;

		if (!isEnabled)
			return;

		// confirm that the PLU is valid!
		if (database.containsKey(enteredPLU)) {
			productPLU = database.get(enteredPLU);
			PLUPanel.PLUValid();
		}
		scanScale.setAddItemController(this);
		scaleController.setAddItemController(this);

		if (productPLU != null) {
			stationLogic.notifyCustomerIO("Please weigh the PLU item.");
		} else {
			stationLogic.notifyCustomerIO("Invalid PLU code! Please enter a new one!" + enteredPLU);
		}

		// Include weight discrepancy exception
	}

	/**
	 * Invoked when a product with a PLU is place in the bagging area.
	 * 
	 * @param weight the exact weight of the item in grams
	 */
	@Override
	public void addToOrder(double weight) {
		expectedWeight = weight;
		stationLogic.addToOrderPLU(productPLU, weight);
	}

	@Override
	public void addToOrderNoBaggingArea() {
		productPLU = null;
		stationLogic.unsuspend();
	}

	@Override
	public void checkForDiscrepancy(double weight) {
		stationLogic.suspend();
		if (expectedWeight == weight) {
			expectedWeight = 0;
			stationLogic.getOrder().updateWeightHashMap(productPLU, weight);
			productPLU = null;
			stationLogic.unsuspend();
		} else {
			stationLogic.notifyAttendantIO("Weight discrepancy in bagging area.\n");
			stationLogic.notifyCustomerIO("Weight discrepancy in bagging area.\n");
		}

	}

}
