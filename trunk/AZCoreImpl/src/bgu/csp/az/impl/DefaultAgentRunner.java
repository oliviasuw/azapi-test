package bgu.csp.az.impl;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.tools.IdleDetector;
import bgu.csp.az.impl.infra.AbstractExecution;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this class should receive an agent and just run it simply - no schedualing what so ever..
 * @author bennyl
 *
 */
public class DefaultAgentRunner implements AgentRunner, IdleDetector.Listener {

    private Agent currentExecutedAgent;
    private Thread cthread;
    private AbstractExecution exec;
    /**
     * a flag designed to be a crush state that is saved between nested execution 
     * using this flag we can know that a nested agent was crushed and in response we shuld recrush..
     */
    private boolean crushed = false;
    private boolean useIdleDetector; //see note about using idle detector within nested agents in AgentRunner.nest
    /**
     * indicating the current nest level
     */
    private int nestLevel = 0;
    /**
     * used for the join method -> using a semaphore means that we are only allowing 1 joining thread, 
     * this is the case currently but if we will want more than one - a different solution should be applied.
     */
    private Semaphore block = new Semaphore(1);

    public DefaultAgentRunner(Agent a, AbstractExecution exec) {
        this.exec = exec;
        currentExecutedAgent = a;
        try {
            block.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(DefaultAgentRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        if (nestLevel == 0) {
            cthread = Thread.currentThread();
            useIdleDetector = false;
            if (exec.getIdleDetector() != null) {
                exec.getIdleDetector().addListener(this);
                useIdleDetector = true;
            }
        }

        nestLevel++;
        try {
            //START THE AGENT
            currentExecutedAgent.start();

            //PROCESS MESSAGE LOOP
            try {
                while (!currentExecutedAgent.isFinished() && !Thread.currentThread().isInterrupted() && !crushed) {
                    if (useIdleDetector) {
                        if (!currentExecutedAgent.hasPendingMessages()) {
                            exec.getIdleDetector().dec();
                            currentExecutedAgent.waitForNewMessages();
                            exec.getIdleDetector().inc();
                        }
                    }

                    currentExecutedAgent.processNextMessage();
                }
            } catch (InterruptedException ex) {
                cthread.interrupt(); //REFLAGING THE CURRENT THREAD.
            }

            //AFTER PROCESSING MESASGE THAT CAN BE THAT THE ALGORITHM INTERUPTED
            //BECAUSE OF AN PANIC SOME OTHER USER ACTIONS
            if (cthread.isInterrupted()) {
                System.out.println("Agent " + currentExecutedAgent.getId() + " Interupted - Terminating.");
            }

        } catch (Exception ex) {

            exec.reportCrushAndStop(ex, "Agent " + currentExecutedAgent.getId() + " Cause An Error!");
            //BECAUSE THE AGENT RUNNER WILL RUN INSIDE EXECUTION SERVICE 
            //THIS IS THE LAST POINT THAT EXCEPTION CAN BE PRINTED
            //AFTER THIS POINT THE EXCEPTION WILL BE LOST (BUT CAN BE RETRIVED VIA THE RESULT OF THIS ALGORITHM)) 
            ex.printStackTrace();

        } finally {
            System.out.println("Agent " + currentExecutedAgent.getId() + " Terminated.");
            nestLevel--;
            
            if (nestLevel == 0){
                block.release();
            }
        }
    }

    @Override
    public void onIdleDetected() {
        System.out.println("Idle Detected - Agent " + currentExecutedAgent.getId() + " Interupting.");
        cthread.interrupt();
    }

    @Override
    public void nest(int originalAgentId, SimpleAgent nestedAgent) {
        //NOTICE that in this implementation we are using the call stack as a code context saver 
        //if in the future java will have some sort of FAST thread context saver we can switch to it
        Agent backUp = currentExecutedAgent;
        currentExecutedAgent = nestedAgent;
        run();
        currentExecutedAgent = backUp;
    }

    @Override
    public void join() throws InterruptedException {
        block.acquire();
    }
}
