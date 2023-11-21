// Sana Abdelhalem    30163580
// Ali Al Yasseen     30151000
// Yang Yang          30156356
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.BronzeBulkyItemHandler;
import com.thelocalmarketplace.software.Session;

public class BronzeBulkyItemHandlerTest {

    private BronzeBulkyItemHandler handler;
    private ElectronicScaleBronze scale;
    private Session session;
    private BarcodedItem bulkyItem;
    private BarcodedItem normalItem;

    @Before
    public void setUp() {
        // Create a dummy self-checkout station instance
        AbstractSelfCheckoutStation station = createDummySelfCheckoutStation();

        session = new Session(station);
        scale = new ElectronicScaleBronze();
        handler = new BronzeBulkyItemHandler(scale, session);

        Numeral[] dummyBarcodeBulky = createBarcodeNumerals("999999999");
        Numeral[] dummyBarcodeNormal = createBarcodeNumerals("888888888");

        // Bulky item with mass exceeding the limit of the bronze scale
        bulkyItem = new BarcodedItem(new Barcode(dummyBarcodeBulky), new Mass(15000)); // 15 kg

        // Normal item within the limit of the bronze scale
        normalItem = new BarcodedItem(new Barcode(dummyBarcodeNormal), new Mass(5000)); // 5 kg
    }

    private AbstractSelfCheckoutStation createDummySelfCheckoutStation() {
        // Create an instance of the actual SelfCheckoutStation class with minimal configuration.
        return new SelfCheckoutStationBronze();
    }

    private Numeral[] createBarcodeNumerals(String barcodeString) {
        Numeral[] numerals = new Numeral[barcodeString.length()];
        for (int i = 0; i < barcodeString.length(); i++) {
            byte value = (byte) Character.getNumericValue(barcodeString.charAt(i));
            numerals[i] = Numeral.valueOf(value);
        }
        return numerals;
    }

    @Test
    public void testProcessBulkyItem_ItemTooBulky() {

    }

    @Test
    public void testProcessBulkyItem_ItemNotTooBulky() {

    }

    @Test
    public void testProcessNormalItem() {

    }

    @Test
    public void testProcessBulkyItem_ScaleLimitEqualsItemMass() {

    }

}



