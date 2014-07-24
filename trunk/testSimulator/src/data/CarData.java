/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

import java.util.ArrayDeque;

/**
 *
 * @author Eran
 */
public class CarData extends Data{
    
    public enum Direction {Work, Home, Spend, None};
    
    private Direction drivingDirection;
    
    public ArrayDeque<String> currPath; //current path from 'source' to 'destination'
    private String source, destination, currSource, currDestination;
    private double speed, currSegmentLength; // holds the current speed of the car, and the length of the current road-segment respectively.
    private boolean parkingAtPL; // holds True if the car is parking in a parking lot.

    private String cache_destination;
    private Direction cache_drivingDirection;
    private boolean cache_parkingAtPL;

    public CarData() {
        this.drivingDirection = Direction.None;
        this.parkingAtPL = false; // initially parking at home.
        this.currSegmentLength = 0;
    }

    public Direction getDrivingDirection() {
        return drivingDirection;
    }

    public void setDrivingDirection(Direction drivingDirection) {
        this.drivingDirection = drivingDirection;
    }

    public void setCurrEdge(String currSource, String currDestination) {
        this.currSource = currSource;
        this.currDestination = currDestination;
    }

    public String getCurrSource() {
        return currSource;
    }

    public String getCurrDestination() {
        return currDestination;
    }
    
    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public double getSpeed() {
        return speed;
    }

    public double getCurrSegmentLength() {
        return currSegmentLength;
    }

    public boolean isParkingAtPL() {
        return parkingAtPL;
    }

    public void setParkingAtPL(boolean parkingAtPL) {
        this.parkingAtPL = parkingAtPL;
    }

    public void setCurrSegmentLength(double currSegmentLength) {
        this.currSegmentLength = currSegmentLength;
    }
    
    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public void cacheData(String dest, Direction dir, boolean park){
        this.cache_destination = dest;
        this.cache_drivingDirection = dir;
        this.cache_parkingAtPL = park;
    }
    
    public void loadData(){
        this.destination = this.cache_destination;
        this.parkingAtPL = this.cache_parkingAtPL;
        this.drivingDirection = this.cache_drivingDirection;
        
        this.cache_destination = null;
        this.cache_drivingDirection = null;
        this.cache_drivingDirection = null;
    }
}
