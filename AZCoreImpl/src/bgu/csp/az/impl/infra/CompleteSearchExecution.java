/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bc.ds.TimeDelta;
import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.AlgorithmMetadata;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.impl.AsyncAgentRunner;
import bgu.csp.az.impl.AsyncMailer;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author bennyl
 */
public class CompleteSearchExecution extends AbstractExecution {

    private TimeDelta timeDelta;
    private Statistic timeDeltaStatistic;

    public CompleteSearchExecution(ExecutorService exec, Problem p, AlgorithmMetadata a) {
        super(exec, p, new AsyncMailer(), a);
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

        if (!generateAgents()) {
            return;
        }
        
        for (int i = 0; i < getAgents().length; i++) {
            getRunners()[i] = new AsyncAgentRunner(getAgents()[i], this);
        }
    }

    @Override
    protected void finish() {
        timeDelta.setEnd();
        timeDeltaStatistic.setValue(timeDelta.getDeltaMilis());
    }
}
