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

public class AddOwnBag {
    private boolean isSystemReady = true;
    private boolean customerHasAddedBags = false;
    int weight;
    int maxWeight;
    
    public void customerAddsBags() {
        if (!isSystemReady) {
            System.out.println("System is not ready to note weight discrepancies.");
            return;
        }
        System.out.println("Please add your own bags now.");
        customerHasAddedBags = true;
        detectWeightChange();
    }
    
    private void detectWeightChange() {
        if (customerHasAddedBags) {
            System.out.println("Bags detected. You may continue with your transaction.");
        } else {
            System.out.println("No bags detected. Please add your bags.");
        }
        if (weight < maxWeight) {
            // Where the extension point for bagsTooHeavy would be implemented. 
        }
    }
}
