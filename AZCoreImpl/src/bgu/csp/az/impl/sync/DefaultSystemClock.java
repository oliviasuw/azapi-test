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
    private long time;
    private volatile boolean closed = false;
    private volatile boolean ticked = false;

    public DefaultSystemClock() {
        this.time = 0;
    }

    public void setExcution(Execution exc) {
        System.out.println("DefaultSystemClock: Barrier Set to Be: " + exc.getNumberOfAgentRunners());
        this.barrier = new CyclicBarrier(exc.getNumberOfAgentRunners());
    }

    @Override
    public void tick() throws InterruptedException {
        if (closed) return;
        try {
            ticked = true;
            long nextTime = time + 1;
            barrier.await();
            
            /**
             * visibility problem should not occure here as every thread must pass through the following line
             * thus its local cpu cache will get updated.
             */
            time = nextTime;
            ticked = false;
            
        } catch (BrokenBarrierException ex) {
            if (closed) return;
            System.err.println("got BrokenBarrierException, translating it to InterruptedException (DefaultSystemClock)");
            throw new InterruptedException(ex.getMessage());
        }
    }

    @Override
    public long time() {
        return time;
    }

    @Override
    public void close() {
        closed = true;
        barrier.reset();
    }

    public boolean isTicked() {
        return ticked;
    }
    
}
