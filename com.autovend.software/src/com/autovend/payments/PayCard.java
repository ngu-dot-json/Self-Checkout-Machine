/**
 *
 * SENG Iteration 3 P3-3 | PayCard.java
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
import java.util.concurrent.atomic.AtomicBoolean;

import com.autovend.Card.CardData;
import com.autovend.MethodOfPayment;
import com.autovend.Order;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.CardReader;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.CardReaderObserver;
import com.autovend.external.CardIssuer;

public final class PayCard extends AbstractPay implements CardReaderObserver {

	private CardReader currentReader;
	private CardData cardData;
	private CardIssuer cardIssuer;
	private MethodOfPayment method;
	private SelfCheckoutStation station;
	private String dataType;
	public boolean cardPaymentChecker;

	public PayCard(SelfCheckoutStationLogic stationLogic) {
		super(stationLogic);
		cardPaymentChecker = true;
	}

	public void pay(Order order, MethodOfPayment methodIn, CardIssuer issuer) {
		method = methodIn;
		cardIssuer = issuer;
		stationLogic.getSelfCheckoutStation().cardReader.enable();
		stationLogic.getSelfCheckoutStation().cardReader.register(this);
		sendCustomerMessage(order);
	}

//	Currently there is no difference in how swipe, tap, and insertions are handled 
//	on the software side. The hardware side has some differences, like inserted cards 
//	also request a pin which is tested on the hardware side.
	@Override
	public void reactToCardInsertedEvent(CardReader reader) {
		// was inserted
		currentReader = reader;
	}

	@Override
	public void reactToCardTappedEvent(CardReader reader) {
		// Was tapped
		currentReader = reader;
	}

	@Override
	public void reactToCardSwipedEvent(CardReader reader) {
		// Was swiped
		currentReader = reader;
	}

	/**
	 * When a card's data is successfully read by any of the methods, this is
	 * called.
	 * 
	 * this also includes a portion for when the card called is a membership card.
	 * it returns so the other reactToCardDataReadEvent for membership can be called
	 * 
	 */
	@Override
	public void reactToCardDataReadEvent(CardReader reader, CardData data) {
		if (cardPaymentChecker) {
			setCardDataToLowerCase(data.getType());
			if (!getCardDataToLowerCase().equals("credit") && !getCardDataToLowerCase().equals("debit")) {
				return;
			}
			BigDecimal payment = stationLogic.getOrder().getTotalDue();
			cardData = data;
			int holdNumber = cardIssuer.authorizeHold(cardData.getNumber(), payment);
			if (holdNumber < 0) {
				stationLogic.notifyCustomerIO("Card declined.");
				endPayment();
				return;
			}
//			With this code, the post transaction status will be attempted up to 5 times, and cut off if it goes over 20 seconds. 
			for (int i = 0; i < 5; i++) {
				AtomicBoolean transactionStatus = new AtomicBoolean(false);

				Thread t = new Thread(() -> {
					long startTime = System.currentTimeMillis();
					boolean result = cardIssuer.postTransaction(cardData.getNumber(), holdNumber, payment);
					long endTime = System.currentTimeMillis();
					long elapsedTime = endTime - startTime;

//					   This code currently never runs
					if (elapsedTime > 20000) { // 20,000 milliseconds = 20 seconds
						Thread.currentThread().interrupt(); // Terminate the thread if the function takes longer than 20
															// seconds
					} else {
//						   System.out.println(elapsedTime);
						transactionStatus.set(result);
					}
				});

				t.start();

				try {

					t.join(); // Wait for the thread to finish executing or get interrupted
//					   System.out.println(transactionStatus.get());
					if (transactionStatus.get()) {
						System.out.println("The payment is done");
						stationLogic.getOrder().addPayment(payment);
						endPayment();
						cardPaymentChecker = false;
						sendCustomerMessage(stationLogic.getOrder());

						return;
					}
				} catch (InterruptedException e) {

				}

			}
//			If the Bank determines that the transaction should not be authorized, the system will be informed
//			and it will not reduce the remaining amount due.
			cardIssuer.releaseHold(cardData.getNumber(), holdNumber);
			endPayment();
		}

	}

	public void setCardDataToLowerCase(String dataType) {
		this.dataType = dataType.toLowerCase();
	}

	public String getCardDataToLowerCase() {
		return dataType;
	}

	@Override
	public void endPayment() {
		stationLogic.getSelfCheckoutStation().cardReader.disable();

		if (stationLogic.getCustomerScreen() == false) {
			stationLogic.customerScreen.change("thankYouPanel");
		}
	}

	protected void sendCustomerMessage(Order order) {
		BigDecimal totalDue = order.getTotalDue();

		if ((totalDue.compareTo(BigDecimal.ZERO) > 0)) {
			updateAmountDueIO(order);
			if (method == MethodOfPayment.CREDIT) {
				if (stationLogic.getCustomerScreen() == false) {
					stationLogic.customerScreen.paymentPanel.sendCustomerMessagePayment("Insert/Tap/Swipe Credit Card");
				}
			} else {
				if (stationLogic.getCustomerScreen() == false) {
					stationLogic.customerScreen.paymentPanel.sendCustomerMessagePayment("Insert/Tap/Swipe Dedit Card");
				}
			}
		} else {
			stationLogic.notifyCustomerIO("Transaction Complete");
		}
	}

	@Override
	public void reactToCardRemovedEvent(CardReader reader) {

	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {

	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {

	}

}
