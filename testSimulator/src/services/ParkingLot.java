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
    private final int max;

    public ParkingLot(int max) {
        this.set = new HashSet<>();
        this.max = max;
    }
    
    public boolean addCar(T car){
        if(set.size() < max){
            this.set.add(car);
            return true;
        }
        else
            return false;
    }
    
    public void removeCar(T car){
        set.remove(car);
    }
}
