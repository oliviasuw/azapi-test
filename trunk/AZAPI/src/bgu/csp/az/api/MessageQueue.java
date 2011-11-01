/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author bennyl
 */
public class MessageQueue {
    LinkedBlockingQueue<Message> q = new LinkedBlockingQueue<Message>();
        Semaphore count = new Semaphore(0);
        
        public Message take() throws InterruptedException {
            Message ret = q.take();
            count.acquire();
            return ret;
        }

        public void add(Message e) {
            q.add(e);
            count.release();
        }
        
        public int size(){
            return q.size();
        }
        
        public void waitForNewMessages() throws InterruptedException{
            count.acquire();
            count.release();
        }
}
