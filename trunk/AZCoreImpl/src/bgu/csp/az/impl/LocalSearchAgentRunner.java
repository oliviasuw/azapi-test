/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Agt0DSL;
import bgu.csp.az.api.lsearch.SystemClock;
import bgu.csp.az.impl.infra.AbstractExecution;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            clock.tick();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Agt0DSL.throwUncheked(ex);
        }
    }
    
}
