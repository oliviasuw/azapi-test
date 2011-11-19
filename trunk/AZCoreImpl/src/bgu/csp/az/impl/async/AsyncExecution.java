/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.async;

import bc.ds.TimeDelta;
import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.impl.AlgorithmMetadata;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.api.infra.stat.Statistic;
import bgu.csp.az.impl.infra.AbstractExecution;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author bennyl
 */
public class AsyncExecution extends AbstractExecution {

    private TimeDelta timeDelta;
    private Statistic timeDeltaStatistic;

    public AsyncExecution(ExecutorService exec, Problem p, AlgorithmMetadata a, Round r) {
        super(exec, p, new AsyncMailer(), a, r);
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
