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
public class TickEvent implements SimulatorEvent {

    int number;

    public TickEvent() {
    }
    
    

    public TickEvent(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
