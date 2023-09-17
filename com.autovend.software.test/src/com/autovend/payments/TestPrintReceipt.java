/**
 *
 * SENG Iteration 3 P3-3 | TestPrintReceipt.java
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

import org.junit.Test;
import org.junit.*;
import com.autovend.Order;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.SelfCheckoutStationLogic;
import com.autovend.products.*;
import com.autovend.devices.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Bill;
import com.autovend.MethodOfAdd;
import com.autovend.Numeral;
import com.autovend.external.ProductDatabases;
import com.autovend.payments.ReceiptPrinterController;
import com.autovend.payments.UnpaidTotalException;

import java.text.NumberFormat;
import java.util.List;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.ReceiptPrinterObserver;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.PriceLookUpCode;
import com.autovend.products.Product;

/**
 * It will ensure that the items have been paid for prior to printing, and it
 * ensures that the system won't print out a receipt if there are still
 * outstanding items that have not been paid for
 * 
 *
 */

public class TestPrintReceipt {
    public ReceiptPrinterObserver printerListener;

    private Order order;
    private SelfCheckoutStation check;
    private SelfCheckoutStationLogic stationLogic;
    private ReceiptPrinterController receiptPrinterController;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private PLUCodedProduct apple;
    private PriceLookUpCodedUnit appleUnit;
    private BarcodedProduct banana;
    private BarcodedUnit bananaUnit;
    private ReceiptPrinter printer;
    public BillValidator validatorDevice;
    public Currency currency;

    public int[] denominations;
    public List<BigDecimal> coinDenominations;

    @Before
    public void setup() {

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

        int a = 50;
        int b = 1;
        check = new SelfCheckoutStation(currency, denominations, CoinDenominationsArray, a, b);
        stationLogic = new SelfCheckoutStationLogic(check);
        receiptPrinterController = new ReceiptPrinterController(stationLogic, check.printer);

        // products = new Arraylist<>();

        String ap1 = "apple";
        BigDecimal p1 = BigDecimal.valueOf(6.0);
        double d1 = 2.0;
        Numeral[] numerals = new Numeral[4];
        numerals[0] = Numeral.one;
        numerals[1] = Numeral.one;
        numerals[2] = Numeral.one;
        numerals[3] = Numeral.one;
        PriceLookUpCode plu1 = new PriceLookUpCode(numerals);
        // Barcode bar1 = new Barcode(Numeral.one);
        apple = new PLUCodedProduct(plu1, ap1, p1);
        // apple = new BarcodedProduct(bar1, ap1, p1, d1);
        appleUnit = new PriceLookUpCodedUnit(plu1, d1);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(plu1, apple);

        String ap2 = "banana";
        BigDecimal p2 = BigDecimal.valueOf(4.0);
        double d2 = 5.0;
        Barcode bar2 = new Barcode(Numeral.one);
        banana = new BarcodedProduct(bar2, ap2, p2, d2);
        bananaUnit = new BarcodedUnit(bar2, d2);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bar2, banana);

        stationLogic.startOrder();

