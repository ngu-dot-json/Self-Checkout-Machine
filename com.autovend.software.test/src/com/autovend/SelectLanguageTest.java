/**
 *
 * SENG Iteration 3 P3-3 | SelectLanguageTest.java
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

import com.autovend.devices.SelfCheckoutStation;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

public class SelectLanguageTest {
	/*
	 * making global object accessable to all tests
	 */
    private ArrayList<String> supportedLanguages;
    private SelfCheckoutStation station;
    private Currency currency;
    private BigDecimal[] coindenom;
    private int[] denom = {5,10,20,50,100};
    private SelfCheckoutStationLogic scsl;
    private CustomerIOStub1 customerIOStub1 = new CustomerIOStub1() {
        /*
         * simulates customer input made on gui
         */
    	@Override
        public String getSelectedLanguage() {
            return CustomerIOStub1.super.getSelectedLanguage();
        }
    };

    /*
     * simulates customer input made on gui
     */
    private CustomerIOStub2 customerIOStub2 = new CustomerIOStub2() {
        @Override
        public String getSelectedLanguage() {
            return CustomerIOStub2.super.getSelectedLanguage();
        }
    };
    	
    	/*
    	 * set up for the station to be tested, specify currency, denomination, and create instances of objects
    	 * used and accessible to all tests
    	 */
        @Before
        public void testSetup() {
            currency = Currency.getInstance("CAD");
            coindenom = new BigDecimal[]{new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")};
            station = new SelfCheckoutStation(currency, denom, coindenom, 5, 5);
            scsl = new SelfCheckoutStationLogic(station);
            supportedLanguages = new ArrayList<>();
            supportedLanguages.add("English");
            supportedLanguages.add("French");
        }

        /*
         * 
         */
        @Test(expected = NullPointerException.class)
        public void testNullSCSL(){
            SelectLanguage nullTest = new SelectLanguage(null, supportedLanguages, customerIOStub1);
            nullTest.changeLanguage();
        }

        @Test(expected = NullPointerException.class)
        public void testNullSupportedLanguages() {
            SelectLanguage nullTest = new SelectLanguage(scsl, null, customerIOStub1);
            nullTest.changeLanguage();
        }

        @Test(expected = NullPointerException.class)
        public void testNullCustomerIO() {
            SelectLanguage nullTest = new SelectLanguage(scsl, supportedLanguages, null);
            nullTest.changeLanguage();
        }

        @Test
        public void testValidLanguageChoice() {
            SelectLanguage languageTest = new SelectLanguage(scsl, supportedLanguages, customerIOStub1);
            languageTest.changeLanguage();
        }

        @Test(expected = NullPointerException.class)
        public void testInvalidLanguageChoice(){
            SelectLanguage languageTest = new SelectLanguage(scsl, supportedLanguages, customerIOStub2);
            languageTest.changeLanguage();
        }

        @Test
        public void cancelLanguageChoice() {
            SelectLanguage languageTest = new SelectLanguage(scsl, supportedLanguages, customerIOStub1);
            languageTest.cancelChoice();
        }
}