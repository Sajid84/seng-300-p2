// Shaikh Sajid Mahmood 30182396
// Sana Abdelhalem    30163580
// Ali Al Yasseen     30151000
// Yang Yang          30156356
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851

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
	/**
	 * Constructor for coin validator listener.
	 * 
	 * @param cart
	 *           cart assocatied to session running on station this listener is registered to
	 * 
	 */
	public validatorObserver(Cart cart) {
		c = cart;
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
