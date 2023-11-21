package com.thelocalmarketplace.software.test;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.card.Card;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.Cart;
import com.thelocalmarketplace.software.PayWithCredit;
import com.thelocalmarketplace.software.Session;

import powerutility.NoPowerException;
import powerutility.PowerGrid;

import java.io.IOException;

import static org.junit.Assert.*;

public class PayWithCreditTest {

    private TestCardIssuer testCardIssuer;
    private PayWithCredit payWithCredit;
    public Session session;
    public PowerGrid power;
    public AbstractSelfCheckoutStation station;

    @Before
    public void setup() {
        // initialize test objects before each test
        testCardIssuer = new TestCardIssuer();
        Cart cart = new Cart(); 
        payWithCredit = new PayWithCredit(testCardIssuer, cart);
        payWithCredit.setHoldAmount(100L);
        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        station = new SelfCheckoutStationBronze();

        power = PowerGrid.instance();
        power.engageUninterruptiblePowerSource();
    }

    @Test
    public void testSwipeCreditCard() throws IOException {
        // prepare the station for the test
        prepareStation();

        // create a test credit card
        Card card = new Card("Credit", "1234567890123456", "Cardholder", "123");

        try {
            // attempt to swipe the credit card
            payWithCredit.swipeCreditCard(card);
        } catch (IOException e) {
            e.printStackTrace(); // handle IOException if needed
        }

        // assert parameters and check payment result
        assertEquals(100L, testCardIssuer.getAuthorizeHoldParamAmount());
        assertEquals(123L, testCardIssuer.getPostTransactionParamHoldNumber());

        // display payment result based on the test card issuer's response
        if (testCardIssuer.isTransactionSuccessful()) {
            System.out.println("Payment successful!");
        } else {
            System.out.println("Payment failed!");
        }
    }

    // custom CardIssuer class for testing purposes
    public class TestCardIssuer extends CardIssuer {
        private long authorizeHoldParamAmount;
        private long postTransactionParamHoldNumber;
        private boolean transactionSuccessful;

        public TestCardIssuer() {
            super("TestCardIssuer", 10);
        }

        @Override
        public long authorizeHold(String cardNumber, double amount) {
            // record parameters for later assertion
            authorizeHoldParamAmount = (long) amount;
            return authorizeHoldParamAmount; // return the recorded amount for testing
        }

        @Override
        public boolean postTransaction(String cardNumber, long holdNumber, double actualAmount) {
            postTransactionParamHoldNumber = holdNumber;
            transactionSuccessful = actualAmount <= 100L;
            return transactionSuccessful;
        }

        public long getAuthorizeHoldParamAmount() {
            return authorizeHoldParamAmount;
        }

        public long getPostTransactionParamHoldNumber() {
            return postTransactionParamHoldNumber;
        }

        public boolean isTransactionSuccessful() {
            return transactionSuccessful;
        }
    }

    // helper method to prepare the self-checkout station
    public void prepareStation() {
        station.plugIn(power);
        station.turnOn();
        session = new Session(this.station);
        session.startSession();
    }
}