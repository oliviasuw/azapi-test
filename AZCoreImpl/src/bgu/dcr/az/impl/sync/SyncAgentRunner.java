/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.sync;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.AgentRunner;
import bgu.dcr.az.api.ContinuationMediator;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.SystemClock;
import bgu.dcr.az.impl.infra.AbstractExecution;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author bennyl
 */
public class SyncAgentRunner implements AgentRunner {

    private State[] states;
    private SystemClock clock;
    private AbstractExecution exec;
    private Semaphore joinLock = new Semaphore(0);

    private SyncAgentRunner(State[] states, SystemClock clock, AbstractExecution exec) {
        this.states = states;
        this.clock = clock;
        this.exec = exec;
    }

    public static SyncAgentRunner[] createAgentRunners(int amount, SystemClock clock, AbstractExecution exec, Agent[] agents) {
        SyncAgentRunner[] list = new SyncAgentRunner[amount];

        State[] states = new State[agents.length];
        for (int i = 0; i < states.length; i++) {
            states[i] = new State(agents[i]);
        }

        for (int i = 0; i < amount; i++) {
            list[i] = new SyncAgentRunner(states, clock, exec);
        }

        return list;
    }

    @Override
    public void nest(int originalAgentId, SimpleAgent nestedAgent, ContinuationMediator cmed) {
        State s = states[originalAgentId];
        s.nested.addFirst(s.current);
        s.current = nestedAgent;
        s.nestedContinuations.addFirst(cmed);
        System.out.println("Starting Inner Agent Of Type: " + nestedAgent.getClass().getSimpleName());
        s.current.start();
    }

    @Override
    public void join() throws InterruptedException {
        joinLock.acquire();
    }

    @Override
    public void run() {
        ContinuationMediator cmed;
        boolean allFinished = false;
        try {
            while (!Thread.currentThread().isInterrupted() && !allFinished) {
                long currentTime = clock.time();
                allFinished = true;
                for (State s : states) {
                    if (clock.isTicked()) {
                        allFinished = false;
                        break;
                    }

                    if (!s.current.isFinished()) {
                        allFinished = false;
                    }

                    long got = s.time.getAndSet(currentTime);
                    if (got == -1) {
                        s.current.start();
                    } else if (got < currentTime) {
                        if (!s.current.isFinished()) {
                            try {
                                while (s.current.hasPendingMessages()) {
                                    s.current.processNextMessage();
                                }
                                s.current.onMailBoxEmpty();
                                if (s.current.isFinished() && !s.nested.isEmpty()) {
                                    cmed = s.nestedContinuations.removeFirst();
                                    s.current = s.nested.removeFirst();
                                    cmed.executeContinuation();
//                                    System.out.println("Returning Back to " + s.current.getClass().getSimpleName());
                                }
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt(); //REFLAGING THE CURRENT THREAD.
                                System.out.println("Agent Runner Interupted While Handling " + "[" + Agent.PlatformOperationsExtractor.extract(s.current).getMailGroupKey() + "]: " + s.current.getId());
                            }
                        }
                    }
                }

                if (allFinished) {
                    clock.close();
                    return; //DONE..
                }
                if (!Thread.currentThread().isInterrupted()) {
                    try {
//                        System.out.println("Agent Runner '" + Thread.currentThread().getName() + "' Ticking");
                        clock.tick();
//                        System.out.println("Agent Runner '" + Thread.currentThread().getName() + "' Done Ticking");
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt(); //REFLAGING THE CURRENT THREAD.
                        System.out.println("Agent Runner Interrupted While Ticking!");
                    }
                }

            }
        } catch (Exception ex) {
            exec.reportCrushAndStop(ex, "Agent Cause An Error!");
            //BECAUSE THE AGENT RUNNER WILL RUN INSIDE EXECUTION SERVICE 
            //THIS IS THE LAST POINT THAT EXCEPTION CAN BE PRINTED
            //AFTER THIS POINT THE EXCEPTION WILL BE LOST (BUT CAN BE RETRIVED VIA THE RESULT OF THIS ALGORITHM)) 
            ex.printStackTrace();
        } finally {
            clock.close();
            System.out.println("Agent Runner '" + Thread.currentThread().getName() + "' Terminated.");
            joinLock.release();
        }
    }

    private static class State {

        public Agent current;
        public final LinkedList<Agent> nested;
        public final AtomicLong time;
        public final LinkedList<ContinuationMediator> nestedContinuations;

        public State(Agent current) {
            this.current = current;
            this.nested = new LinkedList<Agent>();
            this.time = new AtomicLong(-1);
            this.nestedContinuations = new LinkedList<ContinuationMediator>();
        }
    }
}
