/**
 *
 * SENG Iteration 3 P3-3 | PermitStationUse.java
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
import java.util.Iterator;

public class PermitStationUse {
	/*
	 * Simple class that gives access to settings that relate to permitting the use
	 * of a station
	 */
	private SelfCheckoutStationLogic scsl;

	private ArrayList<SelfCheckoutStationLogic> suspendedList = new ArrayList<>();
	private ArrayList<SelfCheckoutStationLogic> permitList = new ArrayList<>();

	// List of stations that are able to be ideally suspended
	private ArrayList<SelfCheckoutStationLogic> suspendableStations = new ArrayList<>();

	// List of stations that a customer is suspendable, but that a customer is using
	private ArrayList<SelfCheckoutStationLogic> customerSuspendableStations = new ArrayList<>();

	public PermitStationUse(SelfCheckoutStationLogic scsl) {
		this.scsl = scsl; // the selfcheckoutlogic for the station you want to suspend
	}

	/*
	 * adds to either suspendableStations or suspendCustomerStations checks if
	 * station is not already suspended and not on suspendedList, if a customer is
	 * not using the station, add to suspendableStations. if a customer is using the
	 * station, add to customerSuspendableStations.
	 */
	public void addSuspendableStationsToList(SelfCheckoutStationLogic scsl) {
		if (scsl.isSuspended() == false && !suspendedList.contains(scsl)) {
			if (scsl.getOrder() == null) {
				if (!suspendableStations.contains(scsl))
					suspendableStations.add(scsl);
			} else if (scsl.getOrder() != null) {
				if (!customerSuspendableStations.contains(scsl))
					customerSuspendableStations.add(scsl);
			}
		}
	}

	/*
	 * refreshes / updates the lists of suspendable stations
	 **/
	public void updateSuspendableLists() {
		Iterator<SelfCheckoutStationLogic> suspendableStationsIterator = suspendableStations.iterator();
		while (suspendableStationsIterator.hasNext()) {
			SelfCheckoutStationLogic station = suspendableStationsIterator.next();
			if (station.getOrder() != null) {
				suspendableStationsIterator.remove();
				customerSuspendableStations.add(station);
			}
			if (station.isSuspended()) {
				suspendableStationsIterator.remove();
				permitList.add(station);
			}
		}

		Iterator<SelfCheckoutStationLogic> customerSuspendableStationsIterator = customerSuspendableStations.iterator();
		while (customerSuspendableStationsIterator.hasNext()) {
			SelfCheckoutStationLogic station = customerSuspendableStationsIterator.next();
			if (station.getOrder() == null) {
				customerSuspendableStationsIterator.remove();
				suspendableStations.add(station);
			}
			if (station.isSuspended()) {
				customerSuspendableStationsIterator.remove();
				permitList.add(station);
			}
		}
	}

	/*
	 * if maintenance finished on station, the station can be added to permitList.
	 * Method checks that the station was suspended in the first place (if
	 * statement)
	 */
	public void maintenenceComplete(SelfCheckoutStationLogic scsl) {

		if (suspendedList.contains(scsl)) {
			permitList.add(scsl);
		} else {
			scsl.notifyAttendantIO("Station was not suspended");
		}

	}

	/*
	 * goes through list of stations to check if they are suspended and returns the
	 * list of suspended stations that can be unsuspended (i.e. maintenance has been
	 * completed).
	 */
	public ArrayList<SelfCheckoutStationLogic> getListofStationsForUnsuspending() {
		return permitList;
	}

	/*
	 * returns list of stations that are ideally able to be suspended.
	 */
	public ArrayList<SelfCheckoutStationLogic> getListOfSuspendableStations() {
		return suspendableStations;
	}

	/*
	 * returns list of stations that a customer is using that is able to be
	 * suspended.
	 */
	public ArrayList<SelfCheckoutStationLogic> getListOfCustomerSuspendableStations() {
		return customerSuspendableStations;
	}

	/**
	 * Allows Attendant to permit the use of a selfcheckout station
	 * 
	 * @parm scsl the selfCheckoutStation's logic (software) that the Attendant
	 *       wants to enable
	 * @throws simulationexception? if unable to access selfcheckoutStation
	 */
	public void permitUse(SelfCheckoutStationLogic scsl) {

		if (permitList.contains(scsl)) {
			scsl.unsuspend();
			suspendedList.remove(scsl);
			permitList.remove(scsl);
			suspendableStations.add(scsl);
			customerSuspendableStations.remove(scsl);
			scsl.notifyCustomerIO("This station is now unsuspended ");
		}

	}

	/**
	 * If a station is suspendable (in the suspendableStations arraylist), allows
	 * attendant to prevent the use of a selfcheckoutstation.
	 * 
	 * @param scsl The station that the attendent wants to prevent use to
	 */
	public void preventUse(SelfCheckoutStationLogic scsl) {
		if (suspendableStations.contains(scsl)) {
			scsl.suspend();
			suspendedList.add(scsl);
			suspendableStations.remove(scsl);
			permitList.add(scsl);
			scsl.notifyCustomerIO("This station is suspended");

		}
	}

	/**
	 * If a station is being used by a customer, allows attendant to prevent the use
	 * of that station.
	 * 
	 * @param scsl The station that the attendent wants to prevent use to
	 */

	public void forcePreventUse(SelfCheckoutStationLogic scsl) {
		if (customerSuspendableStations.contains(scsl)) {
			scsl.suspend();
			suspendedList.add(scsl);
			customerSuspendableStations.remove(scsl);
			permitList.add(scsl);
			scsl.notifyCustomerIO("This station is suspended");
		}
	}

}