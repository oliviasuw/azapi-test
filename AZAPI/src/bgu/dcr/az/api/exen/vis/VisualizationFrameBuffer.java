/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.Execution;
import static bgu.dcr.az.api.Agt0DSL.*;
import bgu.dcr.az.api.exen.stat.NCSCToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Administrator
 */
public class VisualizationFrameBuffer<T> {

    private AtomicLong messageIdGenerator = new AtomicLong(0);
    private Execution ex;
    private int[] ncsc;
    private int[] linkTime;
    
    private Map<Long, Frame<T>> frames = new HashMap<Long, Frame<T>>();
    private ReentrantReadWriteLock framesLock = new ReentrantReadWriteLock();
    private long currentFrame = 0;
    private boolean frameAutoIncreaseEnabled = true;
    private Map<String, Delta> deltas = new HashMap<String, Delta>();
    
    ////////////////////////////////////////////////////////////////////////////
    /// Message Handling Per Frame                                           ///
    ////////////////////////////////////////////////////////////////////////////
    private ArrayList[] agentHandling;
    private Interval[] currentIntervals;

    public VisualizationFrameBuffer(Execution ex) {
        this.ex = ex;
        //msgInQueueAndOnLine = new AtomicInteger[ex.getAgents().length];
        currentIntervals = new Interval[ex.getAgents().length];
        agentHandling = new ArrayList[ex.getAgents().length];
        linkTime = new int[ex.getAgents().length];

        for (int i = 0; i < ex.getAgents().length; i++) {
            agentHandling[i] = new ArrayList<Interval<String>>();
            //msgInQueueAndOnLine[i] = new AtomicInteger(0);
        }

        getOrCreateFrame(currentFrame);
        hookIn();
    }

    private void hookIn() {
        Frame.currentFrameBuffer = this;
        Agent[] agents = ex.getAgents();
        ncsc = new int[agents.length];

        new Hooks.BeforeMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
//                createMessageDeliverFrames(a, msg);
                int newNcsc = (int) NCSCToken.extract(msg).getValue();
//                System.out.println("Agent " + a.getId() + "Before Interval submit: " + ncsc[a.getId()]);

//                System.out.println("ncsc update for agent " + a.getId() + " from " + ncsc[a.getId()] + " to " + (Math.max(newNcsc, ncsc[a.getId()]) + 1) + " because msg with " + newNcsc);
                ncsc[a.getId()] = Math.max(newNcsc, ncsc[a.getId()]);

                getOrCreateFrame(ncsc[a.getId()] + 1);
                currentIntervals[a.getId()] = new Interval(ncsc[a.getId()], 0, msg.getName());
                ncsc[a.getId()]++;
//                System.out.println("Agent "  + a.getId() + " Interval Submit: " + ncsc[a.getId()]);
            }
        }.hookInto(ex);

        new Hooks.BeforeMessageSentHook() {
            @Override
            public void hook(int sender, int recepient, Message msg) {
//                msg.getMetadata().put("exit-ncsc", ncsc[sender]);
                NCSCToken.extract(msg).setValue(ncsc[sender]);
                //msg.getMetadata().put("ncsc", ncsc[sender]);
                //msgInQueueAndOnLine[recepient].incrementAndGet();
            }
        }.hookInto(ex);

        new Hooks.TerminationHook() {
            @Override
            public void hook() {
                int max = max(ncsc) + 1;
                for (int i = 0; i < ncsc.length; i++) {
                    ncsc[i] = max;
                }

                for (Delta d : deltas.values()) {
                    d.streach((int) max);
                }

                for (long i = 0; i < max; i++) {
                    Frame<T> frame = getOrCreateFrame(i);
                    for (Entry<String, Delta> d : deltas.entrySet()) {
                        frame.set(d.getKey(), d.getValue().get((int) i));
                    }
                }

                deltas = null;
            }
        }.hookInto(ex);

        new Hooks.AfterMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
                currentIntervals[a.getId()].end = ncsc[a.getId()];
                agentHandling[a.getId()].add(currentIntervals[a.getId()]);
//                System.out.println("Agent " + a.getId() + " => " + currentIntervals[a.getId()]);
                getOrCreateFrame(ncsc[a.getId()]);
            }
        }.hookInto(ex);
    }

    //TODO: switch to semaphore
    public synchronized void setMessageDelay(Message message, int recepient, int addition) {
        final NCSCToken ncsct = NCSCToken.extract(message);
        //        NCSCToken.extract(message).increaseValue(addition);
        int ntime = (int) Math.max(linkTime[recepient] + addition/3, ncsc[message.getSender()] + addition);
        if (linkTime[recepient] < ntime) linkTime[recepient] = ntime;
        ncsct.setValue(ntime);
        //        System.out.println("after message delayed " + NCSCToken.extract(message).getValue());
        //        Long current = (Long) message.getMetadata().get("ncsc");
        //        message.getMetadata().put("ncsc", current + addition);
    }

    public int getRecordingFrameOf(int agent) {
        return ncsc[agent];
    }

    private Frame<T> getOrCreateFrame(long frameNumber) {
        Frame<T> ret = frames.get(frameNumber);
        if (ret == null) {
            framesLock.writeLock().lock();
            try {
                ret = frames.get(frameNumber);
                if (ret == null) {
                    ret = new Frame<T>(frameNumber);
//                    ret.copyAgentHandling(agentHandling);
                    frames.put(frameNumber, ret);
                }
            } finally {
                framesLock.writeLock().unlock();
            }
        }

        return ret;
    }
