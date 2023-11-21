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

import java.io.IOException;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.AbstractCardReader;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import powerutility.NoPowerException;

public class PayWithCredit extends AbstractCardReader {    
    private long holdAmount;
    private Cart cart;

    public PayWithCredit(CardIssuer cardIssuer, Cart cart) {
        this.cart = cart;

        // define card reader listener to handle events related to card reading
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
                // check if the card type is credit
                if (!data.getType().toLowerCase().equals("credit")) {
                    throw new SecurityException("Invalid card type!");
                }

                // authorize hold on the card and proceed with the transaction
                long holdNumber = cardIssuer.authorizeHold(data.getNumber(), holdAmount);

                if (holdNumber != -1) {
                    if (cardIssuer.postTransaction(data.getNumber(), holdNumber, cart.getCartTotal())) {
                        cart.reduce_total_by(cart.getCartTotal());
                        System.out.println("Transaction successful!");
                    } else {
                        // handle unsuccessful transaction
                        System.out.println("Unsuccessful transaction.");
                    }
                } else {
                    // handle unauthorized hold
                    System.out.println("Transaction unauthorized!");
                }

                System.out.println("Total price after: " + cart.getCartTotal());
            }            
        };

    }

    // method to initiate card swiping process
    public void swipeCreditCard(Card card) throws IOException {
        System.out.println("Total price before: " + cart.getCartTotal());
        // StartSession.getStation().cardReader.swipe(card);
        System.out.println("Total price after: " + cart.getCartTotal());
    }

    // method to set the hold amount for authorization
    public void setHoldAmount(long num) {
        holdAmount = num;
    }
}
