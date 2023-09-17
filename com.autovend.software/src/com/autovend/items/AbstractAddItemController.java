/**
 *
 * SENG Iteration 3 P3-3 | AbstractAddItemController.java
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

import com.autovend.ReusableBag;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;

/**
 * Abstract base class representing adding items to the order by various means.
 */
public abstract class AbstractAddItemController {
	protected SelfCheckoutStationLogic stationLogic;
	protected BaggingAreaScaleController scaleController;
	double expectedWeight;
	protected BarcodedProduct product;
	protected PLUCodedProduct productPLU;
	protected ReusableBag bag;
	protected ScanningScale scanScale;

	/**
	 * Creates an AddItemController.
	 * 
	 * @param stationLogic The SelfCheckoutStationLogic for the station to which
	 *                     this controller belongs.
	 * @param scale        The BaggingAreaScaleController for the bagging area of
	 *                     this station.
	 */
	public AbstractAddItemController(SelfCheckoutStationLogic stationLogic, BaggingAreaScaleController scale,
			ScanningScale scanScale2) {
		this.expectedWeight = 0;
		this.stationLogic = stationLogic;
		this.scaleController = scale;
		this.scanScale = scanScale2;
	}

	/**
	 * Adds an item to the current order.
	 * 
	 * @param weight The weight detected by the bagging area scale.
	 */
	public abstract void addToOrder(double weight);

	// The weight detected on the scale does not match the expected weight of the
	// item added.
	// In future implementations there will be an option to override the weight
	// discrepancy
	// and still add the item to the order.
	public abstract void checkForDiscrepancy(double weight);

	public abstract void addToOrderNoBaggingArea();

	public void OverLoadEvent() {
		stationLogic.notifyAttendantIO("OVERLOAD EVENT\n");
		stationLogic.scaleOverloaded();

	}

	public void OutOfOverloadEvent() {
		stationLogic.notifyAttendantIO("OVERLOAD EVENT OVER\n");
		stationLogic.scaleOutOverloaded();

	}

}