//
//    private void createMessageDeliverFrames(Agent receivingAgent, Message msg) {
//        Long exit = (Long) msg.getMetadata().get("exit-ncsc");
//        Long received = NCSCToken.extract(msg).getValue();
//
//        if (exit != received) { // the message delivery is not instentanious
//            Event.MessageTransferData mtd = new Event.MessageTransferData(msg.getName(), msg.getSender(), receivingAgent.getId(), exit, received, messageIdGenerator.getAndIncrement());
//            final Event<T> event = new Event<T>();
//            event.setMessageTransferData(mtd);
//            for (long i = exit; i <= received; i++) {
//                getOrCreateFrame(i).addEvent(receivingAgent.getId(), event);
//            }
//        }
//    }

    public void storeDelta(String name, Delta d) {
        deltas.put(name, d);
    }

    public void submitChange(String delta, int agent, Change change) {
        //System.out.println("submit change at " + ncsc[agent] + " for agent " + agent + " => " + delta + ": " + change);
        deltas.get(delta).add(ncsc[agent], change);
    }
    
    public void submitChangeInFrame(String delta, int frame, Change change) {
//        System.out.println("submit change in " + ncsc[agent].get() + " in agent " + agent + " => " + delta);
        deltas.get(delta).add(frame, change);
    }
    
    
//    public void submitChange(String delta, Change change, int... timeZones) {
//        int max = -1;
//        for (int t : timeZones) {
//            if (max == -1 || ncsc[t].get() > max) {
//                max = (int) ncsc[t].get();
//            }
//        }
//
//        deltas.get(delta).add((int) max, change);
//    }

//    public void submitChangeInFrame(String delta, int frame, Change change) {
//        deltas.get(delta).add(frame, change);
//    }
    public void stall(int agent, int frames) {
        ncsc[agent]+=frames;
//        System.out.println("agent " + agent + " stalled to frame " + ncsc[agent]);
    }

//    public void submitEvent(int agent, Event<T> event) {
//        framesLock.readLock().lock();
//        try {
//            frames.get(ncsc[agent]).addEvent(agent, event);
//        } finally {
//            framesLock.readLock().unlock();
//        }
//    }
//    public void submitEvent(int agent, Event<T> event, int framesToComplete) {
//        framesLock.readLock().lock();
//        try {
//            frames.get(ncsc[agent]).addEvent(agent, event);
//        } finally {
//            framesLock.readLock().unlock();
//        }
//
//        getOrCreateFrame(ncsc[agent] + framesToComplete);
//        ncsc[agent] += framesToComplete;
//    }

    public Frame nextFrame() {
//        for (int i = 0; i < ncsc.length; i++) {
//            if (ncsc[i] <= currentFrame) {
//                return null;
//            }
//        }
        if (currentFrame < numberOfFrames()) {
            Frame<T> frame = frames.get(currentFrame);
            if (frameAutoIncreaseEnabled) {
                currentFrame++;
            }
            return frame;
        }

        return null;
    }

    public long numberOfFrames() {
        return ncsc[0];
    }

    public void gotoFrame(long frame) {
        currentFrame = min(frame, numberOfFrames());
    }

    public long getCurrentFrame() {
        return currentFrame;
    }

    public boolean isFrameAutoIncreaseEnabled() {
        return frameAutoIncreaseEnabled;
    }

    public void setFrameAutoIncreaseEnabled(boolean enable) {
        this.frameAutoIncreaseEnabled = enable;
    }

    public String[] getMessageHandledInFrame(Frame frame) {
//        System.out.println("message handling for " + frame.getFrameNumber());
        String[] ret = new String[agentHandling.length];
        for (int i = 0; i < agentHandling.length; i++) {
            ArrayList<Interval<String>> a = agentHandling[i];
            int pos = Collections.binarySearch(a, frame.getFrameNumber());
            if (pos >= 0) {
                ret[i] = a.get(pos).data;
            }

        }
//        System.out.println("" + Arrays.toString(ret));
        return ret;
    }

    public static void main(String[] args) {
        ArrayList<Interval<String>> ret = new ArrayList<Interval<String>>();
        ret.add(new Interval<String>(0, 12, "/0/12"));
        ret.add(new Interval<String>(24, 24, "/0/12"));
        ret.add(new Interval<String>(28, 36, "/0/12"));

        int pos = Collections.binarySearch(ret, 7L);
        System.out.println("pos 7 => " + pos);

        pos = Collections.binarySearch(ret, 24L);
        System.out.println("pos 24 => " + pos);

        pos = Collections.binarySearch(ret, 25L);
        System.out.println("pos 25 => " + pos);

        pos = Collections.binarySearch(ret, 28L);
        System.out.println("pos 28 => " + pos);

    }

    private static class Interval<T> implements Comparable<Long> {

        public long start;
        public long end;
        public T data;

        public Interval(long start, long end, T data) {
            this.start = start;
            this.end = end;
            this.data = data;
        }

        @Override
        public int compareTo(Long t) {
            if (t < start) {
                return 1;
            }
            if (t > end) {
                return -1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "[" + data + ": " + start + "=>" + end + "]";
        }
    }
}
