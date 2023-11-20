// Yang Yang          30156356
// Sana Abdelhalem    30163580
// Ali Al Yasseen     30151000
// Andres Genatios    30142768
// Abdullah Ishtiaq   30153185
// Nicholas MacKinnon 30172737
// Carlos Serrouya    30192761
// Logan Miszaniec    30156384
// Ali Sebbah         30172851


package com.thelocalmarketplace.software.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.BarcodeListener;
import com.thelocalmarketplace.software.Cart;
import com.thelocalmarketplace.software.ScaleListener;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.validatorObserver;
import ca.ucalgary.seng300.simulation.InvalidStateSimulationException;
import powerutility.PowerGrid;
import com.jjjwelectronics.Numeral;
public class SessionTests {
        public ProductDatabases BARCODED_PRODUCT_DATABASE;
        public SelfCheckoutStation checkoutStation;
        public boolean inCheckout;
        public BarcodeListener bl;
        public ScaleListener sl;
        public validatorObserver vo;
        public Product product;
        public Barcode barcode;
        public BarcodedProduct barcodedProduct;
        public Numeral [] code = {Numeral.one, Numeral.two, Numeral.three};
        public Session session;
        
        @Before
        public void setupSession() {
                
                //create barcoded product
                long price = 1;
                boolean isPerUnit = true;
                String description = "Milk";
                double expectedWeight = 1.00;
                barcode = new Barcode(code);
                barcodedProduct = new BarcodedProduct(barcode, description, price, expectedWeight);
                
                //setup small database
                ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, barcodedProduct);
                
                //Setup and create checkout station
                checkoutStation = new SelfCheckoutStation();
                checkoutStation.plugIn(PowerGrid.instance());
                checkoutStation.turnOn();
                
