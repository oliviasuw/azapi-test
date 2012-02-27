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

 
    /**
     * flag the nested agent to be started - it will only start after the current
     * message handling ends (or if you are using it from within the start
     * function after it ends)
     *
     * you should use continuations (by calling
     * calculate(..).andWhenDoneDo(Continuation) to get notified when the nested
     * agent terminated.
     *
     * this method variant is the preferred variant if you use nested agents
     * more then once all the agents with the same group name will be executed
     * in the same environment for example if you want to run nested agent once
     * you can call this method with mail group "one" all the agents will do the
     * same and they will all be switched to "one" environment when they done
     * and you want to run other nested agent call this method with the group
     * name "two" then if there are any other agents that still not exit "one"
     * you will run isolated from them.
     *
     * @param groupName
     * @param callingAgent
     * @return
     */
    public ContinuationMediator calculate(final Agent callingAgent, String groupName) {
        return startCalculation(callingAgent, groupName);
    }

    /**
     * flag the nested agent to be started it will only start after the current
     * message handling ends (or if you are using it from within the start
     * function after it ends)
     *
     * you should use continuations (by calling
     * calculate(..).andWhenDoneDo(Continuation) to get notified when the nested
     * agent terminated.
     *
     * this method variant is the preferred variant if you use nested agents
     * more then once all the agents with the same group name will be executed
     * in the same environment for example if you want to run nested agent once
     * you can call this method with mail group "one" all the agents will do the
     * same and they will all be switched to "one" environment when they done
     * and you want to run other nested agent call this method with the group
     * name "two" then if there are any other agents that still not exit "one"
     * you will run isolated from them. 
     * 
     * ********************************************
     * * same as calling calculate(callingAgent,  *
     * * callingAgent.getClass.getName())         *
     * ********************************************
     * @param groupName
     * @param callingAgent
     * @return
     */
    public ContinuationMediator calculate(final Agent callingAgent) {
        return startCalculation(callingAgent, null);
    }

    /**
     * flag the nested agent to be started it will only start after the current
     * message handling ends (or if you are using it from within the start
     * function after it ends)
     *
     * you should use continuations (by calling
     * calculate(..).andWhenDoneDo(Continuation) to get notified when the nested
     * agent terminated.
     *
     *
     * @param callingAgent
     * @return
     */
    private ContinuationMediator startCalculation(final Agent callingAgent, String mailGroup) {
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
        if (mailGroup != null) {
            nestedOps.setMailGroupKey(mailGroup);
        }
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
