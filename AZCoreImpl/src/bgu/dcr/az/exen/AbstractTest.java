
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.exen.escan.Configuration;
import bgu.dcr.az.api.exen.mdef.CorrectnessTester;
import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exp.BadConfigurationException;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.api.exen.mdef.CorrectnessTester.CorrectnessTestResult;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.Test.TestResult;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import bgu.dcr.az.api.Problem;
import bgu.dcr.az.api.exen.escan.ConfigurationMetadata;
import bgu.dcr.az.api.exen.escan.ExternalConfigurationAware;
import bgu.dcr.az.api.exen.mdef.ProblemGenerator;
import bgu.dcr.az.api.exen.mdef.Limiter;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import bgu.dcr.az.exen.pgen.MapProblem;
import bgu.dcr.az.exenl.stat.AbstractStatisticCollector;
import bgu.dcr.az.utils.DeepCopyUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public abstract class AbstractTest extends AbstractProcess implements Test, ExternalConfigurationAware {

    public static final String PROBLEM_GENERATOR_PROBLEM_METADATA = "PROBLEM GENERATOR";
    public static final String SEED_PROBLEM_METADATA = "SEED";
    /**
     * V A R I A B L E S
     */
    @Variable(name = "name", description = "the test name", defaultValue = "")
    String name = "";
    @Variable(name = "seed", description = "seed for creating randoms for the problem generator", defaultValue = "-1")
    long seed = -1;
    //TODO: FOR NOW THIS PARAMETERS ARE GOOD BUT NEED TO TALK WITH ALON AND SEE WHAT TYPE OF EXPIREMENT MORE EXISTS
    @Variable(name = "repeat-count", description = "the number of executions in each tick", defaultValue = "100")
    int repeatCount = 100;
    @Variable(name = "run-var", description = "the variable to run", defaultValue = "")
    private String runVar = "";
    @Variable(name = "start", description = "starting value of the running variable", defaultValue = "0.1")
    float start = 0.1f;
    @Variable(name = "end", description = "ending value of the running variable (explicit)", defaultValue = "0.9")
    float end = 0.9f;
    @Variable(name = "tick-size", description = "the runinig variable value increasment", defaultValue = "0.1")
    float tickSize = 0.1f;
    /**
     * F I E L D S
     */
    private List<AlgorithmMetadata> algorithms = new LinkedList<AlgorithmMetadata>();
    private ProblemGenerator pgen = null;
    private List<StatisticCollector> collectors = new LinkedList<StatisticCollector>();
    private TestResult res = new TestResult().toSuccessState();
    private Random rand;
    private int currentExecutionNumber = 0;
    private CorrectnessTester ctester = null;
    private List<TestListener> listeners = new LinkedList<TestListener>();
    private double currentVarValue;
    private DebugInfo di = null;
    private List<Long> problemSeeds;
    boolean initialized = false;
    private Experiment experiment; // the executing experiment
    private int currentProblemNumber = 0;
    private AlgorithmMetadata currentAlgorithm;
    private Limiter limiter = null;

    public AbstractTest() {
    }

    @Override
    public double getCurrentVarValue() {
        return this.currentVarValue;
    }

    @Override
    public <T extends StatisticCollector> T getStatisticCollector(Class<T> type) {
        for (StatisticCollector s : this.collectors) {
            if (type == s.getClass()) {
                return (T) s;
            }
        }
        return null;
    }

    @Override
    public int getLength() {
        return getNumberOfExecutions() * getAlgorithms().size();
    }

    @Override
    public List<String> getIncludedAlgorithmsInstanceNames() {
        LinkedList<String> ret = new LinkedList<String>();
        for (AlgorithmMetadata a : algorithms) {
            ret.add(a.getInstanceName());
        }

        return ret;
    }

    @Override
    public int getNumberOfExecutions() {
        if (tickSize == 0) {
            tickSize = 0.1f;
        }
        int perAlgorithm = (int) (((end - start) / tickSize) + 1.0) * repeatCount; //(int) Math.floor((((end - start) / tickSize ) + 1.0) * (double)repeatCount);
        return perAlgorithm;
    }

    public void setExperiment(Experiment exp) {
        this.experiment = exp;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * return the current execution number that is being tested
     */
    @Override
    public int getCurrentExecutionNumber() {
        return currentExecutionNumber;
    }

    /**
     * set the test name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the test name
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    @Configuration(name = "Statistic Collector", description = "Adding statistic collector to collect statistics for this round")
    public void addStatisticCollector(StatisticCollector collector) {
        if (collector instanceof AbstractStatisticCollector) {
            ((AbstractStatisticCollector) collector).setTest(this);
        }
        collectors.add(collector);
    }

    @Override
    public List<StatisticCollector> getStatisticCollectors() {
        return collectors;
    }

    public List<AlgorithmMetadata> getAlgorithms() {
        return algorithms;
    }

    @Configuration(name = "Algorithm", description = "add algorithm to be tested")
    public void addAlgorithm(AlgorithmMetadata alg) {
        algorithms.add(alg);
    }

    @Configuration(name = "Limiter", description = "add limiter to kill executions that cause some limitation to reach")
    public void setLimiter(Limiter timer) {
        this.limiter = timer;
    }

    public Limiter getLimiter() {
        return limiter;
    }

    public Problem generateProblem(int number) {
        if (number > getLength() || number <= 0) {
            throw new InvalidValueException("there is no such problem");
        }

        int ticksPerformed = (number - 1) / repeatCount;
        float vvar = start + tickSize * (float) ticksPerformed;
        ProblemGenerator tpgen = DeepCopyUtil.deepCopy(pgen);

        ConfigurationMetadata.bubbleDownVariable(tpgen, runVar, vvar);
        Problem p = new MapProblem();

        tpgen.generate(p, new Random(problemSeeds.get(number - 1)));
        return p;
    }

    public void debug(DebugInfo di) {
        initialize();
        fireTestStarted();
        ExecutionResult exr;
        try {

            Problem p = generateProblem(di.getFailedProblemNumber());

            AlgorithmMetadata alg = null;
            for (AlgorithmMetadata a : algorithms) {
                if (a.getName().equals(di.getAlgorithmName())) {
                    alg = a;
                    break;
                }
            }

            if (alg != null) {

                exr = execute(p, alg);
                if (exr.getState() != ExecutionResult.State.SUCCESS) {
                    return; //TODO - is it ok?
                }
            }

            res.toSuccessState();
        } catch (Exception ex) {
            res.toCrushState(ex, null);
        } finally {
            DatabaseUnit.UNIT.signal(this);
        }
    }
//
//    private float inc(float original, float inc, int precesion) {
//        long p = (int) Math.pow(10, precesion);
//        long iorg = (int) (original * p);
//        long iinc = (int) (inc * p);
//        return ((float) (iorg + iinc)) / (float) p;
//    }

    public static double round3(double num) {
        double result = num * 1000;
        result = Math.round(result);
        result = result / 1000;
        return result;
    }

    @Override
    protected void _run() {
        try {
            initialize();
            validateConfiguration();

            fireTestStarted();

            if (tickSize == 0) {
                tickSize = 0.1f;
            }

            int pnum = 0;
            currentExecutionNumber = 0;

            for (currentVarValue = start; currentVarValue <= end; currentVarValue = round3(currentVarValue + tickSize)) {
                ConfigurationMetadata.bubbleDownVariable(this, runVar, (float) currentVarValue);
                for (int i = 0; i < repeatCount; i++) {


                    Problem p = nextProblem(++pnum);
                    boolean limited = false;
                    if (p == null) {
                        res.toCrushState(new NullPointerException("problem generator generated bad problem"), getProblemGenerator().getClass());
                    } else {
                        limited = false;
                        for (AlgorithmMetadata alg : getAlgorithms()) {
                            if (!limited && res.getState() == State.SUCCESS) {
                                final ExecutionResult exr = execute(p, alg);
                                switch (exr.getState()) {
                                    case CRUSHED:
                                        res.toCrushState(exr.getCrushReason(), alg.getAgentClass());
                                        break;
                                    case LIMITED:
                                        limited = true;
                                        break;
                                    case SUCCESS:
                                        break;
                                    case WRONG:
                                        res.toWrongResultState(exr.getCorrectAssignment(), exr.getResultingExecution(), alg.getAgentClass());
                                        break;
                                }
                            }

                            currentExecutionNumber++;
                        }
                    }

                    if (limited) {
                        System.out.println("Problem: " + getCurrentProblemNumber() + " was limited");
                    }

                    if (res.getState() != State.SUCCESS || Thread.currentThread().isInterrupted()) {
                        String algName;
                        if (Agent.class.isAssignableFrom(res.getFailedClass())) {
                            algName = ((Algorithm) res.getFailedClass().getAnnotation(Algorithm.class)).name();
                        } else {
                            algName = algorithms.get(0).getName();
                        }
                        di = new DebugInfo(this.getName(), algName, pnum);
                        return;
                    }
                }
            }
            res = new TestResult();

        } catch (Exception ex) {
            res.toCrushState(ex, null);
            ex.printStackTrace();
            //res = new TestResult(ex, null);
        } finally {
            DatabaseUnit.UNIT.signal(this);
        }

    }

    public DebugInfo getFailoreDebugInfo() {
        return di;
    }

    private ExecutionResult execute(Problem p, AlgorithmMetadata alg) {
        currentAlgorithm = alg;
        Execution e = provideExecution(p, alg);
        try {
            e.setStatisticCollectors(collectors);
            e.setLimiter(limiter);

            fireNewExecution(e);
            e.run();

            //test result
            ExecutionResult r = e.getResult();
            if (r.getState() == ExecutionResult.State.SUCCESS && getCorrectnessTester() != null) {
                CorrectnessTestResult testRes = getCorrectnessTester().test(e, r);
                if (!testRes.passed) {
                    r.toWrongState(testRes.rightAnswer);
                }
            }

            return r;

//            switch (r.getState()) {
//                case CRUSHED:
//                    res = new TestResult(r.getCrushReason(), e);
//                    return false;
//                case LIMITED:
//                    -- -- -- -- -- --
//                    
//                    return false;
//                default:
//                    if (getCorrectnessTester() != null) {
//                    } else {
//                        return true;
//                    }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ExecutionResult(e).toCrushState(ex);
        } finally {
            fireExecutionEnded(e);
        }
    }

    protected abstract Execution provideExecution(Problem p, AlgorithmMetadata alg);

    @Override
    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public TestResult getResult() {
        return res;
    }

    public DebugInfo getDebugInfo() {
        return di;
    }

    @Override
    public String getCurrentExecutedAlgorithmInstanceName() {
        return currentAlgorithm.getInstanceName();
    }

    private void initialize() {
        Thread.currentThread().setName("Test Runner Thread");
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        rand = new Random(seed);
        //creating problem seeds 
        problemSeeds = new ArrayList<Long>(getLength());
        for (int i = 0; i < getLength(); i++) {
            problemSeeds.add(rand.nextLong());
        }
    }

    @Override
    public ProblemGenerator getProblemGenerator() {
        return pgen;
    }

    @Override
    public int getCurrentProblemNumber() {
        return this.currentProblemNumber;
    }

    @Configuration(name = "Problem Generator", description = "Set problem generator")
    public void setProblemGenerator(ProblemGenerator pgen) {
        this.pgen = pgen;
    }

    /**
     * 1 is the first problem
     *
     * @param num
     * @return
     */
    protected Problem nextProblem(int num) {
        try {
            currentProblemNumber = num;
            long pseed = problemSeeds.get(num - 1);
            Random nrand = new Random(pseed);
            MapProblem p = new MapProblem();
            HashMap<String, Object> metadata = p.getMetadata();

            metadata.put(SEED_PROBLEM_METADATA, pseed);
            metadata.put(PROBLEM_GENERATOR_PROBLEM_METADATA, pgen.getClass().getName());

            System.out.println("Generating Problem: " + num);
            pgen.generate(p, nrand);
            System.out.println("Problem Generation Done.");
            return p;
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public CorrectnessTester getCorrectnessTester() {
        return this.ctester;
    }

    @Configuration(name = "Correctness Tester", description = "set correcteness tester to check solutions")
    @Override
    public void setCorrectnessTester(CorrectnessTester ctester) {
        this.ctester = ctester;
    }

    @Override
    public void addListener(TestListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListener(TestListener l) {
        listeners.remove(l);
    }

    private void fireTestStarted() {
        for (TestListener l : listeners) {
            l.onTestStarted(this);
        }
    }

    private void fireNewExecution(Execution e) {
        for (TestListener l : listeners) {
            l.onExecutionStarted(this, e);
        }
    }

    private void fireExecutionEnded(Execution e) {
        for (TestListener l : listeners) {
            l.onExecutionEnded(this, e);
        }
    }

    @Override
    public String getRunningVarName() {
        return runVar;
    }

    @Override
    public float getVarStart() {
        return this.start;
    }

    @Override
    public float getVarEnd() {
        return this.end;
    }

    @Override
    public float getTickSize() {
        return this.tickSize;
    }

    @Override
    public int getRepeatCount() {
        return this.repeatCount;
    }

    @Override
    public void afterExternalConfiguration() {
        initialize();
    }

    /**
     * validating the experiment setup
     *
     * @throws BadConfigurationException upon bad configuration
     */
    private void validateConfiguration() {
        validate(this.pgen != null, "no problem generator defined - please define one, see the tutorial for more details.");

        Set<String> aInstances = new HashSet<String>();
        for (AlgorithmMetadata a : this.getAlgorithms()) {
            validate(!aInstances.contains(a.getInstanceName()), "two or more algorithms with the same instance name '" + a.getInstanceName() + "' are defined (if no instance name defined then it is equals to the algorithm name) "
                    + " please define different algorithm 'instance-name'");
            aInstances.add(a.getInstanceName());
        }
    }

    private void validate(boolean predicate, String message) {
        if (!predicate) {
            throw new BadConfigurationException("[Test " + getName() + "] bad configuration discovered: " + message);
        }
    }
}
