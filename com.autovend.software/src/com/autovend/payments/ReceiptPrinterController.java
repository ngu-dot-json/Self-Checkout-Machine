/**
 *
 * SENG Iteration 3 P3-3 | ReceiptPrinterController.java
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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.autovend.Order;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.ReceiptPrinterObserver;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

/**
 * The class responsible for dealing with the printing of receipts in software.
 * <p>
 * Implements {@link com.autovend.devices.observers.ReceiptPrinterObserver
 * ReceiptPrinterObserver}.
 * </p>
 */
public class ReceiptPrinterController implements ReceiptPrinterObserver {
	private static final String header = "                   The Local Marketplace\n";
	private static final String footer = "                Thanks for shopping with us!";

	private final int LOW_PAPER_THRESHOLD = ReceiptPrinter.MAXIMUM_PAPER / 5;
	private final int LOW_INK_THRESHOLD = ReceiptPrinter.MAXIMUM_INK / 5; // The low ink threshold (20% of max)

	private SelfCheckoutStationLogic stationLogic;
	private ReceiptPrinter printer;
	private SelfCheckoutStation station;

	private int linesOfPaper = 0;
	private int charsOfInk = 0; // The amount of ink the in the station

	private boolean inkLow = false;
	private boolean paperLow = false;

	/**
	 * Creates a ReceiptPrinterController.
	 * 
	 * @param stationLogic The SelfCheckoutStationLogic for the station to which the
	 *                     printer belongs.
	 * @param printer      The ReceiptPrinter that this PrintReceipt controls.
	 */
	public ReceiptPrinterController(SelfCheckoutStationLogic stationLogic, ReceiptPrinter printer) {
		this.stationLogic = stationLogic;
		this.printer = printer;
		this.station = stationLogic.getSelfCheckoutStation();
	}

	/**
	 * Prints the receipt for the given Order.
	 * 
	 * @param order The Order for which to print a receipt.
	 * @throws OverloadException Should never happen. If thrown, see
	 *                           {@link #generateReceiptString(Order)
	 *                           generateReceiptString()}.
	 */
	public void print(Order order) {
		String receiptString = generateReceiptString(order);

		linesOfPaper -= receiptString.lines().count();

		for (int i = 0; i < receiptString.length(); i++) {
			if (stationLogic.isSuspended()) {
				break;
			}

			try {
				printer.print(receiptString.charAt(i));
			} catch (EmptyException e) {
				suspendForMaintenance(e.getMessage());
			} catch (OverloadException e) {
				// Should not happen, indicates error with generateReceiptString
				e.printStackTrace();
			}
		}

		// Check lines of paper left and notify attendant if threshold is exceeded

		if (linesOfPaper < LOW_PAPER_THRESHOLD) {

			stationLogic.notifyAttendantIO("Station requires maintenance: Low Paper. \n");

			stationLogic.suspend();
		}

		// Checking the amount of ink left
		if (charsOfInk < LOW_INK_THRESHOLD) {
			// Signal to AttendantIO (Step 1)
			stationLogic.notifyAttendantIO("Station requires maintenance: Low Ink. \n");

			// AttendantIO signals an acknowledgement (Step 2)

			// If a current session is in progress, wait until it is complete (Step 3)
			// We check check for low ink after the receipt is printed, so no session should
			// be in progress

			// Set the system to out of order by suspending the system (Step 4)
			stationLogic.suspend();
		}

	}

	/**
	 * Adds paper to the Receipt Printer and updates the linesOfPaper.
	 * 
	 * @param unit
	 * @throws OverloadException
	 */
	public void addPaper(int unit) throws OverloadException {
		this.printer.addPaper(unit);
		this.linesOfPaper += unit;

	}

	/**
	 * Adds ink to the Receipt Printer and updates the charsOfInk.
	 * 
	 * @param quantity
	 * @throws OverloadException
	 */
	public void addInk(int quantity) throws OverloadException {
		this.printer.addInk(quantity);
		this.charsOfInk += quantity;

	}

