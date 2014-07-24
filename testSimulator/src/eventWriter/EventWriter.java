/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventWriter;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Shl
 */
public class EventWriter {

    private final Kryo kryo;

    public EventWriter(Kryo kryo) {
        this.kryo = kryo;
    }
    
    public void writeEventsToNewTick(Collection<SimulatorEvent> events, Output output, int tickNumber) {
        writeTick(output, tickNumber);
        writeEvents(output, events);
    }
    
    
    public void writeTick(Output output, int tickNumber) {
        kryo.writeClassAndObject(output, new TickEvent(tickNumber));
    }
    
    
    public void writeEvent(Output output, SimulatorEvent event) {
            kryo.writeClassAndObject(output, event);
    }
    
    public void writeEvents(Output output, Collection<SimulatorEvent> events) {
        for (Iterator<SimulatorEvent> it = events.iterator(); it.hasNext();) {
            writeEvent(output, it.next());
        }
    }
    

}
