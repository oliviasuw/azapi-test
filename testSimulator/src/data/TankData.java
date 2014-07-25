/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import attributes.Behavior;
import data.CarData.Direction;
import statistics.Utility;

/**
 *
 * @author Eran
 */
public class TankData extends Data {

    private boolean electric;
    private final double capacity;
    private double currAmount;

    public TankData(boolean electric) {
        this.capacity = Utility.generateCapacity(electric);
        this.currAmount = this.capacity;
        this.electric = electric;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getCurrAmount() {
        return currAmount;
    }

    /**
     * Decrease the amount of fuel/power of the tank according to the distance
     * traveled.
     *
     * @param distance
     */
    public void consume(double distance) {
        double factor;

        if (this.electric) {
            factor = Utility.ELECTRICITY_CONSUMPTION_RATIO;
        } else {
            factor = Utility.FUEL_CONSUMPTION_RATIO;
        }

        this.currAmount -= factor * distance;
        if (this.currAmount < 0) {
            this.currAmount = 0;
        }
        Behavior.debug(String.format("Agent:: %f%% power left.", 100.0 * (this.currAmount / this.capacity)));
    }

    /**
     * Recharge the car's tank. Return true if the tank is full, false
     * otherwise.
     *
     * @return
     */
    public void refill() {
        double factor;

        if (this.electric) {
            factor = Utility.ELECTRICITY_RECHARGE_RATE;
        } else {
            factor = Utility.FUEL_RECHARGE_RATE;
        }

        this.currAmount += factor;

        if (this.currAmount > this.capacity) {
            this.currAmount = this.capacity;
        }
    }

    /**
     * Check if need to recharge tank.
     *
     * @return
     */
    public boolean isCritical() {
        return (this.currAmount / this.capacity) < Utility.TANK_CRITICAL;
    }

    /**
     * Check if the tank is full.
     * @return 
     */
    public boolean isFull() {
        return this.currAmount == this.capacity;
    }

}
