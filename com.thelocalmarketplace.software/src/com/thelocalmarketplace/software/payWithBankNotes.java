package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.Currency;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;

import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;

public class payWithBankNotes implements BanknoteValidatorObserver{
	
	private Cart cart;
	
	public payWithBankNotes(BigDecimal amountOwed, Cart cart) {
		
		this.cart = cart;

	}

	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
		// valid banknote has been injected
		if (cart.getCanChange() && cart.getInPayment() && cart.getCartTotal()>=0) {// no discre
			cart.reduce_total_by(denomination.doubleValue());
		}else {
			throw new InvalidStateSimulationException("cannot add bank note");
		}
	}

	@Override
	public void badBanknote(BanknoteValidator validator) {
		System.out.println("Please enter valid currency!");
	}
	
}
