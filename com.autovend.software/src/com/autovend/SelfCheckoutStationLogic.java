/**
 *
 * SENG Iteration 3 P3-3 | SelfCheckoutStationLogic.java
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

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.autovend.devices.BillDispenser;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.external.CardIssuer;
import com.autovend.items.AddItemByBrowsingController;
import com.autovend.items.AddItemPLUController;
import com.autovend.items.AddItemScanController;
import com.autovend.items.AddItemTextSearchController;
import com.autovend.items.AddOwnBag;
import com.autovend.items.BaggingAreaScaleController;
import com.autovend.items.RemoveItemController;
import com.autovend.items.ReusableBagDisObserver;
import com.autovend.items.ScanningScale;
import com.autovend.payments.PayCard;
import com.autovend.payments.PayCash;
import com.autovend.payments.PayWithGiftCard;
import com.autovend.payments.ReceiptPrinterController;
import com.autovend.payments.UnpaidTotalException;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.gui.CustomerScreen;
import com.autovend.software.gui.CustomerScreenController;

/**
 *
 * The main class for the software of the self-checkout station.
 * 
 */
public class SelfCheckoutStationLogic {
	// These are separate inputs for testing
	private String language;
	private String input;
	private String input2;
	String type;

	// To keep track of desired level of bank notes
	private int billThreshold;

	// To keep track of desired level of coins for change
	private int coinThreshold;

	private Membership member;
	/**
	 * The SelfCheckoutStation hardware to which this controller belongs.
	 */
	private SelfCheckoutStation station;

	/**
	 * The controller for the bagging area scale.
	 */
	private BaggingAreaScaleController baggingAreaController;

	private RemoveItemController removeItemController;

	/**
	 * The controller for the receipt printer.
	 */
	private ReceiptPrinterController printerController;

	/**
	 * The controller for the primary and handheld barcode scanners.
	 */
	private AddItemScanController scannerController;
	private AddItemPLUController PLUController;
	private AddItemTextSearchController searchController;
	private AddItemByBrowsingController addByBrowseController;
	private AddOwnBag addOwnBagController;

	public ScanningScale scannerScaleController;

	/**
	 * The controller for cash payment.
	 */
	private PayCash payCashController;

	private PayCard payCardController;

	private PayWithGiftCard payGcController;

	private CardIssuer cardIssuer;

	public ReusableBagDisObserver rbdo;

	public CustomerScreenController customerScreenController;

	public CustomerScreen customerScreen;

	private SupervisionStationLogic supervisor;

	/**
	 * The current Order instance for this station.
	 */
	private Order order;

	/**
	 * True if this station is currently suspended from customer interactions,
	 * otherwise false.
	 */
	private boolean suspended = false;

	private boolean enabled = false;

	private boolean addByScanBool = false;

	private boolean overLoaded = false;

	private boolean orderStarted = false;

	// Set if bill/coins are below threshold, to suspend after transaction
	private boolean suspendNext = false;

	public BigDecimal priceOfBag;

	// This variable is used to prevent multiple card observers from reacting to the
	// same event.

