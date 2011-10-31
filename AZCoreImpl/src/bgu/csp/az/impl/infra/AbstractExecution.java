package bgu.csp.az.impl.infra;

import bgu.csp.az.api.AgentRunner;
import bgu.csp.az.api.Algorithm;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.ExecutionResult;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.api.tools.IdleDetector;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the object that represents the run time environment for a single algorithm execution, can be reused with the use of 
 * the reset function
 * There can be several of those for example: one especially designed for testing,
 * one for a distributed environment and one for using in UI’s etc.
 * this way the same algorithm can run without changes in every environment that we choose
 * – with this objects you control the running of the experiment / project / algorithm, 
 * get notifications about interesting thing that happened there, let you shut down an execution etc.
 * @author bennyl
 */
public abstract class AbstractExecution extends ProcessImpl implements Execution {

    private Statistic statTree; //will get constracted when the global problem is setted.
    private Problem problem;
    private Mailer mailer;
    private boolean shuttingdown; //this variable is used to check that the execution is doing the process of shuting down only once.
    private ExecutionResult result = new ExecutionResult();
    private ExecutionResult partialResult = new ExecutionResult();
    private Map<String, Object> parameterValues;
    private Algorithm alg;
    private IdleDetector idet;

    /**
     * 
     */
    public AbstractExecution() {
        parameterValues = new HashMap<String, Object>();
        shuttingdown = false;
        parameterValues.clear();
    }

    /**
     * will stop the current execution and set the result to no solution
     * TODO: maybe keep track of the execution status via enum (working, done, crushed, etc.)
     * @param ex
     * @param error
     */
    @Override
    public void reportCrushAndStop(Exception ex, String error) {
        if (!shuttingdown) {
            setResult(new ExecutionResult(ex));
            shuttingdown = true;
            stop();

            if (error != null) {
                System.out.println("PANIC! " + error);
            }
        }
    }

    @Override
    public void reportFinalAssignment(Assignment answer) {
        result = new ExecutionResult(answer);
    }

    public IdleDetector getIdleDetector() {
        return idet;
    }

    public void setIdleDetector(IdleDetector idet) {
        this.idet = idet;
    }

    /**
     * @return the statistics tree - a tree contains statistics about this execution
     */
    @Override
    public Statistic getStatisticsTree() {
        return statTree;
    }

    /**
     * @param p
     */
    protected void setGlobalProblem(Problem p) {
        this.problem = p;
        statTree = new Statistic(p.getMetadata());
    }

    /**
     * @return the global problem - 
     * each agent have its own "version" of problem that is based on the global problem 
     * using the global problem is reserved to the execution environment or to the execution tools - do not use it inside 
     * your algorithms - use Agents getProblem() instaed.
     */
    @Override
    public Problem getGlobalProblem() {
        return this.problem;
    }

    /**
     * @return the mailer attached to this execution.
     */
    @Override
    public Mailer getMailer() {
        return this.mailer;
    }

    /**
     * attach a mailer to this execution.
     * @param ml
     */
    public void setMailer(Mailer ml) {
        this.mailer = ml;
    }

    /**
     * cause the executed environment to log the given data
     * this implementation only print the data into the screen
     * @param agent
     * @param data
     */
    @Override
    public void log(int agent, String data) {
        System.out.println("Agent " + agent + ": " + data);
    }

    protected void setResult(ExecutionResult result) {
        this.result = result;
    }

    @Override
    public ExecutionResult getResult() {
        return result;
    }

    public Algorithm getAlgorithm() {
        return alg;
    }

    public void setAlgorithm(Algorithm alg) {
        this.alg = alg;
    }

    
    /**
     * ugly synchronization - replace with semaphore..
     * @param var
     * @param val 
     */
    @Override
    public synchronized void reportPartialAssignment(int var, int val) {
        if (partialResult.getAssignment() == null) {
            partialResult = new ExecutionResult(new Assignment());
        }
        partialResult.getAssignment().assign(var, val);
    }
    

    @Override
    public Object getParameterValue(String name) {
        return parameterValues.get(name);
    }

    @Override
    public ExecutionResult getPartialResult() {
        return partialResult;
    }

    @Override
    public void swapPartialAssignmentWithFullAssignment() {
        result = partialResult.deepCopy();
    }
    
}
