/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl;

import bgu.csp.az.api.Message;
import bgu.csp.az.api.MessageQueue;
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
            q.add(e);
            count.release();
        }
        
    @Override
        public int size(){
            return q.size();
        }
        
    @Override
        public void waitForNewMessages() throws InterruptedException{
            count.acquire();
            count.release();
        }
}
