/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.async;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.MessageQueue;
import bgu.dcr.az.api.mdelay.MessageDelayer;
import bgu.dcr.az.impl.AbstractMailer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author bennyl
 */
public class AsyncDelayedMailer extends AbstractMailer {

    MessageDelayer dman;
    AtomicInteger time;

    public AsyncDelayedMailer(MessageDelayer dman) {
        this.dman = dman;
        this.time = new AtomicInteger(dman.getInitialTime());
    }

    @Override
    protected MessageQueue generateNewMessageQueue(String groupKey) {
        return new DelayedMessageQueue(dman);
    }

    @Override
    public void send(Message msg, int to, String groupKey) {
        int mtime = dman.extractTime(msg); //time before delay
        dman.addDelay(msg, msg.getSender(), to); //adding delay
        MessageQueue[] qus = takeQueues(groupKey); //all the queues from the current group

        ((DelayedMessageQueue) qus[to]).tryAdd(msg, time.get());//try to add the message to the queue, if the time after the delay < time

        if (mtime > time.get()) { //if the time before the delay is bigger then the current time
            int ctime; //try to update the current time
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
                ((DelayedMessageQueue) q).release(ctime);
            }
        }

    }
}
