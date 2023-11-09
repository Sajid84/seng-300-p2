//Jason Very, 30222040
//Tara Strickland, 10105877
//Ali Sebbah, 30172851
//Fikayo Akande, 30185937
//Maleeha Siddiqui, 30179762
package com.thelocalmarketplace.software;

import java.math.BigDecimal;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Mass.MassDifference;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;

public class ScaleListener implements ElectronicScaleListener {
	private Cart c;
	private SelfCheckoutStation station;
	
	/**
	 * Constructor for scale listener.
	 * 
	 * @param cart
	 *           cart assocatied to session running on station this listener is registered to
	 * @param ins
	 * 			  checkout station that this listener is registered to
	 * 
	 */
	public ScaleListener(Cart cart, SelfCheckoutStation ins) {
		c = cart;
		station = ins;
		//d = pd;
	}
	/**
	 *If the mass on the scale has changed, it is compared to the expected mass of the cart, to check weather it is within
	 *the error margin of the scale (sensitivity). If the difference between the expected mass and the actual mass 
	 *is greater than the error margin, the session is blocked, the components of the station are disabled, and the cart
	 *can no longer have items added until the mass on the scale changes to what is expected.
	 * 
	 * 		
	 * 
	 */
	@Override
	public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
		
		
		//if the current weight of the cart is not equal to the current weight on the scale we have a weight discrepency
		double totalCart = c.getMass();
		Mass m = new Mass(totalCart);
		
		//since total cart is in grams i will convert mass to grams
		
		double mass_grams = mass.inGrams().doubleValue();
		Mass mg = new Mass(mass_grams);
		
		//check if the difference expected to actual is within sensativity limit
		
		
		Mass delta_mass = m.difference(mg).abs();
		int compare = delta_mass.compareTo(new Mass(station.baggingArea.getSensitivityLimit().inGrams().doubleValue()));
		
		
		if ( compare==-1 || compare == 0) {
			c.canChange();
			station.scanner.enable();
			station.coinValidator.enable();
		}else {
			c.cantChange();
			station.scanner.disable();
			station.coinValidator.disable();
		}
	};
	@Override
	public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
		
	};
	@Override
	public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
		
	};
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		
		
	};
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		
	};
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		
	};
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		
	};

}
