//Yang Yang           30156356
// Sana Abdelhalem    30163580
// Ali Al Yasseen     30151000
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851
// Shaikh Sajid Mahmood 30182396

package com.thelocalmarketplace.software;

import java.util.ArrayList;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;

/*
 * Represents a cart that a customer will have in a session. it contains a list of products
 * that the customer has added to their cart. 
 * */
public class Cart{
	/**
	 *List of products a customer has added to their cart
	 */
	private static ArrayList<Product> cart = new ArrayList<Product>();
	/**
	 *Current mass of the cart in grams
	 */
	private double massOfCart = 0.0;
	/**
	 *Current price of the cart
	 */
	private static double priceOfCart = 0.0;
	/**
	 *Current number of products in the cart
	 */
	int numberProductsInCart;
	/**
	 *flag set to true when additions can take place in the cart. This is set to false as soon
	 *as a product is added to the cart, until the item is placed in the bagging area successfully
	 */
	boolean canChangeCart = true;
	/**
	 *flag set to true when the cart is in a checkout state in the session
	 */
	boolean payingFor = false;

	public Cart() {
		
	}
	
	/**
	 *Adds a barcoded product to the cart if there is not a weight discrepency currently happenening
	 * updates the cart total and mass accordingly
	 * @param p
	 * 		barcoded product to be added to the cart
	 * 		
	 * 
	 */
	
	public void addBarcodedProductToCart(BarcodedProduct p) {
		if (getCanChange() && !getInPayment()) {
		cart.add(p);
		numberProductsInCart++;
		massOfCart += p.getExpectedWeight();
		if (p.isPerUnit()) {
			priceOfCart+=(double)p.getPrice();
			
		}else {// this is not accessed because all barcoded products are pay per unit by default
			//get mass in kg:
			double grams = p.getExpectedWeight();
			double kgrams = grams/1000.0;//g to kg
			priceOfCart += (double) p.getPrice()*kgrams;
		}
		//block until item is placed in bagging area
		cantChange();
		}
	}
	
	public void removeBarcodedProductFromCart (BarcodedProduct p) {
		if (getCanChange() && !getInPayment()) {
			if (getNumberItems() > 0) {
				cart.remove(p);
				numberProductsInCart--;
				massOfCart -= p.getExpectedWeight();
				if (p.isPerUnit()) {
					priceOfCart -= (double)p.getPrice();		
					}
				else {
					double grams = p.getExpectedWeight();
					double kgrams = grams/1000.0;//g to kg
					priceOfCart -= (double) p.getPrice()*kgrams;
				}
			}
			else {
				System.out.print("There are no items in your cart");
			}
			
			
		}
		else {
			cantChange();
		}
		
	}

	/**
	 *getter for number of products in the cart
	 * 		
	 */
	public int getNumberItems() {
		return numberProductsInCart; 
		
	}
	/**
	 *getter for last item in the cart if there is something in it, returns null otherwise.
	 * 		
	 */
	public Product getLastItem() {
		if (cart.size()>0) {
			return cart.get(cart.size()-1);
		}else {
			return null;
		}
		
	}
	/**
	 *getter the current mass of the cart
	 * 		
	 */
	public double getMass() {
		return massOfCart;
		
	}
	
	public static ArrayList<Product> getCart() { return cart; }
	
	/**
	 *reduces the price of the cart by reduction. This is used when paying for a cart
	 * 		
	 */
	public void reduce_total_by (double reduction) {
		priceOfCart-=reduction;
	}
	
	public static void updateExpectedPrice (double newprice) {
		priceOfCart = newprice;
	}
	
	/**
	 *getter for the current price of the cart
	 * 		
	 */
	public static double getCartTotal() {
		return priceOfCart;
	}
	/**
	 *setter for canChangeCart. sets the values to false
	 * 		
	 */
	public void cantChange() {
		canChangeCart = false;
	}
	/**
	 *setter for canChangeCart. sets the values to true
	 * 		
	 */
	public void canChange() {
		canChangeCart = true;
	}
	/**
	 *getter for canChangeCart. 
	 * 		
	 */
	public boolean getCanChange() {
		return canChangeCart;
	}
	/**
	 *sets state paying for, this matches the variable in session inCheckout
	 * 		
	 */
	public void startPayment() {
		payingFor = true;
	}
	/**
	 *sets state paying for, this matches the variable in session inCheckout
	 * 		
	 */
	
	public void endPayment() {
		payingFor = false;
	}

	public boolean getPayment(){
		return payingFor;
	}
	/**
	 *gets is paying for state
	 * 		
	 */
	public boolean getInPayment() {
		return payingFor;
	}
}
