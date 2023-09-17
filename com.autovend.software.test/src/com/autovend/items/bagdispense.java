/**
 *
 * SENG Iteration 3 P3-3 | bagdispense.java
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

package com.autovend.items;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.*;

import com.autovend.*;
import com.autovend.devices.*;
import com.autovend.devices.observers.*;

public class bagdispense {
	private SelfCheckoutStation station;
	private SelfCheckoutStationLogic selfCheckLogic;
	private SupervisionStation attend;
	private SupervisionStationLogic supervise;
	private Currency currency;

	private BaggingAreaScaleController scaleController;
	private ElectronicScale elecScale;
	private ScanningScale scanScale;

	private int[] billDenoms;
	private BigDecimal[] coinDenoms;
	private int maxWeight;
	private int sensitivity;
	private RemoveItemController remove;

	public ReusableBag Bag;
	public ReusableBagDisObserver dispense;
	public ReusableBagDispenser dispenser;
	public ReusableBagDispenserObserver observe;
	
	@Before
	public void setup() {
		currency = Currency.getInstance("CAD");
		billDenoms = new int[] {5, 10, 20, 50, 100};
		coinDenoms = new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"),
				new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};
		maxWeight = 100;
		sensitivity = 1;

		station = new SelfCheckoutStation(currency, billDenoms, coinDenoms, maxWeight, sensitivity);
		selfCheckLogic = new SelfCheckoutStationLogic(station);
		attend = new SupervisionStation();
		supervise = new SupervisionStationLogic(attend);
		supervise.getSupervisionStation().screen.disable();
		supervise.getSupervisionStation().screen.enable();
		selfCheckLogic.setSupervisor(supervise);
		supervise.supervise(selfCheckLogic);
		remove = new RemoveItemController(selfCheckLogic);
		scaleController = new BaggingAreaScaleController(remove);
		scanScale = new ScanningScale();
		elecScale = new ElectronicScale(5000, 1);
		Bag = new ReusableBag();
		dispense = new ReusableBagDisObserver(selfCheckLogic, scaleController, scanScale, Bag);
		dispenser = new ReusableBagDispenser(1000);
		dispenser.register(dispense);
		dispenser.enable();
	}
	@Test
	public void dispenseemptybag() {
		try {
			dispenser.dispense();
		} catch (EmptyException e) {
			// TODO Auto-generated catch block
			assertFalse(dispense.areBagsEmpty());
		}
	}
	@Test
	public void loadbags() {
		try {
			dispenser.load(Bag);
			assertTrue(dispense.areBagsEmpty());
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void dispensebag() {
		try {
			selfCheckLogic.startOrder();
			dispenser.load(Bag);
			dispenser.dispense();
			dispense.addToOrder(Bag.getWeight());
		} catch (OverloadException | EmptyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void dispensebadbag() {
		try {
			selfCheckLogic.startOrder();
			dispenser.load(Bag);
			dispenser.dispense();
			dispense.addToOrder(3);
		} catch (OverloadException | EmptyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void dispensebagnobagging() {
		try {
			selfCheckLogic.startOrder();
			dispenser.load(Bag);
			dispenser.dispense();
			dispense.addToOrderNoBaggingArea();
		} catch (OverloadException | EmptyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@After
	public void ded() {
		this.supervise.stopSupervising(selfCheckLogic);
		this.supervise = null;
		this.currency = null;
		this.billDenoms = null;
		this.coinDenoms = null;
		this.maxWeight = 0;
		this.sensitivity = 0;
		this.station = null;
		this.selfCheckLogic = null;
		this.scaleController = null;
		this.elecScale = null;
	}
}
