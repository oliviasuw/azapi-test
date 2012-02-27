/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.async;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.MessageQueue;
import bgu.dcr.az.api.mdelay.MessageDelayer;
import bgu.dcr.az.impl.AbstractMailer;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author bennyl
 */
public class AsyncDelayedMailer extends AbstractMailer {

    MessageDelayer dman;
    AtomicLong time;

    public AsyncDelayedMailer(MessageDelayer dman, int numberOfAgents) {
        this.dman = dman;
        this.time = new AtomicLong(dman.getInitialTime());
    }

    @Override
    protected MessageQueue generateNewMessageQueue(String groupKey) {
        return new DelayedMessageQueue(dman);
    }

    @Override
    public void send(Message msg, int to, String groupKey) {
        msg = msg.copy(); //deep copying the message.
        long mtime = dman.extractTime(msg); //time before delay
        dman.addDelay(msg, msg.getSender(), to); //adding delay
        MessageQueue[] qus = takeQueues(groupKey); //all the queues from the current group

        ((DelayedMessageQueue) qus[to]).tryAdd(msg, time.get());//try to add the message to the queue, if the time after the delay < time

        if (mtime > time.get()) { //if the time before the delay is bigger then the current time
            long ctime; //try to update the current time
            while (true) {
                ctime = time.get();
                if (ctime >= mtime) {
                    return;
                }

                if (time.compareAndSet(ctime, mtime)) {
                    break; //this is me that updated the time so continue to the release code
                }
            }

            for (MessageQueue q : qus) { //release all queues by the known time.
                ((DelayedMessageQueue) q).release(mtime);
            }
        }

    }

    /**
     * @param groupKey
     * @return true if idle was resolved
     */
    public boolean resolveIdle(String groupKey) {

        MessageQueue[] qus = takeQueues(groupKey); //all the queues from the current group
        boolean found = false;
        long min = -1;
       
        for (MessageQueue q : qus) {
            Long v = ((DelayedMessageQueue) q).minimumMessageTime();
            if (v == null) {
                continue;
            }
            if (min > v || !found) {
                min = v;
                found = true;
            }
        }

        if (found) {
            for (MessageQueue q : qus) { //release all queues by the known time.
                ((DelayedMessageQueue) q).release(min);
            }
            return true;
        }

        return false;
    }

}
