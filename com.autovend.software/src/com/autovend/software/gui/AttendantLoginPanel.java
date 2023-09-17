/**
 *
 * SENG Iteration 3 P3-3 | AttendantLogicPanel.java
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

import java.awt.Dimension;

import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import com.autovend.Login_Logout;
import com.autovend.SupervisionStationLogic;

import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;

public class AttendantLoginPanel extends JPanel {
	
	private AttendantScreen attendantScreen;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JLabel lblError;
	private SupervisionStationLogic logic;
	private JLabel lblNewLabel;
	private JLabel lblSuccess;
	
	public AttendantLoginPanel(AttendantScreen screen, SupervisionStationLogic logic) {
		this.attendantScreen = screen;
		setLayout(null);
		setBackground(new Color(222, 234, 220));
		this.setPreferredSize(new Dimension(800,600));
		this.logic = logic;
		
		usernameField = new JTextField();
		usernameField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		usernameField.setBounds(373, 299, 353, 34);
		add(usernameField);
		usernameField.setColumns(10);
		
		JLabel lblUsername = new JLabel("Attendant ID:\r\n");
		lblUsername.setForeground(new Color(0,0,0));
		lblUsername.setFont(new Font("Muna", Font.PLAIN, 11));
		lblUsername.setBounds(373, 270, 100, 26);
		add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setForeground(new Color(0,0,0));
		lblPassword.setFont(new Font("Muna", Font.PLAIN, 11));
		lblPassword.setBounds(373, 345, 100, 26);
		add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		passwordField.setBounds(373, 373, 353, 34);
		add(passwordField);
		
		lblError = new JLabel("ATTENDANT LOGIN");
		lblError.setForeground(new Color(239, 159, 58));
		lblError.setFont(new Font("Muna", Font.BOLD, 20));
		lblError.setHorizontalAlignment(SwingConstants.LEFT);
		lblError.setBounds(373, 222, 417, 34);
		add(lblError);
		
		GradientButton loginButton = new GradientButton("Login", new Color(227, 129, 2), new Color(255, 186, 105));
		loginButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		loginButton.setBounds(373, 434, 171, 34);
		add(loginButton);
		lblSuccess = new JLabel();
		loginButton.addActionListener(e ->{ // Checks the entered login info and logs in if valid
			
			// Tries to login with info, returns true if login info is in the database
			if (screen.getStation().loginlogout.login(usernameField.getText(), String.valueOf(passwordField.getPassword()))) {
				
				// Logs in
				screen.getStation().loginlogout.login(usernameField.getText(), String.valueOf(passwordField.getPassword()));
				
				// Resets text fields on login panel
				usernameField.setText("");
				passwordField.setText("");
				lblError.setText("ATTENDANT LOGIN");
				
				// Switches to main attendant screen
				screen.change("mainPanel");
				
			// Invalid login
			} else {
				// Resets login fields
				usernameField.setText("");
				passwordField.setText("");
				
				// Shows error on screen
				lblError.setText("Invalid ID or Password.");
			}
		});
		
		GradientButton registerButton = new GradientButton("Register", new Color(227, 129, 2), new Color(255, 186, 105));
		registerButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		registerButton.setBounds(574, 434, 171, 34);
		add(registerButton);
		registerButton.addActionListener(e -> { // Registers an attendant using the info entered in the login fields
			
			// Checks if login info is valid to register
			if (Login_Logout.register(usernameField.getText(), String.valueOf(passwordField.getPassword()))) {
				
				// Registers entered login info
				Login_Logout.register(usernameField.getText(), String.valueOf(passwordField.getPassword()));
				
				// Resets text fields
				usernameField.setText("");
				passwordField.setText("");
				
				// Shows success message
				lblError.setText("Attendant successfully registered");
			
			// Login info is not valid
			} else {
				// Resets text fields
				usernameField.setText("");
				passwordField.setText("");
				
				// Shows error message
				lblError.setText("Invalid ID or Password.");
			}
		});
		
		JLabel lblNewLabel = new JLabel();
		ImageIcon icon = new ImageIcon("gui.pictures/leader.png");
		Image image = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH); // 150 is the new width, 200 is the new height
		ImageIcon newIcon = new ImageIcon(image);
		lblNewLabel.setIcon(newIcon);
		lblNewLabel.setBounds(59, 171, 272, 309);
		add(lblNewLabel);
	}
}
