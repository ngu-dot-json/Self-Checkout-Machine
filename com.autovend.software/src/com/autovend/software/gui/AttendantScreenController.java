/**
 *
 * SENG Iteration 3 P3-3 | AttendantScreenController.java
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

package com.autovend.software.gui;

import com.autovend.SupervisionStationLogic;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.SupervisionStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.TouchScreenObserver;

public class AttendantScreenController implements TouchScreenObserver {
	SupervisionStation device;
	SupervisionStationLogic supervisionStationLogic;
	AttendantScreen screen;
	
	public AttendantScreenController(SupervisionStationLogic supervisionStationLogic, SupervisionStation station) {
		this.device = station;
		this.supervisionStationLogic = supervisionStationLogic;
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		this.screen = new AttendantScreen(this.supervisionStationLogic);
		
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}
	
	public AttendantScreen getAttendantScreen() {
		return this.screen;
	}

}
