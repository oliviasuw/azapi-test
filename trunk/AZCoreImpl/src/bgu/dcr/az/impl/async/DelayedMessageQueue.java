/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bgu.dcr.az.impl.async;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.MessageQueue;
import bgu.dcr.az.api.exp.InternalErrorException;
import bgu.dcr.az.api.mdelay.MessageDelayer;
import bgu.dcr.az.impl.DefaultMessageQueue;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author bennyl
 */
public class DelayedMessageQueue implements MessageQueue {

    PriorityBlockingQueue<Message> internalq;
    PriorityBlockingQueue<Message> q;
    Semaphore count = new Semaphore(0);
    MessageDelayer dman;
    boolean agentFinished = false;
    
    public DelayedMessageQueue(final MessageDelayer dman) {
        this.dman = dman;
        MessageTimeComparator mtc = new MessageTimeComparator(dman);

        q = new PriorityBlockingQueue<Message>(1000, mtc);
        internalq = new PriorityBlockingQueue<Message>(1000, mtc);
    }

    @Override
    public void onAgentFinish() {
        agentFinished = true;
    }

    /**
     * release from the internal queue all the messages with the time lower or
     * equal then the given time
     *
     * @param time
     */
    public void release(long time) {
        Message peeked;
        while ((peeked = internalq.peek()) != null && dman.extractTime(peeked) <= time) {
            Message msg = internalq.poll();
            if (msg == null) {
                return;
            }

            long mtime = dman.extractTime(msg);
            if (mtime <= time) {
                add(msg);
            } else {
                internalq.offer(msg);
                return;
            }
        }
    }

    @Override
    public Message take() throws InterruptedException {
        Message ret = q.take();
        count.acquire();
        if (ret == DefaultMessageQueue.SYSTEM_RELEASE_MESSAGE) return null;
        return ret;
    }

    /**
     * will try to add the message if the time is right else will put it in the
     * internal q
     *
     * @param e
     * @param time
     */
    public void tryAdd(Message e, long time) {
        long mtime = dman.extractTime(e);

        if (mtime <= time) { 
            add(e);
        } else {
            internalq.offer(e);
        }
    }

    @Override
    public void add(Message e) {
        if (!q.offer(e)) {
            throw new InternalErrorException("cannot insert message " + e + " to agent queue");
        }
        count.release();
    }

    
    
    @Override
    public int size() {
        if (agentFinished) return 0;
        return q.size();
    }

    @Override
    public void waitForNewMessages() throws InterruptedException {
        count.acquire();
        count.release();
    }

    @Override
    public boolean isEmpty() {
        return this.q.isEmpty();
    }

    @Override
    public boolean isNotEmpty() {
        return !this.q.isEmpty();
    }

    /**
     * @return from all the messages in the innerq the minimum message time - or
     * null if the innerq is empty.
     */
    public Long minimumMessageTime() {
        if (agentFinished) return null;
        Message m = internalq.peek();
        if (m == null) {
            return null;
        }

        return dman.extractTime(m);
    }

    @Override
    public void releaseBlockedAgent() {
        add(DefaultMessageQueue.SYSTEM_RELEASE_MESSAGE);
    }

    public static class MessageTimeComparator implements Comparator<Message> {

        MessageDelayer dman;

        public MessageTimeComparator(MessageDelayer dman) {
            this.dman = dman;
        }

        @Override
        public int compare(Message o1, Message o2) {
            if (o1 == DefaultMessageQueue.SYSTEM_RELEASE_MESSAGE) return -1;
            if (o2 == DefaultMessageQueue.SYSTEM_RELEASE_MESSAGE) return 1;
            if ((o1 == o2) &&( o1 == DefaultMessageQueue.SYSTEM_RELEASE_MESSAGE)) return 0;
            
            Long time1 = null, time2 = null;
            try {
                time1 = dman.extractTime(o1);
            } catch (Exception ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                } else {
                    throw new InternalErrorException("Cannot take time from message " + o1 + " cannot perform message delay.", ex);
                }
            }

            try {
                time2 = dman.extractTime(o2);
            } catch (Exception ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                } else {
                    throw new InternalErrorException("Cannot take time from message " + o2 + " cannot perform message delay.", ex);
                }
            }

            return (int) (time1 - time2);
        }
    }
}
