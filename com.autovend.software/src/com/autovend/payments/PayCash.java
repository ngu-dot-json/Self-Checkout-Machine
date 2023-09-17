/**
 *
 * SENG Iteration 3 P3-3 | PayCash.java
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

package com.autovend.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.Order;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.BillStorage;
import com.autovend.devices.BillValidator;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.CoinSlot;
import com.autovend.devices.CoinStorage;
import com.autovend.devices.CoinTray;
import com.autovend.devices.CoinValidator;
import com.autovend.devices.DisabledException;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.BillDispenserObserver;
import com.autovend.devices.observers.BillStorageObserver;
import com.autovend.devices.observers.BillValidatorObserver;
import com.autovend.devices.observers.CoinDispenserObserver;
import com.autovend.devices.observers.CoinSlotObserver;
import com.autovend.devices.observers.CoinStorageObserver;
import com.autovend.devices.observers.CoinTrayObserver;
import com.autovend.devices.observers.CoinValidatorObserver;

/**
 * The controller class for cash payment. This class is instantiated by the
 * SelfCheckoutStationLogic class, and then enables the station's billInput slot
 * when the "pay()" method is called. From there, the instantiated object will
 * wait for a reactToValidBillDetectedEvent from the BillValidatorObserver it
 * implements. Once this happens it will record the value of that bill as the
 * payment being processed. This payment is then processed once the
 * reactToBillAddedEvent or the reactToBillsFullEvent from the
 * BillStorageObserver are called, which indicate that the payment successfully
 * entered the bill storage. This class assumes that the
 * reactToValidBillDetectedEvent will not be called again until a bill has made
 * it to storage and one of reactToBillAddedEvent or reactToBillsFullEvent have
 * been called.
 * 
 * <p>
 * Implements {@link com.autovend.devices.observers.BillValidatorObserver
 * BillValidatorObserver} and
 * {@link com.autovend.devices.observers.BillStorageObserver
 * BillStorageObserver}.
 * </p>
 */
