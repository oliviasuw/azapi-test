/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.AgentRunner;
import bgu.dcr.az.api.Hooks.ReportHook;
import bgu.dcr.az.api.Hooks.TerminationHook;
import bgu.dcr.az.api.Mailer;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.api.SystemClock;
import bgu.dcr.az.api.infra.stat.StatisticCollector;
import bgu.dcr.az.api.tools.Assignment;
import java.util.List;

/**
 *
 * @author bennyl
 */
public interface Execution extends Process {

    Test getTest();
    
    void report(String to, Agent a, Object[] args);
    
    void hookIn(String name, ReportHook hook);
    
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
    
}
