/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphmovementvisualization.impl;

import graphmovementvisualization.api.SimulatorEvent;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Shl
 */
public class SimpleSimulatorEvent implements SimulatorEvent {
    
    private String name;
    private Collection<? extends Object> parameters;

    public SimpleSimulatorEvent() {
        parameters = new LinkedList<>();
        name = "anonymous";
    }

    
    public SimpleSimulatorEvent(String name, Collection<? extends Object> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends Object> getParameters() {
        return parameters;
    }
    
}
