package bgu.dcr.az.impl.async;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agent.PlatformOps;
import bgu.dcr.az.api.AgentRunner;
import bgu.dcr.az.api.ContinuationMediator;
import bgu.dcr.az.api.Hooks.BeforeMessageProcessingHook;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.tmr.Timer;
import bgu.dcr.az.api.tools.IdleDetector;
import bgu.dcr.az.impl.infra.AbstractExecution;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class should receive an agent and it will execute it 
 * this agent runner specialized in asynchronous execution
 * it supports idle detection and message delays.
 *
 * @author bennyl
 *
 */
public class AsyncAgentRunner implements AgentRunner, IdleDetector.Listener {

    private Agent currentExecutedAgent;
    private LinkedList<Agent> nestedAgents;
    private LinkedList<ContinuationMediator> nestedAgentsCalculationMediators;
    private Thread cthread;
    private AbstractExecution exec;
    private Semaphore idleDetectionLock = new Semaphore(1);
    private boolean useIdleDetector; //see note about using idle detector within nested agents in AgentRunner.nest
    
    /**
     * used for the join method -> using a semaphore means that we are only
     * allowing 1 joining thread, this is the case currently but if we will want
     * more than one - a different solution should be applied.
     */
    private Semaphore joinBlock = new Semaphore(1);

    public AsyncAgentRunner(Agent a, AbstractExecution exec) {
        this.exec = exec;
        this.currentExecutedAgent = a;
        try {
            this.joinBlock.acquire();
        } catch (InterruptedException ex) {
            //SHOULD NEVER HAPPEN
            Logger.getLogger(AsyncAgentRunner.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.nestedAgents = new LinkedList<Agent>();
        this.nestedAgentsCalculationMediators = new LinkedList<ContinuationMediator>();
    }

    @Override
    public void run() {
        ContinuationMediator cmed;
        Thread.currentThread().setName("Agent Runner For Agents With ID " + currentExecutedAgent.getId());

        if (nestedAgents.isEmpty()) {
            cthread = Thread.currentThread();
            useIdleDetector = false;
            if (exec.getIdleDetector() != null) {
                exec.getIdleDetector().addListener(this);
                useIdleDetector = true;
            }

            registerIdleDetectionCallback(currentExecutedAgent);
        }
        //START THE AGENT
        currentExecutedAgent.start();
        while (true) {
            try {
                //PROCESS MESSAGE LOOP
                try {
                    while (!currentExecutedAgent.isFinished() && !Thread.currentThread().isInterrupted()) {
                        performIdleDetection();
                        currentExecutedAgent.processNextMessage();
                        if (!exec.haveTimeLeft()) {
                            System.out.println("[" + Agent.PlatformOperationsExtractor.extract(currentExecutedAgent).getMailGroupKey() + "] " + currentExecutedAgent.getId() + " Interupted due to timeout - Terminating.");
                            return;
                        };
                    }
                } catch (InterruptedException ex) {
                    cthread.interrupt(); //REFLAGING THE CURRENT THREAD.
                    System.out.println("[" + Agent.PlatformOperationsExtractor.extract(currentExecutedAgent).getMailGroupKey() + "] " + currentExecutedAgent.getId() + " Interupted - Terminating.");
                }

            } catch (Exception ex) {

                exec.reportCrushAndStop(ex, "Agent " + currentExecutedAgent.getId() + " Cause An Error!");
                //BECAUSE THE AGENT RUNNER WILL RUN INSIDE EXECUTION SERVICE 
                //THIS IS THE LAST POINT THAT EXCEPTION CAN BE PRINTED
                //AFTER THIS POINT THE EXCEPTION WILL BE LOST (BUT CAN BE RETRIVED VIA THE RESULT OF THIS ALGORITHM)) 
                ex.printStackTrace();

            } finally {
                System.out.println("[" + Agent.PlatformOperationsExtractor.extract(currentExecutedAgent).getMailGroupKey() + "] " + currentExecutedAgent.getId() + " Terminated.");

                if (nestedAgents.isEmpty()) {
                   
                    
                    if (useIdleDetector) {
                        exec.getIdleDetector().notifyAgentIdle(); //Im finished so i am idle...
                    }
                    
                    joinBlock.release();
                    return;
                } else {
                    cmed = this.nestedAgentsCalculationMediators.removeFirst();
                    this.currentExecutedAgent = this.nestedAgents.removeFirst();
                    cmed.executeContinuation();
                    System.out.println("Returning Back to " + currentExecutedAgent.getClass().getSimpleName());
                }
            }
        }
    }

    private void performIdleDetection() throws InterruptedException {
        if (useIdleDetector && nestedAgents.isEmpty()) {
            if (!currentExecutedAgent.hasPendingMessages()) {
                exec.getIdleDetector().notifyAgentIdle();
                currentExecutedAgent.waitForNewMessages();
                exec.getIdleDetector().notifyAgentWorking();
            }
        }
    }

    @Override
    public void idleResolved() {
        idleDetectionLock.release();
        if (currentExecutedAgent.isFirstAgent()) {
            exec.getMailer().releaseAllBlockingAgents(Agent.PlatformOperationsExtractor.extract(currentExecutedAgent).getMailGroupKey());
        }
    }

    @Override
    public boolean tryResolveIdle() {
        if (exec.getMailer() instanceof AsyncDelayedMailer) {
            PlatformOps pop = Agent.PlatformOperationsExtractor.extract(currentExecutedAgent);
            if (((AsyncDelayedMailer) exec.getMailer()).resolveIdle(pop.getMailGroupKey())) {
//                System.out.println("Idle resolved");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onIdleDetection() {
        try {
            idleDetectionLock.acquire(); //will catch the idle detection lock - inorder to block
        } catch (InterruptedException ex) {
            Logger.getLogger(AsyncAgentRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void idleCannotBeResolved() {
        if (currentExecutedAgent.isFinished()) return; //IDLE RESULVED AS AGENT LEAVE THE ALGORITHM...
        System.out.println("Idle Detected - Agent " + currentExecutedAgent.getId() + " Being Notified.");
        currentExecutedAgent.onIdleDetected();
        idleDetectionLock.release();
        exec.getMailer().releaseAllBlockingAgents(Agent.PlatformOperationsExtractor.extract(currentExecutedAgent).getMailGroupKey());
    }

    @Override
    public void nest(int originalAgentId, SimpleAgent nestedAgent, ContinuationMediator cmed) {
        this.nestedAgents.addFirst(currentExecutedAgent);
        currentExecutedAgent = nestedAgent;
        registerIdleDetectionCallback(currentExecutedAgent);
        this.nestedAgentsCalculationMediators.addFirst(cmed);
        System.out.println("Starting Inner Agent Of Type: " + nestedAgent.getClass().getSimpleName());
        currentExecutedAgent.start();
    }

    @Override
    public void join() throws InterruptedException {
        joinBlock.acquire();
    }

    /**
     * @return the current executed agent id
     */
    protected int getRunningAgentId() {
        return this.currentExecutedAgent.getId();
    }

    private void registerIdleDetectionCallback(Agent a) {
        if (useIdleDetector) {
            a.hookIn(new BeforeMessageProcessingHook() {

                @Override
                public void hook(Agent a, Message msg) {
                    try {
                        idleDetectionLock.acquire();
                    } catch (InterruptedException ex) {
                        System.out.println("Interrupted while waiting for idle detection to be complete");
                        Thread.currentThread().interrupt();
                    } finally {
                        idleDetectionLock.release();
                    }
                }
            });
        }
    }

}
