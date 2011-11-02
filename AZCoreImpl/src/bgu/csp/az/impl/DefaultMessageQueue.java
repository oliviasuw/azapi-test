/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bgu.csp.az.impl;

import bgu.csp.az.api.Message;
import bgu.csp.az.api.MessageQueue;
import bgu.csp.az.api.exp.InternalErrorException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author bennyl
 */
public class DefaultMessageQueue implements MessageQueue {

    LinkedBlockingQueue<Message> q = new LinkedBlockingQueue<Message>();
    Semaphore count = new Semaphore(0);

    @Override
    public Message take() throws InterruptedException {
        Message ret = q.take();
        count.acquire();
        return ret;
    }

    @Override
    public void add(Message e) {
        //System.out.println("Mailer add message");
        if (! q.offer(e)) throw new InternalErrorException("cannot insert message " + e + " to agent queue");
        //System.out.println("Going to notify agent");
        count.release();
        //System.out.println("Agent notified");
    }

    @Override
    public int size() {
        return q.size();
    }

    @Override
    public void waitForNewMessages() throws InterruptedException {
        //System.out.println("Waiting for messages: q is " + q.size());
        count.acquire();
        count.release();
        //System.out.println("Done Waiting for messages");

    }
}
