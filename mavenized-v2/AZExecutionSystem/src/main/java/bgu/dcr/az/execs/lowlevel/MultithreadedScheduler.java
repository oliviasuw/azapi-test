/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.lowlevel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class MultithreadedScheduler implements Scheduler {

    private Core[] cores;
    private ProcTable table;
    private SystemCalls systemCalls;
    private final ExecutorService execs;
    private int numCores;
    private boolean allowIdle = true;
    private volatile Core failingCore = null;

    private final Semaphore idleDetectionEnterenceLock = new Semaphore(0);
    private final AtomicInteger idleDetectionEnterenceCount = new AtomicInteger(0);

    //for debugging
    volatile int tick = 0;

    private double lastContention = 0;

    public MultithreadedScheduler(ExecutorService execs) {
        this.execs = execs;
        this.numCores = numCores;
    }

    /**
     * in the case where allow-idle set to true and the scheduler found an idle
     * state during the execution the scheduler will attempt to resolve the idle
     * (by idle resolving iteration), in the case it will set to false - then
     * the scheduler will treat idle as execution error
     *
     * @param allowIdle
     */
    public void setAllowIdle(boolean allowIdle) {
        this.allowIdle = allowIdle;
    }

    public boolean isAllowIdle() {
        return allowIdle;
    }

    @Override
    public TerminationReason schedule(ProcTable table, int numCores) throws InterruptedException {

        this.numCores = numCores;
        this.table = table;
        cores = new Core[numCores];
        systemCalls = new SystemCallsImpl();

        for (int i = 0; i < numCores; i++) {
            cores[i] = new Core(i);
        }

        long timer = System.currentTimeMillis();

        executeAndWait(cores);

        timer = System.currentTimeMillis() - timer;
        long totalWaitingTime = 0;
        for (Core c : cores) {
            totalWaitingTime += c.getWaitingTime();
        }

        lastContention = timer == 0 ? 0 : ((double) totalWaitingTime) / ((double) (timer * numCores));

        if (failingCore != null) {
            return new TerminationReason(true, failingCore.exitError, failingCore.misbihavingProcess);
        } else if (table.isEmpty()) {
            return new TerminationReason();
        }

        return new TerminationReason(true, new UnexpectedTerminationException(), null);
    }

    @Override
    public double getContention() {
        return lastContention;
    }

    private void executeAndWait(Core[] cores) throws InterruptedException {
        //execute cores
        List<Future> joins = new ArrayList<>(numCores);
        for (int i = 0; i < numCores; i++) {
            joins.add(execs.submit(cores[i]));
        }

        //wait for something to happen
        for (Future j : joins) {
            try {
                j.get();
            } catch (ExecutionException ex) {
                //should never happen!
                Logger.getLogger(MultithreadedScheduler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void reportFailingCore(Core core) {
        System.out.println("Failing core reported!");

        if (failingCore == null) {
            failingCore = core;
            core.exitError.printStackTrace();
        }

        for (Core c : cores) {
            if (c != core) {
                c.interrupt();
            }
        }

    }

    private class Core implements Runnable {

        Exception exitError = null;
        Proc misbihavingProcess = null;
        Thread currentThread;
        int coreId;
        long waitingTime;

        public Core(int coreId) {
            this.coreId = coreId;
        }

        public long getWaitingTime() {
            return waitingTime;
        }

        @Override
        public void run() {
            waitingTime = 0;

            currentThread = Thread.currentThread();
            Proc proc = null;
            try {
                while (!currentThread.isInterrupted()) {
                    long time = System.currentTimeMillis();
                    proc = table.acquire();
                    waitingTime += System.currentTimeMillis() - time;
                    if (proc != null) {
                        proc.quota(systemCalls);
                        table.release(proc.pid());
                        proc = null;
                    } else {
                        if (allowIdle && !table.isEmpty()) {
                            resolveIdle();
                        } else {
//                            System.out.println("Core " + coreId + " is down");
                            return; //the table is empty - shutdown
                        }
                    }
                }
            } catch (Exception ex) {
                exitError = ex;
                misbihavingProcess = proc;
                reportFailingCore(this);
            } finally {
                if (proc != null) {
                    table.release(proc.pid());
                }
            }
        }

        public void resolveIdle() throws InterruptedException {

            if (idleDetectionEnterenceCount.incrementAndGet() == numCores) {
                idleDetectionEnterenceCount.set(0);

                table.signalIdle();

                idleDetectionEnterenceLock.release(numCores - 1);
            } else {
                long time = System.currentTimeMillis();
                idleDetectionEnterenceLock.acquire();
                waitingTime += System.currentTimeMillis() - time;
            }

        }

        public void interrupt() {
            while (currentThread == null) {
                Thread.yield(); //busy waiting until thread started
            }
            
            currentThread.interrupt();
        }

    } //Class Core.

    private class SystemCallsImpl implements SystemCalls {

        @Override
        public boolean wake(int pid) {
            return table.wake(pid);
        }

        @Override
        public void exec(Proc p) {
            table.add(p);
        }

        @Override
        public int nextProcessId() {
            return table.nextProcessId();
        }

    }//Class SystemCallsImpl

}
