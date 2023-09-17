/**
 *
 * SENG Iteration 3 P3-3 | StartUpShutDown.java
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

package com.autovend;

import com.autovend.devices.BillDispenser;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.SelfCheckoutStation;

public class StartUpShutDown {
	SelfCheckoutStation checkoutStation;
	private boolean enabledStatus;

	public StartUpShutDown(SelfCheckoutStation checkoutStation) {
		this.checkoutStation = checkoutStation;
	}

	/**
	 * A method to shutdown the entire system.
	 */

	public void shutdownStation() {
		// Disabling all the hardware of the selfCheckoutStaion.
		this.checkoutStation.scale.disable();
		this.checkoutStation.printer.disable();
		this.checkoutStation.coinSlot.disable();
		this.checkoutStation.coinTray.disable();
		this.checkoutStation.billInput.disable();
		this.checkoutStation.cardReader.disable();
		this.checkoutStation.billOutput.disable();
		this.checkoutStation.billStorage.disable();
		this.checkoutStation.baggingArea.disable();
		this.checkoutStation.mainScanner.disable();
		this.checkoutStation.coinStorage.disable();
		this.checkoutStation.billValidator.disable();
		this.checkoutStation.coinValidator.disable();
		this.checkoutStation.handheldScanner.disable();
		this.checkoutStation.screen.disable();

		for (BillDispenser bDispenser : checkoutStation.billDispensers.values()) {
			bDispenser.disable();
		}

		for (CoinDispenser cDispenser : checkoutStation.coinDispensers.values()) {
			cDispenser.disable();
		}
		this.enabledStatus = false;
	}

	/**
	 * A method to start the entire system.
	 */

	public void startupStation() {
		// Disabling all the hardware of the selfCheckoutStaion.
		this.checkoutStation.scale.enable();
		this.checkoutStation.printer.enable();
		this.checkoutStation.coinSlot.enable();
		this.checkoutStation.coinTray.enable();
		this.checkoutStation.billInput.enable();
		this.checkoutStation.cardReader.enable();
		this.checkoutStation.billOutput.enable();
		this.checkoutStation.billStorage.enable();
		this.checkoutStation.baggingArea.enable();
		this.checkoutStation.mainScanner.enable();
		this.checkoutStation.coinStorage.enable();
		this.checkoutStation.billValidator.enable();
		this.checkoutStation.coinValidator.enable();
		this.checkoutStation.handheldScanner.enable();
		this.checkoutStation.screen.enable();

		for (BillDispenser bDispenser : checkoutStation.billDispensers.values()) {
			bDispenser.enable();
		}

		for (CoinDispenser cDispenser : checkoutStation.coinDispensers.values()) {
			cDispenser.enable();
		}

		this.enabledStatus = true;
	}

	/**
	 * Check if system has been started or not (for Attendant Station)
	 * 
	 * @return true if station is started up, false if station is shutdown
	 */
	public boolean isEnabled() {
		return this.enabledStatus;
	}
}