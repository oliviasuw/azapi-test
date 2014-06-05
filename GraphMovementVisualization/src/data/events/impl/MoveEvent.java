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

    private int id;
    private String fromNode;
    private String toNode;
    private double startPrecent;
    private double endPrecent;

    public MoveEvent() {
    }

    public MoveEvent(int id, String fromNode, String toNode, double startPrecent, double endPrecent) {
        this.id = id;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.startPrecent = startPrecent;
        this.endPrecent = endPrecent;
    }

    public int getId() {
        return id;
    }

    public String getFromNode() {
        return fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public double getStartPrecent() {
        return startPrecent;
    }

    public double getEndPrecent() {
        return endPrecent;
    }

    


}
