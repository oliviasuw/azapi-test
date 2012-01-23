/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agent.PlatformOps;
import bgu.dcr.az.api.AgentRunner;
import bgu.dcr.az.api.Continuation;
import bgu.dcr.az.api.ContinuationMediator;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.infra.Execution;

/**
 *
 * @author bennyl
 */
public abstract class NestableTool {

    private int finalAssignment = Integer.MIN_VALUE;
    private boolean hasAssignment = false;
    
    public ContinuationMediator calculate(final Agent callingAgent) {
        final Execution exec = Agent.PlatformOperationsExtractor.extract(callingAgent).getExecution();
        ContinuationMediator ret = new ContinuationMediator() {

            @Override
            public void andWhenDoneDo(final Continuation c) {
                super.andWhenDoneDo(new Continuation() {

                    @Override
                    public void doContinue() {
                        final Assignment assignment = exec.getResult().getAssignment();
                        if (assignment != null) {
                            finalAssignment = assignment.getAssignment(callingAgent.getId());
                            hasAssignment = true;
                        }
                        c.doContinue();
                    }
                });
            }
        };
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

    public int getFinalAssignment() {
        return this.finalAssignment;
    }

    public boolean isHasAssignment() {
        return hasAssignment;
    }
    
    
}
