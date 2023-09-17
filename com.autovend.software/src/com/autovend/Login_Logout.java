/**
 *
 * SENG Iteration 3 P3-3 | Login_Logout.java
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

import com.autovend.devices.SupervisionStation;
import java.util.HashMap;

public class Login_Logout {

	// Stores all attendant information (static all attendants share the same
	// database)
	private static HashMap<String, String> attendantLoginInfo = new HashMap<>();

	private SupervisionStation station; // the station the attendant is supervising

	private String attendant = null; // The attendant currently associated with this station (only one attendant per
										// time)

	// Min and max in case there are no more available x length IDs then, a range is
	// allowed for more IDs
	private static int memberIDAllowableLengthMin = 8; // set min initial length of member IDs to 8
	private static int memberIDAllowableLengthMax = 8; // set max initial length of member ID to 8

	public Login_Logout(SupervisionStation station) {
		this.station = station;
	}

	/**
	 * Registers an attendant into the system (if the id was not used before, and it
	 * is valid)
	 * 
	 * @param userID   the attendants ID
	 * @param password the password the attendant will use
	 * @return False if the attendant ID has been registered before, true if the ID
	 *         has not been registered before and the new attendant has been
	 *         registered
	 */
	public static boolean register(String userID, String password) {
		if (attendantLoginInfo.containsKey(userID)) {
			return false;
		} else if (isValidID(userID)) {
			attendantLoginInfo.put(userID, password);
			return true;
		}
		return false;
	}

	/**
	 * Removes an attendant from the system if they were registered previously (ID
	 * in the system)
	 * 
	 * @param userID the ID the attendant was assigned
	 * @return true if attendant removed from system, false otherwise
	 */
	public static boolean remove(String userID) {
		if (attendantLoginInfo.containsKey(userID)) {
			attendantLoginInfo.remove(userID);
			return true;
		}
		return false;
	}

	/**
	 * Allows the attendant to login if their userID was registered previously in
	 * the system, the provided password matches (and there was no one previously
	 * logged in
	 * 
	 * @param userID   attendants ID
	 * @param password attendants password
	 * @return true if login successful, false if login unsuccessful
	 */
	public boolean login(String userID, String password) {
		if (attendantLoginInfo.containsKey(userID) && attendantLoginInfo.get(userID).compareTo(password) == 0
				&& this.attendant == null) {
			this.attendant = userID;
			return true;
		}
		return false;
	}

	/**
	 * Logs out an attendant if they were previously logged in to the system
	 * 
	 * @return true if logout successful, false otherwise
	 */
	public boolean logout() {
		if (this.attendant != null) {
			this.attendant = null;
			return true;
		}
		return false;
	}

	/**
	 * Checks if the given ID is valid, assuming ID can be a length of 8
	 * 
	 * @param ID the ID for the attendant
	 * @return True if valid, false otherwise
	 */
	private static boolean isValidID(String ID) {
		if (ID.length() >= memberIDAllowableLengthMin && ID.length() <= memberIDAllowableLengthMax) {
			try {
				int int_ID = Integer.parseInt(ID); // IDs should only be an integer
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Sets the minimum length attendant IDs can be (cannot be 0)
	 * 
	 * @param min the min length
	 */
	public static void setMemberIDAllowableLengthMin(int min) {
		if (min == 0) {
			throw new IllegalArgumentException("Cannot have 0 length IDs");
		}

		if (min > memberIDAllowableLengthMax) {
			throw new IllegalArgumentException("Min cannot be larger than max");
		}

		memberIDAllowableLengthMin = min;
	}

	/**
	 * Sets the maximum length attendant IDs can be (cannot be 0)
	 * 
	 * @param max the max length
	 */
	public static void setMemberIDAllowableLengthMax(int max) {
		if (max == 0) {
			throw new IllegalArgumentException("Cannot have 0 length IDs");
		}

		if (max < memberIDAllowableLengthMin) {
			throw new IllegalArgumentException("Max cannot be smaller than min");
		}

		memberIDAllowableLengthMax = max;
	}

	// Getters for testing

	/**
	 * @return The hashmap containing all attendant login and logout information
	 */
	public static HashMap<String, String> getAttendantLoginInfo() {
		return attendantLoginInfo;
	}

	/**
	 * @return the ID of the associate attendant that is logged in
	 */
	public String getAttendant() {
		return attendant;
	}

	/**
	 * @return the minimum length for IDs
	 */
	public static int getMemberIDAllowableLengthMin() {
		return memberIDAllowableLengthMin;
	}

	/**
	 * @return the minimum length for IDs
	 */
	public static int getMemberIDAllowableLengthMax() {
		return memberIDAllowableLengthMax;
	}
}