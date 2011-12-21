/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.async;

import bgu.dcr.az.api.AgentRunner;
import bgu.dcr.az.api.Mailer;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.impl.AlgorithmMetadata;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.impl.infra.AbstractExecution;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author bennyl
 */
public class AsyncExecution extends AbstractExecution {

    public AsyncExecution(ExecutorService exec, Problem p, AlgorithmMetadata a, Test r) {
        super(exec, p, new AsyncMailer(), a, r);
    }

    public AsyncExecution(ExecutorService exec, Problem p, AlgorithmMetadata a, Test r, Mailer mailer) {
        super(exec, p, mailer, a, r);
    }

    @Override
    protected void configure() {
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
    }

}
