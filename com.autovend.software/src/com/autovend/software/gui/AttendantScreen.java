/**
 *
 * SENG Iteration 3 P3-3 | AttendantScreen.java
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

package com.autovend.software.gui;

import javax.swing.JFrame;

import com.autovend.SupervisionStationLogic;

public class AttendantScreen {

	// Fields for the panels and the frame of attendant screen
	public JFrame screen;
	public AttendantLoginPanel loginPanel = null;
	public AttendantMainPanel mainPanel = null;
	public SupervisionStationLogic station;

	public AttendantScreen(SupervisionStationLogic astation) {
		screen = astation.getSupervisionStation().screen.getFrame();
		this.station = astation;
		screen.setTitle("Attendant Station");
		this.loginPanel = new AttendantLoginPanel(this, astation);
		this.mainPanel = new AttendantMainPanel(this, astation);
		
		screen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		screen.setExtendedState(JFrame.NORMAL);
		screen.setSize(800,610);
		screen.setLocationRelativeTo(null);
		screen.setUndecorated(false);
		screen.getContentPane().add(loginPanel);
		screen.setVisible(true);
		
	}
	
	/*
	 * Changes Attendant Screen's frame to a different panel
	 * 
	 */
	public void change(String panelName) {
	    switch (panelName) {
	        case "loginPanel":
	            screen.getContentPane().removeAll();
	            screen.getContentPane().add(loginPanel);
	            break;
	        case "mainPanel":
	            screen.getContentPane().removeAll();
	            this.mainPanel.lblUser.setText("User: " + this.station.loginlogout.getAttendant()); // Sets a label with the user's ID
	            screen.getContentPane().add(mainPanel);
	            break;
	        default: // Throws error if an unknown panel is entered
	            throw new IllegalArgumentException("Unknown panel name: " + panelName);
	    }
	    screen.revalidate();
	    screen.repaint();
	}
	
	// Gets the station logic
	public SupervisionStationLogic getStation() {
		return this.station;
	}

}