	/**
	 * Creates the SelfCheckoutStationLogic.
	 * 
	 * @param stationLogic The self-checkout station for which to create the logic.
	 */
	public SelfCheckoutStationLogic(SelfCheckoutStation stationLogic) {
		this.setLanguage("english");
		this.station = stationLogic;

		removeItemController = new RemoveItemController(this);

		// Initialize and register BaggingAreaScaleController
		baggingAreaController = new BaggingAreaScaleController(removeItemController);
		station.baggingArea.register(baggingAreaController);

		scannerScaleController = new ScanningScale();
		station.scale.register(scannerScaleController);

		// Add Item Controllers
		PLUController = new AddItemPLUController(this, baggingAreaController, scannerScaleController);
		addByBrowseController = new AddItemByBrowsingController(this, baggingAreaController, scannerScaleController);
		searchController = new AddItemTextSearchController(this, baggingAreaController, scannerScaleController);
		addOwnBagController = new AddOwnBag(this, baggingAreaController, scannerScaleController);

		// Initialize and register ReceiptPrinterController
		printerController = new ReceiptPrinterController(this, station.printer);
		station.printer.register(printerController);

		// Initialize and register AddItemScanController
		scannerController = new AddItemScanController(this, baggingAreaController, scannerScaleController);
		station.mainScanner.register(scannerController);
		station.handheldScanner.register(scannerController);

		// Initialize and register PayCash controller
		payCashController = new PayCash(this);
		station.billValidator.register(payCashController);
		station.billStorage.register(payCashController);
		station.coinValidator.register(payCashController);
		station.coinStorage.register(payCashController);
		// station.coinDispenser.register(payCashController);
		for (CoinDispenser coinDispenser : station.coinDispensers.values())
			coinDispenser.register(payCashController);

		// Initialize and register PayCredit
		payCardController = new PayCard(this);
		station.cardReader.register(payCardController);

		// Initialize and register PayWithGiftCard
		payGcController = new PayWithGiftCard(this);
		station.cardReader.register(payGcController);

		// initialize and register membership card
		member = new Membership(this);
		station.cardReader.register(member);
		station.mainScanner.register(member);

		// Disable input devices that should not accept input by default
		station.billInput.disable();
		station.mainScanner.disable();
		station.handheldScanner.disable();

		customerScreenController = new CustomerScreenController(this, station);
		station.screen.register(customerScreenController);

		ReusableBag bag = new ReusableBag();
		rbdo = new ReusableBagDisObserver(this, baggingAreaController, scannerScaleController, bag);
		station.bagDispenser.register(rbdo);

		priceOfBag = BigDecimal.valueOf(0.50); // Default value of bag price is 50 cents

		this.cardIssuer = new CardIssuer("temp bank");

	}

	public void setBagPrice(BigDecimal price) {
		priceOfBag = price;
	}

	/**
	 * Creates a new order.
	 * 
	 * This will be triggered by an event from the CustomerI/O once implemented in
	 * hardware. Can be called directly for testing purposes for now.
	 */
	public void startOrder() {
		order = new Order(this);
		this.orderStarted = true;
	}

	/**
	 * Sets the attendant station that is supervising this self checkout station
	 * 
	 * @param supervisionStation Is the logic class in charge of a
	 *                           SupervisionStation
	 * 
	 */
	public void setSupervisor(SupervisionStationLogic supervisionStation) {
		this.supervisor = supervisionStation;
	}

	public boolean isOrderStarted() {
		return this.orderStarted;
	}

	/**
	 * Enables input devices for the addition of items to the order, and prompts the
	 * customer to add items.
	 * 
	 * @param method Is the enum that will be passed to describe the appropriate
	 *               methods and set up
	 * 
	 *               This will be triggered by an event from the CustomerI/O once
	 *               implemented in hardware. Can be called directly for testing
	 *               purposes for now.
	 */
	public void addItems(MethodOfAdd method) {
		if (isSuspended()) {
			return;
		}
		// DEFAULT IS ADD BY SCAN
		if (method == MethodOfAdd.TEXT) {

		} else if (method == MethodOfAdd.BROWSE) {

		} else if (method == MethodOfAdd.PLU) {

		} else {
			station.mainScanner.enable();
			station.handheldScanner.enable();
			notifyCustomerIO("Please scan your next item.");
		}

		// In future iterations, the customer will also have the option to add items
		// by other methods, in addition to the option to scan.

	}

	/**
	 * Prompts the customer to select a payment method, and then proceeds with
	 * payment via the chosen method.
	 * <p>
	 * Additional payment methods (card, crypto, coin) will be implemented in future
	 * iterations.
	 * </p>
	 * <p>
	 * This will be triggered by an event from the CustomerI/O once implemented in
	 * hardware. Can be called directly for testing purposes for now.
	 * </p>
	 */
	public void pay(MethodOfPayment method) throws PaymentMethodException {
		if (isSuspended()) {
			return;
		}
		if (method == MethodOfPayment.CASH) {
			payCashController.pay(order);
		} else {
			throw new PaymentMethodException();
		}
	}

