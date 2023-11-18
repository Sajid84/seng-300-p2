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
	
// Check if the item's mass exceeds the scale's limit.
if (item.getMass().compareTo(ElectronicScaleBronze.MASS_LIMIT) > 0) {
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
