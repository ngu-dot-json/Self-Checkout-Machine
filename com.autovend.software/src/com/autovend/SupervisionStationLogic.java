/**
 *
 * SENG Iteration 3 P3-3 | SupervisionStationLogic.java
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

import java.util.ArrayList;
import java.util.HashMap;

import com.autovend.devices.SelfCheckoutStation;

import com.autovend.devices.SupervisionStation;
import com.autovend.items.AddItemTextSearchController;
import com.autovend.software.gui.AttendantScreenController;

public class SupervisionStationLogic {
	SupervisionStation station;
	private AttendantScreenController screenObserver;
	private ArrayList<SelfCheckoutStationLogic> supervisedStations;
	public Login_Logout loginlogout;
	private int stationCount;
	public AddItemTextSearchController searchController;

	/*
	 * Supervision Station Logic constructor, needs a SupervisionStation as input
	 * 
	 */
	public SupervisionStationLogic(SupervisionStation supervisionStation) {

		this.station = supervisionStation; // The hardware station

		screenObserver = new AttendantScreenController(this, this.station); // Attendant's screen observer

		this.station.screen.register(screenObserver); // Register observer to station screen

		this.loginlogout = new Login_Logout(supervisionStation); // Login_Logout logic

		this.stationCount = 0; // Currently supervising 0 stations

		this.supervisedStations = new ArrayList<SelfCheckoutStationLogic>(); // List of supervised stations
	}

	/*
	 * Takes a SelfCheckoutStation's Logic as input and begins supervising it's
	 * station
	 * 
	 */
	public void supervise(SelfCheckoutStationLogic customerStation) {

		// Adds station logic to supervised station list
		this.supervisedStations.add(customerStation);

		// Adds the SelfCheckoutStation to the SupervisionStation
		this.station.add(customerStation.getSelfCheckoutStation());

		// Sets the supervisor of the SelfCheckoutStation to this
		customerStation.setSupervisor(this);

		// Increase station count
		stationCount++;

		// Notifies attendant that a station has been registered
		notifyAttendant("Now supervising SelfCheckoutStation #" + String.valueOf(stationCount));

		// Adds the station to the GUI menu
		screenObserver.getAttendantScreen().mainPanel.addStationToGUI(customerStation, stationCount);
	}

	/*
	 * Stops supervising a SelfCheckoutStation
	 * 
	 */
	public void stopSupervising(SelfCheckoutStationLogic customerStation) {

		// Checks if the input station is being supervised by this station
		if (this.supervisedStations.contains(customerStation)) {

			// Removes station logic from list
			this.supervisedStations.remove(customerStation);

			// Removes station from supervision station
			this.station.remove(customerStation.getSelfCheckoutStation());

			// Notifies attendant that a station is no longer being supervised
			notifyAttendant("No longer supervising SelfCheckoutStation #" + String.valueOf(stationCount));
			
			screenObserver.getAttendantScreen().mainPanel.removeStationFromGUI(customerStation);

			// Reduce station count
			stationCount--;
		}
	}

	/*
	 * Gets the Supervision Station for this SupervisionStationLogic
	 * 
	 */
	public SupervisionStation getSupervisionStation() {
		return this.station;
	}

	/*
	 * Sends a string to the attendant's screen
	 * 
	 */
	public void notifyAttendant(String message) {
		screenObserver.getAttendantScreen().mainPanel.notifyAttendantGUI(message);
	}

}
