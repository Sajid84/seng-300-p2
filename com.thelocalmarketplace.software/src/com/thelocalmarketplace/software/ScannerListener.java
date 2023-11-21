// Shaikh Sajid Mahmood 30182396
// Sana Abdelhalem    30163580
// Ali Al Yasseen     30151000
// Yang Yang          30156356
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851

package com.thelocalmarketplace.software;

import java.util.Map;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

public class ScannerListener implements BarcodeScannerListener {
	Cart cart;
	AbstractSelfCheckoutStation station;

    //Constructor
    public ScannerListener(Cart cart) 
    {
        this.cart = cart;
    }
    
    @Override
    public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) 
    {

        BarcodedProduct product;
        Map<Barcode, BarcodedProduct> productDatabase = ProductDatabases.BARCODED_PRODUCT_DATABASE;
        
        if (productDatabase.containsKey(barcode)) 
        {
            product =productDatabase.get(barcode);
            if (ProductDatabases.INVENTORY.get(product)>0) 
            {
                cart.addBarcodedProductToCart(product);
            }else 
            {
            	//there is no inventory of this item or barcode was not scanned successfully
                throw new InvalidArgumentSimulationException("There is no inventory for this item");
            }
        }else {
        	// barcode is not in database
            throw new InvalidArgumentSimulationException("Product is not in database");
        }

    }

    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {

    }

}
