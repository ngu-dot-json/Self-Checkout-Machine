/**
 *
 * SENG Iteration 3 P3-3 | Membership.java
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import com.autovend.Card.CardData;
import com.autovend.Card.CardSwipeData;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.CardReader;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.BarcodeScannerObserver;
import com.autovend.devices.observers.CardReaderObserver;
import com.autovend.software.gui.EnterMembershipPanel;

public class Membership implements CardReaderObserver, BarcodeScannerObserver {
	private String memberID;
	private SelfCheckoutStationLogic logicStation;
	private File file;
	private MembershipCard membershipCard;
	private CardData cardData;
	private MethodOfMembership method;
	private SelfCheckoutStation station;
	private CardReader cardReader;
	private BarcodeScanner bscanner;
	private boolean isCardScan, isScanFoundInDatabase;
	private int wrongScanCount;
	private BufferedImage signature;
	private boolean cardSwiped, isSwipeFoundInDatabase;
	private String dataType;

	/**
	 * constructor for Membership
	 * 
	 * @param = logic the station logic to be initiated.
	 */
	public Membership(SelfCheckoutStationLogic logic) {
		logicStation = logic;
		file = new File("memberData.txt");
		station = logicStation.getSelfCheckoutStation();
		station.cardReader.register(this);
		station.cardReader.enable();
		this.cardSwiped = false;
		isScanFoundInDatabase = false;
		isSwipeFoundInDatabase = false;
	}

	public Membership(SelfCheckoutStationLogic logic, MembershipCard memCard) {
		logicStation = logic;
		file = new File("memberData.txt");
		this.membershipCard = memCard;
		station = logicStation.getSelfCheckoutStation();
		station.cardReader.register(this);
		station.cardReader.enable();
		station.mainScanner.enable();
		station.handheldScanner.enable();
		station.mainScanner.register(this); // register member to the scan observer
		station.handheldScanner.register(this);
		isCardScan = false;
		isScanFoundInDatabase = false;
		wrongScanCount = 0;
		isSwipeFoundInDatabase = false;
	}

	// This method allows the user to select which method they want to use for
	// entering membership card.
	// it should redirect the user to what method they specify.
	/**
	 * 
	 * @param methodOfMembership the method the user wants to use
	 */
	public void selectInputMethod(MethodOfMembership methodOfMembership) {
		method = methodOfMembership;

		switch (method) {
		case SCAN:

			if (membershipCard.hasBarcode() == true) {

				logicStation.notifyCustomerIO("Please scan your membership card");
				station.mainScanner.scan(membershipCard);
				logicStation.notifyCustomerIO("Membership ID: " + membershipCard.getBarcode().toString());

			} else {
				logicStation.notifyCustomerIO("This card does not support scanning.");
			}
			break;
		case SWIPE:
			logicStation.notifyCustomerIO("Please swipe your membership card");
			break;
		case TYPE:
			logicStation.notifyCustomerIO("Please type your membership card ID");
			break;
		}
	}

	/**
	 * 
	 * This method will check if a user is in the data base and update accordingly
	 * will prompt the user to input their membership card number.
	 * 
	 * @param memberID the member number to attempt to be signed in
	 * 
	 */
	public void enterMembershipByTyping(String memberID) throws MembershipNotFoundException {
		boolean found = false;
		this.memberID = memberID;

		isValidMembership(memberID);
		try (Scanner scanner = new Scanner(System.in)) {
			try {
				Scanner fileScanner = new Scanner(file);
				fileScanner.useDelimiter(",");

				while (fileScanner.hasNext()) {
					String value = fileScanner.next();
					if (value.equals(this.memberID)) {
						this.memberID = value;
						found = true;
						break;
					}
				}

				fileScanner.close();

			} catch (FileNotFoundException e) {
				return;
			}
			// closes the scanner (not sure why showing error above)
			scanner.close();
		}

		if (found == true) {
			setID(this.memberID);
		} else {
			throw new MembershipNotFoundException();
		}
	}

	// simply returns the id of a specific member (Use this to complete scenario 2
	// on UML)
	/**
	 * 
	 * @param member Membership class instance
	 * @return memberID number
	 */
	public String GetId() {
		return memberID;
	}

	public void setID(String cardNumber) {
		this.memberID = cardNumber;
	}

	// Will check if a certain member is in the database of "memberData.txt" or not
	/**
	 * @param memberID card number of the member
	 * 
	 * @return true or false if a member is in the system
	 */
	public boolean inSystem(String memberID) {
		// If the user can't find the member database, assume it doesn't exist, and
		// return false
		if (!file.exists()) {
			return false;
		}

		// else the file does exist, and therefore we will search through every number
		// until
		else {
			try {
				Scanner fileScanner = new Scanner(file);
				fileScanner.useDelimiter(",");

				while (fileScanner.hasNext()) {
					String value = fileScanner.next();
					if (value.equals(memberID)) {
						fileScanner.close();
						return true;
					}
				}
				fileScanner.close();
				return false;
			} catch (FileNotFoundException e) {
				return false;
			}
		}

	}

	// Ensure that AddMember is called before making an instance of a "Member"
	// object!!
	/**
	 * 
	 * @param memberID to add a member to a database
	 * @return returns the success of the add as a boolean
	 */
	public boolean AddMember(String memberID) {
		isValidMembership(memberID);
		if (!file.exists()) {
			try {
				PrintWriter writer = new PrintWriter(file);
				writer.println(memberID + ",");
				writer.close();
				return true;
			} catch (FileNotFoundException e) {
			}
		}

		else if (file.exists()) {
			try {
				// if the ID is already in the system just return false and don't add them
				// again.
				if (inSystem(memberID) == true) {
					return false;
				}
				FileWriter fileWriter = new FileWriter(file, true);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				printWriter.print(memberID + ",");
				printWriter.close();
				fileWriter.close();
				return true;
			} catch (IOException e) {
			}
		}

		// Issue has occurred if we're here, this line shouldn't occur
		return false;
	}

	// this is for testing purposes, it resets the contents of the file
	// in order to test.
	public void clearDatabase() {
		try {
			PrintWriter writer = new PrintWriter("memberData.txt");
			writer.write("");
			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Checks whether the current number is valid
	 * 
	 * @param cardNumber membership card number
	 * 
	 * @return boolean true if card number is valid. false otherwise.
	 */
	public boolean isValidMembership(String cardNumber) throws IllegalDigitException {
		if (cardNumber == null || cardNumber.length() != 12) {
			throw new IllegalDigitException("the membership number should be 12 digits long");
		}
		for (int i = 0; i < cardNumber.length(); i++) {
			char c = cardNumber.charAt(i);
			if (!Character.isDigit(c)) {
				throw new IllegalDigitException("The membership number should only contain digits between 0-9");
			}
		}
		return true;
	}

	// performs the action of swiping a membership card
	/**
	 * 
	 * @param memCard   the membership card
	 * @param signature the buffered image signature
	 */
	public void swipeMembershipCard(MembershipCard memCard, BufferedImage signature) {
		this.cardSwiped = false;
		// this should swipe the card until the swipe succeeds.
		while (cardSwiped == false) {
			try {
				logicStation.getSelfCheckoutStation().cardReader.swipe(memCard, signature);
				cardSwiped = true;
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCardInsertedEvent(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCardRemovedEvent(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCardTappedEvent(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCardSwipedEvent(CardReader reader) {
		cardReader = reader;
	}

	/**
	 * Reads the card data and verifies whether the card data is valid and the
	 * member is valid.
	 * 
	 * If the card is not a membership it notifies the customer then returns.
	 * 
	 * @param reader the CardReader
	 * @param data   the membership card data
	 * 
	 */
	@Override
	public void reactToCardDataReadEvent(CardReader reader, CardData data) {
		this.cardData = data;
		setCardDataToLowerCase(data.getType());

		if (!getCardDataToLowerCase().equals("membership")) {
			return;
		} else {
			isValidMembership(data.getNumber());
			if (inSystem(data.getNumber()) == true) {
				if (data.getClass() == CardSwipeData.class) {
					setID(data.getNumber());
					if (EnterMembershipPanel.inputArea() == false) {
						EnterMembershipPanel.sendMembershipToGUI(GetId());
					}
					setIsSwipeFoundInDatabase(true);
					endMembershipEntering();
				}
			}
		}
	}

	public void setCardDataToLowerCase(String dataType) {
		this.dataType = dataType.toLowerCase();
	}

	public String getCardDataToLowerCase() {
		return dataType;
	}

	public void setIsSwipeFoundInDatabase(boolean swipeFound) {
		this.isSwipeFoundInDatabase = swipeFound;
	}

	public boolean getIsSwipeFoundInDatabase() {
		return this.isSwipeFoundInDatabase;
	}

	// disables the card reader once entering membership is done
	public void endMembershipEntering() {
		logicStation.getSelfCheckoutStation().cardReader.disable();
	}

	/**
	 * Event that is called when a barcode is scanned by a scanner device, event is
	 * notified to all registered observers of this type.
	 * 
	 * @param barcodeScanner the barcode scanner
	 * @param barcode        the barcode of the item being scanned
	 */
	@Override
	public void reactToBarcodeScannedEvent(BarcodeScanner barcodeScanner, Barcode barcode) {
		// checks if the barcode is not the barcode for a membership card and just
		// returns if so.
		if (inSystem(barcode.toString()) == false) {
			return;
		}

		// if the scanner is disabled then nothing should happen.
		if (barcodeScanner.isDisabled())
			return;
		// suspends station from taking any further actions.
		logicStation.suspend();
		isScanFoundInDatabase = false;
		while (!isScanFoundInDatabase) {
			if (inSystem(barcode.toString()) == true) {
				this.isScanFoundInDatabase = true;
				setIsScanFoundInDatabase(isScanFoundInDatabase);
				setID(barcode.toString());
				if (EnterMembershipPanel.inputArea() == false) {
					EnterMembershipPanel.sendMembershipToGUI(GetId());
				}
				logicStation.unsuspend();

			} else {
				this.isScanFoundInDatabase = false;
			}
		}
	}

	// sets the variable isScanFoundInDatabase
	/**
	 * @param isScanFound boolean for whether scan is found
	 */
	public void setIsScanFoundInDatabase(boolean isScanFound) {
		this.isScanFoundInDatabase = isScanFound;
	}

	// gets the variable isScanFoundInDatabase
	/**
	 * 
	 * @return boolean isScanFoundInDatabase
	 */
	public boolean getIsScanFoundInDatabase() {
		return this.isScanFoundInDatabase;
	}
}
