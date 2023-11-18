package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.Currency;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;

public class payWithBankNotes implements BanknoteValidatorObserver{
	
	private BigDecimal amountOwed;
	private Cart cart;
	private static final BigDecimal zero = new BigDecimal("0");
	
	public payWithBankNotes(BigDecimal amountOwed, Cart cart) {
		
		this.cart = cart;
		
		amountOwed = new BigDecimal("0");
		amountOwed.add(amountOwed);
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
		amountOwed.subtract(denomination);
		if(amountOwed.compareTo(zero) <= 0) {
			// cart has been paid for
			cart.endPayment();
		}
		else {
			System.out.println("Remaining Balace: " + amountOwed);
		}
	}

	@Override
	public void badBanknote(BanknoteValidator validator) {
		System.out.println("Please enter valid currency!");
	}
	
}
