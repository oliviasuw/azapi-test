/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bc.ds.TimeDelta;
import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.impl.DefaultAgentRunner;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class CompleteSearchExecution extends AbstractExecution {

    private TimeDelta timeDelta;
    private Statistic timeDeltaStatistic;

    /**
     * 
     * @param alg can be null and setted later..
     * @param prob 
     */
    public CompleteSearchExecution(ExecutorService exec) {
        super(exec);
    }

    @Override
    protected void configure() {
        timeDelta = new TimeDelta();
        timeDelta.setStart();
        timeDeltaStatistic = getStatisticsTree().getChild("Physical Running Time (Mili seconds)");
        final int numberOfVariables = getGlobalProblem().getNumberOfVariables();

        /**
         * THIS EXECUTION MOD USES 1 AGENT RUNNER FOR EACH AGENT
         */
        setAgentRunners(new AgentRunner[numberOfVariables]);

        try {
            generateAgents();
            for (int i = 0; i < getAgents().length; i++) {
                getRunners()[i] = new DefaultAgentRunner(getAgents()[i], this);
            }
        } catch (InstantiationException ex) {
            Logger.getLogger(CompleteSearchExecution.class.getName()).log(Level.SEVERE, "every agent must have empty constractor", ex);
            super.reportCrushAndStop(ex, "execution failed on initial state - every agent must have empty constractor");
            return;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CompleteSearchExecution.class.getName()).log(Level.SEVERE, "agent cannot be abstract/ cannot have a private constractor", ex);
            super.reportCrushAndStop(ex, "execution failed on initial state - agent cannot be abstract/ cannot have a private constractor");
            return;
        }
    }

    @Override
    protected void finish() {
        timeDelta.setEnd();
        timeDeltaStatistic.setValue(timeDelta.getDeltaMilis());
    }
}
