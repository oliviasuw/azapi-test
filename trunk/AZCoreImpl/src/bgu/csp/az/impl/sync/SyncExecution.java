/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.sync;

import bc.ds.TimeDelta;
import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.AlgorithmMetadata;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.lsearch.SystemClock;
import bgu.csp.az.impl.infra.AbstractExecution;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author bennyl
 */
public class SyncExecution extends AbstractExecution {

    private TimeDelta timeDelta;
    private Statistic timeDeltaStatistic;

    @SuppressWarnings("LeakingThisInConstructor")
    public SyncExecution(ExecutorService exec, Problem p, AlgorithmMetadata a) {
        super(exec, p, new SyncMailer(), a);
    }

    @Override
    protected void configure() {
        DefaultSystemClock clock = new DefaultSystemClock();
        setSystemClock(clock);
        ((SyncMailer) getMailer()).setClock(clock);
        timeDelta = new TimeDelta();
        timeDelta.setStart();
        timeDeltaStatistic = getStatisticsTree().getChild("Physical Running Time (Mili seconds)");
        final int numberOfVariables = getGlobalProblem().getNumberOfVariables();
        final int numberOfCores = Runtime.getRuntime().availableProcessors();
        final int numberOfAgentRunners = Math.min(numberOfCores, numberOfVariables); 
        
        /**
         * THIS EXECUTION MOD USES AGENT RUNNER IN POOL MODE
         */

        if (!generateAgents()) {
            return;
        }
        
        setAgentRunners(SyncAgentRunner.createAgentRunners(numberOfAgentRunners, getSystemClock(), this, getAgents()));
        clock.setExcution(this); //MUST BE CALLED AFTER THE AGENT RUNNERS HAVE BEEN ASSIGNED...
    }

    @Override
    protected void finish() {
        timeDelta.setEnd();
        timeDeltaStatistic.setValue(timeDelta.getDeltaMilis());
    }
}
