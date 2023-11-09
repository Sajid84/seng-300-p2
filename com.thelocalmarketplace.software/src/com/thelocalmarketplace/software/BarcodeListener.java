//Jason Very, 30222040
//Tara Strickland, 10105877
//Ali Sebbah, 30172851
//Fikayo Akande, 30185937
//Maleeha Siddiqui, 30179762

package com.thelocalmarketplace.software;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

public class BarcodeListener implements BarcodeScannerListener {
	Cart c;
	ProductDatabases d;
	/**
	 * Constructor for barcode listener.
	 * 
	 * @param cart
	 *           cart assocatied to session running on station this listener is registered to
	 * @param prods
	 * 			  product database for the store.
	 * 
	 */
	public BarcodeListener(Cart cart, ProductDatabases prods) {
		c = cart;
		d = prods;
		
	}
	
	
	
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		
		
	};
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		
		
	};
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		
		
	};
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		
		
	};
	@Override
	/**
	 *If the barcode is associated to an item in the product database that has at least one
	 *item in inventory, product gets added to the cart associated to the session currently running on the station 
	 *this listener is associated to 
	 * @throws InvalidArgumentSimulationException
	 * 		if the item doesnt exist or there are none of the item in inventory
	 * 		
	 * 
	 */
	public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
		BarcodedProduct p;
		if (d.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
			p = d.BARCODED_PRODUCT_DATABASE.get(barcode);
			if (d.INVENTORY.get(p)>0) {
				c.addBarcodedProductToCart(p);
				//c.addBarcodedProductToCart(p);
				//should we remove it from inventory here? or wait until they pay to remove from inventory
			}else {
				throw new InvalidArgumentSimulationException("there is no inventory for this item");
				//there is no inventory of this item or barcode was not scanned successfully
			}
		}else {
			throw new InvalidArgumentSimulationException("product is not in database");
			// barcode is not in database
		}
		
	};
	


}
