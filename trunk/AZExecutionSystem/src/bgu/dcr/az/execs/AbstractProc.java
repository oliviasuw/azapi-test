/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs;

import bgu.dcr.az.execs.api.Proc;
import bgu.dcr.az.execs.api.ProcState;
import bgu.dcr.az.execs.api.SystemCalls;
import bgu.dcr.az.execs.api.UnexpectedIdleDetected;

/**
 *
 * @author bennyl
 */
public abstract class AbstractProc implements Proc {

    private ProcState state = ProcState.INITIALIZING;
    private SystemCalls systemCalls = null;
    private final int pid;
    private final boolean deamon;
    private boolean idleSignal = false;

    public AbstractProc(int pid) {
        this.pid = pid;
        this.deamon = false;
    }

    public AbstractProc(int pid, boolean deamon) {
        this.pid = pid;
        this.deamon = deamon;
    }

    @Override
    public boolean isDeamon() {
        return deamon;
    }

    @Override
    public ProcState state() {
        return state;
    }

    @Override
    public final void quota(SystemCalls systemCalls) {
        switch (state) {
            case BLOCKING:
                state = ProcState.RUNNING;
            case RUNNING:
                if (idleSignal) {
                    idleSignal = false;
//                    System.out.println("Proc " + pid() + " handling idle");
                    onIdleDetected();
                } else {
//                    System.out.println("Proc " + pid() + " handling quota");
                    quota();
                }
                break;
            case INITIALIZING:
                this.systemCalls = systemCalls;
                start();
                if (state == ProcState.INITIALIZING) {
                    state = ProcState.RUNNING;
                }
                break;
            case TERMINATED:
                throw new UnsupportedOperationException("terminated process cannot run");
            default:
                throw new AssertionError(state.name());
        }
    }

    protected abstract void start();

    protected abstract void quota();

    /**
     * @param pid
     * @return true if process wake up successfully (not terminated or
     * something)
     */
    protected boolean wakeup(int pid) {
        if (systemCalls == null) {
            return false;
        }
        return systemCalls.wake(pid);
    }

    protected void exec(Proc p) {
        systemCalls.exec(p);
    }

    protected int nextProcessId() {
        return systemCalls.nextProcessId();
    }

    protected void sleep() {
        if (state != ProcState.TERMINATED) {
            state = ProcState.BLOCKING;
        }
    }

    protected void onIdleDetected() {
        throw new UnexpectedIdleDetected();
    }

    protected void terminate() {
        state = ProcState.TERMINATED;
    }

    @Override
    public int pid() {
        return pid;
    }

    @Override
    public String toString() {
        return "Process " + pid();
    }

    @Override
    public void signalIdle() {
        idleSignal = true;
    }

}
