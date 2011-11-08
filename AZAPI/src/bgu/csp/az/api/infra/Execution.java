/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.lsearch.SystemClock;
import bgu.csp.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
public interface Execution extends Process {

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

    Object getParameterValue(String name);

    ExecutionResult getResult();
    
    ExecutionResult getPartialResult();
    
    AgentRunner getAgentRunnerFor(Agent a);

    SystemClock getSystemClock();
    
    int getNumberOfAgentRunners();
    
    /**
     * @return the statistics tree - a tree contains statistics about this execution
     */
    Statistic getStatisticsTree();

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
    
    void swapPartialAssignmentWithFullAssignment();
    
    /**
     * will stop the execution - interupting all the agent runners!
     */
    void stop();
    
}
