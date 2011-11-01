/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.lsearch;

import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.lsearch.SystemClock;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class DefaultSystemClock implements SystemClock{
    private CyclicBarrier barrier;
    private Execution exc;
    private volatile long time;
    private LinkedList<TickListener> tickListeners;
    
    public DefaultSystemClock(Execution exc) {
        this.exc = exc;
        this.barrier = new CyclicBarrier(exc.getGlobalProblem().getNumberOfVariables());
        this.time = 0;
        this.tickListeners = new LinkedList<TickListener>();
    }
    
    @Override
    public void tick() throws InterruptedException {
        try {
            long nextTime = time + 1;
            barrier.await();
            if (time < nextTime) {
                time = nextTime;
                fireTickHappend();
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
        for (TickListener l : tickListeners) l.onTickHappend(this);
    }
    
}
