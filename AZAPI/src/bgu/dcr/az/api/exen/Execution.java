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
import bgu.dcr.az.api.exen.mdef.Limiter;
import bgu.dcr.az.api.exen.vis.VisualizationFrameSynchronizer;
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
     * this method will set the execution to be visual
     * @param vsync 
     */
    void setVisualizationFrameSynchronizer(VisualizationFrameSynchronizer vsync);
    
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
     * will stop this execution
     * and add the error and the exception to result
     * @param ex
     * @param error
     */
    void terminateDueToCrush(Exception ex, String error);
    
    /**
     * will stop this execution and mark the result accordingly
     */
    void terminateDueToLimiter();

    void submitPartialAssignment(int var, int val);

    void reportFinalAssignment(Assignment answer);
    
    /**
     * will stop the execution - interupting all the agent runners!
     */
    void stop();
    
    /**
     * @return the agents that executed
     */
    Agent[] getAgents();

    public void setLimiter(Limiter timer);
    
    public Limiter getLimiter();
    
    /**
     * @return true if this execution is a visual execution which means that one or more visualizations
     * is attached to it, if this a visual execution then a frame synchronizer will be also given to the 
     * execution so it will be able to inform the visualization engine that a frame should be recorded
     */
    public boolean isVisual();
    
    public VisualizationFrameSynchronizer getVisualizationFrameSynchronizer();
}
