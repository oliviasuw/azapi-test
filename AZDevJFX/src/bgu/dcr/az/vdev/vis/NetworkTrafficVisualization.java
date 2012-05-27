/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis;

import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.exen.vis.AbstractVisualization;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
@Register(name="net-traffic-vis")
public class NetworkTrafficVisualization extends AbstractVisualization<NetworkTrafficVisualization.State> {

    /**
     * agent to agent messages count
     */
    State nextState = new State();

    public NetworkTrafficVisualization() {
        super(NetworkTrafficVisualizationDrawer.class);
    }

    @Override
    public synchronized List<State> sample() {
        List<State> next = Collections.singletonList(nextState);
        nextState = new State();
        return next;
    }

    @Override
    public void initialize(Execution ex) {
        new Hooks.BeforeMessageSentHook() {

            @Override
            public void hook(int senderId, int recepientId, Message msg) {
                synchronized (NetworkTrafficVisualization.this) {
                    nextState.sentInThisFrame.add(new MessageData(msg, recepientId));
                }
            }
        }.hookInto(ex);

//        return ex;
    }

    public static class MessageData{
        String name;
        int from;
        int to;

        @Override
        public String toString() {
            return  "" + name + " from " + from + " to " + to;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MessageData){
                MessageData other = (MessageData)obj;
                return other.from == from && other.to == to;
            }
            
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + this.from;
            hash = 37 * hash + this.to;
            return hash;
        }

        
        
        

        public MessageData(Message m, int to) {
            this.name = m.getName();
            this.from = m.getSender();
            this.to = to;
        }
        
    }
    
    public static class State {
        List<MessageData> sentInThisFrame = new LinkedList<>();
        int numberOfMessages;
    }
}
