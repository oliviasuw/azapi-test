/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphData;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Eran
 */
public class EdgeData {

    private double roadLength;
    private int maxCapacity; //edge's maximal capacity.

    private HashSet<Integer> cars; // contain the IDs of the cars on that road segment
    private HashMap<String,String> attributes; // contain the IDs of the cars on that road segment


    public EdgeData(double roadLen, int maxCap) {
        this.roadLength = roadLen;
        this.maxCapacity = maxCap;
        this.cars = new HashSet<>();
        this.attributes = new HashMap<>();
    }

    /**
     * Get the length of the edge.
     * @return 
     */
    public double getRoadLength() {
        return roadLength;
    }

    /**
     * Get maximal capacity of edges within the edge.
     * @return 
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Add car to the edge.
     * @param id 
     */
    public void addCar(int id) {
        cars.add(id);
    }

    /**
     * Check whether the edge contains car(id) or not.
     * @param id
     * @return 
     */
    public boolean contains(int id) {
        return cars.contains(id);
    }

    /**
     * Remove car(id) from the current edge.
     * @param id 
     */
    public void removeCar(int id) {
        cars.remove(id);
    }

    /**
     * Return the number of cars on the edge.
     * @return 
     */
    public int getNumOfCars() {
        return cars.size();
    }

    /**
     * Return a set containing the IDs of the cars on the edge.
     * @return 
     */
    public HashSet<Integer> getCarsID() {
        return cars;
    }
    
    /**
     * Add a collection of attributes to the current edge.
     * @param attributes 
     */
    public void addAttributes(HashMap<String, String> attributes) { 
        if(attributes != null)
            this.attributes.putAll(attributes);
    }
    
    /**
     * Get the set of attributes of the current edge.
     * @return 
     */
    public HashMap<String,String> getAttributes() {
        return attributes;
    }
}
