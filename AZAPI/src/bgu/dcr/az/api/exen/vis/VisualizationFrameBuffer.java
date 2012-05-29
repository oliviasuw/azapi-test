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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Administrator
 */
public class VisualizationFrameBuffer<T> {

    private AtomicLong messageIdGenerator = new AtomicLong(0);
    private Execution ex;
    private long[] ncsc;
    private Map<Long, Frame<T>> frames = new HashMap<Long, Frame<T>>();
    private ReentrantReadWriteLock framesLock = new ReentrantReadWriteLock();
    private long currentFrame = 0;

    public VisualizationFrameBuffer(Execution ex) {
        this.ex = ex;
        getOrCreateFrame(currentFrame);
        hookIn();
    }

    private void hookIn() {
        Agent[] agents = ex.getAgents();
        ncsc = new long[agents.length];

        new Hooks.BeforeMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
                createMessageDeliverFrames(a, msg);

                long newNcsc = (Long) msg.getMetadata().get("ncsc");
                ncsc[a.getId()] = max(newNcsc, ncsc[a.getId()]);
                getOrCreateFrame(ncsc[a.getId()] + 1);
                ncsc[a.getId()]++;
            }
        }.hookInto(ex);

        new Hooks.BeforeMessageSentHook() {
            @Override
            public void hook(int sender, int recepient, Message msg) {
                msg.getMetadata().put("exit-ncsc", ncsc[sender]);
                msg.getMetadata().put("ncsc", ncsc[sender]);
            }
        }.hookInto(ex);

        new Hooks.TerminationHook() {
            @Override
            public void hook() {
                long max = max(ncsc) + 1;
                for (int i = 0; i < ncsc.length; i++) {
                    ncsc[i] = max;
                }
            }
        }.hookInto(ex);

    }

    public void addToMessageSentFrames(Message message, int addition) {
        Long current = (Long) message.getMetadata().get("ncsc");
        message.getMetadata().put("ncsc", current + addition);
    }

    private Frame<T> getOrCreateFrame(long frameNumber) {
        Frame<T> ret = frames.get(frameNumber);
        if (ret == null) {
            framesLock.writeLock().lock();
            try {
                ret = frames.get(frameNumber);
                if (ret == null) {
                    ret = new Frame<T>(frameNumber);
                    frames.put(frameNumber, ret);
                }
            } finally {
                framesLock.writeLock().unlock();
            }
        }

        return ret;
    }

    private void createMessageDeliverFrames(Agent receivingAgent, Message msg) {
        Long exit = (Long) msg.getMetadata().get("exit-ncsc");
        Long received = (Long) msg.getMetadata().get("ncsc");

        if (exit != received) { // the message delivery is not instentanious
            Event.MessageTransferData mtd = new Event.MessageTransferData(msg.getName(), msg.getSender(), receivingAgent.getId(), exit, received, messageIdGenerator.getAndIncrement());
            final Event<T> event = new Event<T>();
            event.setMessageTransferData(mtd);
            for (long i = exit; i <= received; i++) {
                getOrCreateFrame(i).addEvent(receivingAgent.getId(), event);
            }
        }
    }

    public void submitEvent(int agent, Event<T> event) {
        framesLock.readLock().lock();
        try {
            frames.get(ncsc[agent]).addEvent(agent, event);
        } finally {
            framesLock.readLock().unlock();
        }
    }

    public void submitEvent(int agent, Event<T> event, int framesToComplete) {
        framesLock.readLock().lock();
        try {
            frames.get(ncsc[agent]).addEvent(agent, event);
        } finally {
            framesLock.readLock().unlock();
        }

        getOrCreateFrame(ncsc[agent] + framesToComplete);
        ncsc[agent] += framesToComplete;
    }

    public Frame nextFrame() {
//        for (int i = 0; i < ncsc.length; i++) {
//            if (ncsc[i] <= currentFrame) {
//                return null;
//            }
//        }
        if (currentFrame < numberOfFrames()) {
            return frames.get(currentFrame++);
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
}
