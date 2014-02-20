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
public class MoveEvent implements SimulatorEvent {

    int id;
    String edge;
    int percentage;

    public MoveEvent() {
    }

    public int getId() {
        return id;
    }

    public String getEdge() {
        return edge;
    }

    public int getPercentage() {
        return percentage;
    }

    public MoveEvent(int id, String edge, int percentage) {
        this.id = id;
        this.edge = edge;
        this.percentage = percentage;
    }

}
