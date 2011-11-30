/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.Mailer;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author bennyl
 */
public class IdleDetector {

    private int version = 0;
    private volatile int waiting;
    private Semaphore s = new Semaphore(1);
    private Mailer m;
    private LinkedList<Listener> listeners = new LinkedList<Listener>();
    private String groupKey;

    public IdleDetector(int waiting, Mailer m, String groupKey) {
        this.waiting = waiting;
        this.m = m;
        this.groupKey = groupKey;
    }

    public void inc() {
        try {
            s.acquire();
            version++;
            waiting++;
            s.release();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

    }

    public void dec() {
        int oversion;
        try {
            s.acquire();
            version++;
            oversion = version;
            waiting--;
            s.release();

            
            if (waiting == 0) {
                if (m.isAllMailBoxesAreEmpty(groupKey)) {
                    s.acquire();
                    if (oversion == version) {
                        fireIdleDetected();
                    }
                    s.release();
                }
            }

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

    }

    public synchronized void addListener(Listener l){
        listeners.add(l);
    }
    
    private void fireIdleDetected() {
        System.out.println("IDLE DETECTED!");
        for (Listener l : listeners) l.onIdleDetected();
    
    }
    
    public static interface Listener {
        void onIdleDetected();
    }
}
