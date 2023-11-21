package com.thelocalmarketplace.software;

import java.io.IOException;

import com.jjjwelectronics.card.AbstractCardReader;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import powerutility.NoPowerException;

public class PayWithCredit extends AbstractCardReader {
    private long holdAmount;
    private CardIssuer cardIssuer;
    private Cart cart;

    public PayWithCredit(CardIssuer cardIssuer, Cart cart) {
        this.cardIssuer = cardIssuer;
        this.cart = cart;
    }

    public void swipeCard(Card card) throws IOException {
        System.out.println("Total price before: " + cart.getCartTotal());

        try {
            CardData cardData = this.swipe(card);
            //further processing is handled by the CardReaderListener
        } catch (NoPowerException e) {
            // Handle power exception
            e.printStackTrace();
        } catch (IOException e) {
            //other IO exceptions
            e.printStackTrace();
        }
    }

    public void setHoldAmount(long num) {
        holdAmount = num;
    }

    private void processCreditCardTransaction(CardData data) throws IOException {
        if (!data.getType().toLowerCase().equals("credit")) {
            throw new SecurityException("Invalid card type!");
        }

        long holdNumber = cardIssuer.authorizeHold(data.getNumber(), holdAmount);

        if (holdNumber != -1) {
            if (cardIssuer.postTransaction(data.getNumber(), holdNumber, cart.getCartTotal())) {
                cart.reduce_total_by(cart.getCartTotal());
                System.out.println("Transaction successful!");
            } else {
                //handle unsuccessful transaction
                System.out.println("Unsuccessful transaction.");
            }
        } else {
            //handle unauthorized hold
            System.out.println("Transaction unauthorized!");
        }

        System.out.println("Total price after: " + cart.getCartTotal());
    }
}