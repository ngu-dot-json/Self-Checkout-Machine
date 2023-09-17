/**
 *
 * SENG Iteration 3 P3-3 | SelectLanguage.java
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

import java.util.ArrayList;

public class SelectLanguage {

	// class variables
	private SelfCheckoutStationLogic scsl;
	private ArrayList<String> supportedLanguages;
	private String customerLanguageChoice;
	private CustomerIO IO;

	// initializer
	public SelectLanguage(SelfCheckoutStationLogic scsl, ArrayList<String> supportedLanguages, CustomerIO IO) {
		this.scsl = scsl;
		this.supportedLanguages = supportedLanguages;
		this.IO = IO;
	}

	// void function to change language on machine
	public void changeLanguage() {

		if (scsl == null || IO == null || supportedLanguages == null) {
			throw new NullPointerException();
		}

		// display the supported languages to output
		IO.displayLanguages(supportedLanguages);

		// takes in the user selected supported language

		customerLanguageChoice = IO.getSelectedLanguage();

		if (supportedLanguages.contains(customerLanguageChoice)) {
			scsl.setLanguage(customerLanguageChoice);
		} else {
			throw new NullPointerException();
		}
	}

	// void function to cancel action for selecting language
	public void cancelChoice() {
		IO.cancelAction();
	}

}