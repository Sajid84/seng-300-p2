//Jason Very, 30222040
//Tara Strickland, 10105877
//Ali Sebbah, 30172851
//Fikayo Akande, 30185937
//Maleeha Siddiqui, 30179762
package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Cart;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.validatorObserver;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

public class PayByCoinTests {
	
	private SelfCheckoutStation checkoutStation;
	private Currency currency;
	private Coin fiveCentCoin;
	private Coin oneDollarCoin;
	private Coin invalidCoin;
	private BigDecimal[] coinDenoms;
	private validatorObserver obs;
	private CoinValidator coinVal; 
	public ProductDatabases database;
	public Barcode barcode;
	public Numeral digits;
	public Cart cart;
	public Session session;
	public BarcodedItem bitem;
	public Mass itemMass;

	public Numeral[] barcode_numeral;
	public BarcodedProduct product;
	public Numeral[] barcode_numeral2;
	public BarcodedProduct product2;
	public Barcode barcode2;
	

	
	//Creates instance of checkout station thats powered up and plugged in
	//Creates dummy coins to be tested
	@Before
	public void setupStation() {
		currency = Currency.getInstance("CAD");
		invalidCoin = new Coin(currency,new BigDecimal(0.35));
		fiveCentCoin = new Coin(currency,new BigDecimal(0.05));
		oneDollarCoin = new Coin(currency,new BigDecimal(1.00));
		checkoutStation = new SelfCheckoutStation();
		checkoutStation.plugIn(PowerGrid.instance());
		checkoutStation.turnOn();
		barcode_numeral = new Numeral[]{digits.one,digits.two, digits.three};
		barcode_numeral2 = new Numeral[]{digits.two,digits.two, digits.one};
		barcode = new Barcode(barcode_numeral);
		barcode2 = new Barcode(barcode_numeral2);
		product = new BarcodedProduct(barcode, "some item",(long)5.99,(double)3.0);
		product2 = new BarcodedProduct(barcode2, "some item 2",(long)20,(double)3000.0);
		database.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		database.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		database.INVENTORY.put(product, 1);
		database.INVENTORY.put(product2, 1);
		BigDecimal denominations[] = new BigDecimal[] {new BigDecimal(1.0), new BigDecimal(2.0), new BigDecimal(0.25), new BigDecimal(0.05)}; 
		checkoutStation.configureCoinDenominations(denominations);
		checkoutStation.configureCoinDispenserCapacity(10);
		checkoutStation.configureCoinStorageUnitCapacity(10);
		checkoutStation.configureCoinTrayCapacity(10);
		checkoutStation.configureCurrency(currency);
		session = new Session(checkoutStation,database);	
	}

	//Tests for when a valid coin is inserted but station is 
	// not plugged in
	@Test(expected = SimulationException.class)
	public void insertValidCoinNoPower() throws DisabledException, CashOverloadException {
		checkoutStation.turnOff();
		session.startSession();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.checkoutStation.scanner.scan(bitem);
		session.checkout();
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
	}
	@Test
	public void insertValidCoinWithPower() throws DisabledException, CashOverloadException {
		session.startSession();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.checkoutStation.scanner.scan(bitem);
		session.checkoutStation.baggingArea.addAnItem(bitem);
		session.checkout();
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 5 - 1 = 4
		assertTrue("cart total was not updated correctlt", session.cart.getCartTotal()==4);
	}@Test(expected = SimulationException.class)
	public void insertInvalidCoinWithPower() throws DisabledException, CashOverloadException {
		session.startSession();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.checkoutStation.scanner.scan(bitem);
		session.checkoutStation.baggingArea.addAnItem(bitem);
		session.checkout();
		session.checkoutStation.coinSlot.sink.receive(invalidCoin);
		//expected price of cart 0.003*5 = 0.015-1.00 = -0.985
		//assertTrue("cart total was not updated correctlt", session.cart.getCartTotal()==0.015);
	}@Test(expected = SimulationException.class)
	public void insertValidCoinWithPowerAfterWeightDiscrepency() throws DisabledException, CashOverloadException {
		session.startSession();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.checkoutStation.scanner.scan(bitem);
		//session.checkoutStation.baggingArea.addAnItem(bitem);
		session.checkout();
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 0.003*5 = 0.015-1.00 = -0.985
		//assertTrue("cart total was not updated correctlt", session.cart.getCartTotal()==0.015);
	}@Test(expected = SimulationException.class)
	public void insertValidCoinWithPowerNotInCheckout() throws DisabledException, CashOverloadException {
		session.startSession();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.checkoutStation.scanner.scan(bitem);
		session.checkoutStation.baggingArea.addAnItem(bitem);
		//session.checkout();
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 0.003*5 = 0.015-1.00 = -0.985
		//assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==0.015);
	}@Test(expected = SimulationException.class)
	public void insertValidCoinWithPowerNegativeValue() throws DisabledException, CashOverloadException {
		session.startSession();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.checkoutStation.scanner.scan(bitem);
		session.checkoutStation.baggingArea.addAnItem(bitem);
		//session.checkout();
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 0.003*5 = 0.015-1.00 = -0.985
		//assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==-0.985);
	}@Test
	public void insertValidCoinWithPowerTwoCoinsSame() throws DisabledException, CashOverloadException {
		session.startSession();
		itemMass = new Mass((double) 3000.0);
		bitem = new BarcodedItem(barcode2, itemMass);
		session.checkoutStation.scanner.scan(bitem);
		session.checkoutStation.baggingArea.addAnItem(bitem);
		session.checkout();
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 20 -1 -1 
		assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==18.00);
	}@Test
	public void insertValidCoinWithPowerTwoCoinsDifferent() throws DisabledException, CashOverloadException {
		session.startSession();
		itemMass = new Mass((double) 3000.0);
		bitem = new BarcodedItem(barcode2, itemMass);
		session.checkoutStation.scanner.scan(bitem);
		session.checkoutStation.baggingArea.addAnItem(bitem);
		session.checkout();
		session.checkoutStation.coinSlot.sink.receive(oneDollarCoin);
		session.checkoutStation.coinSlot.sink.receive(fiveCentCoin);
		//expected price of cart 20 - 1 - .05 = 18.95
		assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==18.95);
	}
	
	
	
	
		
		
	
}


