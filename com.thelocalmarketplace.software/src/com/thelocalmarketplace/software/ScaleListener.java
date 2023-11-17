//Yang Yang:30156356
//Edited on the base of Iteration1

package com.thelocalmarketplace.software;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.*;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;

public class ScaleListener implements ElectronicScaleListener {

    private Cart cart;

    private  AbstractSelfCheckoutStation station;

    private ScaleListener(){}

    public ScaleListener(Cart cart, AbstractSelfCheckoutStation station)
    {
        this.cart = cart;
        this.station = station;
    }

    @Override
    public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) 
    {
        IElectronicScale  baggingArea;
        IElectronicScale  scanningArea ;
        
       baggingArea =  station.baggingArea;
       scanningArea  = station.scanningArea;

        //if the current weight of the cart is not equal to the current weight on the scale we have a weight discrepency
        double totalCart = this.cart.getMass();
        Mass m = new Mass(totalCart);

        //since total cart is in grams i will convert mass to grams
        double mass_grams = mass.inGrams().doubleValue();
        Mass mg = new Mass(mass_grams);

        //check if the difference expected to actual is within sensativity limit
        Mass delta_mass = m.difference(mg).abs();
        int compare = delta_mass.compareTo(new Mass(baggingArea.getSensitivityLimit().inGrams().doubleValue()));


        if ( compare==-1 || compare == 0) {
            this.cart.canChange();
            scanningArea.enable();
//            coinValidator.enable();
        }else {
            this.cart.cantChange();
            scanningArea.disable();
//            station.coinValidator.disable();
        }
    }



    @Override
    public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {

    }

    @Override
    public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {

    }

    @Override
    public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {

    }

    @Override
    public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {

    }
}
