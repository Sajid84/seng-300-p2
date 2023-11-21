// Yang Yang          30156356
// Sana Abdelhalem    30163580
// Ali Al Yasseen     30151000
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851
// Shaikh Sajid Mahmood 30182396

package src.com.thelocalmarketplace.software.test;

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
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Cart;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.validatorObserver;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

public class PayByCoinTests {
	
	public AbstractSelfCheckoutStation station;
	public Session session;
	public PowerGrid powerGrid;
	
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
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		station = new SelfCheckoutStationBronze();
		powerGrid =PowerGrid.instance();
		PowerGrid.engageUninterruptiblePowerSource();
		
		currency = Currency.getInstance("CAD");
		invalidCoin = new Coin(currency,new BigDecimal(0.35));
		fiveCentCoin = new Coin(currency,new BigDecimal(0.05));
		oneDollarCoin = new Coin(currency,new BigDecimal(1.00));

		//station.turnOn();
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
	}

	
	//Prepare the station to be plugged in, powered on and start the checkout progress.
		public void prepareStation()
		{
			station.plugIn(powerGrid);
			station.turnOn();
			session = new Session(this.station);
			session.startSession();
		}
		
		
	//Tests for when a valid coin is inserted but station is 
	// not plugged in
	@Test(expected = SimulationException.class)
	public void insertValidCoinNoPower() throws DisabledException, CashOverloadException {
//		station.turnOff();
//		session.startSession();
		station.plugIn(powerGrid);
		station.turnOff();
		session = new Session(this.station);
		session.startSession();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.station.mainScanner.scan(bitem);
		session.checkout();
		session.station.coinSlot.sink.receive(oneDollarCoin);
	}
	
	@Test
	public void insertValidCoinWithPower() throws DisabledException, CashOverloadException {
		prepareStation();
		//session.startSession();
		//System.out.println("Before test, The cart total is" + session.cart.getCartTotal());
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.station.mainScanner.scan(bitem);
		session.station.baggingArea.addAnItem(bitem);
		//System.out.println("Before insert, The cart total is" + session.cart.getCartTotal());
		session.checkout();
		session.station.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 5 - 1 = 4
		//System.out.println("After insertion, The cart total is" + session.cart.getCartTotal());
		assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==4);
		
	}
	
	@Test(expected = SimulationException.class)
	public void insertInvalidCoinWithPower() throws DisabledException, CashOverloadException {
		prepareStation();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.station.mainScanner.scan(bitem);
		session.station.baggingArea.addAnItem(bitem);
		session.checkout();
		session.station.coinSlot.sink.receive(invalidCoin);
		//expected price of cart 0.003*5 = 0.015-1.00 = -0.985
		//assertTrue("cart total was not updated correctlt", session.cart.getCartTotal()==0.015);
	}
	
	@Test(expected = SimulationException.class)
	public void insertValidCoinWithPowerAfterWeightDiscrepency() throws DisabledException, CashOverloadException {
		prepareStation();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.station.mainScanner.scan(bitem);
		//session.checkoutStation.baggingArea.addAnItem(bitem);
		session.checkout();
		session.station.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 0.003*5 = 0.015-1.00 = -0.985
		//assertTrue("cart total was not updated correctlt", session.cart.getCartTotal()==0.015);
	}
	
	@Test(expected = SimulationException.class)
	public void insertValidCoinWithPowerNotInCheckout() throws DisabledException, CashOverloadException {
		prepareStation();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.station.mainScanner.scan(bitem);
		session.station.baggingArea.addAnItem(bitem);
		//session.checkout();
		session.station.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 0.003*5 = 0.015-1.00 = -0.985
		//assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==0.015);
	}
	
	@Test(expected = SimulationException.class)
	public void insertValidCoinWithPowerNegativeValue() throws DisabledException, CashOverloadException {
		prepareStation();
		itemMass = new Mass((double) 3.0);
		bitem = new BarcodedItem(barcode, itemMass);
		session.station.mainScanner.scan(bitem);
		session.station.baggingArea.addAnItem(bitem);
		//session.checkout();
		session.station.coinSlot.sink.receive(oneDollarCoin);
		session.station.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 0.003*5 = 0.015-1.00 = -0.985
		//assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==-0.985);
	}
	
	@Test
	public void insertValidCoinWithPowerTwoCoinsSame() throws DisabledException, CashOverloadException {
		prepareStation();
		itemMass = new Mass((double) 3000.0);
		bitem = new BarcodedItem(barcode2, itemMass);
		session.station.mainScanner.scan(bitem);
		session.station.baggingArea.addAnItem(bitem);
		session.checkout();
		session.station.coinSlot.sink.receive(oneDollarCoin);
		session.station.coinSlot.sink.receive(oneDollarCoin);
		//expected price of cart 20 -1 -1 
		assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==18.00);
	}
	
	@Test
	public void insertValidCoinWithPowerTwoCoinsDifferent() throws DisabledException, CashOverloadException {
		prepareStation();
		itemMass = new Mass((double) 3000.0);
		bitem = new BarcodedItem(barcode2, itemMass);
		session.station.mainScanner.scan(bitem);
		session.station.baggingArea.addAnItem(bitem);
		session.checkout();
		session.station.coinSlot.sink.receive(oneDollarCoin);
		session.station.coinSlot.sink.receive(fiveCentCoin);
		//expected price of cart 20 - 1 - .05 = 18.95
		assertTrue("cart total was not updated correctly", session.cart.getCartTotal()==18.95);
	}
}


