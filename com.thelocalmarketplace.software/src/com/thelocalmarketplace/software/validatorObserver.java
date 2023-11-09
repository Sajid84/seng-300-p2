//Jason Very, 30222040
//Tara Strickland, 10105877
//Ali Sebbah, 30172851
//Fikayo Akande, 30185937
//Maleeha Siddiqui, 30179762
package com.thelocalmarketplace.software;

import java.math.BigDecimal;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;

public class validatorObserver implements CoinValidatorObserver{
	Cart c;
	ProductDatabases d;
	/**
	 * Constructor for coin validator listener.
	 * 
	 * @param cart
	 *           cart assocatied to session running on station this listener is registered to
	 * @param db
	 * 			  product database for the store.
	 * 
	 */
	public validatorObserver(Cart cart, ProductDatabases db) {
		c = cart;
		d = db;
	}
	
	/**
	 *If the coin inserted is valid, then we check if we are in a payment state, and that we are not experiencing a weight discrepency
	 *and that the total price of the cart is more than 0.
	 * @throws InvalidArgumentSimulationException
	 * 		if one of the aforementioned prerequisites is not true.
	 * 		
	 * 
	 */
	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		if (c.getCanChange() && c.getInPayment() && c.getCartTotal()>=0) {// no discre
			c.reduce_total_by(value.doubleValue());
		}else {
			throw new InvalidStateSimulationException("cannot add coin");
		}
		
	};
	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		throw new InvalidArgumentSimulationException("coin is not valid");
	};
	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		
	};
	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		
	};
	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
		
	};
	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
		
	};

}
