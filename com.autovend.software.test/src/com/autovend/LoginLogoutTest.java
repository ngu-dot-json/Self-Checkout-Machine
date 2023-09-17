/**
 *
 * SENG Iteration 3 P3-3 | LoginLogoutTest.java
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

import com.autovend.devices.SupervisionStation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class LoginLogoutTest {

    Login_Logout ll0, ll1;
    SupervisionStation svs0, svs1;

    String userID = "12345678", password = "qwerty";

    @Before
    public void setup(){
        // Create an attendant station and log an attendant into it
        svs0 = new SupervisionStation();
        ll0 = new Login_Logout(svs0);
        svs1 = new SupervisionStation();
        ll1 = new Login_Logout(svs1);
    }

    @After
    public void teardown(){
        ll0 = null;
        Login_Logout.getAttendantLoginInfo().clear(); // clear after every test since, every station shares IDs
        // and passwords (otherwise extra information will be carried)
    }

    // Test registering of new attendant into the system (should put attendant into Hashmap and return true)
    @Test
    public void testRegisterSuccess(){
        assertTrue(Login_Logout.register(userID, password));
    }

    // Registering should put the new attendant into the hashmap, with the same ID and password
    @Test
    public void testRegisterAddsAttToMap(){
        Login_Logout.register(userID, password);

        HashMap<String, String> actual = Login_Logout.getAttendantLoginInfo();

        assertTrue(actual.containsKey(userID));
        assertEquals(actual.get(userID), password);
    }

    @Test
    public void testDuplicateIDFails(){
        // Register once with ID should be successful
        assertTrue(Login_Logout.register(userID, password));

        // Register same ID should return false indicating failure
        assertFalse(Login_Logout.register(userID, password));
    }

    @Test
    public void testInvalidNonDupIDFails(){
        // ID too short but is not registered before should return false
        assertFalse(Login_Logout.register("1234", password));
    }

    @Test
    public void testTooLongID(){
        // ID too long should fail
        assertFalse(Login_Logout.register("12345678912", password));
    }

    @Test
    public void testIDCorrectLengthNotInt(){
        // IDs need to be an integer
        assertFalse(Login_Logout.register("abcdefgh", password));
    }

    // The provided ID to remove is in the database, removal should be successful
    @Test
    public void testRemoveAttendantSuccess(){
        Login_Logout.register(userID, password); // register attendant 12345678
        assertTrue(Login_Logout.remove(userID)); // returns true on successful remove
        assertTrue(Login_Logout.getAttendantLoginInfo().isEmpty()); // hashmap data should be empty
    }

    // An attendant that has not been registered cannot be removed should return false
    @Test
    public void testRemoveNonRegAtt(){
        assertFalse(Login_Logout.remove(userID));
    }

    // If no attendants were registered into the database then an attempt at logging in will fail
    @Test
    public void testLoginEmptyDatabase(){
        assertFalse(ll0.login(userID, password));
    }

    // Test that logging into one self-checkout station does not log into all stations
    @Test
    public void logInto1NotToAll(){
        Login_Logout.register(userID, password);
        ll0.login(userID, password);

        // ll0 should be logged in
        assertEquals(ll0.getAttendant(), userID);

        // ll1 should not be logged in, and it should not have the ID of the person logged into ll1
        assertNull(ll1.getAttendant());
        assertNotEquals(ll1.getAttendant(), userID);
    }

    // Incorrect passwords should return false
    @Test
    public void testIncorrectPassword(){
        Login_Logout.register(userID, password);
        ll0.login(userID, password);

        assertFalse(ll1.login(userID, "wrongpassword"));
    }

    // If someone is already logged in cannot log in again
    @Test
    public void testTryDualLog(){
        Login_Logout.register(userID, password);
        assertTrue(ll0.login(userID, password));
        assertFalse(ll0.login(userID, password)); // trying to log in when someone is already logged in should fail
    }

    // Logs user out by setting the attendant associated to the station as null
    @Test
    public void testLogoutWhenAttLogIn(){
        Login_Logout.register(userID, password);
        ll0.login(userID, password);

        assertTrue(ll0.logout());
        assertNull(ll0.getAttendant());
    }

    // If no is one logged in then, cannot log out
    @Test
    public void testLogoutNoLogIn(){
        assertFalse(ll0.logout());
        assertNull(ll0.getAttendant()); // attendant should still be null for station
    }

    // Test setting the minimum length of IDs
    @Test
    public void testSetMinID(){
        Login_Logout.setMemberIDAllowableLengthMin(5);

        // Expected value is 5 because it was changed
        assertEquals(5, Login_Logout.getMemberIDAllowableLengthMin());
    }

    // Minimum length cannot be zero
    @Test(expected = IllegalArgumentException.class)
    public void testIDMinZero(){
        Login_Logout.setMemberIDAllowableLengthMin(0);
    }

    // Minimum length cannot be greater than max
    @Test(expected = IllegalArgumentException.class)
    public void testMinLargerThanMax(){
        Login_Logout.setMemberIDAllowableLengthMin(12);
    }

    // Test setting of max length for IDs
    @Test
    public void testSetIDMax(){
        Login_Logout.setMemberIDAllowableLengthMax(12);
        assertEquals(12, Login_Logout.getMemberIDAllowableLengthMax());
    }

    // Max length cannot be zero
    @Test(expected = IllegalArgumentException.class)
    public void testIDMaxZero(){
        Login_Logout.setMemberIDAllowableLengthMax(0);
    }

    // Max length cannot be larger than min length
    @Test(expected = IllegalArgumentException.class)
    public void testMaxSmallerThanMin(){
        Login_Logout.setMemberIDAllowableLengthMax(2);
    }
}