	public void pay(MethodOfPayment method, CardIssuer cardIssuer) throws PaymentMethodException {
		if (method == MethodOfPayment.CREDIT) {
			payCardController.pay(order, method, cardIssuer);
		} else if (method == MethodOfPayment.DEBIT) {
			// Debit
			payCardController.pay(order, method, cardIssuer);
		} else if (method == MethodOfPayment.GIFTCARD) {
			// Gift Card
			payGcController.pay(order, method, cardIssuer);
		} else {
			// Cash should have been called
			pay(method);
		}
	}

	/**
	 * Attempts to print the receipt for the current order, if the order has been
	 * paid for in its entirety.
	 * 
	 * @throws PaymentMethodException Thrown if this method is called before the
	 *                                entire total cost of the order has been paid.
	 * @throws OverloadException      Should never happen. If thrown, see
	 *                                {@link com.autovend.payments.ReceiptPrinterController#generateReceiptString(Order)
	 *                                generateReceiptString()}.
	 * @throws UnpaidTotalException
	 */
	public void printReceipt() throws UnpaidTotalException {
		if (order.getTotalDue().compareTo(BigDecimal.ZERO) > 0) {
			throw new UnpaidTotalException();
		}

		printerController.print(order);
	}

	/**
	 * Displays a message to the Customer I/O.
	 * 
	 * @param message The message to be displayed to the customer.
	 */
	public void notifyCustomerIO(String message) {

		// Puts popup on Customer IO screen
		String language = this.getLanguage();
		System.out.println(message);
		if (language == "french") {
			JOptionPane.showMessageDialog(this.station.screen.getFrame(), message + "(in French)");
		} else {
			JOptionPane.showMessageDialog(this.station.screen.getFrame(), message);
		}

	}

	/**
	 * Displays a message to the Attendant I/O.
	 * 
	 * @param message The message to be displayed to the attendant.
	 */
	public void notifyAttendantIO(String message) {
		if (this.supervisor != null) {
			System.out.println(message);
			this.supervisor.notifyAttendant(message);
		}
	}

	/**
	 * Gets the current order for this station.
	 * 
	 * @return The Order instance for this station.
	 */
	public Order getOrder() {
		return this.order;
	}

	public void addToOrder(BarcodedProduct product) {
		order.add(product);

	}

	public void addToOrderPLU(PLUCodedProduct product, double weight) {
		order.addPLU(product, weight);
	}

	public void removePLUProduct(PLUCodedProduct plu, double weight) {
		order.removePLU(plu, weight);
	}

	public void removeBarcodedProduct(BarcodedProduct bprod) {
		order.removeBarcodedProduct(bprod);
	}

	public BigDecimal getOrderTotalDue() {
		return order.getTotalDue();
	}

	/**
	 * Gets the station object of this station.
	 * 
	 * @return The SelfCheckoutStation managed by this class.
	 */
	public SelfCheckoutStation getSelfCheckoutStation() {
		return station;
	}

	/**
	 * Gets the BaggingAreaScaleController object of this station.
	 * 
	 * @return The BaggingAreaScaleController managed by this class.
	 */

	public BaggingAreaScaleController getBaggingAreaScaleController() {
		return baggingAreaController;
	}

	/**
	 * Gets the ReceiptPrinterController object of this station.
	 * 
	 * @return The ReceiptPrinterController managed by this class.
	 */
	public ReceiptPrinterController getReceiptPrinterController() {
		return printerController;
	}

	/**
	 * Gets the AddItemScanController object of this station.
	 * 
	 * @return The AddItemScanController managed by this class.
	 */
	public AddItemScanController getAddItemScanController() {
		return scannerController;
	}

