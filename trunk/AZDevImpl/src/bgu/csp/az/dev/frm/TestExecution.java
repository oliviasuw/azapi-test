/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.frm;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Agent.PlatformOps;
import bgu.csp.az.impl.infra.AbstractExecution;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.dev.slog.ScenarioLogger;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class TestExecution extends AbstractExecution {

    ExecutorService exec;
    private long mstart;
    private ScenarioLogger logger;

    /**
     * 
     * @param alg can be null and setted later..
     * @param prob 
     */
    public TestExecution() {
    }

    public ScenarioLogger getLogger() {
        return logger;
    }

    @Override
    public void setGlobalProblem(Problem p) {
        super.setGlobalProblem(p);
    }

    @Override
    public void _run() {

        try {
            logger = new ScenarioLogger(this, getGlobalProblem().getNumberOfVariables());
            mstart = new Date().getTime();
            Agent[] agents = new Agent[getGlobalProblem().getNumberOfVariables()];
            Mailer tmailer = getMailer();
            PlatformOps apops;

            try {
                for (int i = 0; i < agents.length; i++) {
                    agents[i] = getAlgorithm().getAgentClass().newInstance();
                    apops = Agent.PlatformOperationsExtractor.extract(agents[i]);
                    apops.setExecution(this);
                    apops.setId(i);
                    tmailer.register(agents[i]);
                }
            } catch (InstantiationException ex) {
                Logger.getLogger(TestExecution.class.getName()).log(Level.SEVERE, "every agent must have empty constractor", ex);
                super.reportCrushAndStop(ex, "execution failed on initial state - every agent must have empty constractor");
                return;
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TestExecution.class.getName()).log(Level.SEVERE, "agent cannot be abstract/ cannot have a private constractor", ex);
                super.reportCrushAndStop(ex, "execution failed on initial state - agent cannot be abstract/ cannot have a private constractor");
                return;
            }

            System.out.println("Number Of Agents Is: " + getGlobalProblem().getNumberOfVariables());
            while (true) {
                exec = Executors.newFixedThreadPool(getGlobalProblem().getNumberOfVariables());
                try {
                    for (Agent a : agents) {
                        System.out.println("Executing Agent: " + a.getId());
                        exec.execute(new TestAgentRunner((SimpleAgent) a, this));
                    }
                    break;
                } catch (RejectedExecutionException exec) {
                    //THIS EXCEPTION WAS BEEN THROWED PROBABLY BECAUSE WE STARTING AND KILL EXECUTORS TOO FAST
                    //NEXT VERSION OF AZ SHOULD CONTAIN ITS OWN THREAD POOL IMPLEMENTATION THAT ACTUALY WORK...
                    killExec();
                }
            }

            exec.shutdown();
            while (!exec.isTerminated()) {
                try {
                    exec.awaitTermination(1, TimeUnit.DAYS);
                    getStatisticsTree().getChild("Total Running Time (ms)").setValue(new Date().getTime() - mstart);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TestExecution.class.getName()).log(Level.SEVERE, null, ex);
                    reportCrushAndStop(ex, "interupted while waiting for all agents to finish");
                }
            }
        } finally {
            killExec();

            System.out.println("Execution Ended!\n\n");
        }
    }

    private void killExec() {
        //KILLING THE EXECUTION QUEUE - SOMTIMES THE POOL IS STIL ACTIVE EVEN THOUGH ITS TERMINATED FLAG IS ON.. 
        exec.shutdownNow();
        try {
            exec.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestExecution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void log(int agent, String data) {
        super.log(agent, data);
        logger.logAgentLog(agent, data);
    }

    @Override
    public void stop() {
        exec.shutdownNow();
    }
}
