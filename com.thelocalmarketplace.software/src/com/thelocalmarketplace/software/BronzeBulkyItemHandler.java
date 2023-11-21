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

import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scanner.BarcodedItem;

public class BronzeBulkyItemHandler {
    private ElectronicScaleBronze scale;
    private Session session;

    /**
     * Constructor for BronzeBulkyItemHandler.
     *
     * @param scale The electronic scale used to weigh items.
     * @param session The current checkout session.
     */
    public BronzeBulkyItemHandler(ElectronicScaleBronze scale, Session session) {
        this.scale = scale;
        this.session = session;
    }

    /**
     * Processes a bulky item, adjusting the session accordingly.
     *
     * @param item The barcoded item to be processed.
     */
    public void processBulkyItem(BarcodedItem item) {
        // Use the scale instance to get the mass limit
        if (item.getMass().compareTo(scale.getMassLimit()) > 0) {
            // The item is too bulky to be weighed on the scale.
            // Block further customer input and simulate attendant interaction.
            session.blockStationForBulkyItem();
            
            // Adjust the expected weight in the bagging area.
            session.adjustForBulkyItem(item);

            // Unblock the station for further customer input.
            session.unblockStationForBulkyItem();

            // Provide feedback to the user that the bulky item has been processed.
            System.out.println("Bulky item processed: " + item.getBarcode());
        } else {
            // If the item is not too heavy, process as normal.
            session.processNormalItem(item);
        }
    }
}

