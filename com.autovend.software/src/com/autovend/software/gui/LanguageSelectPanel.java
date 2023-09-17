/**
 *
 * SENG Iteration 3 P3-3 | LanguageSelectPanel.java
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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.autovend.SelfCheckoutStationLogic;
/**
 * This is a panel that displays the language selection screen.
 * */
public class LanguageSelectPanel extends JPanel{
	private CustomerScreen customerScreen;
	private GradientButton english;
	private static GradientButton french;
	private GradientButton back;
	
	public LanguageSelectPanel(CustomerScreen customerScreen, SelfCheckoutStationLogic stationLogic) {
		this.customerScreen = customerScreen;
		setLayout(null);
		this.setPreferredSize(new Dimension(800,600));
		setBackground(new Color(205, 219, 210));

		english = new GradientButton("English",new Color(55, 196, 67), Color.GREEN);
		english.setBounds(121,130,252,287);
		add(english);
		// if customer clicks on English button, the language changes to English
		english.addActionListener(e ->{
			stationLogic.setLanguage("english");
			customerScreen.change("welcomePanel");
		});
		
		french = new GradientButton("Français", new Color(55, 196, 67), Color.GREEN);
		french.setBounds(415,130,252,280);
		add(french);
		// if customer clicks on Français button, the language changes to French
		french.addActionListener(e ->{
			stationLogic.setLanguage("french");
			customerScreen.change("welcomePanel");
		});
		
		
		back = new GradientButton("Back", Color.GRAY, Color.LIGHT_GRAY);
		back.setBounds(303,445,171,81);
		add(back);
		// if customer clicks on back button, the screen changes to the previous screen(welcome panel)
		back.addActionListener(e ->{
			customerScreen.change("welcomePanel");
		});
	}
	
	/**
	 * This method updates the language of the station logic
	 * 
	 * @param language
	 * 			The supported languages for changing are English and French.
	 * */	
	public void updateLanguage(String language) {
		if (language == "english") {
			back.setText("Back");
		} else {
			back.setText("Dos");
		}
	}
	
	/**
	 *  getter methods for testing purpose
	 * */
	public static JButton getFrench() {
		return french;
	}
	

}
