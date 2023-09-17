/**
 *
 * SENG Iteration 3 P3-3 | PermitStationUseTest.java
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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;

import com.autovend.*;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.items.AddItemScanController;

import junit.framework.Assert;
import com.autovend.*;

public class PermitStationUseTest {
	/*
	* Make objects and structures available for use for all tests
	*/
	private SelfCheckoutStationLogic scsl1;
	private SelfCheckoutStationLogic scsl2;
	private SelfCheckoutStationLogic scsl3;
	//private ArrayList<SelfCheckoutStationLogic> suspendedList;

	private SelfCheckoutStation station;
	public ArrayList<SelfCheckoutStationLogic> suspendedList;
	public Currency currency;
	public int[] denominations;
	public BigDecimal[] total;
	
	/*
	* set up all the basic requirements to get the station running for testing
	*/
	@Before 
	public void setUp() {
		currency = Currency.getInstance("CAD");
		denominations = new int[2];
		denominations[0]= 5;
		denominations[1] = 10;
		total = new BigDecimal[1];
		total[0] = BigDecimal.valueOf(0.05);

		SelfCheckoutStation station = new SelfCheckoutStation(currency, denominations, total, 5000, 1 );
    	
		
		scsl1 = new SelfCheckoutStationLogic(station);
		scsl2 = new SelfCheckoutStationLogic(station);
		scsl3 = new SelfCheckoutStationLogic(station);
	}
	
	
	/*
	* test to see if suspended station is in the unsuspendable list after maintence given by getList function
	* expected: station in arraylist
	* result: expected
	*/
	@Test
	public void addStationToSuspendedListTest() {
		PermitStationUse permit = new PermitStationUse(scsl1);
		permit.addSuspendableStationsToList(scsl1); // add station to list
		permit.preventUse(scsl1); // susepnd
		permit.maintenenceComplete(scsl1); // finish maintenance
		
		suspendedList = permit.getListofStationsForUnsuspending();
	

		System.out.print(suspendedList);
		assertTrue(suspendedList.contains(scsl1));
		
	}
	

	/*
	* test to check if permitUse works, station is set to suspended, before use
	* expected: station not suspended, is in suspendable list and not in unsuspendable
	* result: expected
	*/
	@Test
	public void permitUseTest1() {
	
		PermitStationUse permit = new PermitStationUse(scsl1);
		permit.addSuspendableStationsToList(scsl1);
		suspendedList = permit.getListOfCustomerSuspendableStations();
		permit.preventUse(scsl1);
		permit.permitUse(scsl1);
		assertFalse(permit.getListofStationsForUnsuspending().contains(scsl1));
		assertTrue(permit.getListOfSuspendableStations().contains(scsl1));
	}
	
	/*
	* test permitUse given station not suspended
	* expected: station not suspended, is in suspendable and not in unsuspendable
	* result: expected
	*/
	@Test
	public void permitUseTest2() {
	
		PermitStationUse permit = new PermitStationUse(scsl1);
		permit.addSuspendableStationsToList(scsl1);
		suspendedList = permit.getListOfCustomerSuspendableStations();
		permit.permitUse(scsl1);
		assertTrue(permit.getListOfSuspendableStations().contains(scsl1));
		assertFalse(permit.getListofStationsForUnsuspending().contains(scsl1));
	}
	
	/*
	 * test to see if addSuspendableStationsToList adds to suspendable station array
	 * expected: in suspendable arraylist not in customerSuspendable arraylist
	 * result: expected
	 */
	@Test
	public void addSuspendableStationsToList1(){
		PermitStationUse p = new PermitStationUse(scsl1);
		p.addSuspendableStationsToList(scsl1);
		assertTrue(p.getListOfSuspendableStations().contains(scsl1));
		assertTrue(!p.getListOfCustomerSuspendableStations().contains(scsl1));
	}
	
	/*
	 * test to check addtoSuspendableStationsList adds to customer suspendable list given 
	 * order is made
	 * expected: is in customer suspendable arraylist not in suspendableStations list
	 */
	@Test
	public void addSuspendableStationsToList2(){
		PermitStationUse p = new PermitStationUse(scsl1);
		scsl1.startOrder();
		p.addSuspendableStationsToList(scsl1);
		assertTrue(p.getListOfCustomerSuspendableStations().contains(scsl1));
		assertTrue(!p.getListOfSuspendableStations().contains(scsl1));
	}
	
	/*
	 * test adding to suspendable list given station is already suspended
	 * expected: not in any suspendable arraylist
	 * result: expected
	 */
	@Test
	public void addSuspendableStationsToLists3(){
		PermitStationUse p = new PermitStationUse(scsl1);
		scsl1.suspend();
		p.addSuspendableStationsToList(scsl1);
		assertTrue(!p.getListOfSuspendableStations().contains(scsl1));
		assertTrue(!p.getListOfCustomerSuspendableStations().contains(scsl1));
	}
	
	/*
	 * test given the same station is us called to be added to suspendable list more than once
	 * expected: station should only appear once regardless of the amount of times added
	 * result: as expected
	 */
	@Test
	public void addSuspendableStationsToLists4(){
		PermitStationUse p = new PermitStationUse(scsl1);
		p.addSuspendableStationsToList(scsl1);
		p.addSuspendableStationsToList(scsl1);
		scsl3.startOrder();
		p.addSuspendableStationsToList(scsl3);
		p.addSuspendableStationsToList(scsl3);
		assertTrue(p.getListOfSuspendableStations().size() == 1);
		assertTrue(p.getListOfCustomerSuspendableStations().size() == 1);	
	}
	
	/*
	 * test after stations are added to suspendable list, after all stations have orders started
	 * use updateSuspendablelists to check if they are moved to correct arraylist
	 * expected: stations should be moved to customer suspendable arraylist
	 * result: as expected
	 */
	@Test
	public void updateSuspendableStationsList1(){
		PermitStationUse p = new PermitStationUse(scsl1);
		
		p.addSuspendableStationsToList(scsl1);
		p.addSuspendableStationsToList(scsl2);
		p.addSuspendableStationsToList(scsl3);
		
		assertTrue(p.getListOfSuspendableStations().contains(scsl1));
		assertTrue(p.getListOfSuspendableStations().contains(scsl2));
		assertTrue(p.getListOfSuspendableStations().contains(scsl3));
		
		scsl1.startOrder();
		scsl2.startOrder();
		scsl3.startOrder();
		
		p.updateSuspendableLists();
		
		assertTrue(p.getListOfCustomerSuspendableStations().contains(scsl1));
		assertTrue(p.getListOfCustomerSuspendableStations().contains(scsl2));
		assertTrue(p.getListOfCustomerSuspendableStations().contains(scsl3));
		assertTrue(p.getListOfSuspendableStations().size() == 0);
		
		scsl1.suspend();
		scsl2.suspend();
		scsl3.suspend();
		
		p.updateSuspendableLists();
		
		assertTrue(p.getListOfCustomerSuspendableStations().size() == 0);
		assertTrue(p.getListOfSuspendableStations().size() == 0);
		assertTrue(p.getListofStationsForUnsuspending().contains(scsl1));
		assertTrue(p.getListofStationsForUnsuspending().contains(scsl2));
		assertTrue(p.getListofStationsForUnsuspending().contains(scsl3));
		
		}
	
	
	/*
	 * test all stations are added, station 2 has its order started, and 3 is
	 * suspended, update is called
	 * expected: station 1 should be suspendable 2 should be put into customer 
	 * suspendable list and 3 should be in unsuspendable list
	 * result: expected
	 */
	@Test
	public void updateSuspendableStationsList2(){
		PermitStationUse p = new PermitStationUse(scsl1);
		
		p.addSuspendableStationsToList(scsl1);
		p.addSuspendableStationsToList(scsl2);
		scsl2.startOrder();
		p.addSuspendableStationsToList(scsl3);
		
		scsl3.suspend();
		
		p.updateSuspendableLists();
		
		assertTrue(p.getListOfSuspendableStations().size() == 1);
		assertTrue(p.getListOfSuspendableStations().contains(scsl1));
		assertTrue(p.getListOfCustomerSuspendableStations().size() == 1);
		assertTrue(p.getListOfCustomerSuspendableStations().contains(scsl2));
		assertTrue(scsl3.isSuspended());
		assertTrue(p.getListofStationsForUnsuspending().size() == 1);
		assertTrue(p.getListofStationsForUnsuspending().contains(scsl3));
		
	}
	
	/*
	 * test to see if prevent use suspends station
	 * expected: isn't in suspendableStations list, is in unsuspendable list
	 * result: expected
	 */
	@Test 
	public void preventUse1(){
		PermitStationUse p = new PermitStationUse(scsl1);
		p.addSuspendableStationsToList(scsl1);
		
		p.preventUse(scsl1);
		
		assertTrue(scsl1.isSuspended());
		assertTrue(!p.getListOfSuspendableStations().contains(scsl1));
		assertTrue(p.getListofStationsForUnsuspending().contains(scsl1));
		
	}
	
	/*
	 * test to see if station is suspended given addSuspenableList not called
	 * expected: station not suspended
	 * result: expected
	 */
	@Test
	public void preventUse2(){
		PermitStationUse p = new PermitStationUse(scsl1);
		p.preventUse(scsl1);
		assertTrue(!scsl1.isSuspended());

	}
	
	/*
	 * test to check if prevent use is used after station is already suspended and added 
	 * to suspenable lsits
	 * expected: station suspended, is in suspendable list and not in unsuspendable
	 * result: expected
	 */
	@Test
	public void preventUse3(){
		PermitStationUse p = new PermitStationUse(scsl1);
		p.addSuspendableStationsToList(scsl1);
		scsl1.suspend();
		p.preventUse(scsl1);
		assertTrue(scsl1.isSuspended());
		assertTrue(!p.getListOfSuspendableStations().contains(scsl1));
		assertTrue(p.getListofStationsForUnsuspending().contains(scsl1));
		
	}
	
	/*
	 * test to check prevent use given an order is ebing made
	 * expected: station not suspended, is in customer suspendable list, and is not in
	 * unsupendable lsit
	 * result: expected
	 */
	@Test
	public void preventUse4(){
		PermitStationUse p = new PermitStationUse(scsl1);
		scsl1.startOrder();
		p.addSuspendableStationsToList(scsl1);
		p.preventUse(scsl1);
		assertTrue(!scsl1.isSuspended());
		assertTrue(p.getListOfCustomerSuspendableStations().contains(scsl1));
		assertTrue(!p.getListofStationsForUnsuspending().contains(scsl1));
		
	}
	
	
	/*
	 * test forcePreventUse proper usage
	 * expected: station suspended, is not in suspenable list, is in unsuspenable list
	 * result: expected
	 */
	@Test 
	public void forcePreventUse1(){
		PermitStationUse p = new PermitStationUse(scsl1);
		scsl1.startOrder();
		p.addSuspendableStationsToList(scsl1);
		
		p.forcePreventUse(scsl1);
		
		assertTrue(scsl1.isSuspended());
		assertTrue(!p.getListOfCustomerSuspendableStations().contains(scsl1));
		assertTrue(p.getListofStationsForUnsuspending().contains(scsl1));
		
	}
	
	/*
	 * test forcePreventUse is called while station without addToSuspendable being called
	 * expected: station not suspended
	 * result: as expected
	 */
	@Test
	public void forcePreventUse2(){
		PermitStationUse p = new PermitStationUse(scsl1);
		scsl1.startOrder();
		p.forcePreventUse(scsl1);
		assertTrue(!scsl1.isSuspended());

	}
	
	/*
	 * test forcePreventUse given station arleady suspended
	 * expected: station suspended, is not in suspendable list, is in list for 
	 * unsuspending
	 */
	@Test
	public void forcePreventUse3(){
		PermitStationUse p = new PermitStationUse(scsl1);
		scsl1.startOrder();
		p.addSuspendableStationsToList(scsl1);
		scsl1.suspend();
		p.forcePreventUse(scsl1);
		assertTrue(scsl1.isSuspended());
		assertTrue(!p.getListOfCustomerSuspendableStations().contains(scsl1));
		assertTrue(p.getListofStationsForUnsuspending().contains(scsl1));
		
	}
	
	/*
	 * test forcePrevent use given that customer is not using station
	 * expected: station not suspended, is in suspenable list, and not in 
	 * unsuspendable list
	 * result: expected
	 */
	@Test
	public void forcePreventUse4(){
		PermitStationUse p = new PermitStationUse(scsl1);
		p.addSuspendableStationsToList(scsl1);
		p.forcePreventUse(scsl1);
		assertTrue(!scsl1.isSuspended());
		assertTrue(p.getListOfSuspendableStations().contains(scsl1));
		assertTrue(!p.getListofStationsForUnsuspending().contains(scsl1));
		
	}

	/*
	 * test maintenceComplete given system never suspended
	 * expected: system print noting not suspended, not in unsuspendable list
	 * result: expected
	 */
	@Test
	public void maintenenceStationNotSuspended() {
		PermitStationUse p = new PermitStationUse(scsl1);
		p.maintenenceComplete(scsl1);
		assertTrue(!p.getListofStationsForUnsuspending().contains(scsl1));
	}
}