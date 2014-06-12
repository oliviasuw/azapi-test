/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.events.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import data.events.api.SimulatorEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import resources.img.R;

/**
 *
 * @author Shl
 */
public class EventReader {

    private final Kryo kryo;

    public EventReader(Kryo kryo) {
        this.kryo = kryo;

    }

    public Tick readNextTickFromInput(Input input) {
        LinkedList tickEvents = new LinkedList();
        int tickNum = -1;
        try {
            if (input.available() != 0) {
                Object readObject = null;
                try {
                 readObject = kryo.readClassAndObject(input);
                }
                catch (Exception e) {
                    System.out.println("");
                }
                int lastPosition = input.position();
                if (readObject instanceof TickEvent) {
                    tickNum = ((TickEvent)readObject).getNumber();
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
            Logger.getLogger(R.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Tick(tickNum, tickEvents);
    }
}
