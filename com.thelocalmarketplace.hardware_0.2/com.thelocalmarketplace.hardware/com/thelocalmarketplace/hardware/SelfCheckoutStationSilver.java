package com.thelocalmarketplace.hardware;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sound.sampled.AudioSystem;

import com.jjjwelectronics.card.CardReaderGold;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.scale.AbstractElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.BarcodeScannerBronze;
import com.jjjwelectronics.scanner.BarcodeScannerSilver;
import com.tdc.Sink;
import com.tdc.banknote.AbstractBanknoteDispenser;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispenserGold;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserGold;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinValidator;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

/**
 * Represents the overall self-checkout station.
 * <p>
 * A self-checkout possesses the following units of hardware that the customer
 * can see and interact with:
 * <ul>
 * <li>two electronic scales, with a configurable maximum weight before it
 * overloads, one for the bagging area and one for the scanning area;</li>
 * <li>one receipt printer;</li>
 * <li>one card reader;</li>
 * <li>two scanners (the main one and the handheld one);</li>
 * <li>one input slot for banknotes;</li>
 * <li>one output slot for banknotes;</li>
 * <li>one input slot for coins;</li>
 * <li>one output tray for coins; and,</li>
 * <li>one speaker for audio output (note: you should directly use the
 * {@link AudioSystem} class, if you want to produce sounds).</li>
 * </ul>
 * <p>
 * In addition, these units of hardware are accessible to personnel with a key
 * to unlock the front of the station:
 * <ul>
 * <li>one banknote storage unit, with configurable capacity;</li>
 * <li>one or more banknote dispensers, one for each supported denomination of
 * banknote, as configured;</li>
 * <li>one coin storage unit, with configurable capacity; and,</li>
 * <li>one or more coin dispensers, one for each supported denomination of coin,
 * as configured.</li>
 * </ul>
 * <p>
 * And finally, there are certain, additional units of hardware that would only
 * be accessible to someone with the appropriate tools (like a screwdriver,
 * crowbar, or sledge hammer):
 * <ul>
 * <li>one banknote validator; and</li>
 * <li>one coin validator.</li>
 * </ul>
 * <p>
 * Many of these devices are interconnected, to permit coins or banknotes to
 * pass between them. Specifically:
 * <ul>
 * <li>the coin slot is connected to the coin validator (this is a
 * one-directional chain of devices);</li>
 * <li>the coin validator is connected to each of the coin dispensers (i.e., the
 * coin dispensers can be replenished with coins entered by customers), to the
 * coin storage unit (for any overflow coins that do not fit in the dispensers),
 * and to the coin tray for any rejected coins either because the coins are
 * invalid or because even the overflow storage unit is full (this is a
 * one-directional chain of devices);
 * <li>each coin dispenser is connected to the coin tray, to provide change
 * (this is a one-directional chain of devices);</li>
 * <li>the banknote input slot is connected to the banknote validator (this is a
 * <b>two</b>-directional chain of devices as any entered banknotes that are
 * rejected by the validator can be returned to the customer);</li>
 * <li>the banknote validator is connected to the banknote storage unit (this is
 * a one-directional chain of devices); and,</li>
 * <li>each banknote dispenser is connected to the output banknote slot; these
 * dispensers cannot be replenished by banknotes provided by customers (this is
 * a one-directional chain of devices).</li>
 * </ul>
 * <p>
 * All other functionality of the system must be performed in software,
 * installed on the self-checkout station through custom observer classes
 * implementing the various observer interfaces provided.
 * </p>
 * <p>
 * Note that banknote denominations are required to be positive integers, while
 * coin denominations are positive decimal values.
 */
public class SelfCheckoutStationSilver {
	static {
		resetConfigurationToDefaults();
	}

