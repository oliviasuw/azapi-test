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

    public synchronized void addListener(Listener l) {
        listeners.add(l);
    }

    private void fireIdleDetected() {
//        System.out.println("IDLE DETECTED!");
        boolean resolved = false;
        for (Listener l : listeners) {
            l.onIdleDetection();
        }

        for (Listener l : listeners) {
            resolved |= l.tryResolveIdle();
            if (resolved) {
                break;
            }
        }

        if (!resolved) {
            for (Listener l : listeners) {
                l.idleCannotBeResolved();
            }
        } else {
            for (Listener l : listeners) {
                l.idleResolved();
            }
        }
    }

    public static interface Listener {

        /**
         * this callback will get called once an idle detection was found 
         * this method should never try to resolve the idle - it is only meant to 
         * make updates for shared stated before the resolving phase
         */
        void onIdleDetection();

        /**
         * this callback will get called when idle was detected - this callback should 
         * try to recover from the idle state
         * if the recovery succedded then the callback should return true and false otherwise
         * once one of the listeners return true there will be no more invokations of this callback
         */
        boolean tryResolveIdle();

        /**
         * this callback will get called after all the listeners was notified about idle detection 
         * and after all of them returned false
         */
        void idleCannotBeResolved();

        /**
         * this callback will get called if the idle was resolved
         * like the "onIdleDetection" function it is meant to update shared state
         */
        void idleResolved();
    }
}
