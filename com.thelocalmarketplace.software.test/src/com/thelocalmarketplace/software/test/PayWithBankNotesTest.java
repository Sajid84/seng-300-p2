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

        assertEquals(new BigDecimal("30.00"), payment.amountOwed);
        assertFalse(cart.paymentComplete);
    }

    @Test
    public void testGoodBanknoteCartPaid() {
        Currency currency = Currency.getInstance("USD");
        BigDecimal denomination = new BigDecimal("50.00");

        payment.goodBanknote(new BanknoteValidator(currency, null), currency, denomination);

        assertEquals(BigDecimal.ZERO, payment.amountOwed);
        assertTrue(cart.paymentComplete);
    }

    @Test
    public void testGoodBanknoteRemainingBalance() {
        Currency currency = Currency.getInstance("USD");
        BigDecimal denomination = new BigDecimal("30.00");

        payment.goodBanknote(new BanknoteValidator(currency, null), currency, denomination);

        assertEquals(new BigDecimal("20.00"), payment.amountOwed);
        assertFalse(cart.paymentComplete);
    }

    @Test
    public void testBadBanknote() {
        payment.badBanknote(new BanknoteValidator(null, null));

        assertFalse(cart.paymentComplete);
    }

}