                //create new session environment
                session = new Session(checkoutStation,BARCODED_PRODUCT_DATABASE);
        
        }
        
        //Expected is no exceptions
        //Cart needs to be empty in order for session to start
        @Test
        public void testCanStartValid() {
                //create new empty cart                
                Cart cart = new Cart();
                session.cart = cart;
                assertEquals(cart.getNumberItems(), 0);
                
                //Session should be false since we haven't called start session
                boolean c = session.hasSessionStarted();
                assertFalse(c);
                
                //conditions are met so result should be true meaning session can start
                boolean result = session.canStart();
                assertTrue(result);
                
        }
        
        //Tests if result is false when cart size != 0
        @Test
        public void testCanStartInvalidCartSize() {
                //create new cart with size of one
                Cart cart = new Cart();
                session.cart = cart;
                cart.addBarcodedProductToCart(barcodedProduct);
                assertEquals(cart.getNumberItems(), 1);
                
                //Ensure session is still not started;
                boolean c = session.hasSessionStarted();
                assertFalse(c);
                
                //Expected: result should be false as we can't start session
                //when cart already has items
                
                boolean result = session.canStart();
                assertFalse(result);
        }
        
        
        //Tests if the session can start when a session is already
        //in progress. Expected == false
        @Test
        public void testCanStartAlreadyStarted() {
                //create new empty cart
                Cart cart = new Cart();
                session.cart = cart;
                
                session.startSession();
                boolean result = session.canStart();
                assertFalse(result);
                
        
        }
        
        //Tests if the session can be started with
        //scanner powered off. Expected == false
        @Test
        public void testCanStartBSOff() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.scanner.disable();
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        //Tests if the session can be started with
        //scanner unplugged. Expected == false
        @Test
        public void testCanStartBSunplugged() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.scanner.unplug();;
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        
        //Tests if the session can be started with
        //Bagging area off . Expected == false
        @Test
        public void testCanStartBAoff() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.baggingArea.disable();;
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        //Tests if the session can be started with
        //Bagging area unplugged . Expected == false
        @Test
        public void testCanStartBAunplugged() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.baggingArea.unplug();;
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        //Tests if the session can be started with
        //CoinSlot off . Expected == false
        @Test
        public void testCanStartCSoff() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.coinSlot.disable();;
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        //Tests if the session can be started with
        //CoinSlot unplugged . Expected == false
        @Test
        public void testCanStartCSunplugged() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.coinSlot.disactivate();;
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        //Tests if the session can be started with
        //CoinSlot non connected . Expected == false
        @Test
        public void testCanStartCSnoGrid() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.coinSlot.disconnect();;
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        //Tests if the session can be started with
        //Coin Storage off . Expected == false
        @Test
        public void testCanStartCStoreoff() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.coinStorage.disable();;
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        //Tests if the session can be started with
        //Coin Storage unplugged . Expected == false
        @Test
        public void testCanStartCStoreunplugged() {
                Cart cart = new Cart();
                session.cart = cart;
                checkoutStation.coinStorage.disactivate();;
                boolean c = session.canStart();
                assertFalse(c);
                
        }
        
        //Tests if the session can be started with
                //Coin Storage no Grid . Expected == false
                @Test
                public void testCanStartCStorenoGrid() {
                        Cart cart = new Cart();
                        session.cart = cart;
                        checkoutStation.coinStorage.disconnect();;
                        boolean c = session.canStart();
                        assertFalse(c);
                        
                }
                
                //Tests if the session can be started with
                //Coin Validator off . Expected == false
                @Test
                public void testCanStartCVoff() {
                        Cart cart = new Cart();
                        session.cart = cart;
                        checkoutStation.coinValidator.disable();;
                        boolean c = session.canStart();
                        assertFalse(c);
                        
                }
                        
                //Tests if the session can be started with
                //Coin Validator uplugged . Expected == false
                @Test
                public void testCanStartCVunplugged() {
                        Cart cart = new Cart();
                        session.cart = cart;
                        checkoutStation.coinValidator.disactivate();;
                        boolean c = session.canStart();
                        assertFalse(c);
                        
                }
                        
                
                //Tests if the session can be started with
                //Coin Validator no Grid . Expected == false
                @Test
                public void testCanStartCVnoGrid() {
                        Cart cart = new Cart();
                        session.cart = cart;
                        checkoutStation.coinValidator.disconnect();;
                        boolean c = session.canStart();
                        assertFalse(c);
                        
                }
                        
                        
        
        //Tests if the session can be started with invalid environment
        @Test(expected = InvalidStateSimulationException.class)
        public void testStartSessionInvalidStart() {
                Cart cart = new Cart();
                cart.addBarcodedProductToCart(barcodedProduct);
                session.cart = cart;
                session.startSession();
        }
        
        //Tests if the software recognizes if the session is started with valid
        // conditions. Expected: hasStarted == true
        @Test
        public void testHasSessionStartedValid() {
                //create empty cart and connect to session
                Cart cart = new Cart();
                session.cart = cart;
                
                //Start a session
                session.startSession();
                boolean hasStarted = session.hasSessionStarted();
                
                assertTrue(hasStarted);
                
        }
        
        
        //Tests if the software recognizes that session has not been started
        //Expected: hasStarted == false
        @Test
        public void testHasSessionStartedNonValid() {
                //create empty cart and connect to session
                Cart cart = new Cart();
                session.cart = cart;
                
                boolean hasStarted = session.hasSessionStarted();
                
                assertFalse(hasStarted);
                
        }
        
        //Tests if can enter a checkout state with
        //one item in cart: Expected is no errors
        @Test
        public void testValidCheckout() {
                Cart cart = new Cart();
                session.startSession();
                session.cart = cart;
                cart.addBarcodedProductToCart(barcodedProduct);
                session.checkout();
        }
        
        //Tests if can enter a checkout state with
        //noitem in cart: Expected is ----
        @Test
        public void testinValidCheckout() {
                Cart cart = new Cart();
                session.startSession();
                session.cart = cart;
                session.checkout();
        }
        
        //Tests if checkout can successfully exit once completed
        @Test
        public void testExitCheckout() {
                Cart cart = new Cart();
                session.startSession();
                session.cart = cart;
                session.exitCheckout();
        }
}