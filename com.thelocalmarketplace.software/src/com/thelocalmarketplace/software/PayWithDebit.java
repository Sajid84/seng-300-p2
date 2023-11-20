package com.thelocalmarketplace.software;

import java.io.IOException;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.AbstractCardReader;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import powerutility.NoPowerException;

public class PayWithDebit extends AbstractCardReader{    
    private long holdAmount;
    public Cart cart;

    public PayWithDebit(CardIssuer bank) {

        CardReaderListener cardReaderListener = new CardReaderListener() {

            @Override
            public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
                System.out.println("Device has been enabled.");
            }

            @Override
            public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
                throw new NoPowerException();
            }

            @Override
            public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
                System.out.println("Device has been turned on.");
            }

            @Override
            public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
                throw new NoPowerException();
            }

            @Override
            public void aCardHasBeenSwiped() {
                System.out.println("Card has been swiped.");
            }

            @Override
            public void theDataFromACardHasBeenRead(CardData data) {
                if (!data.getType().toLowerCase().equals("debit"))
                    throw new SecurityException("Invalid card type!");
                
                if (bank.authorizeHold(data.getNumber(), holdAmount) == -1) 
                    throw new SecurityException("Transaction unauthorized!");

                if (!bank.postTransaction(data.getNumber(), 0, cart.getCartTotal()))
                    throw new SecurityException("Unsuccessful transaction.");
                cart.reduce_total_by(cart.getCartTotal());
            }            
        };

        // StartSession.getStation().cardReader.register(cardReaderListener);
    }

    public void swipeCard(Card card) throws IOException {
        System.out.println("Total price before: " + cart.getCartTotal());
        // StartSession.getStation().cardReader.swipe(card);
        System.out.println("Total price after: " + cart.getCartTotal());
    }

    public void setHoldAmount(long num) {
        holdAmount = num;
    }
}
