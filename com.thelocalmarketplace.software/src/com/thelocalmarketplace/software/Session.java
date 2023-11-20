//Yang Yang           30156356
// Sana Abdelhalem    30163580
// Ali Al Yasseen     30151000
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851

//Edited on the base of Iteration1


package com.thelocalmarketplace.software;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Vector;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
/*
* Represents a self checkout session that a customer will start
* */
public class Session {
	/**
	 * Flag set when a session has successfully started.
	 */
	private boolean hasStarted;
	/**
	 * Checkout station that the session will be running on
	 */
	public AbstractSelfCheckoutStation station;
	
	private boolean isStationBlockedForBulkyItem;
	
	/*
	 * flag to check the register status of devices
	 */
	public Boolean registerStatus;
	/**
	 * flag notifying when we can begin a payment
	 */
	public boolean inCheckout;
	
	/**
	 * Customers cart that is associated with this session
	 */
	public Cart cart;
	
	/**
	 * barcode scanner listener that will be attached to the scanner of the station
	 */
	public ScannerListener scannerListener;
	
	/**
	 * scale listener that will be attached to the bagging area of the station
	 */
	public ScaleListener scaleListener;
	
	/**
	 * coin validator listener that will be attached to the coin validator of the station
	 */
	public validatorObserver validatorObserver;
	/**
	 * Constructor without any arguments
	 */
	private Session(){}
	/**
	 * Constructor from self checkout station and product database.
	 *
	 * @param station
	 */
	public Session(AbstractSelfCheckoutStation station){
		cart = new Cart();
		inCheckout = false;
		this.registerStatus = false;
		registerStation(station);
		
		this.isStationBlockedForBulkyItem = false;
		
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
		boolean res = false;
		
		if(cart.getNumberItems() > 0)
		{
			return res;
		}
		
		if (hasStarted)
		{
			return res;
		}
		
		if(this.station != null)
		{
			res = canStartDevice(this.station.mainScanner,this.station.handheldScanner,
					this.station.baggingArea,this.station.scanningArea);
			return res;
		}else
		{
			throw new InvalidArgumentSimulationException("Cannot start the station");
		}
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
			cart.canChange();
		}else {
			throw new InvalidStateSimulationException("Could not start session");
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
	 */
	public void exitCheckout() {
		inCheckout = false;
		cart.endPayment();
		giveChange(cart.getCartTotal());
		//cart.canChange();
	}
	
	
	/**
	 * Register the station
	 * @param station
	 */
	public void registerStation(AbstractSelfCheckoutStation station){
		if(station == null ){
			throw new InvalidStateSimulationException("Invalid Argument");
		}
		
		if(this.registerStatus){
			throw new InvalidStateSimulationException("Invalid State: already registered");
		}
		scannerListener = new ScannerListener(cart);
		scaleListener = new ScaleListener(cart,station);
		validatorObserver = new validatorObserver(cart);
		this.station = station;
		registerListener(this.station.mainScanner,this.station.handheldScanner,this.station.baggingArea,this.station.scanningArea);
		//Change the register status of station to be true
		this.registerStatus = true;
	}
	/**
	 * Register the listeners of devices
	 * @param mainScanner
	 * @param handheldScanner
	 * @param baggingArea
	 * @param scanningArea
	 */
	private void registerListener(IBarcodeScanner mainScanner,IBarcodeScanner handheldScanner,
			IElectronicScale baggingArea,IElectronicScale scanningArea)
	{
		mainScanner.register(scannerListener);
		handheldScanner.register(scannerListener);
		baggingArea.register(scaleListener);
		scanningArea.register(scaleListener);
	}
	
	/**
	 * Determines if the devices that would be used in the checkout process could be
	 * started, will check if the devices are powered up and not disabled.
	 * @param mainScanner
	 * @param handheldScanner
	 * @param baggingArea
	 * @param scanningArea
	 * @return
	 */
	private boolean canStartDevice(IDevice mainScanner,IDevice handheldScanner,
								 IDevice baggingArea,IDevice scanningArea){
		boolean res = true;
		
		res &= mainScanner.isPoweredUp() && !mainScanner.isDisabled();
		res &= handheldScanner.isPoweredUp() && !handheldScanner.isDisabled();
		res &= baggingArea.isPoweredUp() && !baggingArea.isDisabled();
		res &= scanningArea.isPoweredUp() && !scanningArea.isDisabled();
		return res;
	}
	/**
	* Blocks the self-checkout station from further customer input while handling a bulky item.
	* This could involve disabling certain hardware components or user interface elements.
	*/
	public void blockStationForBulkyItem() {
	    // Set the flag indicating the station is blocked for bulky item processing.
	    this.isStationBlockedForBulkyItem = true;
	    // Disable necessary components
	    if (this.station != null) {
	        // Disable scanners
	        if (this.station.mainScanner != null) {
	            this.station.mainScanner.disable();
	        }
	        if (this.station.handheldScanner != null) {
	            this.station.handheldScanner.disable();
	        }
	        // Disable scale
	        if (this.station.baggingArea != null) {
	            this.station.baggingArea.disable();
	        }
	        // Additional disabling logic for other components (e.g., payment system)
	        // Example:
	        // if (this.station.cardReader != null) {
	        // this.station.cardReader.disable();
	        // }
	        // Update GUI if necessary
	        // Example: updateGUIForBulkyItemProcessing();
	    }
	}

	/**
	* Unblocks the self-checkout station, re-enabling components disabled during bulky item processing.
	*/
	public void unblockStationForBulkyItem() {
	    // Reset the flag
	    this.isStationBlockedForBulkyItem = false;
	    // Re-enable components
	    if (this.station != null) {
	        // Re-enable scanners
	        if (this.station.mainScanner != null) {
	            this.station.mainScanner.enable();
	        }
	        if (this.station.handheldScanner != null) {
	            this.station.handheldScanner.enable();
	        }
	        // Re-enable scale
	        if (this.station.baggingArea != null) {
	            this.station.baggingArea.enable();
	        }
	        if (this.station.cardReader != null) {
	            this.station.cardReader.enable();
	        }
	    }
	}

	/**
	* Adjusts the checkout session for handling a bulky item. This could involve
	* modifying the expected total weight at the bagging area.
	*
	* @param item The bulky item being processed.
	*/
	public void adjustForBulkyItem(BarcodedItem item) {
	    if (item == null) {
	        throw new IllegalArgumentException("Item cannot be null.");
	    }
	    // Assuming there is a way to get the expected weight from the bagging area
	    // and a method to set the expected weight.
	    // Adjust the expected weight to account for the bulky item.
	    Mass expectedWeight = this.station.baggingArea.getExpectedWeight();
	    expectedWeight = expectedWeight.subtract(item.getMass());
	    this.station.baggingArea.setExpectedWeight(expectedWeight);
	    // Optionally, add the bulky item to the cart with a special flag or category.
	    cart.addBulkyItem(item);
	}

	/**
	* Processes a normal item by scanning it and adding it to the cart.
	*
	* @param item The item to be processed.
	*/
	public void processNormalItem(BarcodedItem item) {
	    if (item == null) {
	        throw new IllegalArgumentException("Item cannot be null.");
	    }
	    // Scan the item's barcode and add it to the cart.
	    Barcode barcode = item.getBarcode();
	    // Assuming the scannerListener has a method to process the scanned item.
	    scannerListener.scanItem(barcode);
	    // Add the item to the cart.
	    cart.addItem(item);
	    // Update the expected weight in the bagging area.
	    Mass expectedWeight = this.station.baggingArea.getExpectedWeight();
	    expectedWeight = expectedWeight.add(item.getMass());
	    this.station.baggingArea.setExpectedWeight(expectedWeight);
	}
	
	/**
	* Returns change to user in form of banknotes and coins.
	* 
	* @param returnAmount
	* 	double representing the total change to be dispensed
	*/
	public void giveChange(double returnAmount) throws DisabledException, CashOverloadException {
		BigDecimal toReturn = BigDecimal.valueOf(Math.abs(returnAmount));
		Vector<BigDecimal> noteDenoms = new Vector<BigDecimal>();
		Vector<BigDecimal> coinDenoms = new Vector<BigDecimal>(station.coinDenominations);
		for(int i = 0; i < station.banknoteDenominations.length; i++) {
			noteDenoms.add(station.banknoteDenominations[i]);
		}
		coinDenoms.sort(Comparator.reverseOrder());
		noteDenoms.sort(Comparator.reverseOrder());
		
		for(int i = 0; i < noteDenoms.size(); i++) {
			while(toReturn.compareTo(noteDenoms.elementAt(i)) >= 0) {
				try {
					station.banknoteDispensers.get(noteDenoms.elementAt(i)).emit();
					toReturn.subtract(noteDenoms.elementAt(i));
				} catch (NoCashAvailableException e) {
					// notify attendant of empty dispenser
				} catch (DisabledException | CashOverloadException e) {}
			}
		}
		station.banknoteOutput.dispense();
		
		for(int i = 0; i < coinDenoms.size(); i++) {
			while(toReturn.compareTo(coinDenoms.elementAt(i)) >= 0) {
				try {
					station.coinDispensers.get(coinDenoms.elementAt(i)).emit();
					toReturn.subtract(coinDenoms.elementAt(i));
				} catch (NoCashAvailableException e) {
					// notify attendant of empty dispenser
				} catch (DisabledException | CashOverloadException e) {}
			}
		}
		if (toReturn.compareTo(BigDecimal.ZERO) == 1) {
			// notify attendant for remaining change due
		}

		
		
	}
}
