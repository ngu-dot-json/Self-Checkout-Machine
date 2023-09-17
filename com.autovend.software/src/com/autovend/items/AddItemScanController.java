/**
 *
 * SENG Iteration 3 P3-3 | AddItemScanController.java
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

import com.autovend.Barcode;
import com.autovend.Membership;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.BarcodeScannerObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;

/**
 * Implementation of observer for hardware scanners.
 * <p>
 * Implements {@link com.autovend.devices.observers.BarcodeScannerObserver
 * BarcodeScannerObserver}.
 * </p>
 */
public class AddItemScanController extends AbstractAddItemController implements BarcodeScannerObserver {
	private boolean isEnabled = false;
	private Map<Barcode, BarcodedProduct> database = ProductDatabases.BARCODED_PRODUCT_DATABASE;

	/**
	 * Creates an AddItemScanController.
	 * 
	 * @param stationLogic The SelfCheckoutStationLogic for the station to which the
	 *                     scanners belongs.
	 * @param scale        The BaggingAreaScaleController for the bagging area of
	 *                     this station.
	 */
	public AddItemScanController(SelfCheckoutStationLogic stationLogic, BaggingAreaScaleController scaleController,
			ScanningScale scaleForScanner) {
		super(stationLogic, scaleController, scaleForScanner);
	}

	/**
	 * This method will add an item to the order by communicating with the
	 * logicClass
	 * 
	 * @param weight The weight detected on the scale
	 * 
	 */
	@Override
	public void addToOrder(double weight) {

	}

	/**
	 * Event that is called when a barcode is scanned by a scanner device, event is
	 * notified to all registered observers of this type.
	 * 
	 * @param barcodeScanner the scanner which triggered this event.
	 * @param barcode        the barcode of the item scanned.
	 */
	@Override
	public void reactToBarcodeScannedEvent(BarcodeScanner barcodeScanner, Barcode barcode) {
		// it checks if the barcode is actually a barcode for a membership card and
		// returns if so.
		Membership member = new Membership(stationLogic);
		if (member.inSystem(barcode.toString()) == true) {
			return;
		}
		// if the scanner is disabled then nothing should happen.
		if (barcodeScanner.isDisabled())
			return;

		// so scale can call AddToOrder Method once it gets the detected weight change.
		scanScale.setAddItemController(this);
		scaleController.setAddItemController(this);

		// gets product information from store database.
		product = database.get(barcode);
		expectedWeight = product.getExpectedWeight();
		stationLogic.addToOrder(product);
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		isEnabled = true;
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		isEnabled = false;
	}

	@Override
	public void addToOrderNoBaggingArea() {
		stationLogic.unsuspend();
	}

	@Override
	public void checkForDiscrepancy(double weight) {
		// suspends station from taking any further actions.
		stationLogic.suspend();
		if (expectedWeight == weight) {
			expectedWeight = 0;
			stationLogic.getOrder().updateWeightArray(product);
			product = null;
			stationLogic.unsuspend();
		} else {
			stationLogic.notifyAttendantIO("Weight discrepancy in bagging area.\n");
			stationLogic.notifyCustomerIO("Weight discrepancy in bagging area.\n");
		}

	}
}