public final class PayCash extends AbstractPay
		implements BillValidatorObserver, BillStorageObserver, BillDispenserObserver, CoinSlotObserver,
		CoinValidatorObserver, CoinTrayObserver, CoinStorageObserver, CoinDispenserObserver {

	public boolean coinsLow;
	public boolean suspendLowCoins;

	/**
	 * Creates a PayCash controller.
	 * 
	 * @param stationLogic The SelfCheckoutStationLogic for the station to which
	 *                     this controller belongs.
	 */
	public PayCash(SelfCheckoutStationLogic stationLogic) {
		super(stationLogic);
		coinsLow = false;
		suspendLowCoins = false;
	}

	/**
	 * Activate the cash payment process by activating billInput slot and CoinSlot
	 * and sending customer a message.
	 */
	public BigDecimal pay(Order order) {

		stationLogic.getSelfCheckoutStation().billInput.enable();
		stationLogic.getSelfCheckoutStation().coinSlot.enable();
		sendCustomerMessage(order);
		return order.getTotalDue();
	}

	/**
	 * Sends out messages about payment progress. If the customer has not paid in
	 * full yet, they are prompted to insert a bill, otherwise they are told their
	 * change. If totalDue is equal to 0, the user is still told their change due is
	 * 0.
	 * 
	 * @param order The Order for which to send a message about.
	 */
	protected void sendCustomerMessage(Order order) {
		BigDecimal totalDue = order.getTotalDue();

		if ((totalDue.compareTo(BigDecimal.ZERO) > 0)) {
			updateAmountDueIO(order);
			stationLogic.notifyCustomerIO("Insert Cash");
		} else {
			stationLogic.notifyCustomerIO("Change: %.2f".formatted(Math.abs(totalDue.floatValue())));
		}
	}

	/**
	 * Processes the currentProcessingPayment, notifies the customer of the
	 * remaining total due or the change they are owed. Then, calls to return the
	 * change if needed, before printing the receipt.
	 * 
	 * @param order The Order for which to process the payment.
	 */
	public void processPayment(Order order) {
		order.addPayment(currentProcessingPayment);
		currentProcessingPayment = BigDecimal.ZERO;
		sendCustomerMessage(order);

		System.out.println(order.getTotalDue());

		// If the full cost of the order has been paid, print the receipt, or if
		// change is due, return the customer's change, then print the receipt
		if (order.getTotalDue().compareTo(BigDecimal.ZERO) == 0) {
			endPayment();
			sendPrintSignal();
		} else if (order.getTotalDue().compareTo(BigDecimal.ZERO) < 0) {
			// Signal to cash I/O the amount of change due. Print receipt from there.
			endPayment();

			try {
				sendOutChange(order.getTotalDue().abs());
			} catch (SimulationException | DisabledException | OverloadException | EmptyException e) {
			}
			sendPrintSignal();
		}
	}

	/**
	 * This function will call the print function within the stationLogic class, and
	 * catch any potential errors.
	 * 
	 * @throws UnpaidTotalException
	 */
	public void sendPrintSignal() {
		try {
			stationLogic.printReceipt();
		} catch (UnpaidTotalException e) {
			// Should never happen
			e.printStackTrace();
		}
	}

	/**
	 * When a bill is detected, this payment processing function will be called to
	 * set the bill's value to our currentProcessingPayment int. This payment will
	 * be subtracted from the order later when the bill reaches storage and calls
	 * reactToBillAddedEvent/reactToBillsFullEvent.
	 * 
	 * @param validator The BillValidator device that emitted this event.
	 * @param currency  The Currency of the bill being processed.
	 * @param value     The value denomination of the bill that has been detected.
	 */
	public void paymentInProcessing(BillValidator validator, Currency currency, int value) {
		currentProcessingPayment = BigDecimal.valueOf(value);
	}

	public void paymentInProcessing(CoinValidator validator, BigDecimal value) {
		currentProcessingPayment = value;
	}

	/**
	 * Manages sending out change through coin and bill Dispensers.
	 * 
	 * @param changeDue The amount in change to be returned to the customer.
	 * @throws SimulationException
	 * @throws DisabledException
	 * @throws EmptyException
	 * @throws OverloadException
	 */
	public void sendOutChange(BigDecimal changeDue)
			throws SimulationException, OverloadException, DisabledException, EmptyException {
		SelfCheckoutStation station = stationLogic.getSelfCheckoutStation();

		station.billOutput.enable();
		station.coinSlot.enable();
		// sort bill/coin denoms
		int[] billDenominations = station.billDenominations.clone();
		Arrays.sort(billDenominations);
		List<BigDecimal> cDenominations = new ArrayList<BigDecimal>();
		cDenominations.addAll(station.coinDenominations);
		Collections.sort(cDenominations);
		int largestCoinIndex = cDenominations.size() - 1;
		int largestBillIndex = billDenominations.length - 1;
		Map<Integer, BillDispenser> billDispensers = station.billDispensers;
		Map<BigDecimal, CoinDispenser> coinDispensers = station.coinDispensers;

		// while changeDue > 0 and there are still bills to iterate through
		while (changeDue.compareTo(BigDecimal.ZERO) > 0 && largestBillIndex >= 0) {
			// get the largest untested billDispenser
			BillDispenser billDispenser = billDispensers.get(billDenominations[largestBillIndex]);

			// if billDispenser is not empty, and changeDue is less than value of bill
			if ((changeDue.compareTo(new BigDecimal(billDenominations[largestBillIndex]))) >= 0
					&& billDispenser.size() > 0) {
				// subtract from changeDue bill value
				changeDue = changeDue.subtract(new BigDecimal(billDenominations[largestBillIndex]));
				// emit bill
				billDispenser.emit();
			} else {
				// if billDispenser capacity is below threshold
				if (billDispenser.size() < stationLogic.getBillThreshold()) {
					// system react low
					stationLogic.setSuspendNext(true);
					// signal to attendant
					stationLogic.notifyAttendantIO("Bill levels low for " + billDenominations[largestBillIndex]
							+ " bills. Station will be suspended after session.");
				}
				// decrement bill index, repeat
				largestBillIndex--;
			}
		}

		while (changeDue.compareTo(BigDecimal.ZERO) > 0 && largestCoinIndex >= 0) {
			CoinDispenser coinDispenser = coinDispensers.get(cDenominations.get(largestCoinIndex));

			if ((changeDue.compareTo((cDenominations.get(largestCoinIndex)))) >= 0 && coinDispenser.size() > 0) {
				changeDue = changeDue.subtract(cDenominations.get(largestCoinIndex));
				coinDispenser.emit();
			} else {
				// if coinDispenser capacity is below threshold
				if (coinDispenser.size() < stationLogic.getCoinThreshold()) {
					// system react low
					stationLogic.setSuspendNext(true);
					// signal to attendant
					stationLogic.notifyAttendantIO("Coin levels low for " + cDenominations.get(largestCoinIndex)
							+ " coins. Station will be suspended after session.");
				}
				// decrement coin index, repeat
				largestCoinIndex--;
			}
		}

		// if customer is still owed change
		if (changeDue.compareTo(BigDecimal.ZERO) > 0) {
			// alert attendant station with amount
			stationLogic.notifyAttendantIO("Insufficient change avaliable. Customer is still owed " + changeDue
					+ " in change. Station will be suspended after session.");
			// suspend station
			stationLogic.setSuspendNext(true);
		}

		station.billOutput.disable();
		station.coinSlot.disable();
	}

	/**
	 * This function is called once the order's totalDue has been paid and the
	 * billInput Slot and CoinSlot can be disabled.
	 */
	public void endPayment() {
		currentProcessingPayment = BigDecimal.ZERO;
		stationLogic.getSelfCheckoutStation().billInput.disable();
		stationLogic.getSelfCheckoutStation().coinSlot.disable();

		if (stationLogic.getCustomerScreen() == false) {
			stationLogic.customerScreen.change("thankYouPanel");
		}

		// if station is marked for suspension
		if (stationLogic.getSuspendNext()) {
			// suspend, set variable to false
			stationLogic.suspend();
			stationLogic.setSuspendNext(false);
		}
	}

	/**
	 * Called when valid bill is entered into bill slot. Sets paymentMethod to cash,
	 * and loads the inserted bill as the payment being processed.
	 * 
	 * @param validator The BillValidator device that emitted this event.
	 * @param currency  The Currency of the bill that has been detected.
	 * @param value     The value denomination of the bill that has been detected.
	 */
	@Override
	public void reactToValidBillDetectedEvent(BillValidator validator, Currency currency, int value) {
		paymentInProcessing(validator, currency, value);
	}

	/**
	 * This event is called when the inserted bill is not valid, meaning either the
	 * bills currency or denomination are unsupported, or their was a false
	 * rejection. This method sends a message to notify the customer of the issue.
	 * 
	 * @param validator The BillValidator device that emitted this event.
	 */
	@Override
	public void reactToInvalidBillDetectedEvent(BillValidator validator) {
		stationLogic.notifyCustomerIO("Please insert a valid bill.");
	}

	@Override
	public void reactToValidCoinDetectedEvent(CoinValidator validator, BigDecimal value) {
		System.out.println("valid coin found. amount: " + value.toString());
		paymentInProcessing(validator, value);
	}

	/**
	 * Called when bill makes it into billStorage. Not called if storage is full.
	 * Will record the amount of money in processing and call processPayment()
	 * method again to update Customer I/O.
	 * 
	 * @param unit The BillStorage device that emitted this event.
	 */
	@Override
	public void reactToBillAddedEvent(BillStorage unit) {
		Order order = stationLogic.getOrder();
		processPayment(order);
	}

	/**
	 * Called when coin makes it into CoinStorage. Not called if storage is full.
	 * Will record the amount of money in processing and call processPayment()
	 * method again to update Customer I/O.
	 * 
	 * @param unit The CoinStorage device that emitted this event.
	 */
	@Override
	public void reactToCoinAddedEvent(CoinDispenser dispenser, Coin coin) {
		System.out.println("wrong");
		coinsLow = false;
		stationLogic.unsuspend();
		Order order = stationLogic.getOrder();
		processPayment(order);
	}

	@Override
	public void reactToCoinAddedEvent(CoinStorage unit) {
		System.out.println("right");
		Order order = stationLogic.getOrder();
		processPayment(order);
	}

	/**
	 * This event is called if the successful addition of a bill causes the storage
	 * to be full.
	 * 
	 * Future iterations may include a call to the attendant.
	 */
	@Override
	public void reactToBillsFullEvent(BillStorage unit) {
		// reactToBillAddedEvent(unit);
		stationLogic.notifyAttendantIO("Bill Storage Filled");
	}

	@Override
	public void reactToCoinsFullEvent(CoinStorage unit) {
		reactToCoinAddedEvent(unit);
		stationLogic.notifyAttendantIO("Coin Storage Filled");
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void reactToBillsLoadedEvent(BillStorage unit) {
	}

	@Override
	public void reactToBillsUnloadedEvent(BillStorage unit) {
	}

	@Override
	public void reactToCoinsFullEvent(CoinDispenser dispenser) {
		stationLogic.notifyAttendantIO("Coins Dispenser is full.");
	}

	@Override
	public void reactToCoinsEmptyEvent(CoinDispenser dispenser) {
		stationLogic.notifyAttendantIO("Coins Dispenser is empty.");
	}

	@Override
	public void reactToCoinRemovedEvent(CoinDispenser dispenser, Coin coin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCoinsLoadedEvent(CoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCoinsUnloadedEvent(CoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCoinsLoadedEvent(CoinStorage unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCoinsUnloadedEvent(CoinStorage unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCoinAddedEvent(CoinTray tray) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToInvalidCoinDetectedEvent(CoinValidator validator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToCoinInsertedEvent(CoinSlot slot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToBillsFullEvent(BillDispenser dispenser) {
		// TODO Auto-generated method stub
		stationLogic.notifyAttendantIO("Bills Dispenser is full.");
	}

	@Override
	public void reactToBillsEmptyEvent(BillDispenser dispenser) {
		stationLogic.notifyAttendantIO("Bills Dispenser is empty.");
	}

	@Override
	public void reactToBillAddedEvent(BillDispenser dispenser, Bill bill) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reactToBillRemovedEvent(BillDispenser dispenser, Bill bill) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reactToBillsLoadedEvent(BillDispenser dispenser, Bill... bills) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reactToBillsUnloadedEvent(BillDispenser dispenser, Bill... bills) {
		// TODO Auto-generated method stub
	}

}
