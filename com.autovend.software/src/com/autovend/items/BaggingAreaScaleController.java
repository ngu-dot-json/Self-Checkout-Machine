/**
 *
 * SENG Iteration 3 P3-3 | BaggingAreaScaleController.java
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

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.ElectronicScaleObserver;

/**
 * Observer for bagging area scale.
 * <p>
 * Implements {@link com.autovend.devices.observers.ElectronicScaleObserver
 * ElectronicScaleObserver}.
 * </p>
 */
public class BaggingAreaScaleController implements ElectronicScaleObserver {
	public double currentWeightInGrams = 0;
	private AbstractAddItemController addItemController;
	private RemoveItemController removeItemController;

	public BaggingAreaScaleController(RemoveItemController removeItemController2) {
		removeItemController = removeItemController2;
	}

	/**
	 * Announces that the weight has been changed on a scale
	 * 
	 * @param scale         the scale where this event occurred
	 * @param weightInGrams the amount that the weight on the scale changed by
	 */
	@Override
	public void reactToWeightChangedEvent(ElectronicScale scale, double weightInGrams) {
		double changeInWeight = weightInGrams - currentWeightInGrams;
		currentWeightInGrams = weightInGrams;
		if (changeInWeight > 0) {
			addItemController.checkForDiscrepancy(changeInWeight);
		} else if (changeInWeight < 0) {
			removeItemController.checkForDiscrepancy(changeInWeight);
		}
	}

	/**
	 * Gets the current weight of the item on the scale.
	 * 
	 * @return A double representing the current weight in grams of item on the
	 *         scale
	 */
	public double getCurrentWeight() {
		return currentWeightInGrams;
	}

	/**
	 * Sets the addItemController field of this object to be the one specified
	 * 
	 * @param addItem The AddItemController to be linked with this controller.
	 */
	public void setAddItemController(AbstractAddItemController addItemController) {
		this.addItemController = addItemController;
	}

	public void noBagRequestGranted() {
		addItemController.addToOrderNoBaggingArea();
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void reactToOverloadEvent(ElectronicScale scale) {
		addItemController.OverLoadEvent();
	}

	@Override
	public void reactToOutOfOverloadEvent(ElectronicScale scale) {
		addItemController.OutOfOverloadEvent();
	}
}