	/**
	 * Gets the AddItemPLUController object of this station.
	 * 
	 * @return The AddItemPLUController managed by this class.
	 */
	public AddItemPLUController getPLUController() {
		return PLUController;
	}

	/**
	 * Gets the AddOwnBag object of this station.
	 * 
	 * @return The AddOwnBag managed by this class.
	 */
	public AddOwnBag getAddOwnBagController() {
		return addOwnBagController;
	}

	/**
	 * Gets the AddItemPLUController object of this station.
	 * 
	 * @return The AddItemPLUController managed by this class.
	 */
	public AddItemTextSearchController getItemTextSearchController() {
		return searchController;
	}

	public RemoveItemController getRemoveItemController() {
		return removeItemController;
	}

	public AddItemByBrowsingController getAddByBrowseController() {
		return addByBrowseController;
	}

	/**
	 * Returns whether this self-checkout station is currently suspended from
	 * receiving customer input.
	 * 
	 * @return True if this station is suspended; false if this station is not
	 *         suspended.
	 */
	public boolean isSuspended() {
		return suspended;
	}

	/**
	 * Suspends this station from receiving customer input. Used when the station
	 * requires maintenance or other attendant action.
	 */
	public void suspend() {
		suspended = true;
		this.station.screen.getFrame().setEnabled(false);
	}

	/**
	 * Resumes this station, if it has been suspended and the required maintenance
	 * has been completed.
	 * 
	 * In future iterations, a more dedicated method will be available to deal with
	 * the "Permit Station Use" use case. For now, testing can call this directly.
	 */
	public void unsuspend() {
		suspended = false;
		this.station.screen.getFrame().setEnabled(true);
	}

	public double getExpectedPLUWeightForRemoval(PLUCodedProduct plu) {
		return order.getPLUWeightOfItem(plu);
	}

	/**
	 * 
	 * @param num the number of the member to be added
	 * @return returns true if successful to add to data base
	 */
	public boolean addMember(String num) {
		return member.AddMember(num);
	}

	/**
	 * 
	 * @param num the number to user to sign into the account
	 * @throws MembershipNotFoundException
	 */
	public void signIn(String num, MembershipCard card, MethodOfMembership method) throws MembershipNotFoundException {
		// whoever does scan add your code here
		if (method == MethodOfMembership.SCAN) {
		} else if (method == MethodOfMembership.SWIPE) {
		}
		// default for else is typing
		else {
			member.enterMembershipByTyping(num);
		}
	}

	// enter Membership number by typing
	public void enterMembershipNumberTyping(String num) throws MembershipNotFoundException {
		member.enterMembershipByTyping(num);
	}

	public Membership getMembershipInstance() {
		return member;
	}

	/**
	 * used for testing
	 */
	public String getInput() {
		return input;
	}

	/**
	 * used for testing
	 */
	public void setInput(String s) {
		input = s;
	}

	/**
	 * used for testing
	 */
	public String getInput2() {
		return input2;
	}

	/**
	 * used for testing
	 */
	public void setInput2(String s) {
		input2 = s;
	}

	public void scaleOverloaded() {
		overLoaded = true;

	}

	public void scaleOutOverloaded() {
		overLoaded = false;

	}

	public void setLanguage(String selectedLanguage) {
		this.language = selectedLanguage;
	}

	public String getLanguage() {
		return this.language;
	}

	// Set acceptable threshold for level of bank notes in dispenser
	public void setBillThreshold(int threshold) {
		billThreshold = threshold;
	}

	// Get current threshold for level of bank notes in dispenser
	public int getBillThreshold() {
		return billThreshold;
	}

	// Set acceptable threshold for level of coins in change dispenser
	public void setCoinThreshold(int threshold) {
		coinThreshold = threshold;
	}

	// Get current threshold for level of coins in change dispenser
	public int getCoinThreshold() {
		return coinThreshold;
	}

	// set boolean if machine is to be suspended after session (change related)
	public void setSuspendNext(boolean value) {
		suspendNext = value;
	}

	// get if machine is to be suspended
	public boolean getSuspendNext() {
		return suspendNext;
	}

