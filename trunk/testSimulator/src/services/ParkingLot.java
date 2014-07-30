/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services;

import java.util.HashSet;

/**
 *
 * @author Eran
 */
class ParkingLot<T>{
    private HashSet<T> set;
    private final int capacity;

    public ParkingLot(int max) {
        this.set = new HashSet<>();
        this.capacity = max;
    }
    
    public void addCar(T car){
//        if(set.size() < capacity){
//            this.set.add(car);
//            return true;
//        }
//        else
//            return false;
        this.set.add(car);
    }
    
    public boolean notFull(){
        return set.size() < capacity;
    }
    
    public void removeCar(T car){
        set.remove(car);
    }
}
