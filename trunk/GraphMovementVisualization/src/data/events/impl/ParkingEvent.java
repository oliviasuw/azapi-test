/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.events.impl;

import data.events.api.SimulatorEvent;

/**
 *
 * @author Shl
 */
public class ParkingEvent implements SimulatorEvent {

    
    //car going in or out
    public enum InOut { IN, OUT };
    public enum CarType { FUEL, ELECTRIC };

    //the car that enters or exits the parking lot
    private int carId;
    
    //the node ID of the parking lot
    private String parkNodeId;
    
    //battery precentage status of car
    private double precentage; 

    private InOut inOut;
    private CarType carType;
    
    
    public ParkingEvent() {
        
    }

    public ParkingEvent(int carId, String parkNodeId, double precentage, InOut inOut, CarType carType) {
        this.carId = carId;
        this.parkNodeId = parkNodeId;
        this.precentage = precentage;
        this.inOut = inOut;
        this.carType = carType;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getParkNodeId() {
        return parkNodeId;
    }

    public void setParkNodeId(String parkNode) {
        this.parkNodeId = parkNode;
    }

    public double getPrecentage() {
        return precentage;
    }

    public void setPrecentage(double precentage) {
        this.precentage = precentage;
    }

    public InOut getInOut() {
        return inOut;
    }

    public void setInOut(InOut inOut) {
        this.inOut = inOut;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

 
    
    
}