	/**
	 * Function to allow attendant to refill bills if low.
	 * 
	 * @param bills ArrayList of bills to load
	 * @throws SimulationException If any of the bills are null
	 * @throws OverloadException   If loading a bill causes a dispenser to overload
	 */
	public void attendantRefillBills(ArrayList<Bill> bills) throws SimulationException, OverloadException {
		// for all bills in the array
		for (Bill bill : bills) {
			// get bill value
			int denom = bill.getValue();
			// get associated dispenser with that value
			BillDispenser dispenser = station.billDispensers.get(denom);
			// load bill into dispenser
			dispenser.load(bill);
		}
		// if station is suspended
		if (isSuspended()) {
			// once complete, unsuspend station
			unsuspend();
		}
	}

	/**
	 * Function to allow attendant to refill coins if low.
	 * 
	 * @param coins ArrayList of coins to load
	 * @throws SimulationException If any of the coins are null
	 * @throws OverloadException   If loading a coin causes a dispenser to overload
	 */
	public void attendantRefillCoins(ArrayList<Coin> coins) throws SimulationException, OverloadException {
		// for all coins in the array
		for (Coin coin : coins) {
			// get bill value
			BigDecimal denom = coin.getValue();
			// get associated dispenser with that value
			CoinDispenser dispenser = station.coinDispensers.get(denom);
			// load bill into dispenser
			dispenser.load(coin);
		}
		// if station is suspended
		if (isSuspended()) {
			// once complete, unsuspend station
			unsuspend();
		}
	}

	/**
	 * Method for start-up of self-checkout station instance. Attendant will allow
	 * this instance of selfcheckout station to start-up.
	 * 
	 **/
	public void startupStation() {
		// Disabling all the hardware of the selfCheckoutStaion.
		this.station.scale.enable();
		this.station.printer.enable();
		this.station.coinSlot.enable();
		this.station.coinTray.enable();
		this.station.billInput.enable();
		this.station.cardReader.enable();
		this.station.billOutput.enable();
		this.station.billStorage.enable();
		this.station.baggingArea.enable();
		this.station.mainScanner.enable();
		this.station.coinStorage.enable();
		this.station.billValidator.enable();
		this.station.coinValidator.enable();
		this.station.handheldScanner.enable();
		this.station.screen.enable();

		for (BillDispenser bDispenser : station.billDispensers.values()) {
			bDispenser.enable();
		}

		for (CoinDispenser cDispenser : station.coinDispensers.values()) {
			cDispenser.enable();
		}
		this.enabled = true;
	}

	/**
	 * Method for shutdown of self-checkout station instance. Attendant may force
	 * the shutdown of this selfchekout station, even if station is in active mode
	 * 
	 **/
	public void shutdownStation() {
		// Disabling all the hardware of the selfCheckoutStaion.
		this.station.scale.disable();
		this.station.printer.disable();
		this.station.coinSlot.disable();
		this.station.coinTray.disable();
		this.station.billInput.disable();
		this.station.cardReader.disable();
		this.station.billOutput.disable();
		this.station.billStorage.disable();
		this.station.baggingArea.disable();
		this.station.mainScanner.disable();
		this.station.coinStorage.disable();
		this.station.billValidator.disable();
		this.station.coinValidator.disable();
		this.station.handheldScanner.disable();
		this.station.screen.disable();

		for (BillDispenser bDispenser : station.billDispensers.values()) {
			bDispenser.disable();
		}

		for (CoinDispenser cDispenser : station.coinDispensers.values()) {
			cDispenser.disable();
		}
		this.enabled = false;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setCardIssuer(CardIssuer bank) {
		this.cardIssuer = bank;
	}

	public CardIssuer getCardIssuer() {
		return this.cardIssuer;
	}

	public boolean getCustomerScreen() {
		if (customerScreen == null) {
			return true;
		} else {
			return false;
		}
	}

	public PayCard getPayCard() {
		return payCardController;
	}

}
