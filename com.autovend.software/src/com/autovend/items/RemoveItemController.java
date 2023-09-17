/**
 *
 * SENG Iteration 3 P3-3 | RemoveItemController.java
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

import com.autovend.SelfCheckoutStationLogic;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

public class RemoveItemController {
	boolean PLU = false;
	boolean barProd = false;
	boolean isBag = false;
	BarcodedProduct barcodedProd =null;
	PLUCodedProduct pluProd = null;
	SelfCheckoutStationLogic logicStation;
	double expectedWeight;
	
	public RemoveItemController(SelfCheckoutStationLogic selfCheckoutStationLogic) {
		
		logicStation =selfCheckoutStationLogic;
	}
	public void removeFromOrder(Product prod) {
		if(prod instanceof PLUCodedProduct) {
			PLU =true;
			pluProd = (PLUCodedProduct) prod;
			expectedWeight = logicStation.getExpectedPLUWeightForRemoval(pluProd);
			logicStation.removePLUProduct(pluProd, expectedWeight);
		}
		else {
			barProd = true;
			barcodedProd = (BarcodedProduct)prod;
			expectedWeight =barcodedProd.getExpectedWeight();
			logicStation.removeBarcodedProduct(barcodedProd);
		}
		logicStation.suspend();
	}
	public void checkForDiscrepancy(double changeInWeight) {
		if(PLU) {
			PLU = false;
			if(expectedWeight == changeInWeight) {
			expectedWeight = 0;
			logicStation.unsuspend();
			}else {
				logicStation.notifyAttendantIO("Weight discrepancy in bagging area.\n");
				logicStation.notifyCustomerIO("Weight discrepancy in bagging area.\n");
			}
		}
		else if(barProd) {
			barProd =false;
			if(expectedWeight == changeInWeight) {
			expectedWeight = 0;
			logicStation.unsuspend();
			}else {
				logicStation.notifyAttendantIO("Weight discrepancy in bagging area.\n");
				logicStation.notifyCustomerIO("Weight discrepancy in bagging area.\n");
			}
		}
		else {
			logicStation.notifyAttendantIO("Weight discrepancy in bagging area.\n");
			logicStation.notifyCustomerIO("Weight discrepancy in bagging area.\n");
		}
		}
		
	}
	
