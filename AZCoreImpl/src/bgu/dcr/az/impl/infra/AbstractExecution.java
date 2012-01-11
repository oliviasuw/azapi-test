package bgu.dcr.az.impl.infra;

import bgu.dcr.az.impl.async.AsyncExecution;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agent.PlatformOps;
import bgu.dcr.az.api.AgentRunner;
import bgu.dcr.az.api.Hooks.ReportHook;
import bgu.dcr.az.api.Hooks.TerminationHook;
import bgu.dcr.az.impl.AlgorithmMetadata;
import bgu.dcr.az.api.Mailer;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.ExecutionResult;
import bgu.dcr.az.api.SystemClock;
import bgu.dcr.az.api.infra.Experiment;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.api.infra.stat.StatisticCollector;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.api.tools.IdleDetector;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public abstract class AbstractExecution extends AbstractProcess implements Execution {

    private Experiment experiment; //the executing experiment
    private Problem problem;//the *global* problem
    private Mailer mailer; //the mailer object used by this execution
    private boolean shuttingdown; //this variable is used to check that the execution is doing the process of shuting down only once.
    private ExecutionResult result = new ExecutionResult(); //the final execution result
    //private ExecutionResult partialResult = new ExecutionResult(); // result object that is collected during the execution time
    private AlgorithmMetadata algorithmMetadata; //the executed algorithm metadata
    private IdleDetector idleDetector; //if this execution need an idle detector then this field will hold it
    private ExecutorService executorService; // this is the thread pool that this execution use
    private AgentRunner[] agentRunners; //the agent runners of this execution
    private Agent[] agents; //the constracted agents
    private LinkedList<LogListener> logListeners = new LinkedList<LogListener>(); //list of listeners that receive events about log usage
    private SystemClock clock; //if this execution uses a system clock - this field will hold it
    private List<StatisticCollector> statisticCollectors = new LinkedList<StatisticCollector>(); //list of activated statistic collectors
    private final Test test; //the test that this execution is running in
    private Map<String, ReportHook> reportHooks = new HashMap<String, ReportHook>();//list of report hook listeners
    private List<TerminationHook> terminationHooks = new LinkedList<TerminationHook>();
    private boolean idleDetectorNeeded = false;//hard set for the execution to use idle detection

    /**
     * 
     */
    public AbstractExecution(Problem p, Mailer m, AlgorithmMetadata a, Test test, Experiment exp) {
        this.shuttingdown = false;
        this.executorService = exp.getThreadPool();
        this.mailer = m;
        this.problem = p;
        this.algorithmMetadata = a;
        this.test = test;
        this.experiment = exp;
    }

    @Override
    public void hookIn(TerminationHook hook) {
        terminationHooks.add(hook);
    }

    @Override
    public void hookIn(String name, ReportHook hook) {
        reportHooks.put(name, hook);
    }

    @Override
    public void report(String to, Agent a, Object[] args) {
        if (reportHooks.containsKey(to)) {
            reportHooks.get(to).hook(a, args);
        }
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

            System.out.println("PANIC! " + ex.getMessage() + ", [USER TEXT]: " + error);
        }
    }

    /**
     * force the execution to use idle detector (even if not stated so in the algorithm metadata)
     * @param needed 
     */
    public void setIdleDetectionNeeded(boolean needed) {
        this.idleDetectorNeeded = needed;
    }

    @Override
    public Test getTest() {
        return test;
    }

    @Override
    public SystemClock getSystemClock() {
        return this.clock;
    }

    @Override
    public int getNumberOfAgentRunners() {
        return agentRunners.length;
    }

    public void setSystemClock(SystemClock clock) {
        this.clock = clock;
    }

    public void addLogListener(LogListener ll) {
        this.logListeners.add(ll);
    }

    public void removeLogListener(LogListener ll) {
        this.logListeners.remove(ll);
    }

    @Override
    public AgentRunner getAgentRunnerFor(Agent a) {
        return agentRunners[a.getId()];
    }

    protected boolean generateAgents() {
        try {
            agents = new Agent[getGlobalProblem().getNumberOfVariables()];
            for (int i = 0; i < agents.length; i++) {
                getAgents()[i] = getAlgorithm().generateAgent();
                PlatformOps apops = Agent.PlatformOperationsExtractor.extract(getAgents()[i]);
                apops.setExecution(this);
                apops.setId(i);

            }
            return true;
        } catch (InstantiationException ex) {
            Logger.getLogger(AsyncExecution.class.getName()).log(Level.SEVERE, "every agent must have empty constractor", ex);
            reportCrushAndStop(ex, "execution failed on initial state - every agent must have empty constractor");
            return false;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AsyncExecution.class.getName()).log(Level.SEVERE, "agent cannot be abstract/ cannot have a private constractor", ex);
            reportCrushAndStop(ex, "execution failed on initial state - agent cannot be abstract/ cannot have a private constractor");
            return false;
        }
    }

    @Override
    public void stop() {
        experiment.stop();

    }

    protected ExecutorService getExecutorService() {
        return executorService;
    }

    protected void setAgentRunners(AgentRunner[] runners) {
        this.agentRunners = runners;
    }

    protected Agent[] getAgents() {
        return agents;
    }

    protected AgentRunner[] getRunners() {
        return agentRunners;
    }

    @Override
    public void reportFinalAssignment(Assignment answer) {
        result = new ExecutionResult(answer);
    }

    public IdleDetector getIdleDetector() {
        return idleDetector;
    }

    @Override
    public void setStatisticCollectors(List<StatisticCollector> statisticCollectors) {
        this.statisticCollectors = statisticCollectors;
    }

    /**
     * @return the global problem - 
     * each agent have its own "version" of problem that is based on the global problem 
     * using the global problem is reserved to the execution environment or to the execution tools - do not use it inside 
     * your algorithms - use Agents getProblem() instead.
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
     * cause the executed environment to log the given data
     * this implementation only print the data into the screen
     * @param agent
     * @param data
     */
    @Override
    public void log(int agent, String mailGroupKey, String data) {
        for (LogListener ll : logListeners) {
            ll.onLog(agent, mailGroupKey, data);
        }
    }

    protected void setResult(ExecutionResult result) {
        this.result = result;
    }

    @Override
    public ExecutionResult getResult() {
        return result;
    }

    public AlgorithmMetadata getAlgorithm() {
        return algorithmMetadata;
    }

    /**
     * ugly synchronization - replace with semaphore..
     * @param var
     * @param val 
     */
    @Override
    public synchronized void reportPartialAssignment(int var, int val) {
/*        if (partialResult.getAssignment() == null) {
            partialResult = new ExecutionResult(new Assignment());
        }
        partialResult.getAssignment().assign(var, val);*/
        if (result.getAssignment() == null){
            result.setFinalAssignment(new Assignment());
        }
        
        result.getAssignment().assign(var, val);
    }

