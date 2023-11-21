package com.thelocalmarketplace.software.test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.Cart;

import java.util.ArrayList;
import java.util.List;

public class removeItemTest {

    private Cart cart;

    @Before
    public void setUp() {
        // Initialize a new ShoppingCart before each test
        cart = new Cart();
    }

    @Test
    public void testRemoveBarcodedProductFromCart() {
        // Test removing a BarcodedProduct from a non-empty cart
    	Numeral[] numeralArray = {Numeral.one, Numeral.two, Numeral.three, Numeral.four};
    	Numeral[] numeralArray1 = {Numeral.two, Numeral.two, Numeral.three, Numeral.four};
    	Barcode barcode1 = new Barcode(numeralArray);
    	Barcode barcode2 = new Barcode(numeralArray1);
        BarcodedProduct product1 = new BarcodedProduct(barcode1, "Product1", 1000, 10.0);
        BarcodedProduct product2 = new BarcodedProduct(barcode2, "Product2", 2000, 15.0);

        // Add products to the cart
        cart.addBarcodedProductToCart(product1);
        cart.addBarcodedProductToCart(product2);

        // Ensure the initial state is correct
        assertEquals(2, cart.getNumberItems());
        assertEquals(25.0, cart.getMass(), 0.001);
        assertEquals(25.0, cart.getCartTotal(), 0.001);

        // Remove product1 from the cart
        cart.removeBarcodedProductFromCart(product1);

        // Ensure the product is removed correctly
        assertEquals(1, cart.getNumberItems());
        assertEquals(15.0, cart.getMass(), 0.001);
        assertEquals(15.0, cart.getCartTotal(), 0.001);
    }

    @Test
    public void testRemoveBarcodedProductFromEmptyCart() {
        // Test removing a product from an empty cart
    	Numeral[] numeralArray = {Numeral.three, Numeral.two, Numeral.one, Numeral.four};
    	Barcode barcode1 = new Barcode(numeralArray);
        BarcodedProduct product = new BarcodedProduct(barcode1, "Product3", 2000, 20.0);

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
    public void testRemoveMultipleBarcodedProductsFromCart() {
        // Test removing multiple BarcodedProducts from a non-empty cart
        Numeral[] numeralArray1 = {Numeral.one, Numeral.two, Numeral.three, Numeral.four};
        Numeral[] numeralArray2 = {Numeral.two, Numeral.two, Numeral.three, Numeral.four};
        Barcode barcode1 = new Barcode(numeralArray1);
        Barcode barcode2 = new Barcode(numeralArray2);
        BarcodedProduct product1 = new BarcodedProduct(barcode1, "Product1", 1000, 10.0);
        BarcodedProduct product2 = new BarcodedProduct(barcode2, "Product2", 2000, 15.0);

        // Add products to the cart
        cart.addBarcodedProductToCart(product1);
        cart.addBarcodedProductToCart(product2);

        // Ensure the initial state is correct
        assertEquals(2, cart.getNumberItems());
        assertEquals(25.0, cart.getMass(), 0.001);
        assertEquals(25.0, cart.getCartTotal(), 0.001);

        // Remove both products from the cart
        cart.removeBarcodedProductFromCart(product1);
        cart.removeBarcodedProductFromCart(product2);

        // Ensure the cart is empty
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

        // Set cart state to cannot change
        cart.cantChange();

        // Add product to the cart
        cart.addBarcodedProductToCart(product);

        // Ensure the initial state is correct
        assertEquals(1, cart.getNumberItems());
        assertEquals(2.5, cart.getMass(), 0.001);
        assertEquals(25.0, cart.getCartTotal(), 0.001);

        // Try to remove product when cannot change
        cart.removeBarcodedProductFromCart(product);

        // Ensure the state remains unchanged
        assertEquals(1, cart.getNumberItems());
        assertEquals(2.5, cart.getMass(), 0.001);
        assertEquals(25.0, cart.getCartTotal(), 0.001);
    }
    
    @Test
    public void testRemoveNonBarcodedProductFromCart() {
        // Test removing a non-barcoded product from a non-empty cart
    	Numeral[] numeralArray = {null};
    	Barcode barcode1 = new Barcode(numeralArray);
        BarcodedProduct nonBarcodedProduct = new BarcodedProduct(barcode1, "Product4", 2500, 25.0);

        // Add a non-barcoded product to the cart
        cart.addBarcodedProductToCart(nonBarcodedProduct);

        // Ensure the initial state is correct
        assertEquals(1, cart.getNumberItems());
        assertEquals(12.0, cart.getMass(), 0.001);
        assertEquals(12.0, cart.getCartTotal(), 0.001);

        // Remove the non-barcoded product from the cart
        cart.removeBarcodedProductFromCart(nonBarcodedProduct);

        // Ensure the cart is empty
        assertEquals(0, cart.getNumberItems());
        assertEquals(0.0, cart.getMass(), 0.001);
        assertEquals(0.0, cart.getCartTotal(), 0.001);
    }
    
    
    @Test
    public void testCartTotalAfterRemovingProduct() {
        // Test cart total calculation after removing a product
        Numeral[] numeralArray = {Numeral.one, Numeral.two, Numeral.three, Numeral.four};
        Barcode barcode = new Barcode(numeralArray);
        BarcodedProduct product1 = new BarcodedProduct(barcode, "Product1", 1000, 10.0);
        BarcodedProduct product2 = new BarcodedProduct(barcode, "Product2", 2000, 15.0);

        // Add products to the cart
        cart.addBarcodedProductToCart(product1);
        cart.addBarcodedProductToCart(product2);

        // Ensure the initial state is correct
        assertEquals(2, cart.getNumberItems());
        assertEquals(25.0, cart.getMass(), 0.001);
        assertEquals(25.0, cart.getCartTotal(), 0.001);

        // Remove product1 from the cart
        cart.removeBarcodedProductFromCart(product1);

        // Ensure the cart total is updated correctly
        assertEquals(1, cart.getNumberItems());
        assertEquals(15.0, cart.getMass(), 0.001);
        assertEquals(15.0, cart.getCartTotal(), 0.001);
    }


   
}
