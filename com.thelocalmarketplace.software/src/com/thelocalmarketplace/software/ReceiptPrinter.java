// Ali Al Yasseen     30151000
// Sana Abdelhalem    30163580
// Yang Yang          30156356
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851

package com.thelocalmarketplace.software;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceiptPrinter {

    // Primary actor: Customer
    // Secondary actors: Attendant

    // Goal in context: To provide a receipt for the purchases and payments made by the customer.
    // Preconditions: Payment in full has been received for the customer’s order.
    // Trigger: Payment in full has been received for the customer’s order.

    // Scenario:
    public void printReceipt(PaymentRecord paymentRecord) {
        try {
            // 1. System: The payment record will be up-to-date with details of the payment(s).
            updatePaymentRecord(paymentRecord);

            // 2. System: Signals to the receipt printer to print the payment record.
            boolean printSuccess = printPaymentRecord(paymentRecord);

            // Check if the receipt printing was successful
            if (printSuccess) {
                // 3. System: Thanks the Customer.
                thankCustomer();

                // 4. System: Ends the session, ready for a new one.
                endSession();
            } else {
                // Handle the case where the receipt printer failed
                throw new ReceiptPrintingException("Receipt printing failed.");
            }
        } catch (OutOfPaperOrInkException e) {
            // Handle the case where the receipt printer runs out of paper or ink
            handleOutOfPaperOrInkException();
        } catch (Exception e) {
            // Handle unexpected exceptions during the process
            handleException(e);
        }
    }

    private void updatePaymentRecord(PaymentRecord paymentRecord) {
        // Implementation to update the payment record with details of the payment(s)
        // For example, adding information about the payment method, amount, timestamp, etc.
        paymentRecord.updateRecord();
    }

    private boolean printPaymentRecord(PaymentRecord paymentRecord) throws OutOfPaperOrInkException {
        // Implementation to signal the receipt printer to print the payment record
        // Return true if printing is successful, false otherwise
        // For sake of the example, assume printing always succeeds.
        // However, simulate an exception if there is an issue with paper or ink.
        if (isOutOfPaperOrInk()) {
            throw new OutOfPaperOrInkException("Out of paper or ink during printing.");
        }

        // Print payment details
        System.out.println("Payment Details:");
        List<String> paymentDetails = paymentRecord.getPaymentDetails();
        for (String detail : paymentDetails) {
            System.out.println(detail);
        }

        return true;
    }

    private void thankCustomer() {
        // Implementation to thank the customer
        System.out.println("Thank you for your purchase!");
    }

    private void endSession() {
        // Implementation to end the session
        System.out.println("Session ended. Ready for a new one.");
    }

    private void handleOutOfPaperOrInkException() {
        // Implementation to handle the case where the receipt printer runs out of paper or ink
        System.out.println("Error: Out of paper or ink. Printing aborted. Station suspended.");
        System.out.println("Attendant informed to print a duplicate receipt and perform maintenance.");
    }

    private void handleException(Exception e) {
        // Implementation to handle unexpected exceptions during the process
        System.out.println("An unexpected error occurred: " + e.getMessage());
    }

    private boolean isOutOfPaperOrInk() {
        // Simulate the condition where the receipt printer runs out of paper or ink
        // For the sake of the example, assume it's out of paper or ink with a certain probability.
        return Math.random() < 0.1; // 10% chance of being out of paper or ink
    }

    
    /*
    // Example usage (may be helpful for testing later)
    public static void main(String[] args) {
        ReceiptPrinter printReceiptUseCase = new ReceiptPrinter();
        PaymentRecord paymentRecord = new PaymentRecord(new Date(), "Credit Card", 50.0);
        printReceiptUseCase.printReceipt(paymentRecord);
    }
    */
}

class PaymentRecord {
    private Date timestamp;
    private String paymentMethod;
    private double amount;
    private List<String> paymentDetails; // List to store payment details

    public PaymentRecord(Date timestamp, String paymentMethod, double amount) {
        this.timestamp = timestamp;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDetails = new ArrayList<>();
    }

    public void updateRecord() {
        // Update payment record details
        paymentDetails.add("Timestamp: " + timestamp);
        paymentDetails.add("Payment Method: " + paymentMethod);
        paymentDetails.add("Amount: " + amount);
        System.out.println("Payment record updated successfully.");
    }

    public boolean isPaymentInFull() {
        return amount > 0;
    }

    public List<String> getPaymentDetails() {
        return paymentDetails;
    }
}

class ReceiptPrintingException extends RuntimeException {
    public ReceiptPrintingException(String message) {
        super(message);
    }
}

class OutOfPaperOrInkException extends Exception {
    public OutOfPaperOrInkException(String message) {
        super(message);
    }
}
