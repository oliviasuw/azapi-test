/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Administrator
 */
public class Frame<T> {
    private long number;
    private Map<Integer, List<Event<T>>> eventsPerAgent = new ConcurrentHashMap<Integer, List<Event<T>>>();

    public Frame(long number) {
        this.number = number;
    }
    
    public void addEvent(int agent, Event<T> event){
        List<Event<T>> list = eventsPerAgent.get(agent);
        if (list == null){
            list = new LinkedList<Event<T>>();
            eventsPerAgent.put(agent, list);
        }
        
        list.add(event);
    }
    
    public List<Event<T>> getEventsForAgent(int agent){
        return eventsPerAgent.get(agent);
    }
    
    public Set<Entry<Integer, List<Event<T>>>> getEvents(){
        return eventsPerAgent.entrySet();
    }

    public long getFrameNumber() {
        return number;
    }
}
