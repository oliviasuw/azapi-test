/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.lsearch;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Agt0DSL;
import bgu.csp.az.api.lsearch.SystemClock;
import bgu.csp.az.impl.DefaultAgentRunner;
import bgu.csp.az.impl.infra.AbstractExecution;

/**
 *
 * @author bennyl
 */
public class LocalSearchAgentRunner extends DefaultAgentRunner {

    SystemClock clock;
    
    public LocalSearchAgentRunner(Agent a, AbstractExecution exec, SystemClock clock) {
        super(a, exec);
        this.clock = clock;
    }

    @Override
    protected void onCurrentExecutedAgentOutOfMessages() {
        try {
            System.out.println("LocalSearchAgentRunner.onCurrentExecutedAgentOutOfMessages: ticking " + super.getRunningAgentId());
            clock.tick();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Agt0DSL.throwUncheked(ex);
        }
    }
    
}
