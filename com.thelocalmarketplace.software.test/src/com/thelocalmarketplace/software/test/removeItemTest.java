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

package com.thelocalmarketplace.software.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.DisabledException;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.BarcodeListener;
import com.thelocalmarketplace.software.Cart;
import com.thelocalmarketplace.software.Session;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.NoPowerException;
import powerutility.PowerGrid;


public class removeItemTest {
	
	AbstractSelfCheckoutStation station;
	Session session;
	PowerGrid pg;
	public Cart cart;
	
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
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		station = new SelfCheckoutStationBronze();
		
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
	public void prepareStation()
	{
		station.plugIn(pg);
		station.turnOn();
		session = new Session(this.station);
		session.startSession();
	}
	

	
	@Test (expected = SimulationException.class) public void testNoPowerOff(){
		station.plugIn(pg);
		station.turnOn();
		session = new Session(this.station);
		session.startSession();
		station.handheldScanner.scan(bitem2);
}
	@Test
	public void testPowerOn(){
		prepareStation();
		station.handheldScanner.scan(bitem2);
		
		//assertTrue("item was not successfully added to cart", session.cart.getNumberItems() ==1);
	}
	
	 
	 @Test
	    public void testRemoveBarcodedProductFromCart() {
	        // Test removing a BarcodedProduct from a non-empty cart
	    	Numeral[] numeralArray = {Numeral.one, Numeral.two, Numeral.three, Numeral.four};
	    	Numeral[] numeralArray1 = {Numeral.two, Numeral.two, Numeral.three, Numeral.four};
	    	Barcode barcode1 = new Barcode(numeralArray);
	    	Barcode barcode2 = new Barcode(numeralArray1);
	        BarcodedProduct product1 = new BarcodedProduct(barcode1, "Product1", 1000, 10.0);
	      
	        cart = new Cart();
	        cart.canChange();
	        cart.endPayment();
	        

	        // Add products to the cart
	        cart.addBarcodedProductToCart(product1);
	     

	        // Ensure the initial state is correct
	        assertEquals(1, cart.getNumberItems());
	        assertEquals(10.0, cart.getMass(), 0.001);
	        assertEquals(1000, cart.getCartTotal(), 0.001);
	        cart.canChange();
	        cart.endPayment();
	        

	        // Remove product1 from the cart
	        cart.removeBarcodedProductFromCart(product1);

	        // Ensure the product is removed correctly
	        assertEquals(0, cart.getNumberItems());
	        assertEquals(0, cart.getMass(), 0.001);
	        assertEquals(0, cart.getCartTotal(), 0.001);
	    }

	   @Test
	    public void testRemoveBarcodedProductFromEmptyCart() {
	        // Test removing a product from an empty cart
	    	Numeral[] numeralArray = {Numeral.three, Numeral.two, Numeral.one, Numeral.four};
	    	Barcode barcode1 = new Barcode(numeralArray);
	        BarcodedProduct product = new BarcodedProduct(barcode1, "Product3", 2000, 20.0);
	        cart = new Cart();
	        cart.canChange();
	        cart.endPayment();
	        

	        // Ensure the initial state is correct
	        assertEquals(0, cart.getNumberItems());
	        assertEquals(0.0, cart.getMass(), 0.001);
	        assertEquals(0.0, cart.getCartTotal(), 0.001);

	        // Remove product from the empty cart
	        cart.removeBarcodedProductFromCart(product);

	        // Ensure the state remains unchanged
	        assertEquals(0, cart.getNumberItems());
	        assertEquals(0.0, cart.getMass(), 0.001);
	        assertEquals(0.0, cart.getCartTotal(), 0.001);
	    }

	    @Test
	    public void testRemoveBarcodedProductFromCartWhenCannotChange() {
	        // Test removing a product when the cart cannot be changed
	    	Numeral[] numeralArray = {Numeral.three, Numeral.two, Numeral.one, Numeral.four};
	    	Barcode barcode1 = new Barcode(numeralArray);
	        BarcodedProduct product = new BarcodedProduct(barcode1, "Product4", 2500, 25.0);
	        cart = new Cart();

	        

	        // Set cart state to cannot change
	       

	        // Add product to the cart
	        cart.addBarcodedProductToCart(product);

	        // Ensure the initial state is correct
	        assertEquals(1, cart.getNumberItems());
	        assertEquals(25.0, cart.getMass(), 0.001);
	        assertEquals(2500.0, cart.getCartTotal(), 0.001);

	        cart.cantChange();
	        // Try to remove product when cannot change
	        cart.removeBarcodedProductFromCart(product);

	        // Ensure the state remains unchanged
	        assertEquals(1, cart.getNumberItems());
	        assertEquals(25.0, cart.getMass(), 0.001);
	        assertEquals(2500.0, cart.getCartTotal(), 0.001);
	    }
	 
	
	
	
}