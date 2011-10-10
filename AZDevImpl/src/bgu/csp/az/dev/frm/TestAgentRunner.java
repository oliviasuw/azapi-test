package bgu.csp.az.dev.frm;

import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.agt.SimpleMessage;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.Hooks.BeforeMessageProcessingHook;
import bgu.csp.az.api.tools.IdleDetector;
import bgu.csp.az.dev.slog.ScenarioLogger;
import java.util.logging.Logger;

/**
 * this class should receive an agent and just run it simply - no schedualing what so ever..
 * @author bennyl
 *
 */
public class TestAgentRunner implements AgentRunner, IdleDetector.Listener {

    private SimpleAgent a;
    private TestExecution exec;
    private ScenarioLogger logger;
    private Thread cthread;

    public TestAgentRunner(SimpleAgent a, TestExecution exec) {
        this.a = a;
        this.exec = exec;
        logger = exec.getLogger();
    }

    @Override
    public void run() {
        cthread = Thread.currentThread();

        boolean useidet = false;
        if (exec.getIdleDetector() != null) {
            exec.getIdleDetector().addListener(this);
            useidet = true;
        }


        try {

            if (TestExpirement.USE_SCENARIO_LOGGER) {
                a.hookIn(createScenarioChangedHook());
            }

            //START THE AGENT
            a.start();

            //PROCESS MESSAGE LOOP
            try {
                while (!a.isFinished() && !Thread.currentThread().isInterrupted()) {
                    if (useidet) {
                        if (!a.hasPendingMessages()) {
                            exec.getIdleDetector().dec();
                            a.peekNextMessage();
                            exec.getIdleDetector().inc();
                        }
                    }

                    a.processNextMessage();
                }
            } catch (InterruptedException ex) {
                cthread.interrupt(); //REFLAGING THE CURRENT THREAD.
            }

            //AFTER PROCESSING MESASGE THAT CAN BE THAT THE ALGORITHM INTERUPTED
            //BECAUSE OF AN PANIC SOME OTHER USER ACTIONS
            if (cthread.isInterrupted()) {
                System.out.println("Agent " + a.getId() + " Interupted - Terminating.");
            }

        } catch (Exception ex) {

            exec.reportCrushAndStop(ex, "Agent " + a.getId() + " Cause An Error!");
            //BECAUSE THE AGENT RUNNER WILL RUN INSIDE EXECUTION SERVICE 
            //THIS IS THE LAST POINT THAT EXCEPTION CAN BE PRINTED
            //AFTER THIS POINT THE EXCEPTION WILL BE LOST (BUT CAN BE RETRIVED VIA THE RESULT OF THIS ALGORITHM)) 
            ex.printStackTrace();

        } finally {
            System.out.println("Agent " + a.getId() + " Terminated.");
        }
    }

    private BeforeMessageProcessingHook createScenarioChangedHook() {
        return new BeforeMessageProcessingHook() {

            @Override
            public void hook(SimpleMessage msg) {
                logger.logScenarioPart(a.getId(), (Long) msg.getMetadata().get(TestMailer.MESSAGE_ID_METADATA));
            }
        };
    }

    @Override
    public void onIdleDetected() {
        System.out.println("Idle Detected - Agent " + a.getId() + " Interupting.");
        cthread.interrupt();
    }
}