	/**
	 * Builds the string to be printed on the receipt.
	 * 
	 * @param order The order for which to generate the receipt string.
	 * @return The String to be printed as a receipt.
	 */
	public String generateReceiptString(Order order) {
		StringBuilder sb = new StringBuilder();
		List<Product> products = order.getProducts();

		sb.append(header);

		String description;
		String priceString;
		int whitespaceCount;
		// Loop over the products in the order and add them to the string
		for (Product product : products) {
			if (product.getClass() == BarcodedProduct.class) {
				// If its a BarcodedProduct, add its description, empty spaces until
				// the end of the line, then its unit price
				description = ((BarcodedProduct) product).getDescription();
				priceString = NumberFormat.getCurrencyInstance(Locale.CANADA).format(product.getPrice());

			} else {
				description = ((PLUCodedProduct) product).getDescription();
				double pluWeight;
				pluWeight = stationLogic.getOrder().getPLUWeightOfItem(((PLUCodedProduct) product));
				priceString = NumberFormat.getCurrencyInstance(Locale.CANADA)
						.format(product.getPrice().multiply(new BigDecimal(pluWeight / 1000)));

			}

			// trim description if too long
			if (description.length() + priceString.length() + 1 > ReceiptPrinter.CHARACTERS_PER_LINE) {
				description = description.substring(0, (ReceiptPrinter.CHARACTERS_PER_LINE - priceString.length() - 1));
			}

			// Finds the number of spaces needed to push the price to the end of the line
			whitespaceCount = ReceiptPrinter.CHARACTERS_PER_LINE - description.length() - priceString.length();

			// Add the entry for this product to the string
			sb.append(description + " ".repeat(whitespaceCount) + priceString + "\n");
		}

		sb.append(footer);

		return sb.toString();
	}

	/**
	 * Listener event, triggered by a reactToOutOfPaperEvent from the registered
	 * ReceiptPrinter device. <br>
	 * <br>
	 * Suspends the station use and notifies the attendant that maintenance is
	 * needed.
	 */
	@Override
	public void reactToOutOfPaperEvent(ReceiptPrinter printer) {
		suspendForMaintenance("There is no paper in the printer.");
		paperLow = true;
	}

	/**
	 * Listener event, triggered by a reactToOutOfInkEvent from the registered
	 * ReceiptPrinter device. <br>
	 * <br>
	 * Suspends the station use and notifies the attendant that maintenance is
	 * needed.
	 */
	@Override
	public void reactToOutOfInkEvent(ReceiptPrinter printer) {
		suspendForMaintenance("There is no ink in the printer.");
		inkLow = true;
	}

	/**
	 * Suspends the station and notifies the AttendantI/O that maintenance is
	 * required on the ReceiptPrinter, and that a duplicate receipt will need to be
	 * printed.
	 * 
	 * @param message The String message describing the issue with the printer.
	 */
	private void suspendForMaintenance(String message) {
		stationLogic.suspend();
		stationLogic.notifyAttendantIO("Station requires maintenance: \n" + message
				+ " A duplicate receipt must be printed after maintenance is complete.");
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void reactToPaperAddedEvent(ReceiptPrinter printer) {

		// Let AttendantIO and CustomerIO know that the paper has been refilled.
		stationLogic.notifyCustomerIO("Paper for the receipt printer has been refilled. \n");
		stationLogic.notifyAttendantIO("Paper for the receipt printer has been refilled. \n");
		paperLow = false;

		// Unsuspend the station and make it ready for operation again.
		stationLogic.unsuspend();
	}

	@Override
	public void reactToInkAddedEvent(ReceiptPrinter printer) {
		// Notify Customer and Attendant IO that the ink has been refilled
		stationLogic.notifyCustomerIO("Ink has been refilled \n");
		stationLogic.notifyAttendantIO("Ink has been refilled");
		inkLow = false;

		// Set customer IO status to in order by unsuspending the system (Step 6)
		stationLogic.unsuspend();
	}

	public boolean isInkLow() {
		return inkLow;
	}

	public boolean isPaperLow() {
		return paperLow;
	}
}
