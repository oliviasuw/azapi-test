/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.sync;

import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.lsearch.SystemClock;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 *
 * @author bennyl
 */
public class DefaultSystemClock implements SystemClock {

    private CyclicBarrier barrier;
    private Semaphore updateListenersLock;
    private Execution exc;
    private volatile long time;
    private LinkedList<TickListener> tickListeners;

    public DefaultSystemClock() {
        this.time = 0;
        this.tickListeners = new LinkedList<TickListener>();
        this.updateListenersLock = new Semaphore(1);
    }

    public void setExcution(Execution exc) {
        this.exc = exc;
        this.barrier = new CyclicBarrier(exc.getGlobalProblem().getNumberOfVariables());
    }

    @Override
    public void tick() throws InterruptedException {
        try {
            long nextTime = time + 1;
            barrier.await();
            try {
                /**
                 * stop the agents untill all the listeners are updated
                 * needed as only after the listener was updated the agents allowed to continue running 
                 * so we will not get retick before the message queue will get updated..
                 */
                updateListenersLock.acquire();
                if (time < nextTime) {
                    time = nextTime;
                    System.out.println("TICK: " + time);
                    fireTickHappend();
                }
            } finally {
                updateListenersLock.release();
            }
        } catch (BrokenBarrierException ex) {
            System.err.println("got BrokenBarrierException, translating it to InterruptedException (DefaultSystemClock)");
            throw new InterruptedException(ex.getMessage());
        }
    }

    @Override
    public long time() {
        return time;
    }

    @Override
    public void addTickListener(TickListener tickListener) {
        this.tickListeners.add(tickListener);
    }

    @Override
    public void removeTickListener(TickListener tickListener) {
        this.tickListeners.remove(tickListener);
    }

    private void fireTickHappend() {
        for (TickListener l : tickListeners) {
            l.onTickHappend(this);
        }
    }
}
