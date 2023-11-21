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

import java.math.BigInteger;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

/**
 * Handler for processing bulky items at a Silver model self-checkout station.
 */
public class SilverBulkyItemHandler {
    private ElectronicScaleSilver scale;
    private Session session;
    private static final Mass SILVER_SCALE_MASS_LIMIT = new Mass(new BigInteger("10000000000")); // 10 kg


    /**
     * Constructs a SilverBulkyItemHandler with a given scale and session.
     *
     * @param scale   The electronic scale used in the Silver model.
     * @param session The current checkout session.
     */
    public SilverBulkyItemHandler(ElectronicScaleSilver scale, Session session) {
        this.scale = scale;
        this.session = session;
    }

    /**
     * Processes a bulky item. If the item is too bulky, handles it accordingly.
     *
     * @param item The item to be processed.
     */
    public void processBulkyItem(BarcodedItem item) {
        if (isItemTooBulky(item)) {
            handleBulkyItem(item);
        } else {
            session.processNormalItem(item);
        }
    }

    /**
     * Determines if an item is too bulky for the scale.
     *
     * @param item The item to check.
     * @return True if the item is too bulky, false otherwise.
     */
    private boolean isItemTooBulky(BarcodedItem item) {
        return item.getMass().compareTo(SILVER_SCALE_MASS_LIMIT) > 0;
    }

    /**
     * Handles the processing of an item deemed too bulky for the scale.
     *
     * @param item The bulky item to be processed.
     */
    private void handleBulkyItem(BarcodedItem item) {
        session.blockStationForBulkyItem();
        session.adjustForBulkyItem(item);
        provideEnhancedFeedback(item);
        session.unblockStationForBulkyItem();
    }

    /**
     * Provides enhanced feedback for a bulky item.
     *
     * @param item The bulky item that has been processed.
     */
    private void provideEnhancedFeedback(BarcodedItem item) {
        String message = "Bulky item processed: " + item.getBarcode();
        String additionalDetails = getProductDetails(item.getBarcode());
        String handlingInstructions = getHandlingInstructions(item.getBarcode());

        message += additionalDetails.isEmpty() ? "" : ". Details: " + additionalDetails;
        message += handlingInstructions.isEmpty() ? "" : ". Handling Instructions: " + handlingInstructions;

        System.out.println(message);
    }

    /**
     * Retrieves additional details about a product given its barcode.
     *
     * @param barcode The barcode of the product.
     * @return A string containing product details.
     */
    private String getProductDetails(Barcode barcode) {
        BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
        if (product != null) {
            String details = product.getDescription();
            double expectedWeight = product.getExpectedWeight();
            details += ", Expected Weight: " + expectedWeight + " grams";
            return details;
        }
        return "Product details not available";
    }

    /**
     * Retrieves handling instructions for a product given its barcode.
     *
     * @param barcode The barcode of the product.
     * @return A string containing handling instructions.
     */
    private String getHandlingInstructions(Barcode barcode) {
        BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
        if (product != null) {
            double expectedWeightInGrams = product.getExpectedWeight();
            String handlingInfo;

            // Silver scale specific values
            double massLimitInGrams = 10000; // 10 kg as 10000 grams
            double sensitivityLimitInGrams = 1; // 1 g sensitivity

            if (expectedWeightInGrams > massLimitInGrams) {
                handlingInfo = "Item is too heavy for standard bagging. Please seek assistance.";
            } else if (expectedWeightInGrams > sensitivityLimitInGrams && expectedWeightInGrams <= massLimitInGrams) {
                handlingInfo = "Carefully place item in the bagging area, if possible.";
            } else {
                handlingInfo = "Standard handling is sufficient for this item.";
            }

            return handlingInfo;
        }
        return "Handling instructions not available.";
    }


}



