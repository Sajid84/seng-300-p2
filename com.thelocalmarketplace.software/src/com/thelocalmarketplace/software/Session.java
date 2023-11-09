//Jason Very, 30222040
//Tara Strickland, 10105877
//Ali Sebbah, 30172851
//Fikayo Akande, 30185937
//Maleeha Siddiqui, 30179762
package com.thelocalmarketplace.software;

import java.util.ArrayList;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScanner;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;

/*
 * Represents a self checkout session that a customer will start
 * */

public class Session {
	/**
	 * Flag set when a session has successfully started.
	 */
	private boolean hasStarted;
	/**
	 * product database version that the session uses.
	 */
	public ProductDatabases storeDatabase;
	/**
	 * Checkout station that the session will be running on
	 */
	public SelfCheckoutStation checkoutStation;
	/**
	 * flag notifying when we can begin a payment 
	 */
	public boolean inCheckout;
	/**
	 * Customers cart that is associated with this session
	 */
	public Cart cart;
	/**
	 * barcode listener that will be attached to the scanner of the station
	 */
	public BarcodeListener bl;
	/**
	 * scale listener that will be attached to the bagging area of the station
	 */
	public ScaleListener sl;
	/**
	 * coin validator listener that will be attached to the coin validator of the station
	 */
	public validatorObserver vo;
	
	/**
	 * Constructor from self checkout station and product database.
	 * 
	 * @param scs
	 *            self checkout station associated to the machine the customer is at
	 * @param database
	 * 			  product database for the store
	 * 
	 */
	public Session(SelfCheckoutStation scs, ProductDatabases database){
		cart = new Cart();
		checkoutStation = scs;
		storeDatabase = database;
		bl = new BarcodeListener(cart, database);
		sl = new ScaleListener(cart,scs);
		vo = new validatorObserver(cart, database);
		inCheckout = false;
		
	}
	
	/**
	 * Determines weather a session can start. a session cant start if there are items
	 * in the cart, or if a session has already been started or if a component of the 
	 * checkout station has not been powered on and enabled
	 * 
	 * @return 
	 * 		boolean that is true if a session can be started and false otherwise
	 * 
	 */
	public boolean canStart() {
		if(cart.getNumberItems() > 0) {
			return false;
		}
		if (hasStarted) {
			return false;
		}
		
	
		int c = 0;
		if (checkoutStation.scanner.isPoweredUp() && !checkoutStation.scanner.isDisabled()) {
			c++;
		}
		if (checkoutStation.baggingArea.isPoweredUp()&& !checkoutStation.baggingArea.isDisabled()) {
			c++;
		}
		if (checkoutStation.coinSlot.hasPower() && checkoutStation.coinSlot.isActivated() && !checkoutStation.coinSlot.isDisabled()) {
			c++;
			
		}if (checkoutStation.coinStorage.hasPower() && checkoutStation.coinStorage.isActivated() && !checkoutStation.coinStorage.isDisabled()) {
			c++;
		}
	if (checkoutStation.coinValidator.hasPower() && checkoutStation.coinValidator.isActivated()&& !checkoutStation.coinValidator.isDisabled()) {
			
			c++;
		}
		return c==5;
		
	}
	/**
	 * starts a session. registers listeners to station components and allows the cart to change
	 * 
	 * @throws InvalidStateSimulationException
	 * 		if a session could not be started
	 * 
	 */
	public void startSession(){
		// check list of product is empty too
		//isactivated, power and enabled
		
		if (canStart()) {
			hasStarted = true;
			checkoutStation.scanner.register(bl);
			checkoutStation.baggingArea.register(sl);
			checkoutStation.coinValidator.attach(vo);
			cart.canChange();
		}else {
			throw new InvalidStateSimulationException("could not start session");
		}
	
	}
	/**
	 * checks if the session has started
	 * 
	 * @return boolean representing weather the session has started or not
	 * 
	 */
	public boolean hasSessionStarted() {
		
		return hasStarted;
	}
	/**
	 *Initializes a checkout state, where the customer can pay for an order.
	 * 
	 * 
	 */
	public void checkout() {
		if (cart.getNumberItems()>0 && cart.getCanChange()) {
			inCheckout = true;
			cart.startPayment();
			//cart.cantChange();
		}
		
	}
	/**
	 *Exits a checkout state, where the customer can not pay for an order.
	 * 
	 * 
	 */
	public void exitCheckout() {
		inCheckout = false;
		cart.endPayment();
		//cart.canChange();
	}
	
	
	
}
