//Yang Yang:30156356
//Edited on the base of Iteration1

package com.thelocalmarketplace.software.test;

import ca.ucalgary.seng300.simulation.SimulationException;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem; 
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;

import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;

import static org.junit.Assert.assertTrue;

public class GoldStationHandheldScannerTest {

	public AbstractSelfCheckoutStation station;
	Session session;
	PowerGrid powerGrid;
	
	
//	stuff for database
//	public ProductDatabases database_one_item;
	public Barcode barcode;
	public Barcode barcode2;
	public Numeral digits;
	
	public BarcodedItem bitem;
	public Mass itemMass;
	
	public BarcodedItem bitem2;
	public Mass itemMass2;
	public BarcodedItem bitem3;
	public Mass itemMass3;
	
	public BarcodedItem bitem4;
	public Mass itemMass4;
	public Numeral[] barcode_numeral;
	public Numeral[] barcode_numeral2;
	public Numeral[] barcode_numeral3;
	public Barcode b_test;
	public BarcodedProduct product;
	public BarcodedProduct product2;
	public BarcodedProduct product3;
	
	@Before
	public void setUp() {
		//d1 = new dummyProductDatabaseWithOneItem();
		//d2 = new dummyProductDatabaseWithNoItemsInInventory();
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		station = new SelfCheckoutStationGold();

		powerGrid =PowerGrid.instance();
		PowerGrid.engageUninterruptiblePowerSource();


		initDataBase();

		//initialize barcoded item
		itemMass = new Mass(1000000);
		bitem = new BarcodedItem(barcode2, itemMass);
		itemMass2 = new Mass(300.0);//300.0 grams
		bitem2 = new BarcodedItem(barcode, itemMass2);
		itemMass3 = new Mass(3.0);//3.0 grams
		bitem3 = new BarcodedItem(barcode, itemMass3);
		bitem4 = new BarcodedItem(b_test, itemMass3);
	}

	private void initDataBase(){
		//initialize database
		barcode_numeral = new Numeral[]{digits.one,digits.two, digits.three};
		barcode_numeral2 = new Numeral[]{digits.three,digits.two, digits.three};
		barcode_numeral3 = new Numeral[]{digits.three,digits.three, digits.three};
		barcode = new Barcode(barcode_numeral);
		barcode2 = new Barcode(barcode_numeral2);
		b_test = new Barcode(barcode_numeral3);
		product = new BarcodedProduct(barcode, "some item",(long)5.99,(double)3.0);
		product2 = new BarcodedProduct(barcode2, "some item 2",(long)1.00,(double)300.0);
		product3 = new BarcodedProduct(b_test, "some item 3",(long)1.00,(double)3.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.INVENTORY.put(product, 1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.INVENTORY.put(product2, 1);
	}

	private void clearDataBase(){
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		ProductDatabases.INVENTORY.clear();
		ProductDatabases.INVENTORY.clear();
	}
	
	//Prepare the station to be plugged in, powered on and start the checkout progress.
	public void prepareStation()
	{
		station.plugIn(powerGrid);
		station.turnOn();
		session = new Session(this.station);
		session.startSession();
	}
	
	//Test for when there is no power for the barcode to be scanned
	@Test (expected = SimulationException.class)
	public void testNoPowerOff(){
		station.plugIn(powerGrid);
		station.turnOff();
		session = new Session(this.station);
		session.startSession();
		station.handheldScanner.scan(bitem2);
	}

	@Test
	public void testPowerOn(){
		prepareStation();
		station.handheldScanner.scan(bitem2);
		
		assertTrue("item was not successfully added to cart", session.cart.getNumberItems() ==1);
	}
	@Test
	public void testPowerOnRightItem(){
		prepareStation();
		station.handheldScanner.scan(bitem2);
		long s = session.cart.getLastItem().getPrice();
		
		assertTrue("item was not successfully added to cart", s == (long) 5.99);
	}
	@Test
	public void testPowerOntwoScansNoBaggingAreaUpdates(){
		prepareStation();
		station.handheldScanner.scan(bitem2);
		station.handheldScanner.scan(bitem);
		
		assertTrue("item was not successfully added to cart", session.cart.getNumberItems() == 1);
	}
	@Test
	public void testPowerOntwoScansWithBaggingAreaUpdates(){
		prepareStation();
		station.handheldScanner.scan(bitem3);
		station.baggingArea.addAnItem(bitem3);
		station.handheldScanner.scan(bitem2);
		
		assertTrue("item was not successfully added to cart", session.cart.getNumberItems() == 2);
	}
	
	@Test
	public void testPowerOntwoScansWithBaggingAreaUpdatesRightItem(){
		prepareStation();
		station.handheldScanner.scan(bitem3);
		station.baggingArea.addAnItem(bitem3);
		station.handheldScanner.scan(bitem);
		long s = session.cart.getLastItem().getPrice();
		
		assertTrue("item was not successfully added to cart", s == (long) 1.00);
	}
	
	@Test(expected = SimulationException.class)
	public void testPowerOnScanItemNoInventory(){
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(b_test, product3);
		ProductDatabases.INVENTORY.put(product3, 0);
		prepareStation();
		station.handheldScanner.scan(bitem4);
		
//		assertTrue("item was not successfully added to cart", s == (long) 1.00);
	}
	
	@Test(expected = SimulationException.class)
	public void testPowerOnScanItemNotInDatabase(){
		prepareStation();
		station.handheldScanner.scan(bitem4);
		
		//assertTrue("item was not successfully added to cart", s == (long) 1.00);
	}
	//test for when the power is turned on for the barcode to be scanned
	
}
