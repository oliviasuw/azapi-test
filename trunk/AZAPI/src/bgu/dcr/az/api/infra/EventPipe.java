/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author bennyl
 */
public class EventPipe<EVENT> {

    private LinkedBlockingDeque<EVENT> q;
    private ReentrantReadWriteLock locks;

    public EventPipe() {
        q = new LinkedBlockingDeque<EVENT>();
        locks = new ReentrantReadWriteLock();
    }

    public void append(EVENT e) {
        try {
            locks.readLock().lock();
            q.addLast(e);
        } finally {
            locks.readLock().unlock();
        }
    }

    public synchronized void waitForEvents() throws InterruptedException{
        EVENT e = q.takeFirst();
        q.addFirst(e);
    }
    
    public synchronized List<EVENT> takeAll() {
        LinkedList<EVENT> ret = new LinkedList<EVENT>();
        try {
            locks.writeLock().lock();
            q.drainTo(ret);
        } finally {
            locks.writeLock().unlock();
        }
        return ret;
    }
}