//    @Override
//    public ExecutionResult getPartialResult() {
//        return partialResult;
//    }

//    @Override
//    public void swapPartialAssignmentWithFullAssignment() {
//        result = partialResult.deepCopy();
//    }

    @Override
    protected void _run() {
        try {
            doStaticConfigurations();
            configure();
            for (StatisticCollector sc : statisticCollectors) {
                sc.hookIn(agents, this);
            }
            startExecution();
            finish();
        } finally {
            try {
                for (TerminationHook hook : terminationHooks) {
                    hook.hook();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Execution Ended.");
        }
    }

    private void doStaticConfigurations() {
        mailer.setExecution(this);

        if (isIdleDetectionIsNeeded()) {
            this.idleDetector = new IdleDetector(getGlobalProblem().getNumberOfVariables(), getMailer(), getAlgorithm().getAgentClass().getName());
        }

    }

    /**
     * this function return true if idle detection is needed
     * overrite this function to change the idle detection activation setup
     */
    protected boolean isIdleDetectionIsNeeded() {
        return getAlgorithm().isUseIdleDetector() || this.idleDetectorNeeded;
    }

    protected void startExecution() {
        System.out.println("Starting new execution");
        while (true) {
            for (int i = 0; i < agentRunners.length; i++) {
//                System.out.println("Executing Agent: " + getAgents()[i].getId());
                getExecutorService().execute(getRunners()[i]);
            }
            break;
        }

        for (AgentRunner runner : getRunners()) {
            try {
                runner.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(AsyncExecution.class.getName()).log(Level.SEVERE, null, ex);
                reportCrushAndStop(ex, "interupted while waiting for all agents to finish");
            }
        }
    }

    protected abstract void configure();

    protected abstract void finish();
}
