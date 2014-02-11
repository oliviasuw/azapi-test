/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphmovementvisualization.impl;

import graphmovementvisualization.api.SimulatorEvent;
import java.util.Collection;

/**
 *
 * @author Shl
 */
public class SimpleSimulatorEvent implements SimulatorEvent {
    
    private String name;
    private Collection<Object> parameters;

    public SimpleSimulatorEvent(String name, Collection<Object> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<Object> getParameters() {
        return parameters;
    }
    
}
