//Jason Very, 30222040
//Tara Strickland, 10105877
//Ali Sebbah, 30172851
//Fikayo Akande, 30185937
//Maleeha Siddiqui, 30179762
package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScanner;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.DisabledException;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.BarcodeListener;
import com.thelocalmarketplace.software.Cart;
import com.thelocalmarketplace.software.Session;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

public class AddItemBarcodeTests {
	
	SelfCheckoutStation station;
	Session session;
	PowerGrid pg;
	
	
	//stuff for database
	public ProductDatabases database_one_item;
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
	@Before public void setUp() {
		//d1 = new dummyProductDatabaseWithOneItem();
		//d2 = new dummyProductDatabaseWithNoItemsInInventory();
		station = new SelfCheckoutStation();
		
		pg =PowerGrid.instance();
		pg.engageUninterruptiblePowerSource();
		
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
		database_one_item.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		database_one_item.INVENTORY.put(product, 1);
		database_one_item.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		database_one_item.INVENTORY.put(product2, 1);
		

		//initialize barcoded item
		itemMass = new Mass((long) 1000000);
		bitem = new BarcodedItem(barcode2, itemMass);
		itemMass2 = new Mass((double) 300.0);//300.0 grams
		bitem2 = new BarcodedItem(barcode, itemMass2);
		itemMass3 = new Mass((double) 3.0);//3.0 grams
		bitem3 = new BarcodedItem(barcode, itemMass3);
		bitem4 = new BarcodedItem(b_test, itemMass3);
	}
	//Test for when there is no power for the barcode to be scanned
	@Test (expected = SimulationException.class) public void testNoPowerOff(){
		station.plugIn(pg);
		station.turnOff();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem2);
	}@Test public void testPowerOn(){
		station.plugIn(pg);
		station.turnOn();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem2);
		
		assertTrue("item was not successfully added to cart", session.cart.getNumberItems() ==1);
	}@Test public void testPowerOnRightItem(){
		station.plugIn(pg);
		station.turnOn();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem2);
		long s = session.cart.getLastItem().getPrice();
		
		assertTrue("item was not successfully added to cart", s == (long) 5.99);
	}@Test public void testPowerOntwoScansNoBaggingAreaUpdates(){
		station.plugIn(pg);
		station.turnOn();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem2);
		station.scanner.scan(bitem);
		
		assertTrue("item was not successfully added to cart", session.cart.getNumberItems() == 1);
	}@Test public void testPowerOntwoScansWithBaggingAreaUpdates(){
		station.plugIn(pg);
		station.turnOn();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem3);
		station.baggingArea.addAnItem(bitem3);
		station.scanner.scan(bitem2);
		
		assertTrue("item was not successfully added to cart", session.cart.getNumberItems() == 2);
	}@Test public void testPowerOntwoScansWithBaggingAreaUpdatesRightItem(){
		station.plugIn(pg);
		station.turnOn();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem3);
		station.baggingArea.addAnItem(bitem3);
		station.scanner.scan(bitem);
		long s = session.cart.getLastItem().getPrice();
		
		assertTrue("item was not successfully added to cart", s == (long) 1.00);
	}@Test(expected = SimulationException.class) 
	public void testPowerOnScanItemNoInventory(){
		station.plugIn(pg);
		database_one_item.BARCODED_PRODUCT_DATABASE.put(b_test, product3);
		database_one_item.INVENTORY.put(product3, 0);
		station.turnOn();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem4);
		
		//assertTrue("item was not successfully added to cart", s == (long) 1.00);
	}@Test(expected = SimulationException.class) 
	public void testPowerOnScanItemNotInDatabase(){
		station.plugIn(pg);
		station.turnOn();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem4);
		
		//assertTrue("item was not successfully added to cart", s == (long) 1.00);
	}
	
	//test for when the power is turned on for the barcode to be scanned
	
}

