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

package PayWithBankNotesTest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import com.tdc.banknote.BanknoteValidator;
import com.thelocalmarketplace.software.Cart;
import com.thelocalmarketplace.software.payWithBankNotes;

public class PayWithBankNotesTest {

    private Cart cart;
    private payWithBankNotes payment;

    @Before
    public void setUp() {
        cart = new Cart();
        payment = new payWithBankNotes(new BigDecimal("50.00"), cart);
    }

    @Test
    public void testGoodBanknote() {
        Currency currency = Currency.getInstance("USD");
        BigDecimal denomination = new BigDecimal("20.00");

        payment.goodBanknote(new BanknoteValidator(currency, null), currency, denomination);

        assertEquals(new BigDecimal("30.00"), BigDecimal.valueOf(cart.getCartTotal()));
        assertFalse(!cart.getPayment());
    }

    @Test
    public void testGoodBanknoteCartPaid() {
        Currency currency = Currency.getInstance("USD");
        BigDecimal denomination = new BigDecimal("50.00");
        BigDecimal[] validDenominations = new BigDecimal[]{new BigDecimal("20.00"), new BigDecimal("50.00")}; // Example valid denominations

        payment.goodBanknote(new BanknoteValidator(currency,validDenominations), currency, denomination);

        assertEquals(BigDecimal.ZERO, BigDecimal.valueOf(cart.getCartTotal()));
        assertTrue(cart.getPayment());
    }

    @Test
    public void testGoodBanknoteRemainingBalance() {
        Currency currency = Currency.getInstance("USD");
        //BigDecimal denomination = new BigDecimal("30.00");
        BigDecimal[] validDenominations = new BigDecimal[]{new BigDecimal("20.00"), new BigDecimal("50.00")}; // Example valid denominations
        
        BigDecimal goodDenomination = new BigDecimal("20.00");
        
        BanknoteValidator validator = new BanknoteValidator(currency, validDenominations);
        
        payment.goodBanknote(validator, currency, goodDenomination);

        assertEquals(new BigDecimal("20.00"), BigDecimal.valueOf(cart.getCartTotal()));
        assertFalse(!cart.getPayment());
    }

    @Test
    public void testBadBanknote() {
    	Currency currency = Currency.getInstance("USD");
        BigDecimal[] validDenominations = new BigDecimal[]{new BigDecimal("20.00"), new BigDecimal("50.00")}; // Example valid denominations

        BanknoteValidator validator = new BanknoteValidator(currency, validDenominations);
        // Simulate a bad banknote here. For example, use a denomination not in validDenominations
        BigDecimal badDenomination = new BigDecimal("0.00"); // Assuming this is an invalid denomination

        payment.badBanknote(validator);

        assertFalse(!cart.getPayment());
    }

}