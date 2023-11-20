// Ali Al Yasseen     30151000
// Sana Abdelhalem    30163580
// Yang Yang          30156356
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851

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

        payment.goodBanknote(new BanknoteValidator(currency, null), currency, denomination);

        assertEquals(BigDecimal.ZERO, BigDecimal.valueOf(cart.getCartTotal()));
        assertTrue(cart.getPayment());
    }

    @Test
    public void testGoodBanknoteRemainingBalance() {
        Currency currency = Currency.getInstance("USD");
        BigDecimal denomination = new BigDecimal("30.00");

        payment.goodBanknote(new BanknoteValidator(currency, null), currency, denomination);

        assertEquals(new BigDecimal("20.00"), BigDecimal.valueOf(cart.getCartTotal()));
        assertFalse(!cart.getPayment());
    }

    @Test
    public void testBadBanknote() {
        payment.badBanknote(new BanknoteValidator(null, null));

        assertFalse(!cart.getPayment());
    }

}
