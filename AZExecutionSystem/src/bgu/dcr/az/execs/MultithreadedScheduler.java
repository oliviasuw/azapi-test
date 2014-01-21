/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs;

import bgu.dcr.az.execs.api.Proc;
import bgu.dcr.az.execs.api.ProcTable;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.execs.api.SystemCalls;
import bgu.dcr.az.execs.api.TerminationReason;
import bgu.dcr.az.execs.api.UnexpectedTerminationException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final int numCores;
    private boolean allowIdle = true;
    private volatile Core failingCore = null;

    //optimization for asynchronized idle resolvation
    private final AtomicInteger numberOfIdleDetectors = new AtomicInteger(0);
    private final Semaphore resumeLock = new Semaphore(0);
    private final Semaphore detectionLeaveLock = new Semaphore(0);

    //better optimization for asynchronized idle resolvation
    private final AtomicBoolean firstToDetectIdle = new AtomicBoolean(true);
    private final AtomicInteger numberOfIdleReolversLeft = new AtomicInteger(0);

    boolean fastDetectionMode = true;

    //for debugging
    volatile int tick = 0;

    public MultithreadedScheduler(ExecutorService execs, int numCores) {
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
    public TerminationReason schedule(ProcTable table) throws InterruptedException {
        this.table = table;
        cores = new Core[numCores];
        systemCalls = new SystemCallsImpl();

        for (int i = 0; i < numCores; i++) {
            cores[i] = new Core(i);
        }

        executeAndWait(cores);

        if (failingCore != null) {
            return new TerminationReason(true, failingCore.exitError, failingCore.misbihavingProcess);
        } else if (table.isEmpty()) {
            return new TerminationReason();
        }

        return new TerminationReason(true, new UnexpectedTerminationException(), null);
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

        public Core(int coreId) {
            this.coreId = coreId;
        }

        @Override
        public void run() {
            currentThread = Thread.currentThread();
            Proc proc = null;
            try {
                while (!currentThread.isInterrupted()) {
                    proc = table.acquire();
                    if (proc != null) {
                        proc.quota(systemCalls, false);
                        table.release(proc.pid());
                        proc = null;
                    } else {
                        if (allowIdle && !table.isEmpty()) {
                            resolveIdle();
                        } else {
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

            //atempt to perform resume all - but only if you are the last to detect idle
            int detectorNumber = 0;
            if (fastDetectionMode) {
                if (firstToDetectIdle.compareAndSet(true, false)) {
                System.out.println("Resolve Idle " + (tick++));
                    table.resumeAll();
                    numberOfIdleReolversLeft.set(numCores);
                }
            } else {
                detectorNumber = numberOfIdleDetectors.incrementAndGet();
                if (detectorNumber == numCores) {
//                    System.out.println("Resolve Idle " + (tick++));
                    table.resumeAll();
                    resumeLock.release(numCores - 1);
                } else {
                    resumeLock.acquire();
                }

            }
            Proc proc = null;
            List<Proc> takenProcesses = new LinkedList<>();

            try {
                while ((proc = table.acquireNonBlocking()) != null) {
                    takenProcesses.add(proc);
                    proc.quota(systemCalls, true);
                }
            }  finally {
                for (Proc t : takenProcesses) {
                    table.release(t.pid());
                }

                if (fastDetectionMode) {
                    //attempt to leave the idle detection process - but only if the last detector has entered
                    if (numberOfIdleReolversLeft.decrementAndGet() == 0) {
                        firstToDetectIdle.set(true);
                    }
                } else {
                    if (detectorNumber == numCores) {
                        numberOfIdleDetectors.set(0);
                        detectionLeaveLock.release(numCores - 1);
                    } else {
                        detectionLeaveLock.acquire();
                    }
                }

            }
        }

        public void interrupt() {
            if (currentThread != null) {
                currentThread.interrupt();
            }
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
