/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs;

import bgu.dcr.az.execs.api.Proc;
import bgu.dcr.az.execs.api.ProcState;
import bgu.dcr.az.execs.api.ProcTable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final AtomicInteger estimatedWaitingCores = new AtomicInteger(0);

    //fast resume optimization
    private ConcurrentLinkedQueue<WeakReference<ProcessInfo>> resumeQueue = new ConcurrentLinkedQueue<>(),
            nextResumeQueue = new ConcurrentLinkedQueue<>();

    @Override
    public Proc acquire() throws InterruptedException {
        attemptProcessResume();

        while (true) {
            estimatedWaitingCores.incrementAndGet();

            if (isInIdleState() || isEmpty()) {
                pendingProcesses.add(currentIdleSignal); //to release the next agent
                return null;
            }

//            System.out.println("Acquire with: " + blockingProcesses.get() + " Blocked / " + totalNumberOfProcesses.get());
            ProcessInfo next = pendingProcesses.take();
            estimatedWaitingCores.decrementAndGet();
            if (isIdleSignal(next)) {
                continue;
            }

            boolean mine = next.acquired.compareAndSet(false, true);
            if (mine && next.process.state() != ProcState.TERMINATED) {
                next.signaled.set(false);
                return next.process;
            } else {
                System.out.println("Found process " + next.process.pid() + " But it is not mine");
            }
        }
    }

    private boolean isIdleSignal(ProcessInfo next) {
        if (next == currentIdleSignal) {
            return true;
        }
        return false;
    }

    /**
     * @return true if any process was seccessfully resumed
     */
    private boolean attemptProcessResume() {
        while (!resumeQueue.isEmpty()) {
            WeakReference<ProcessInfo> result = resumeQueue.poll();
            if (result != null) {
                ProcessInfo p = result.get();
                if (p != null && p.process.state() != ProcState.TERMINATED) {
                    nextResumeQueue.add(result);
                    wake(p);
                    return true;
                }
            } else {
                return false;
            }
        }

        return false;
    }

    private ProcessInfo retreiveProcessInfo(int pid) {
        return processInfos.get(pid);
    }

    @Override
    public Proc acquireNonBlocking() {
        attemptProcessResume();

        while (true) {

            ProcessInfo next = pendingProcesses.poll();

            if (isIdleSignal(next)) {
                continue;
            }

            if (next != null) {
                boolean mine = next.acquired.compareAndSet(false, true);

                if (mine && next.process.state() != ProcState.TERMINATED) {
                    next.signaled.set(false);
                    return next.process;
                } else {
//                    System.out.println("Found one!");
                }
            } else {
                return null;
            }
        }
    }

    private void wake(ProcessInfo procInfo) {
        procInfo.signaled.set(true);
        if (procInfo.inQueue.compareAndSet(false, true)) {
            blockingProcesses.decrementAndGet();
            pendingProcesses.add(procInfo);
        }
    }

    @Override
    public void release(int pid) {
        ProcessInfo proc = retreiveProcessInfo(pid);
        ProcState state = proc.process.state();

        proc.acquired.set(false);

        switch (state) {
            case INITIALIZING:
            case RUNNING:
                pendingProcesses.add(proc);
                break;
            case BLOCKING:

                proc.inQueue.set(false);

                if (proc.signaled.compareAndSet(true, false) && proc.inQueue.compareAndSet(false, true)) {
                    pendingProcesses.add(proc);
                } else {
                    blockingProcesses.incrementAndGet();
//                    System.out.println("Agent " + proc.process.pid() + " goes to sleep");
                    if (isInIdleState()) {
                        System.out.println("Idle detected after " + proc.process.pid() + " goes to sleep");
                    }
                }

                break;
            case TERMINATED:
                totalNumberOfProcesses.decrementAndGet();
                processInfos.remove(pid);
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
    public void resumeAll() {
        if (!isInIdleState()) {
            throw new UnsupportedOperationException("you cannot call resume all if there is no idle state");
        }

        ConcurrentLinkedQueue<WeakReference<ProcessInfo>> temp = resumeQueue;
        resumeQueue = nextResumeQueue;
        nextResumeQueue = temp;

        for (int i = 0; i < estimatedWaitingCores.get(); i++) {
            attemptProcessResume();
        }

    }

    @Override
    public void add(Proc p) {
        ProcessInfo pinfo = new ProcessInfo(p);

        //infoLock.writeLock().lock();
        //try {
        processInfos.put(p.pid(), pinfo);
        //} finally {
        //    infoLock.writeLock().unlock();
        //}

        pinfo.inQueue.set(true);
        pinfo.acquired.set(false);
        totalNumberOfProcesses.incrementAndGet();

        nextResumeQueue.add(new WeakReference<>(pinfo));
        pendingProcesses.add(pinfo);
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
        return totalNumberOfProcesses.get() == 0;
    }

    @Override
    public int nextProcessId() {
        return nextProceId.getAndIncrement();
    }

    @Override
    public Collection<Integer> allProcessIds() {
        return processInfos.keySet();
    }

    private static class ProcessInfo {

        Proc process;
        AtomicBoolean acquired = new AtomicBoolean(false);
        AtomicBoolean inQueue = new AtomicBoolean(false);
        AtomicBoolean signaled = new AtomicBoolean(false);

        public ProcessInfo(Proc process) {
            this.process = process;
        }

    }

}
