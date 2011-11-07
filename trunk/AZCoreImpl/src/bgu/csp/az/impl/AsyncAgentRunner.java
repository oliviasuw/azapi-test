package bgu.csp.az.impl;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.ContinuationMediator;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.tools.IdleDetector;
import bgu.csp.az.impl.infra.AbstractExecution;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class should receive an agent and just run it simply - no schedualing
 * what so ever..
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

    private boolean useIdleDetector; //see note about using idle detector within nested agents in AgentRunner.nest
    /**
     * used for the join method -> using a semaphore means that we are only
     * allowing 1 joining thread, this is the case currently but if we will want
     * more than one - a different solution should be applied.
     */
    private Semaphore block = new Semaphore(1);

    public AsyncAgentRunner(Agent a, AbstractExecution exec) {
        this.exec = exec;
        this.currentExecutedAgent = a;
        try {
            this.block.acquire();
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

        if (nestedAgents.isEmpty()) {
            cthread = Thread.currentThread();
            useIdleDetector = false;
            if (exec.getIdleDetector() != null) {
                exec.getIdleDetector().addListener(this);
                useIdleDetector = true;
            }
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
                    block.release();
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
                exec.getIdleDetector().dec();
                currentExecutedAgent.waitForNewMessages();
                exec.getIdleDetector().inc();
            }
        }
    }

    @Override
    public void onIdleDetected() {
        System.out.println("Idle Detected - Agent " + currentExecutedAgent.getId() + " Being Notified.");
        currentExecutedAgent.onIdleDetected();
        //cthread.interrupt();
    }

    @Override
    public void nest(int originalAgentId, SimpleAgent nestedAgent, ContinuationMediator cmed) {
        this.nestedAgents.addFirst(currentExecutedAgent);
        currentExecutedAgent = nestedAgent;
        this.nestedAgentsCalculationMediators.addFirst(cmed);
        System.out.println("Starting Inner Agent Of Type: " + nestedAgent.getClass().getSimpleName());
        currentExecutedAgent.start();
    }

    @Override
    public void join() throws InterruptedException {
        block.acquire();
    }

    protected int getRunningAgentId() {
        return this.currentExecutedAgent.getId();
    }
}
