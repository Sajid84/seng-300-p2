package com.thelocalmarketplace.software;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.BarcodedItem;
public class SilverBulkyItemHandler {
private ElectronicScaleSilver scale;
private Session session;

/**
* Constructor for the SilverBulkyItemHandler.
*
* @param scale The electronic scale used in the Silver model.
* @param session The current checkout session.
*/
public SilverBulkyItemHandler(ElectronicScaleSilver scale, Session session) {

		this.scale = scale;
		this.session = session;
}
/**
* Processes a bulky item in the Silver model self-checkout.
*
* @param item The bulky item to be processed.
*/
public void processBulkyItem(BarcodedItem item) {

	if (isItemTooBulky(item)) {

		handleBulkyItem(item);
} 	else {
// If the item is not too heavy, process as normal.
		session.processNormalItem(item);
}
}
/**
* Checks if the item's mass exceeds the scale's capabilities.
*
* @param item The item to check.
* @return true if the item is too bulky, false otherwise.
*/
private boolean isItemTooBulky(BarcodedItem item) {
// The Silver model might have a higher mass limit than the Bronze model.
return item.getMass().compareTo(ElectronicScaleSilver.MASS_LIMIT) > 0;
}
/**
* Handles the processing of an item deemed too bulky for the scale.
*
* @param item The bulky item to be processed.
*/
private void handleBulkyItem(BarcodedItem item) {
// Block further customer input.
session.blockStationForBulkyItem();
// Adjust the expected weight in the bagging area.
session.adjustForBulkyItem(item);
// Enhanced feedback to the user, considering Silver's capabilities.
provideEnhancedFeedback(item);
// Unblock the station for further customer input.
session.unblockStationForBulkyItem();
}
/**
* Provide enhanced feedback to the user specific to the Silver model.
*
*
* @param item The bulky item that has been processed.
*/
private void provideEnhancedFeedback(BarcodedItem item) {
System.out.println("Bulky item processed: " + item.getBarcode());
}
}