	private static int reusableBagDispenserCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the reusable-bag dispenser.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureReusableBagDispenserCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		reusableBagDispenserCapacityConfiguration = count;
	}

	private static int coinDispenserCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin dispensers.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinDispenserCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinDispenserCapacityConfiguration = count;
	}

	private static int banknoteStorageUnitCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the banknote storage unit.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureBanknoteStorageUnitCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		banknoteStorageUnitCapacityConfiguration = count;
	}

	private static int coinStorageUnitCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin storage unit.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinStorageUnitCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinStorageUnitCapacityConfiguration = count;
	}

	private static int coinTrayCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin tray.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinTrayCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinStorageUnitCapacityConfiguration = count;
	}

	private static Currency currencyConfiguration;

	/**
	 * Configures the currency to be supported.
	 * 
	 * @param curr
	 *            The currency to be supported.
	 */
	public static void configureCurrency(Currency curr) {
		if(curr == null)
			throw new NullPointerSimulationException("currency");
		currencyConfiguration = curr;
	}

	private static BigDecimal[] banknoteDenominationsConfiguration;

	/**
	 * Configures the set of banknote denominations.
	 * 
	 * @param denominations
	 *            The denominations to use for banknotes.
	 */
	public static void configureBanknoteDenominations(BigDecimal[] denominations) {
		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.length < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		HashSet<BigDecimal> set = new HashSet<>();
		for(BigDecimal denomination : denominations) {
			if(denomination.compareTo(BigDecimal.ZERO) <= 0)
				throw new InvalidArgumentSimulationException("Each denomination must be positive.");

			set.add(denomination);
		}

		if(set.size() != denominations.length)
			throw new InvalidArgumentSimulationException("The denominations must all be unique.");

		// Copy the array to avoid the potential for a security hole
		banknoteDenominationsConfiguration = Arrays.copyOf(denominations, denominations.length);
	}

	private static List<BigDecimal> coinDenominationsConfiguration;

	/**
	 * Configures the set of coin denominations.
	 * 
	 * @param denominations
	 *            The denominations to use for coins.
	 */
	public static void configureCoinDenominations(BigDecimal[] denominations) {
		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.length < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		HashSet<BigDecimal> set = new HashSet<>();
		for(BigDecimal denomination : denominations) {
			if(denomination.compareTo(BigDecimal.ZERO) <= 0)
				throw new InvalidArgumentSimulationException("Each denomination must be positive.");

			set.add(denomination);
		}

		if(set.size() != denominations.length)
			throw new InvalidArgumentSimulationException("The denominations must all be unique.");

		// Copy the array to avoid the potential for a security hole
		coinDenominationsConfiguration = new ArrayList<BigDecimal>();
		for(BigDecimal denomination : denominations)
			coinDenominationsConfiguration.add(denomination);
	}

	private static double scaleMaximumWeightConfiguration;

	/**
	 * Configures the maximum weight permitted for the scales.
	 * 
	 * @param weight
	 *            The maximum weight permitted for the scales.
	 */
	public static void configureScaleMaximumWeight(double weight) {
		if(weight <= 0.0)
			throw new InvalidArgumentSimulationException("The maximum weight must be positive.");

		scaleMaximumWeightConfiguration = weight;
	}

	private static double scaleSensitivityConfiguration;

	/**
	 * Configures the sensitivity of the scales.
	 * 
	 * @param sensitivity
	 *            The sensitivity of the scales.
	 */
	public static void configureScaleSensitivity(double sensitivity) {
		if(sensitivity <= 0.0)
			throw new InvalidArgumentSimulationException("The sensitivity must be positive.");

		scaleSensitivityConfiguration = sensitivity;
	}

	/**
	 * Resets the configuration to the default values.
	 */
	public static void resetConfigurationToDefaults() {
		banknoteDenominationsConfiguration = new BigDecimal[] { BigDecimal.ONE };
		banknoteStorageUnitCapacityConfiguration = 1000;
		coinDenominationsConfiguration = new ArrayList<>();
		coinDenominationsConfiguration.add(BigDecimal.ONE);
		coinDispenserCapacityConfiguration = 100;
		coinStorageUnitCapacityConfiguration = 1000;
		coinTrayCapacityConfiguration = 25;
		currencyConfiguration = Currency.getInstance(Locale.CANADA);
		reusableBagDispenserCapacityConfiguration = 100;
		scaleMaximumWeightConfiguration = 5000.0;
		scaleSensitivityConfiguration = 0.5;
	}

	/**
	 * Represents the large scale where items are to be placed once they have been
	 * scanned or otherwise entered.
	 */
	public final AbstractElectronicScale baggingArea;
	/**
	 * Represents the small scale used to weigh items that are sold by weight.
	 */
	public final AbstractElectronicScale scanningArea;
	/**
	 * Represents a printer for receipts.
	 */
	public final ReceiptPrinterBronze printer;
	/**
	 * Represents a device that can read electronic cards, through one or more input
	 * modes according to the setup of the card.
	 */
	public final CardReaderGold cardReader;
	/**
	 * Represents a large, central barcode scanner.
	 */
	public final BarcodeScannerSilver mainScanner;
	/**
	 * Represents a handheld, secondary barcode scanner.
	 */
	public final BarcodeScannerBronze handheldScanner;

	/**
	 * Represents a device that permits banknotes to be entered.
	 */
	public final BanknoteInsertionSlot banknoteInput;
	/**
	 * Represents a device that permits banknotes to be given to the customer.
	 */
	public final BanknoteDispensationSlot banknoteOutput;
	/**
	 * Represents a device that checks the validity of a banknote, and determines
	 * its denomination.
	 */
	public final BanknoteValidator banknoteValidator;
	/**
	 * Represents a device that stores banknotes.
	 */
	public final BanknoteStorageUnit banknoteStorage;
	/**
	 * Represents the set of denominations supported by the self-checkout system.
	 */
	public final BigDecimal[] banknoteDenominations;
	/**
	 * Represents the set of banknote dispensers, indexed by the denomination that
	 * each contains. Note that nothing prevents banknotes of the wrong denomination
	 * to be loaded into a given dispenser.
	 */
	public final Map<BigDecimal, BanknoteDispenserGold> banknoteDispensers;

	/**
	 * Represents a device that permits coins to be entered.
	 */
	public final CoinSlot coinSlot;
	/**
	 * Represents a device that checks the validity of a coin, and determines its
	 * denomination.
	 */
	public final CoinValidator coinValidator;
	/**
	 * Represents a device that stores coins that have been entered by customers.
	 */
	public final CoinStorageUnit coinStorage;
	/**
	 * Represents the set of denominations of coins supported by this self-checkout
	 * system.
	 */
	public final List<BigDecimal> coinDenominations;
	/**
	 * Represents the set of coin dispensers, indexed by the denomination of coins
	 * contained by each.
	 */
	public final Map<BigDecimal, CoinDispenserGold> coinDispensers;
	/**
	 * Represents a device that receives coins to return to the customer.
	 */
	public final CoinTray coinTray;

	/**
	 * Constructor utilizing the current, static configuration.
	 */
	public SelfCheckoutStationSilver() {
		// Create the devices.
		baggingArea = new ElectronicScaleSilver();
		scanningArea = new ElectronicScaleBronze();
		printer = new ReceiptPrinterBronze();
		cardReader = new CardReaderGold();
		mainScanner = new BarcodeScannerSilver();
		handheldScanner = new BarcodeScannerBronze();

		// Since the array in banknoteDenominationsConfiguration was already copied, we
		// can just use it in multiple stations, as it is immutable.
		banknoteDenominations = banknoteDenominationsConfiguration;
		banknoteInput = new BanknoteInsertionSlot();
		banknoteValidator = new BanknoteValidator(currencyConfiguration, banknoteDenominations);
		banknoteStorage = new BanknoteStorageUnit(banknoteStorageUnitCapacityConfiguration);
		banknoteOutput = new BanknoteDispensationSlot();

		banknoteDispensers = new HashMap<>();

		for(int i = 0; i < banknoteDenominations.length; i++)
			banknoteDispensers.put(banknoteDenominations[i], new BanknoteDispenserGold());

		coinDenominations = coinDenominationsConfiguration;
		coinSlot = new CoinSlot();
		coinValidator = new CoinValidator(currencyConfiguration, this.coinDenominations);
		coinStorage = new CoinStorageUnit(coinStorageUnitCapacityConfiguration);
		coinTray = new CoinTray(coinTrayCapacityConfiguration);

		coinDispensers = new HashMap<>();

		for(int i = 0; i < coinDenominations.size(); i++)
			coinDispensers.put(coinDenominations.get(i), new CoinDispenserGold(coinDispenserCapacityConfiguration));

		// Hook up everything.
		interconnect(banknoteInput, banknoteValidator);
		interconnect(banknoteValidator, banknoteStorage);

		for(AbstractBanknoteDispenser dispenser : banknoteDispensers.values())
			interconnect(dispenser, banknoteOutput);

		interconnect(coinSlot, coinValidator);
		interconnect(coinValidator, coinTray, coinDispensers, coinStorage);

		for(CoinDispenserGold coinDispenser : coinDispensers.values())
			interconnect(coinDispenser, coinTray);
	}

	private void interconnect(BanknoteInsertionSlot slot, BanknoteValidator validator) {
		TwoWayChannel<Banknote> channel = new TwoWayChannel<Banknote>(slot, validator);
		slot.sink = channel;
		validator.source = channel;
	}

	private void interconnect(BanknoteValidator validator, BanknoteStorageUnit storage) {
		OneWayChannel<Banknote> channel = new OneWayChannel<Banknote>(storage);
		validator.sink = channel;
	}

	private void interconnect(AbstractBanknoteDispenser dispenser, BanknoteDispensationSlot slot) {
		OneWayChannel<Banknote> channel = new OneWayChannel<Banknote>(slot);
		dispenser.sink = channel;
	}

	private void interconnect(CoinSlot slot, CoinValidator validator) {
		OneWayChannel<Coin> channel = new OneWayChannel<Coin>(validator);
		slot.sink = channel;
	}

	private void interconnect(CoinValidator validator, CoinTray tray, Map<BigDecimal, CoinDispenserGold> dispensers,
		CoinStorageUnit storage) {
		OneWayChannel<Coin> rejectChannel = new OneWayChannel<Coin>(tray);
		Map<BigDecimal, Sink<Coin>> dispenserChannels = new HashMap<BigDecimal, Sink<Coin>>();

		for(BigDecimal denomination : dispensers.keySet()) {
			CoinDispenserGold dispenser = dispensers.get(denomination);
			dispenserChannels.put(denomination, new OneWayChannel<Coin>(dispenser));
		}

		OneWayChannel<Coin> overflowChannel = new OneWayChannel<Coin>(storage);

		validator.rejectionSink = rejectChannel;
		validator.standardSinks.putAll(dispenserChannels);
		validator.overflowSink = overflowChannel;
	}

	private void interconnect(CoinDispenserGold dispenser, CoinTray tray) {
		OneWayChannel<Coin> channel = new OneWayChannel<Coin>(tray);
		dispenser.sink = channel;
	}

	/**
	 * Plugs in all the devices in the station.
	 * @param grid The power grid to plug into.  Cannot be null.
	 */
	public void plugIn(PowerGrid grid) {
		baggingArea.plugIn(grid);
		for(AbstractBanknoteDispenser bd : banknoteDispensers.values())
			bd.connect(grid);
		banknoteInput.connect(grid);
		banknoteOutput.connect(grid);
		banknoteStorage.connect(grid);
		banknoteValidator.connect(grid);
		cardReader.plugIn(grid);
		for(CoinDispenserGold cd : coinDispensers.values())
			cd.connect(grid);
		coinSlot.connect(grid);
		coinStorage.connect(grid);
		// Don't turn on the coin tray
		coinValidator.connect(grid);
		handheldScanner.plugIn(grid);
		mainScanner.plugIn(grid);
		printer.plugIn(grid);
		scanningArea.plugIn(grid);
	}

	/**
	 * Unplugs all the devices in the station.
	 */
	public void unplug() {
		baggingArea.unplug();
		for(AbstractBanknoteDispenser bd : banknoteDispensers.values())
			bd.disconnect();
		banknoteInput.disconnect();
		banknoteOutput.disconnect();
		banknoteStorage.disconnect();
		banknoteValidator.disconnect();
		cardReader.unplug();
		for(CoinDispenserGold cd : coinDispensers.values())
			cd.disconnect();
		coinSlot.disconnect();
		coinStorage.disconnect();
		// Don't turn on the coin tray
		coinValidator.disconnect();
		handheldScanner.unplug();
		mainScanner.unplug();
		printer.unplug();
		scanningArea.unplug();
	}

	/**
	 * Turns on all the devices in the station.
	 */
	public void turnOn() {
		baggingArea.turnOn();
		for(AbstractBanknoteDispenser bd : banknoteDispensers.values())
			bd.activate();
		banknoteInput.activate();
		banknoteOutput.activate();
		banknoteStorage.activate();
		banknoteValidator.activate();
		cardReader.turnOn();
		for(CoinDispenserGold cd : coinDispensers.values())
			cd.activate();
		coinSlot.activate();
		coinStorage.activate();
		// Don't turn on the coin tray
		coinValidator.activate();
		handheldScanner.turnOn();
		mainScanner.turnOn();
		printer.turnOn();
		scanningArea.turnOn();
	}

	/**
	 * Turns off all the devices in the station.
	 */
	public void turnOff() {
		baggingArea.turnOff();
		for(AbstractBanknoteDispenser bd : banknoteDispensers.values())
			bd.disactivate();
		banknoteInput.disactivate();
		banknoteOutput.disactivate();
		banknoteStorage.disactivate();
		banknoteValidator.disactivate();
		cardReader.turnOff();
		for(CoinDispenserGold cd : coinDispensers.values())
			cd.disactivate();
		coinSlot.disactivate();
		coinStorage.disactivate();
		// Don't turn on the coin tray
		coinValidator.disactivate();
		handheldScanner.turnOff();
		mainScanner.turnOff();
		printer.turnOff();
		scanningArea.turnOff();
	}
}
