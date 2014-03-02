/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.events.impl;

import data.events.api.SimulatorEvent;
import java.util.Collection;

/**
 *
 * @author Shl
 */
public class Tick {
    private int tickNum;
    private Collection<SimulatorEvent> events;

    public Tick(int tickNum, Collection<SimulatorEvent> events) {
        this.tickNum = tickNum;
        this.events = events;
    }

    public int getTickNum() {
        return tickNum;
    }

    public Collection<SimulatorEvent> getEvents() {
        return events;
    }
    
    
    
}
