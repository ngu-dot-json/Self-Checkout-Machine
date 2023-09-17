/**
 *
 * SENG Iteration 3 P3-3 | Order.java
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

/**
 * Contains the list of items and the order total, as well as helper methods.
 */
public class Order {
	private SelfCheckoutStationLogic stationLogic;
	private List<Product> products;
	private List<BarcodedProduct> actualWeight;
	private HashMap<PLUCodedProduct, Double> pluProdActualWeights = new HashMap<PLUCodedProduct, Double>();
	private HashMap<PLUCodedProduct, Double> pluProd = new HashMap<PLUCodedProduct, Double>();

	private List<SellableUnit> sellable;
	/**
	 * The current total price of all the items in this Order.
	 */
	private BigDecimal total;

	/**
	 * The current total unpaid price of all the items in this Order.
	 */
	private BigDecimal totalDue;

	private BigDecimal currentDue;

	/**
	 * Creates an Order.
	 */
	public Order(SelfCheckoutStationLogic stationLogic) {
		this.stationLogic = stationLogic;
		actualWeight = new ArrayList<BarcodedProduct>();
		products = new ArrayList<Product>();
		sellable = new ArrayList<SellableUnit>();
		total = new BigDecimal(0);
		totalDue = new BigDecimal(0);
	}

	/**
	 * Gets the SelfCheckoutStationLogic to which this order belongs.
	 * 
	 * @return The stationLogic to which this order belongs.
	 */
	public SelfCheckoutStationLogic getStationLogic() {
		return stationLogic;
	}

	/**
	 * Gets the list of products.
	 * 
	 * @return The current list of Products in the Order.
	 */
	public List<Product> getProducts() {
		return products;
	}

	/**
	 * Gets the list of SellableUnit.
	 * 
	 * @return The current list of SellableUnit in the Order.
	 */
	public List<SellableUnit> getSellable() {
		return sellable;
	}

	/**
	 * Gets the order's total.
	 * 
	 * @return The current total cost of the Products in the Order.
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * Gets the order's totalDue.
	 * 
	 * @return The current unpaid total cost due.
	 */
	public BigDecimal getTotalDue() {
		return totalDue;
	}

	/**
	 * Adds a given BarcodedProduct to the Order.
	 * 
	 * @param item The BarcodedProduct to be added to the order.
	 */
	public void add(BarcodedProduct item) {
		products.add(item);
		total = total.add(item.getPrice());
		totalDue = totalDue.add(item.getPrice());
	}

	public void addPLU(PLUCodedProduct item, double weight) {
		pluProd.put(item, weight);
		products.add(item);
		total = total.add(item.getPrice().multiply(new BigDecimal(weight / 1000)));
		totalDue = totalDue.add(item.getPrice().multiply(new BigDecimal(weight / 1000)));
	}

	public void addBag(ReusableBag bag) {
		sellable.add(bag);
		total = total.add(this.stationLogic.priceOfBag);
		totalDue = totalDue.add(this.stationLogic.priceOfBag);
	}

	// a method overload of add will be implemented later for non-barcoded items.

	/**
	 * Makes a payment of a given value. Updates the current total accordingly.
	 * 
	 * @param value The value to be paid and subtracted from the total.
	 */
	public void addPayment(int value) {
		totalDue = totalDue.subtract(new BigDecimal(value));
	}

	public void addPayment(BigDecimal value) {
		totalDue = totalDue.subtract((value));
	}

	public double getPLUWeightOfItem(PLUCodedProduct plu) {
		return pluProd.get(plu);
	}

	public void removePLU(PLUCodedProduct plu, double weight) {
		for (int i = 0; i < products.size(); i++) {
			if (plu.equals(products.get(i))) {
				pluProd.remove(plu);
				if (pluProdActualWeights.containsKey(plu)) {
					pluProdActualWeights.remove(plu);
				}
				products.remove(i);
				break;
			}
		}
		total = total.subtract(plu.getPrice().multiply(new BigDecimal(weight / 1000)));
		totalDue = totalDue.subtract(plu.getPrice().multiply(new BigDecimal(weight / 1000)));

	}

	public void removeBarcodedProduct(BarcodedProduct bar) {
		for (int i = 0; i < products.size(); i++) {
			if (bar.equals(products.get(i))) {
				if (actualWeight.contains(bar)) {
					actualWeight.remove(bar);
				}
				products.remove(i);
				break;
			}
		}
		total = total.subtract(bar.getPrice());
		totalDue = totalDue.subtract(bar.getPrice());
	}

//	//ReusableBag bag
//	public void addBag() { 
//		
//	}

	public void updateWeightArray(BarcodedProduct bar) {
		actualWeight.add(bar);
	}

	public void updateWeightHashMap(PLUCodedProduct plu, double weight) {
		pluProdActualWeights.put(plu, weight);
	}

	public double getActualWeight() {
		double total = 0;
		for (BarcodedProduct prod : actualWeight) {
			total += prod.getExpectedWeight();
		}
		for (Entry<PLUCodedProduct, Double> entry : pluProdActualWeights.entrySet()) {
			total += entry.getValue();
			// Do something with key and value
		}
		return total;

	}
}
