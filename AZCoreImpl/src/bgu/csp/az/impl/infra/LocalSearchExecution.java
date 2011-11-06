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
import bgu.csp.az.api.lsearch.SystemClock;
import bgu.csp.az.impl.lsearch.LocalSearchAgentRunner;
import bgu.csp.az.impl.lsearch.LocalSearchMailer;
import bgu.csp.az.impl.lsearch.DefaultSystemClock;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author bennyl
 */
public class LocalSearchExecution extends AbstractExecution {

    private TimeDelta timeDelta;
    private Statistic timeDeltaStatistic;
    private SystemClock clock;

    public LocalSearchExecution(ExecutorService exec, Problem p, AlgorithmMetadata a) {
        super(exec, p, new LocalSearchMailer(), a);
    }

    public SystemClock getClock() {
        return clock;
    }

    @Override
    protected void configure() {
        clock = new DefaultSystemClock(this);
        ((LocalSearchMailer) getMailer()).setClock(clock);
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
            getRunners()[i] = new LocalSearchAgentRunner(getAgents()[i], this, clock);
        }
    }

    @Override
    protected void finish() {
        timeDelta.setEnd();
        timeDeltaStatistic.setValue(timeDelta.getDeltaMilis());
    }
}
