/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agent.PlatformOps;
import bgu.dcr.az.api.AgentRunner;
import bgu.dcr.az.api.ContinuationMediator;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.infra.Execution;

/**
 *
 * @author bennyl
 */
public abstract class NestableTool {
    
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
