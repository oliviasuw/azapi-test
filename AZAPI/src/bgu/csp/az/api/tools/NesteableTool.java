/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.tools;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Agent.PlatformOps;
import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.ContinuationMediator;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.infra.Execution;

/**
 *
 * @author bennyl
 */
public abstract class NesteableTool {
    
    public ContinuationMediator calculate(Agent callingAgent){
        ContinuationMediator ret = new ContinuationMediator();
        Execution exec = Agent.PlatformOperationsExtractor.extract(callingAgent).getExecution();
        AgentRunner runner = exec.getAgentRunnerFor(callingAgent);
        SimpleAgent nested = createNestedAgent();
        System.out.println("Calculating - transforming from " + callingAgent.getClass().getSimpleName() + " to " + nested.getClass().getSimpleName());
        final PlatformOps nestedOps = Agent.PlatformOperationsExtractor.extract(nested);
        nestedOps.setExecution(exec);
        nestedOps.setId(callingAgent.getId());
        runner.nest(callingAgent.getId(), nested, ret);
        return ret;
    }

    protected abstract SimpleAgent createNestedAgent();
}
