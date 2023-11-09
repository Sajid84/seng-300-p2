//Jason Very, 30222040
//Tara Strickland, 10105877
//Ali Sebbah, 30172851
//Fikayo Akande, 30185937
//Maleeha Siddiqui, 30179762
package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Cart;
import com.thelocalmarketplace.software.Session;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;

public class weightDiscrepencyTests {
	SelfCheckoutStation station;
	Session session;
	PowerGrid pg;
	
	
	//stuff for database
	public ProductDatabases database_one_item;
	public Barcode barcode;
	public Numeral digits;
	
	public BarcodedItem bitem;
	public Mass itemMass;
	
	public BarcodedItem bitem2;
	public Mass itemMass2;
	public BarcodedItem bitem3;
	public Mass itemMass3;
	public Numeral[] barcode_numeral;
	public BarcodedProduct product;
	
	@Before public void setUp() {
		//d1 = new dummyProductDatabaseWithOneItem();
		//d2 = new dummyProductDatabaseWithNoItemsInInventory();
		station = new SelfCheckoutStation();
		Cart cart;
		
		pg =PowerGrid.instance();
		pg.engageUninterruptiblePowerSource();
		
		//initialize database
		barcode_numeral = new Numeral[]{digits.one,digits.two, digits.three};
		barcode = new Barcode(barcode_numeral);
		product = new BarcodedProduct(barcode, "some item",5,(double)3.0);
		database_one_item.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		database_one_item.INVENTORY.put(product, 1);


		//initialize barcoded item
		itemMass = new Mass((long) 1000000);
		bitem = new BarcodedItem(barcode, itemMass);
		itemMass2 = new Mass((double) 300.0);//300.0 grams
		bitem2 = new BarcodedItem(barcode, itemMass2);
		itemMass3 = new Mass((double) 3.0);//3.0 grams
		bitem3 = new BarcodedItem(barcode, itemMass3);
	}
	@Test (expected = SimulationException.class) public void testWeightDiscrepencyWithNoPower() {
		station.plugIn(pg);
		station.turnOff();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem);
		station.baggingArea.addAnItem(bitem);
		
	}
	@Test (expected = SimulationException.class)public void testWeightDiscrepencyWithoutPowerTurnOn() {
		station.turnOn();
		session = new Session(station, database_one_item);
		station.scanner.scan(bitem2);
		session.startSession();
		station.baggingArea.addAnItem(bitem2);
	}
	@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepency() {
		station.plugIn(pg);
		station.turnOn();
		session = new Session(station, database_one_item);
		session.startSession();
		station.scanner.scan(bitem3);
		station.baggingArea.addAnItem(bitem3);
		assertTrue("no weight discrepency detected", session.cart.getCanChange());
	}
	@Test public void testWeightDiscrepencyWithPowerTurnOnHasDiscrepencyDifferentThatItem() {
		station.plugIn(pg);
		station.turnOn();
		
		session = new Session(station, database_one_item);
		session.startSession();
		
		station.scanner.scan(bitem2);
		station.baggingArea.addAnItem(bitem);
		assertTrue("weight discrepency not tedected", !session.cart.getCanChange());
	}@Test public void testWeightDiscrepencyWithPowerTurnOnHasDiscrepencyNoItem() {
		station.plugIn(pg);
		station.turnOn();
		
		session = new Session(station, database_one_item);
		session.startSession();
		
		//station.scanner.scan(bitem2);
		station.baggingArea.addAnItem(bitem);
		assertTrue("weight discrepency not tedected", !session.cart.getCanChange());
	}@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepencyOnSensativity() {
		station.plugIn(pg);
		station.turnOn();
		
		session = new Session(station, database_one_item);
		session.startSession();
		
		//sensativity of the scale is 100mg
		double  sensativity = 0.1;
		Mass m = new Mass((double) sensativity+3.0);
		BarcodedItem i= new BarcodedItem(barcode, m);
		station.scanner.scan(bitem2);
		station.baggingArea.addAnItem(i);
		assertTrue("weight discrepency tedected", session.cart.getCanChange());
	}@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepencyWithinSensativity() {
		station.plugIn(pg);
		station.turnOn();
		
		session = new Session(station, database_one_item);
		session.startSession();
		
		//sensativity of the scale is 100mg
		double  sensativity = 0.1/2;
		Mass m = new Mass((double) sensativity+3.0);
		BarcodedItem i= new BarcodedItem(barcode, m);
		station.scanner.scan(bitem2);
		station.baggingArea.addAnItem(i);
		assertTrue("weight discrepency tedected", session.cart.getCanChange());
	}@Test public void testWeightDiscrepencyWithPowerTurnOnHasDiscrepency() {
		station.plugIn(pg);
		station.turnOn();
		
		session = new Session(station, database_one_item);
		session.startSession();
		
		station.scanner.scan(bitem2);
		station.baggingArea.addAnItem(bitem);
		station.baggingArea.removeAnItem(bitem);
		assertTrue("weight discrepency tedected", !session.cart.getCanChange());
	}@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepencyItemPlacedBack() {
		station.plugIn(pg);
		station.turnOn();
		
		session = new Session(station, database_one_item);
		session.startSession();
		
		//station.scanner.scan(bitem2);
		station.baggingArea.addAnItem(bitem);
		station.baggingArea.removeAnItem(bitem);
		assertTrue("weight discrepency tedected", session.cart.getCanChange());
	}@Test public void testWeightDiscrepencyWithPowerTurnOnNoDiscrepencyItemPlacedBackAfterOther() {
		station.plugIn(pg);
		station.turnOn();
		
		session = new Session(station, database_one_item);
		session.startSession();
		
		station.scanner.scan(bitem3);
		station.baggingArea.addAnItem(bitem);
		station.baggingArea.removeAnItem(bitem);
		station.baggingArea.addAnItem(bitem3);
		assertTrue("weight discrepency tedected", session.cart.getCanChange());
	}@Test public void testWeightDiscrepencyWithPowerTurnOnHasDiscrepencyRescan() {
		station.plugIn(pg);
		station.turnOn();
		
		session = new Session(station, database_one_item);
		session.startSession();
		
		station.scanner.scan(bitem2);
		//station.baggingArea.addAnItem(bitem2);
		
		station.scanner.scan(bitem);
		assertTrue("item was scanned when in a weight descrepency", session.cart.getNumberItems()==1);
	}
	
}
