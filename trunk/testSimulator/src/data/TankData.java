/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.CarData.Direction;
import statistics.Utility;

/**
 *
 * @author Eran
 */
public class TankData extends Data{

    private boolean electric;
    private final double capacity;
    private double currAmount;
    
    private Direction cache_drivingDirection;
    private boolean cache_parkingAtPL;
    private String cache_dest;

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

    public void tick(double distance) {
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
    }

    /**
     * Recharge the car's tank. Return true if the tank is full, false
     * otherwise.
     *
     * @return
     */
    public boolean refill() {
        double factor;

        if (this.electric) {
            factor = Utility.ELECTRICITY_RECHARGE_RATE;
        } else {
            factor = Utility.FUEL_RECHARGE_RATE;
        }

        this.currAmount += factor;

        if (this.currAmount < this.capacity) {
            return false;
        } else {
            this.currAmount = this.capacity;
            return true;
        }
    }

    /**
     * Check if need to recharge tank.
     * @return 
     */
    public boolean isCritical() {
        return (this.currAmount / this.capacity) < Utility.TANK_CRITICAL;
    }

}
