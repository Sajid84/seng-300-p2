// Yang Yang          30156356
// Sana Abdelhalem    30163580
// Ali Al Yasseen     30151000
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851
// Shaikh Sajid Mahmood 30182396

package com.thelocalmarketplace.software.test;

import org.junit.Test;

import com.thelocalmarketplace.software.ReceiptPrinter;
import com.thelocalmarketplace.software.ReceiptPrinter.PaymentRecord;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;

public class ReceiptPrinterTest {

    @Test
    public void testPrintReceipt_SuccessfulPrinting() {
        ReceiptPrinter receiptPrinter = new ReceiptPrinter();
        PaymentRecord paymentRecord = receiptPrinter.new PaymentRecord(new Date(), "Credit Card", 50.0);

        try {
            receiptPrinter.printReceipt(paymentRecord);
        } catch (Exception e) {
            fail("Exception should not be thrown for successful printing");
        }
    }

    @Test
    public void testPrintReceipt_OutOfPaperOrInkException() {
        ReceiptPrinter receiptPrinter = new ReceiptPrinter();
        PaymentRecord paymentRecord = receiptPrinter.new PaymentRecord(new Date(), "Credit Card", 50.0);
        
        // Force out of paper or ink scenario
        receiptPrinter.isOutOfPaperOrInk = true;

        try {
            receiptPrinter.printReceipt(paymentRecord);
            fail("OutOfPaperOrInkException should be thrown");
        } catch (Exception e) {
            fail("Unexpected exception thrown");
        }
    }
    
    public class OutOfPaperOrInkException extends Exception {
        public OutOfPaperOrInkException(String message) {
            super(message);
        }
    }

    @Test
    public void testPrintReceipt_ReceiptPrintingException() {
        ReceiptPrinter receiptPrinter = new ReceiptPrinter();
        PaymentRecord paymentRecord = receiptPrinter.new PaymentRecord(new Date(), "Credit Card", 50.0);
        
        // Simulate a failure during printing
        receiptPrinter.printPaymentRecord = false;

        try {
            receiptPrinter.printReceipt(paymentRecord);
            fail("ReceiptPrintingException should be thrown");
        } catch (Exception e) {
            fail("Unexpected exception thrown");
        }
    }
    
    public class ReceiptPrintingException extends Exception {
        public ReceiptPrintingException(String message) {
            super(message);
        }
    }

 // Placeholder PaymentRecord class for testing
    public static class TestPaymentRecord {
        private Date timestamp;
        private String paymentMethod;
        private double amount;

        public TestPaymentRecord(Date timestamp, String paymentMethod, double amount) {
            this.timestamp = timestamp;
            this.paymentMethod = paymentMethod;
            this.amount = amount;
        }

        public void updateRecord() {
            // Dummy implementation for updating the payment record
        }

        public List<String> getPaymentDetails() {
            // Dummy implementation for getting payment details
            return List.of(
                    "Timestamp: " + timestamp,
                    "Payment Method: " + paymentMethod,
                    "Amount: " + amount
            );
        }
    }
    // Add more test cases as needed to cover different scenarios
}
