/**
 *
 * SENG Iteration 3 P3-3 | TestPayCash.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.*;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.Numeral;
import com.autovend.Order;
import com.autovend.PaymentMethodException;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.devices.*;
import com.autovend.devices.observers.BillDispenserObserver;
import com.autovend.devices.observers.BillSlotObserver;
import com.autovend.devices.observers.BillStorageObserver;
import com.autovend.devices.observers.BillValidatorObserver;
import com.autovend.devices.observers.CoinDispenserObserver;
import com.autovend.devices.observers.CoinSlotObserver;
import com.autovend.devices.observers.CoinStorageObserver;
import com.autovend.devices.observers.CoinTrayObserver;
import com.autovend.devices.observers.CoinValidatorObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.payments.PayCash;
import com.autovend.products.BarcodedProduct;
import com.autovend.MethodOfAdd;
import com.autovend.MethodOfPayment;

public class TestPayCash {
    public BillSlotObserver slotListener;
    public BillDispenserObserver dispenserListener;
    public BillStorageObserver storageListener;
    public BillValidatorObserver validatorListener;
    public CoinSlotObserver coinSlotListener;
    public CoinDispenserObserver coinDispenserListener;
    public CoinStorageObserver coinStorageListener;
    public CoinTrayObserver coinTrayListener;
    public CoinValidatorObserver coinValidatorListener;

    public int capacity;
    public int value;
    public Bill bill;

    public Currency currency;
    public int[] denominations;

    public List<BigDecimal> coinDenominations;
    public Coin coin;

    public BillSlot slotDevice;
    public BillDispenser dispenserDevice;
    public BillStorage storageDevice;
    public BillValidator validatorDevice;

    public CoinSlot coinSlotDevice;
    public CoinDispenser coinDispenserDevice;
    public CoinStorage coinStorageDevice;
    public CoinValidator coinValidatorDevice;

    private Order order;
    private PayCash payCash;
    private SelfCheckoutStation check;
    private SelfCheckoutStationLogic stationLogic;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private BarcodedProduct apple;
    private BarcodedUnit appleUnit;
    private BarcodedProduct banana;
    private BarcodedUnit bananaUnit;
    private BarcodedProduct laptop;
    private BarcodedUnit laptopUnit;
    private BarcodedProduct candy;
    private BarcodedUnit candyUnit;

    // Sets up test. Runs before every testing method.
    @Before
    public void setup() {

        // Set up hardware listeners
        capacity = 100;
        slotDevice = new BillSlot(false);
        dispenserDevice = new BillDispenser(capacity);
        storageDevice = new BillStorage(capacity);

        coinSlotDevice = new CoinSlot();
        coinDispenserDevice = new CoinDispenser(capacity);
        coinStorageDevice = new CoinStorage(capacity);

        currency = Currency.getInstance(Locale.CANADA);
        int[] denominations = { 5, 10, 20, 50, 100 };

        BigDecimal penny = new BigDecimal(0.01);
        BigDecimal nickel = new BigDecimal(0.05);
        BigDecimal dime = new BigDecimal(0.10);
        BigDecimal quarter = new BigDecimal(0.25);
        BigDecimal loonie = new BigDecimal(1.00);
        BigDecimal toonie = new BigDecimal(2.00);
        BigDecimal[] CoinDenominationsArray = { penny, nickel, dime, quarter, loonie, toonie };

        coinDenominations = new ArrayList<BigDecimal>();

        coinDenominations.add(penny);
        coinDenominations.add(nickel);
        coinDenominations.add(dime);
        coinDenominations.add(quarter);
        coinDenominations.add(loonie);
        coinDenominations.add(toonie);

        validatorDevice = new BillValidator(currency, denominations);
        coinValidatorDevice = new CoinValidator(currency, coinDenominations);

        int a = 50;
        int b = 1;
        check = new SelfCheckoutStation(currency, denominations, CoinDenominationsArray, a, b);
        stationLogic = new SelfCheckoutStationLogic(check);
        payCash = new PayCash(stationLogic);
        stationLogic.startOrder();

        String ap1 = "apple";
        BigDecimal p1 = BigDecimal.valueOf(5.0);
        double d1 = 5.0;
        Barcode bar1 = new Barcode(Numeral.one);
        apple = new BarcodedProduct(bar1, ap1, p1, d1);
        appleUnit = new BarcodedUnit(bar1, d1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bar1, apple);

        String ap2 = "banana";
        BigDecimal p2 = BigDecimal.valueOf(2.0);
        double d2 = 2.0;
        Barcode bar2 = new Barcode(Numeral.two);
        banana = new BarcodedProduct(bar2, ap2, p2, d2);
        bananaUnit = new BarcodedUnit(bar2, d2);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bar2, banana);

        String ap3 = "laptop";
        BigDecimal p3 = BigDecimal.valueOf(6000.0);
        double d3 = 10.0;
        Barcode bar3 = new Barcode(Numeral.three);
        laptop = new BarcodedProduct(bar3, ap3, p3, d3);
        laptopUnit = new BarcodedUnit(bar3, d3);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bar3, laptop);

        String ap4 = "candy";
        BigDecimal p4 = BigDecimal.valueOf(0.25);
        double d4 = 10.0;
        Barcode bar4 = new Barcode(Numeral.four);
        candy = new BarcodedProduct(bar4, ap4, p4, d4);
        candyUnit = new BarcodedUnit(bar4, d4);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bar4, candy);

        // Register listeners
        slotDevice.register(slotListener);
        dispenserDevice.register(dispenserListener);
        storageDevice.register(storageListener);
        validatorDevice.register(validatorListener);

        coinSlotDevice.register(coinSlotListener);
        coinDispenserDevice.register(coinDispenserListener);
        coinStorageDevice.register(coinStorageListener);
        coinValidatorDevice.register(coinValidatorListener);

        // Initialize the fields inside the listeners?

    }

    // Tear down. Runs after every test method.
    @After
    public void teardown() {
        value = 0;
        currency = null;
        capacity = 0;
        bill = null;
        denominations = null;

        coin = null;
        coinDenominations = null;
        check = null;
    }

    @Test
    // testing if the constructor fine
    public void testPay() {
        int[] denom = { 5, 10, 20, 50, 100 };
        BigDecimal penny = new BigDecimal(0.01);
        BigDecimal nickel = new BigDecimal(0.05);
        BigDecimal dime = new BigDecimal(0.10);
        BigDecimal quarter = new BigDecimal(0.25);
        BigDecimal loonie = new BigDecimal(1.00);
        BigDecimal toonie = new BigDecimal(2.00);
        BigDecimal[] CoinDenominationsArray = { penny, nickel, dime, quarter, loonie, toonie };

        int a = 50;
        int b = 1;
        check = new SelfCheckoutStation(currency, denom, CoinDenominationsArray, a, b);
        SelfCheckoutStationLogic stationLogic = new SelfCheckoutStationLogic(check);
        order = new Order(stationLogic);
        String ap1 = "apple";
        BigDecimal p1 = BigDecimal.ONE;
        double d1 = 1.0;

        Barcode bar1 = new Barcode(Numeral.one);
        BarcodedProduct apple = new BarcodedProduct(bar1, ap1, p1, d1);
        order.add(apple);
        String ap2 = "banana";
        BigDecimal p2 = BigDecimal.valueOf(2.0);
        double d2 = 1.0;
        Barcode bar2 = new Barcode(Numeral.one);
        BarcodedProduct banana = new BarcodedProduct(bar2, ap2, p2, d2);
        order.add(banana);
        order.add(candy);
        BigDecimal total = order.getTotal();
        int temp = 3;
        order.addPayment(new BigDecimal(3.00));
        order.addPayment(new BigDecimal(0.25));
        assertEquals(BigDecimal.valueOf(0).stripTrailingZeros(), payCash.pay(order).stripTrailingZeros());
    }

    // TEST TO SEE IF SENDCUSTOMER MESSAGE WORKS FINE WITH 0 CHANGE DUE(EXACT CASH)
    @Test
    public void testPayExactCashSendCustomerMessage() {
        order = stationLogic.getOrder();
        order.add(apple);
        order.add(banana);

        BigDecimal total = order.getTotal();
        PayCash payCash = new PayCash(stationLogic);
        payCash.pay(order);
        order.addPayment(5);
        order.addPayment(new BigDecimal(1.00));
        order.addPayment(new BigDecimal(1.00));
        System.setOut(new PrintStream(outContent));
        String expected1 = "Customer I/O: Change: 0.00";
        payCash.sendCustomerMessage(order);
        assertTrue(outContent.toString().contains(expected1));
    }

    // FUNCTION TO SEE IF PROCESSPAYEMENT WORKS FINE WHEN YOU SCAN ITEMS
    @Test
    public void testPayNotEnoughCashSendCustomerMessage() {
        order = stationLogic.getOrder();
        order.add(apple);
        order.add(banana);

        BigDecimal total = order.getTotal();
        PayCash payCash = new PayCash(stationLogic);
        payCash.pay(order);
        order.addPayment(5);
        System.setOut(new PrintStream(outContent));
        String expected1 = "Customer I/O: Amount due: 2.00";
        String expected2 = "Customer I/O: Insert Cash";
        payCash.sendCustomerMessage(order);
        assertTrue(outContent.toString().contains(expected1));
        assertTrue(outContent.toString().contains(expected2));
    }

    @Test
    public void testPromptForChange() {
        order = stationLogic.getOrder();
        order.add(apple);
        order.add(banana);

        BigDecimal total = order.getTotal();
        PayCash payCash = new PayCash(stationLogic);
        payCash.pay(order);
        order.addPayment(10);
        System.setOut(new PrintStream(outContent));
        String expected1 = "Customer I/O: Change: 3.00";
        payCash.sendCustomerMessage(order);
        assertTrue(outContent.toString().contains(expected1));
    }

    // TEST TO SEE IF SENDCUSTOMER MESSAGE WORKS PROPERLY WHEN YOU GIVE IF
    // INSUFFICIENT AMOUNT IE PROMPTS YOU TO INSERT MORE BILLS
    @Test
    public void testPromptMoreBill() {
        order = stationLogic.getOrder();
        order.add(apple);
        order.add(banana);

        BigDecimal total = order.getTotal();
        PayCash payCash = new PayCash(stationLogic);
        payCash.pay(order);
        order.addPayment(5);
        String expected1 = "Customer I/O: Amount due: 2.00";
        String expected2 = "Customer I/O: Insert Cash";
        System.setOut(new PrintStream(outContent));
        outContent.reset();
        payCash.sendCustomerMessage(order);
        assertTrue(outContent.toString().contains(expected1));
        assertTrue(outContent.toString().contains(expected2));
    }

    @Test
    public void TestPaymentSystemAmountDue() throws PaymentMethodException {
        stationLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(appleUnit)) {
                break;
            }
        }
        check.baggingArea.add(appleUnit);

        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(bananaUnit)) {
                break;
            }
        }
        check.baggingArea.add(bananaUnit);

        int aa = 5;
        Bill bill1 = new Bill(aa, currency);
        stationLogic.pay(MethodOfPayment.CASH);

        check.billInput.enable();
        try {
            check.billInput.accept(bill1);
        } catch (DisabledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OverloadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BigDecimal expected1 = BigDecimal.valueOf(2);

        assertEquals(expected1.stripTrailingZeros(), (stationLogic.getOrder().getTotalDue().stripTrailingZeros()));
    }

    @Test
    public void TestPaymentSystemWithExactCash() throws PaymentMethodException {
        stationLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(appleUnit)) {
                break;
            }
        }
        check.baggingArea.add(appleUnit);

        int aa = 5;
        Bill bill1 = new Bill(aa, currency);
        stationLogic.pay(MethodOfPayment.CASH);

        check.billInput.enable();
        try {
            check.billInput.accept(bill1);
        } catch (DisabledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OverloadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BigDecimal expected1 = BigDecimal.valueOf(0);

        assertEquals(expected1.stripTrailingZeros(), (stationLogic.getOrder().getTotalDue().stripTrailingZeros()));
    }

    @Test
    public void TestPaymentSystemWithChange() throws PaymentMethodException {
        stationLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(appleUnit)) {
                break;
            }
        }
        check.baggingArea.add(appleUnit);

        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(bananaUnit)) {
                break;
            }
        }
        check.baggingArea.add(bananaUnit);

        int aa = 10;
        Bill bill1 = new Bill(aa, currency);
        stationLogic.pay(MethodOfPayment.CASH);

        check.billInput.enable();
        try {
            check.billInput.accept(bill1);
        } catch (DisabledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OverloadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BigDecimal expected1 = BigDecimal.valueOf(-3);

        assertEquals(expected1.stripTrailingZeros(), (stationLogic.getOrder().getTotalDue().stripTrailingZeros()));
    }

    @Test
    public void TestPaymentSystemWithInvalidDenomination() throws PaymentMethodException {
        stationLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(appleUnit)) {
                break;
            }
        }
        check.baggingArea.add(appleUnit);

        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(bananaUnit)) {
                break;
            }
        }
        check.baggingArea.add(bananaUnit);

        int aa = 7;
        Bill bill1 = new Bill(aa, currency);
        stationLogic.pay(MethodOfPayment.CASH);

        System.setOut(new PrintStream(outContent));
        check.billInput.enable();
        try {
            check.billInput.accept(bill1);
        } catch (DisabledException e) {
            e.printStackTrace();
        } catch (OverloadException e) {
            e.printStackTrace();
        }

        String expected1 = "Customer I/O: Please insert a valid bill.";
        assertTrue(outContent.toString().contains(expected1));

    }

    @Test
    public void TestPaymentSystemWithBillsFullEvent() throws PaymentMethodException {
        stationLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(laptopUnit)) {
                break;
            }
        }
        check.baggingArea.add(laptopUnit);

        stationLogic.pay(MethodOfPayment.CASH);
        System.setOut(new PrintStream(outContent));
        check.billInput.enable();
        while (check.billStorage.hasSpace()) {
            try {
                outContent.reset();
                Bill bill1 = new Bill(5, currency);
                check.billInput.accept(bill1);
                check.billInput.removeDanglingBill();
            } catch (DisabledException e) {
                e.printStackTrace();
            } catch (OverloadException e) {
                e.printStackTrace();
            }
        }
        String expected1 = "Attendant I/O: Bill Storage Filled";
        assertTrue(outContent.toString().contains(expected1));
    }

    @Test
    public void TestSendPrintSignal() throws PaymentMethodException {
        PayCash payCash = new PayCash(stationLogic);

        stationLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(appleUnit)) {
                break;
            }
        }

        int aa = 5;
        Bill bill1 = new Bill(aa, currency);
        stationLogic.pay(MethodOfPayment.CASH);

        check.billInput.enable();
        try {
            check.billInput.accept(bill1);
        } catch (DisabledException e) {
            e.printStackTrace();
        } catch (OverloadException e) {
            e.printStackTrace();
        }

        try {
            payCash.sendPrintSignal();
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void TestSendPrintSignalUnpaidTotalException() throws PaymentMethodException {
        PayCash payCash = new PayCash(stationLogic);
        System.setErr(new PrintStream(errContent));

        stationLogic.addItems(MethodOfAdd.SCAN);
        for (int i = 0; i < 10; i++) {
            if (check.mainScanner.scan(appleUnit)) {
                break;
            }
        }
        check.baggingArea.add(appleUnit);
        // System.out.println(stationLogic.getOrder().getTotalDue());
        stationLogic.pay(MethodOfPayment.CASH);

        payCash.sendPrintSignal();
        assertTrue(errContent.toString().contains("UnpaidTotalException"));
    }

    /**
     * Test to see if change is properly returned if bill dispenser has loaded bills
     */
    @Test
    public void validChangeBillTest() {
        // get $5 dispenser, load one $5 bill
        BillDispenser fiveBillDispen = check.billDispensers.get(5);
        Bill fiveBill = new Bill(5, currency);
        try {
            fiveBillDispen.load(fiveBill);
            // request $5 in change
            payCash.sendOutChange(new BigDecimal(5.00));
        } catch (SimulationException | DisabledException | OverloadException | EmptyException e) {
            // should not happen
            e.printStackTrace();
        }
        // check that returned bill is $5
        assertEquals(fiveBill.getValue(), check.billOutput.removeDanglingBill().getValue());

    }

    /**
     * Test to see if change is properly returned if coin dispenser has loaded coins
     */
    @Test
    public void validChangeCoinTest() {
        BigDecimal loonie = new BigDecimal(1.00);
        CoinDispenser loonieDispen = check.coinDispensers.get(loonie);
        Coin loonieCoin = new Coin(loonie, currency);
        try {
            loonieDispen.load(loonieCoin);
            payCash.sendOutChange(loonie);
        } catch (SimulationException | DisabledException | OverloadException | EmptyException e) {
            // should not happen
            e.printStackTrace();
        }
        List<Coin> result = check.coinTray.collectCoins();
        assertEquals(loonieCoin.getValue(), result.get(0).getValue());
    }

    /**
     * Test to see if machine properly reacts if bill change below threshold
     */
    @Test
    public void emptyChangeBillTest() {
        // set bill threshold to 5 bills
        stationLogic.setBillThreshold(5);
        // get $5 dispenser, load one bill
        BillDispenser fiveBillDispen = check.billDispensers.get(5);
        Bill fiveBill = new Bill(5, currency);
        try {
            fiveBillDispen.load(fiveBill);
            // request $10 change
            payCash.sendOutChange(new BigDecimal(10.00));
        } catch (SimulationException | DisabledException | OverloadException | EmptyException e) {
            // should not happen
            e.printStackTrace();
        }

        // see if machine suspends
        check.billOutput.removeDanglingBill();
        payCash.endPayment();
        assertTrue(stationLogic.isSuspended());
    }

    @Test
    public void emptyChangeCoinTest() {
        // set coin threshold to 5 coins
        stationLogic.setCoinThreshold(5);
        // get loonie dispenser, load one coin
        BigDecimal loonie = new BigDecimal(1.00);
        CoinDispenser loonieDispen = check.coinDispensers.get(loonie);
        Coin loonieCoin = new Coin(loonie, currency);
        try {
            loonieDispen.load(loonieCoin);
            // request $10 change
            payCash.sendOutChange(new BigDecimal(10.00));
        } catch (SimulationException | DisabledException | OverloadException | EmptyException e) {
            // should not happen
            e.printStackTrace();
        }

        // see if machine suspends
        check.coinTray.collectCoins();
        payCash.endPayment();
        assertTrue(stationLogic.isSuspended());
    }

    /**
     * Test to see if machine properly reacts if there is not enough change for the
     * customer
     */
    @Test
    public void notEnoughChangeTest() {
        try {
            // request $10 in change with no bills loaded
            payCash.sendOutChange(new BigDecimal(10.00));
        } catch (SimulationException | DisabledException | OverloadException | EmptyException e) {
            // should not happen
            e.printStackTrace();
        }
        // check machine is suspended
        payCash.endPayment();
        assertTrue(stationLogic.isSuspended());
    }

    /**
     * Test to see if machine properly reacts if bills are loaded, for an suspended
     * machine
     */
    @Test
    public void attendantRefillBillsSuspendTest() {
        // get $5 dispenser, load one $5 bill
        BillDispenser fiveBillDispen = check.billDispensers.get(5);
        Bill fiveBill = new Bill(5, currency);
        try {
            fiveBillDispen.load(fiveBill);
            // request $10 in change
            payCash.sendOutChange(new BigDecimal(10.00));
            check.billOutput.removeDanglingBill();
            payCash.endPayment();
            // check machine is suspended
            assertTrue(stationLogic.isSuspended());
            // get attendant to refill 4 bills
            ArrayList<Bill> bills = new ArrayList<>();
            bills.add(fiveBill);
            bills.add(fiveBill);
            bills.add(fiveBill);
            bills.add(fiveBill);
            stationLogic.attendantRefillBills(bills);
        } catch (SimulationException | DisabledException | OverloadException | EmptyException e) {
            // should not happen
            e.printStackTrace();
        }

        // check machine is unsuspended, 4 bills are loaded
        assertFalse(stationLogic.isSuspended());
        assertEquals(4, fiveBillDispen.size());
    }

    @Test
    public void attendantRefillCoinsSuspendTest() {
        BigDecimal loonie = new BigDecimal(1.00);
        CoinDispenser loonieDispen = check.coinDispensers.get(loonie);
        Coin loonieCoin = new Coin(loonie, currency);

        try {
            loonieDispen.load(loonieCoin);
            // request $2 in change
            payCash.sendOutChange(new BigDecimal(2.00));
            check.coinTray.collectCoins();
            payCash.endPayment();
            // check machine is suspended
            assertTrue(stationLogic.isSuspended());
            // get attendant to refill 4 bills
            ArrayList<Coin> coins = new ArrayList<>();
            coins.add(loonieCoin);
            coins.add(loonieCoin);
            coins.add(loonieCoin);
            coins.add(loonieCoin);
            stationLogic.attendantRefillCoins(coins);
        } catch (SimulationException | DisabledException | OverloadException | EmptyException e) {
            // should not happen
            e.printStackTrace();
        }

        // check machine is unsuspended, 4 bills are loaded
        assertFalse(stationLogic.isSuspended());
        assertEquals(4, loonieDispen.size());
    }

    /**
     * Test to see if machine properly reacts if bills are loaded, for a
     * not-suspended machine
     */
    @Test
    public void attendantRefillBillsTest() {
        // get various dispensers and create various bill amounts
        BillDispenser billDispenTen = check.billDispensers.get(10);
        Bill bill10 = new Bill(10, currency);
        BillDispenser billDispenTwenty = check.billDispensers.get(20);
        Bill bill20 = new Bill(20, currency);
        BillDispenser billDispenFive = check.billDispensers.get(5);
        Bill bill5 = new Bill(5, currency);
        BillDispenser billDispenFifty = check.billDispensers.get(50);

        // check machine is not suspended
        assertFalse(stationLogic.isSuspended());
        // load array with requested bills
        ArrayList<Bill> bills = new ArrayList<>();
        bills.add(bill5);
        bills.add(bill5);
        bills.add(bill10);
        bills.add(bill20);
        bills.add(bill20);
        bills.add(bill20);

        // load bills into machine
        try {
            stationLogic.attendantRefillBills(bills);
        } catch (SimulationException | DisabledException | OverloadException e) {
            // should not happen
            e.printStackTrace();
        }

        // check that requested bills are loaded, and for $50 that nothing is loaded
        assertFalse(stationLogic.isSuspended());
        assertEquals(2, billDispenFive.size());
        assertEquals(3, billDispenTwenty.size());
        assertEquals(1, billDispenTen.size());
        assertEquals(0, billDispenFifty.size());

    }

    @Test
    public void attendantRefillCoinsTest() {
        // get various dispensers and create various coin amounts
        BigDecimal loonie = new BigDecimal(1.00);
        CoinDispenser loonieDispen = check.coinDispensers.get(loonie);
        Coin loonieCoin = new Coin(loonie, currency);

        BigDecimal toonie = new BigDecimal(2.00);
        CoinDispenser toonieDispen = check.coinDispensers.get(toonie);
        Coin toonieCoin = new Coin(toonie, currency);

        BigDecimal quarter = new BigDecimal(0.25);
        CoinDispenser quarterDispen = check.coinDispensers.get(quarter);
        Coin quarterCoin = new Coin(quarter, currency);

        BigDecimal dime = new BigDecimal(0.10);
        CoinDispenser dimeDispen = check.coinDispensers.get(dime);

        // check machine is not suspended
        assertFalse(stationLogic.isSuspended());
        // load array with requested bills
        ArrayList<Coin> coins = new ArrayList<>();
        coins.add(quarterCoin);
        coins.add(quarterCoin);

        coins.add(loonieCoin);

        coins.add(toonieCoin);
        coins.add(toonieCoin);
        coins.add(toonieCoin);

        // load bills into machine
        try {
            stationLogic.attendantRefillCoins(coins);
        } catch (SimulationException | DisabledException | OverloadException e) {
            // should not happen
            e.printStackTrace();
        }

        // check that requested bills are loaded, and for $50 that nothing is loaded
        assertFalse(stationLogic.isSuspended());
        assertEquals(2, quarterDispen.size());
        assertEquals(3, toonieDispen.size());
        assertEquals(1, loonieDispen.size());
        assertEquals(0, dimeDispen.size());

    }

    @Test
    public void TestCoinFullEmptyEvents() {
        System.setOut(new PrintStream(outContent));
        payCash.reactToCoinsFullEvent(coinDispenserDevice);
        String expected = String.format(
                "Attendant I/O: Coins Dispenser is full.");

        assertTrue(outContent.toString().contains(expected));
        payCash.reactToCoinsEmptyEvent(coinDispenserDevice);
        String expected2 = String.format(
                "Coins Dispenser is empty.");
        assertTrue(outContent.toString().contains(expected2));
    }

    /**
     * This test case will check the outputs of the reatToBills full and empty
     * classes
     */
    @Test
    public void TestBillFullEmptyEvents() {
        System.setOut(new PrintStream(outContent));
        payCash.reactToBillsFullEvent(dispenserDevice);
        String expected = String.format(
                "Attendant I/O: Bills Dispenser is full.");

        assertTrue(outContent.toString().contains(expected));
        payCash.reactToBillsEmptyEvent(dispenserDevice);
        String expected2 = String.format(
                "Bills Dispenser is empty.");
        assertTrue(outContent.toString().contains(expected2));
    }

    /**
     * This function tests the reactToCoinAddedEvent for the coinStorage observer
     */
    @Test
    public void TestCoinsAddedStorageEvent() {
        order = stationLogic.getOrder();
        payCash.pay(order);
        payCash.paymentInProcessing(coinValidatorDevice, BigDecimal.ONE);
        payCash.reactToCoinAddedEvent(coinStorageDevice);
    }

    /**
     * This function tests the react to coins full event after a coin fills
     * coinstorage.
     */
    @Test
    public void TestCoinsFullEvents() {
        System.setOut(new PrintStream(outContent));
        order = stationLogic.getOrder();
        payCash.pay(order);
        payCash.paymentInProcessing(coinValidatorDevice, BigDecimal.ONE);
        payCash.reactToCoinsFullEvent(coinStorageDevice);
        String expected1 = String.format(
                "Coin Storage Filled");
        assertTrue(outContent.toString().contains(expected1));
    }

    /**
     * This test checks that the unimplemented methods do not have errors in them.
     */
    @Test
    public void TestCheckUnimplementedCodeForUnexpectedErrors() {

        payCash.reactToEnabledEvent(storageDevice);
        payCash.reactToDisabledEvent(storageDevice);
        payCash.reactToBillsLoadedEvent(storageDevice);
        payCash.reactToBillsUnloadedEvent(storageDevice);
        payCash.reactToCoinsFullEvent(coinDispenserDevice);
        payCash.reactToCoinRemovedEvent(coinDispenserDevice, coin);
        payCash.reactToCoinsLoadedEvent(coinDispenserDevice, coin);
        payCash.reactToCoinsUnloadedEvent(coinDispenserDevice, coin);
        payCash.reactToCoinsLoadedEvent(coinStorageDevice);
        payCash.reactToCoinsUnloadedEvent(coinStorageDevice);
        payCash.reactToCoinAddedEvent(new CoinTray(1));
        payCash.reactToInvalidCoinDetectedEvent(coinValidatorDevice);
        payCash.reactToValidCoinDetectedEvent(coinValidatorDevice, new BigDecimal(0.10));
        payCash.reactToCoinInsertedEvent(coinSlotDevice);
        payCash.reactToBillAddedEvent(dispenserDevice, bill);
        payCash.reactToBillRemovedEvent(dispenserDevice, bill);
        payCash.reactToBillsLoadedEvent(dispenserDevice, bill);
        payCash.reactToBillsUnloadedEvent(dispenserDevice, bill);
    }

    @Test
    public void TestAttendantAddedCoins() {
        payCash.coinsLow = true;
        stationLogic.suspend();
        BigDecimal penny = new BigDecimal(0.01);
        currency = Currency.getInstance(Locale.CANADA);
        Coin dummy_coin = new Coin(penny, currency);
        payCash.reactToCoinAddedEvent(check.coinDispensers.get(penny), dummy_coin);

        assertFalse(payCash.coinsLow);
        assertFalse(stationLogic.isSuspended());
    }

}