        order = stationLogic.getOrder();
        order.addPLU(apple, d1);
        order.add(banana);
    }

    @After
    public void teardown() {
        currency = null;
        receiptPrinterController = null;
        stationLogic = null;
        check = null;

    }

    @Test
    public void testGenerateReceiptString() {
        String receiptString = receiptPrinterController.generateReceiptString(order);

        String expected1 = "The Local Marketplace";
        String expected2 = "apple                                                  $6.00";
        String expected3 = "banana                                                 $4.00";
        String expected4 = "Thanks for shopping with us!";
        assertTrue(receiptString.contains(expected1));
        assertTrue(receiptString.contains(expected2));
        assertTrue(receiptString.contains(expected3));
        assertTrue(receiptString.contains(expected4));
    }

    @Test
    public void testGenerateReceiptStringTrimDescriptionLength() {
        String ap3 = "something with a very long description that should be trimmed to fit the receipt";
        BigDecimal p3 = BigDecimal.valueOf(4.0);
        double d3 = 5.0;
        Barcode bar3 = new Barcode(Numeral.three);
        BarcodedProduct something = new BarcodedProduct(bar3, ap3, p3, d3);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bar3, something);
        order.add(something);

        String receiptString = receiptPrinterController.generateReceiptString(order);

        String expected1 = "The Local Marketplace";
        String expected2 = "apple                                                  $6.00";
        String expected3 = "banana                                                 $4.00";
        String expected4 = "something with a very long description that should be  $4.00";
        String expected5 = "Thanks for shopping with us!";

        assertTrue(receiptString.contains(expected1));
        assertTrue(receiptString.contains(expected2));
        assertTrue(receiptString.contains(expected3));
        assertTrue(receiptString.contains(expected4));
        assertTrue(receiptString.contains(expected5));
    }

    @Test
    public void testPrint() {
        try {
            check.printer.addInk(ReceiptPrinter.MAXIMUM_INK);
            check.printer.addPaper(ReceiptPrinter.MAXIMUM_PAPER);
            receiptPrinterController.print(order);
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }

        assertFalse(stationLogic.isSuspended());
    }

    @Test
    public void testPrintStationDisabled() {
        try {
            stationLogic.suspend();
            receiptPrinterController.print(order);
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }

        assertTrue(stationLogic.isSuspended());
    }

    @Test
    public void testPrintNoInk() {
        System.setOut(new PrintStream(outContent));
        try {
            check.printer.addPaper(ReceiptPrinter.MAXIMUM_PAPER);
            receiptPrinterController.print(order);

            String expected1 = "Attendant I/O: Station requires maintenance:";
            String expected2 = "There is no ink in the printer. A duplicate receipt must be printed after maintenance is complete.";
            assertTrue(stationLogic.isSuspended());
            assertTrue(outContent.toString().contains(expected1));
            assertTrue(outContent.toString().contains(expected2));
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testPrintNoPaper() {
        System.setOut(new PrintStream(outContent));
        try {
            check.printer.addInk(ReceiptPrinter.MAXIMUM_INK);
            receiptPrinterController.print(order);

            String expected1 = "Attendant I/O: Station requires maintenance:";
            String expected2 = "There is no paper in the printer. A duplicate receipt must be printed after maintenance is complete.";
            assertTrue(stationLogic.isSuspended());
            assertTrue(outContent.toString().contains(expected1));
            assertTrue(outContent.toString().contains(expected2));
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testPrintNoPaperAndNoInk() {
        System.setOut(new PrintStream(outContent));
        try {
            receiptPrinterController.print(order);

            String expected1 = "Attendant I/O: Station requires maintenance:";
            String expected2 = "There is no paper in the printer. A duplicate receipt must be printed after maintenance is complete.";
            String expected3 = "There is no ink in the printer. A duplicate receipt must be printed after maintenance is complete.";
            assertTrue(stationLogic.isSuspended());
            assertTrue(outContent.toString().contains(expected1));
            assertTrue(outContent.toString().contains(expected2));
            assertTrue(outContent.toString().contains(expected3));
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testPrinterEnabled() {
        check.printer.disable();
        check.printer.enable();
        assertFalse(check.printer.isDisabled());
    }

    @Test
    public void testPrinterDisabled() {
        check.printer.disable();
        assertTrue(check.printer.isDisabled());
    }

    @Test
    public void testDetectLowInk() {
        try {
            receiptPrinterController.addPaper(600);
        } catch (OverloadException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            receiptPrinterController.addInk(10);
        } catch (OverloadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        receiptPrinterController.print(order);
        assertTrue(stationLogic.isSuspended());
    }

    @Test
    public void testDetectLowPaper() {
        try {
            receiptPrinterController.addInk(ReceiptPrinter.MAXIMUM_INK - 1000);
        } catch (OverloadException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            receiptPrinterController.addPaper(10);
        } catch (OverloadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        receiptPrinterController.print(order);
        assertTrue(stationLogic.isSuspended());
    }

    @Test
    public void testUnsuspendAfterRefillInk() {
        try {
            receiptPrinterController.addPaper(600);
        } catch (OverloadException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        receiptPrinterController.print(order);
        assertTrue(stationLogic.isSuspended());
        try {
            receiptPrinterController.addInk(ReceiptPrinter.MAXIMUM_INK - 100);
            assertFalse(stationLogic.isSuspended());
            receiptPrinterController.print(order);
            assertFalse(stationLogic.isSuspended());
        } catch (OverloadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testUnsuspendAfterRefillPaper() {
        try {
            receiptPrinterController.addInk(ReceiptPrinter.MAXIMUM_INK - 100);
        } catch (OverloadException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        receiptPrinterController.print(order);
        assertTrue(stationLogic.isSuspended());
        try {
            receiptPrinterController.addPaper(600);
            assertFalse(stationLogic.isSuspended());
            receiptPrinterController.print(order);
            assertFalse(stationLogic.isSuspended());
        } catch (OverloadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
