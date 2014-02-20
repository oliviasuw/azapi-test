/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.events.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import data.events.api.SimulatorEvent;
import graphmovementvisualization.GraphMovementVisualization;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shl
 */
public class EventReader {

    private final Kryo kryo;

    public EventReader(Kryo kryo) {
        this.kryo = kryo;

    }

    public Collection<SimulatorEvent> readNextTickFromInput(Input input) {
        LinkedList tickEvents = new LinkedList();
        try {
            if (input.available() != 0) {
                Object readObject = kryo.readClassAndObject(input);
                int lastPosition = input.position();
                if (readObject instanceof TickEvent) {
                    readObject = kryo.readClassAndObject(input);
                    while (!(readObject instanceof TickEvent)) {
                        Object event = readObject;
                        tickEvents.add(event);
                        lastPosition = input.position();
                        if (input.available() == 0) {
                            break;
                        }
                        readObject = kryo.readClassAndObject(input);
                    }
                }
                input.setPosition(lastPosition);
            }
        } catch (IOException ex) {
            Logger.getLogger(GraphMovementVisualization.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tickEvents;
    }
}
