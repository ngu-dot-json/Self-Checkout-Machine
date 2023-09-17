/**
 *
 * SENG Iteration 3 P3-3 | AbstractPay.java
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

import com.autovend.Order;
import com.autovend.SelfCheckoutStationLogic;

/**
 * The abstract base class for all payment method classes.
 */
public abstract class AbstractPay {
	protected BigDecimal currentProcessingPayment;
	protected SelfCheckoutStationLogic stationLogic;

	public AbstractPay(SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic;
	}

//	public abstract void paymentInProcessing(BillValidator validator, Currency currency, int value);

	public abstract void endPayment();

	/**
	 * Updates the CustomerI/O to reflect the new current outstanding amount due.
	 * <br>
	 * <br>
	 * In this iteration, simply calls the notifyCustomerIO method from station
	 * logic, which will be replaced by a proper call to the CustomerI/O once
	 * implemented in hardware.
	 * 
	 * @param order The order for which to update the displayed amount due.
	 */
	protected void updateAmountDueIO(Order order) {
		order.getStationLogic().notifyCustomerIO("Amount due: %.2f".formatted(order.getTotalDue()));
	}
}
