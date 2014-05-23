/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs;

import bgu.dcr.az.common.events.EventListeners;
import bgu.dcr.az.execs.api.Proc;
import bgu.dcr.az.execs.api.ProcState;
import bgu.dcr.az.execs.api.ProcTable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class ThreadSafeProcTable implements ProcTable {

    private final Map<Integer, ProcessInfo> processInfos = new ConcurrentHashMap<>();
    private final LinkedBlockingQueue<ProcessInfo> pendingProcesses = new LinkedBlockingQueue<>();
    
    private final AtomicInteger blockingProcesses = new AtomicInteger(0);
    private final AtomicInteger totalNumberOfProcesses = new AtomicInteger(0);
    private final AtomicInteger nextProceId = new AtomicInteger(0);

//signaling cores in blocked in pending queue that there is an idle
    private final ProcessInfo currentIdleSignal = new ProcessInfo(null);
    //private final AtomicInteger estimatedWaitingCores = new AtomicInteger(0);
    private final AtomicInteger numberOfDeamons = new AtomicInteger(0);

    EventListeners<ProcTableListener> listeners = EventListeners.create(ProcTableListener.class);

    @Override
    public EventListeners<ProcTableListener> listeners() {
        return listeners;
    }

    @Override
    public Proc acquire() throws InterruptedException {
        while (true) {
            //estimatedWaitingCores.incrementAndGet();

            if (isInIdleState() || isEmpty()) {
                pendingProcesses.add(currentIdleSignal); //to release the next agent
                return null;
            }

//            System.out.println("Acquire with: " + blockingProcesses.get() + " Blocked / " + totalNumberOfProcesses.get());
            ProcessInfo next = pendingProcesses.take();
            //estimatedWaitingCores.decrementAndGet();
            if (isIdleSignal(next)) {
                continue;
            }

            boolean mine = next.acquired.compareAndSet(false, true);
            if (mine && next.process.state() != ProcState.TERMINATED) {
//                System.out.println("Taken: " + next.process.pid());
                next.signaled.set(false);
                return next.process;
            } else {
                System.out.println("Found process " + next.process.pid() + " But it is not mine");
            }
        }
    }

    private boolean isIdleSignal(ProcessInfo next) {
        return next == currentIdleSignal;
    }

    private ProcessInfo retreiveProcessInfo(int pid) {
        return processInfos.get(pid);
    }

    @Override
    public void release(int pid) {
        ProcessInfo proc = retreiveProcessInfo(pid);
        release(proc);
    }

    private void wake(ProcessInfo procInfo) {
        if (procInfo.signaled.compareAndSet(false, true)) {
            try {
                procInfo.signalHandlerLock.acquire();

                try {
                    if (procInfo.inQueue.compareAndSet(false, true)) {
                        final int blocking = blockingProcesses.decrementAndGet();
                        if (blocking < 0) {
                            throw new RuntimeException("BAD IMPLEMENTATION OF PROC-TABLE.");
                        }
                        pendingProcesses.add(procInfo);
                    } else {
                        procInfo.signaled.set(true);
                    }
                } finally {
                    procInfo.signalHandlerLock.release();
                }

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

    }

    private void release(ProcessInfo proc) {
        ProcState state = proc.process.state();

        proc.acquired.set(false);

        switch (state) {
            case INITIALIZING:
            case RUNNING:
                pendingProcesses.add(proc);
                break;
            case BLOCKING:

                try {
                    proc.signalHandlerLock.acquire();

                    try {

                        if (proc.signaled.compareAndSet(true, false)) { //double check to eliminate a race condition..
                            pendingProcesses.add(proc);
                        } else {
                            blockingProcesses.incrementAndGet();
                            proc.inQueue.set(false);
                        }

                    } finally {
                        proc.signalHandlerLock.release();
                    }

                } catch (InterruptedException ex) {
                    Logger.getLogger(ThreadSafeProcTable.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.currentThread().interrupt();
                }

                break;
            case TERMINATED:
//                System.out.println("process " + pid + " terminated");
                listeners.fire().onProcessRemoved(this, proc.process.pid());
                totalNumberOfProcesses.decrementAndGet();
                if (proc.process.isDeamon()) {
                    numberOfDeamons.decrementAndGet();
                }
                processInfos.remove(proc.process.pid());
                break;
            default:
                throw new AssertionError(state.name());

        }

    }

    @Override
    public boolean wake(int pid) {
        ProcessInfo procInfo = retreiveProcessInfo(pid);
        if (procInfo != null && procInfo.process.state() != ProcState.TERMINATED) {
            wake(procInfo);
            return true;
        }

        return false;
    }

    @Override
    public void add(Proc p) {
        ProcessInfo pinfo = new ProcessInfo(p);
        if (p.isDeamon()) {
            numberOfDeamons.incrementAndGet();
        }

        processInfos.put(p.pid(), pinfo);

        pinfo.inQueue.set(true);
        pinfo.acquired.set(false);
        totalNumberOfProcesses.incrementAndGet();

        pendingProcesses.add(pinfo);
        listeners.fire().onProcessAdded(this, p.pid());
    }

    @Override
    public boolean isInIdleState() {

        //since additions of processes are assumed to be only performed by a process once the execution started
        //it is safe to say that if in any point in time the number of blocking processes reach the number of processes
        //(by this order) then we reached to an idle.
        return totalNumberOfProcesses.get() == blockingProcesses.get();
    }

    @Override
    public boolean isEmpty() {
        return totalNumberOfProcesses.get() == numberOfDeamons.get() && numberOfDeamons.get() == blockingProcesses.get();
    }

    @Override
    public int nextProcessId() {
        return nextProceId.getAndIncrement();
    }

    @Override
    public void signalIdle() {
//        int numWake = totalNumberOfProcesses.get();

        for (ProcessInfo p : processInfos.values()) {
            wake(p);
            p.process.signalIdle();
        }

//        IntStream.range(0, numWake).forEach(i -> {
//        try {
//
//            for (int i = 0; i < numWake; i++) {
//                Proc p = acquire();
//                resolver.resolve(p);
//                release(p.pid());
//            }
//
//        } catch (InterruptedException ex) {
//            Logger.getLogger(ThreadSafeProcTable.class.getName()).log(Level.SEVERE, null, ex);
//            Thread.currentThread().interrupt();
//        }
//        });
    }

    @Override
    public Collection<Integer> allProcessIds() {
        LinkedList<Integer> all = new LinkedList<>();
        for (ProcessInfo a : processInfos.values()) {
            all.add(a.process.pid());
        }

        return all;
    }

    private static class ProcessInfo {

        Proc process;
        AtomicBoolean acquired = new AtomicBoolean(false);
        AtomicBoolean inQueue = new AtomicBoolean(false);
        AtomicBoolean signaled = new AtomicBoolean(false);
        Semaphore signalHandlerLock = new Semaphore(1);

        public ProcessInfo(Proc process) {
            this.process = process;
        }

    }

}
