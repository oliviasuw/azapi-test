/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks.ReportHook;
import bgu.dcr.az.api.Hooks.TerminationHook;
import bgu.dcr.az.api.Problem;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import bgu.dcr.az.api.exen.mdef.Timer;
import bgu.dcr.az.api.tools.Assignment;
import java.util.List;

/**
 *
 * @author bennyl
 */
public interface Execution extends Process {

    /**
     * @return the test that this execution is running in
     */
    Test getTest();
    
    void report(String to, Agent a, Object[] args);
    
    void hookIn(ReportHook hook);
    
    void hookIn(TerminationHook hook);
    
    /**
     * @return the global problem -
     * each agent have its own "version" of problem that is based on the global problem
     * using the global problem is reserved to the execution environment or to the execution tools - do not use it inside
     * your algorithms - use Agents getProblem() instaed.
     */
    Problem getGlobalProblem();

    /**
     * @return the mailer attached to this execution.
     */
    Mailer getMailer();

    ExecutionResult getResult();
    
//    ExecutionResult getPartialResult();
    
    AgentRunner getAgentRunnerFor(Agent a);

    /**
     * @return the system clock, 
     * if this execution is an asynchronus execution then it will return null.
     */
    SystemClock getSystemClock();
    
    int getNumberOfAgentRunners();
    
    void setStatisticCollectors(List<StatisticCollector> collectors);

    /**
     * cause the executed environment to log the given data
     * @param agent
     * @param data
     */
    void log(int agent, String mailGroupKey, String data);

    /**
     * will stop the current execution represented by this runtime object
     * and raise execution done event, this particular method variant will add the error and the
     * exception to the event
     * @param ex
     * @param error
     */
    void reportCrushAndStop(Exception ex, String error);

    void reportPartialAssignment(int var, int val);

    void reportFinalAssignment(Assignment answer);
    
//    void swapPartialAssignmentWithFullAssignment();
    
    /**
     * will stop the execution - interupting all the agent runners!
     */
    void stop();
    
    /**
     * @return the agents that executed
     */
    Agent[] getAgents();

    public void setTimer(Timer timer);
    
    /**
     * should be called by the agent runners *after* each message handling
     * in the case of a timeout this method will not only return true but also instruct the mailer to free all blocking 
     * agents so that they will be able to check for timeout too, the method set the result of the execution to reflect that there was a timeout,
     * if this method returnes true then the agent runner should return (no need to do anything special - just stop running)
     */
    public boolean haveTimeLeft();
}
