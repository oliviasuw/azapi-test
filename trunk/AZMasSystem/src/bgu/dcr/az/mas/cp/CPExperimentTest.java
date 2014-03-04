/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.anop.reg.RegisteryUtils;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.exen.mdef.ProblemGenerator;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.mas.AgentSpawner;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionEnvironment;
import bgu.dcr.az.mas.ExecutionService;
import bgu.dcr.az.mas.exp.AlgorithmDef;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
import bgu.dcr.az.mas.exp.ExperimentStatusSnapshot;
import bgu.dcr.az.mas.exp.Looper;
import bgu.dcr.az.mas.exp.loopers.SingleExecutionLooper;
import bgu.dcr.az.mas.impl.ExperimentStatusSnapshotImpl;
import bgu.dcr.az.mas.impl.stat.StatisticsManagerImpl;
import bgu.dcr.az.mas.stat.StatisticCollector;
import bgu.dcr.az.mas.stat.StatisticsManager;
import bgu.dcr.az.utils.RandomSequance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author User
 */
@Register("test")
public class CPExperimentTest implements Experiment {

    private static int creationNumber = 0;

    private ProblemGenerator pgen;
    private final List<AlgorithmDef> algorithms = new ArrayList<>();
    private Looper looper = new SingleExecutionLooper();
    private ExecutionEnvironment executionEnvironment = ExecutionEnvironment.async;
    private CPCorrectnessTester correctnessTester;
    private String name = "" + (creationNumber++);
    private long seed = System.currentTimeMillis();
    private final ExperimentStatusSnapshotImpl status = new ExperimentStatusSnapshotImpl();
    private final HashSet<StatisticCollector> statistics = new HashSet<>();
    private final Map<Class, ExecutionService> suppliedServices = new HashMap<>();
    private ExecutionResult result;

    @Override
    public ExecutionResult lastResult() {
        return result;
    }

    /**
     * @propertyName seed
     * @return
     */
    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    /**
     * @propertyName name
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @propertyName algorithms
     * @return
     */
    public List<AlgorithmDef> getAlgorithms() {
        return algorithms;
    }

    /**
     * @propertyName env
     * @return
     */
    public ExecutionEnvironment getExecutionEnvironment() {
        return executionEnvironment;
    }

    public void setExecutionEnvironment(ExecutionEnvironment executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }

    /**
     * @propertyName loop
     * @return
     */
    public Looper getLooper() {
        return looper;
    }

    public void setLooper(Looper looper) {
        this.looper = looper;
    }

    /**
     * @propertyName problem-generator
     * @return
     */
    public ProblemGenerator getProblemGenerator() {
        return pgen;
    }

    public void setProblemGenerator(ProblemGenerator pgen) {
        this.pgen = pgen;
    }

    /**
     * @propertyName correctness-tester
     * @return
     */
    public CPCorrectnessTester getCorrectnessTester() {
        return correctnessTester;
    }

    public void setCorrectnessTester(CPCorrectnessTester correctnessTester) {
        this.correctnessTester = correctnessTester;
    }

    @Override
    public ExecutionResult execute() {
        return execute(-1);
    }

    /**
     *
     * @param executionNumber -1 to run all
     * @return
     */
    private ExecutionResult execute(int executionNumber) {

        int numCores = Runtime.getRuntime().availableProcessors();
        final ExecutorService pool = Executors.newFixedThreadPool(numCores);
        Scheduler scheduler = new MultithreadedScheduler(pool);
        RandomSequance randomSeq = new RandomSequance(seed);

        RuntimeCoreAdapter[] coreAdapters = new RuntimeCoreAdapter[algorithms.size()];

        for (int i = 0; i < coreAdapters.length; i++) {
            coreAdapters[i] = new NaiveRuntimeCoreAdapter();
        }

        if (getProblemGenerator() == null) {
            return new ExecutionResult().toCrushState(new ExperimentExecutionException("No problem generator is defined"));
        }

        if (getAlgorithms().isEmpty()) {
            return new ExecutionResult().toCrushState(new ExperimentExecutionException("At least one algorithm should be defined in order to execute an experiment"));
        }

        int i = 0;
        try {           
            status.start();

            ConfigurationOfElements conf = new ConfigurationOfElements();
            final int count = looper.count() * algorithms.size();
            for (i = executionNumber == -1 ? 0 : executionNumber; i < count; i++) {
                System.out.println("Start Running Problem on " + coreAdapters[i % algorithms.size()].getAdaptedNumberOfCores() + " cores");
                CPExecution exec = createExecutionWithSeed(i, conf, randomSeq.getIthLong(i));
                result = exec.execute(scheduler, coreAdapters[i % algorithms.size()].getAdaptedNumberOfCores());

                if (result.getState() != ExecutionResult.State.SUCCESS) {
                    return result.setLastRunExecution(i);
                }

                coreAdapters[i % algorithms.size()].update(i / algorithms.size(), scheduler);
                if (executionNumber != -1) {
                    return result;
                }

                status.finishedExecutions++;
            }
        } catch (Exception ex) {
            return result = new ExecutionResult().toCrushState(new ExperimentExecutionException("cannot execute experiment - configuration problem, see cause", ex)).setLastRunExecution(i);
        } finally {
            pool.shutdownNow();
            status.end();
        }

        return new ExecutionResult()
                .toSucceefulState(null);
    }

    @Override
    public String toString() {
        return "DCRExperiment{" + "pgen=" + pgen + ", algorithms=" + algorithms + ", looper=" + looper + '}';
    }

    @Override
    public int numberOfExecutions() {
        return looper.count() * algorithms.size();
    }

    public Problem getProblem(int i) throws ConfigurationException {
        try {
            ConfigurationOfElements conf = new ConfigurationOfElements();
            for (int j = 0; j < conf.configurableElements.length; j++) {
                conf.configurableElements[j] = conf.configurationsOfElements[j].create();
            }

            RandomSequance seq = new RandomSequance(seed);

            conf.apply();
            Problem p = new Problem();
            pgen.generate(p, new Random(seq.getIthLong(i)));
            return p;
        } catch (ClassNotFoundException ex) {
            throw new ConfigurationException("cannot create configuration, see cause", ex);
        }
    }

    /* package */ Execution getExecution(int i) throws ConfigurationException {
        try {
            ConfigurationOfElements conf = new ConfigurationOfElements();
            for (int j = 0; j < conf.configurableElements.length; j++) {
                conf.configurableElements[j] = conf.configurationsOfElements[j].create();
            }

            RandomSequance seq = new RandomSequance(seed);
            return createExecutionWithSeed(i, conf, seq.getIthLong(i));
        } catch (ClassNotFoundException ex) {
            throw new ConfigurationException("cannot create configuration, see cause", ex);
        }
    }

    private CPExecution createExecutionWithSeed(int i, ConfigurationOfElements conf, long seed) throws ConfigurationException {
        looper.configure(i, conf.configurationsOfElements);

        conf.apply();
        Problem p = new Problem();
        pgen.generate(p, new Random(seed));

        AlgorithmDef adef = algorithms.get(i % algorithms.size());
        AgentSpawner spawner = new SimpleAgentSpawner(RegisteryUtils.getRegistery().getRegisteredClassByName("ALGORITHM." + adef.getName()));
        CPExecution exec = new CPExecution(this, adef, looper.getRunningVariableValue(i / algorithms.size()), spawner, p, executionEnvironment);

        final StatisticsManagerImpl statm = StatisticsManagerImpl.getInstance();
        statm.clearRegistrations();
        statistics.forEach(statm::register);

        exec.supply(StatisticsManager.class, statm);
        for (Map.Entry<Class, ExecutionService> e : suppliedServices.entrySet()) {
            exec.supply(e.getKey(), e.getValue());
        }

        if (correctnessTester != null) {
            exec.supply(CPCorrectnessTester.class, correctnessTester);
        }

        return exec;

    }

    public String getExecutedAlgorithmName(int i) {
        return algorithms.get(i % algorithms.size()).getName();
    }

    @Override
    public Collection<? extends Experiment> subExperiments() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public ExperimentStatusSnapshot status() {
        return status;
    }

    @Override
    public void supply(Class<? extends ExecutionService> serviceType, ExecutionService service) {
        suppliedServices.put(serviceType, service);
    }

    @Override
    public Iterator<Experiment> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    private static interface RuntimeCoreAdapter {

        public static final int ADAPTIVE_AVERAGE_AMOUNT = 4;
        public static final double DISCOUNT_FACTOR = 0.75;

        void update(int round, Scheduler scheduler);

        int getAdaptedNumberOfCores();
    }

    private class ConfigurationOfElements {

        Object[] configurableElements;
        Configuration[] configurationsOfElements;

        public ConfigurationOfElements() throws ClassNotFoundException {
            configurableElements = new Object[]{pgen};

            configurationsOfElements = new Configuration[configurableElements.length];
            for (int i = 0; i < configurableElements.length; i++) {
                configurationsOfElements[i] = ConfigurationUtils.createConfigurationFor(configurableElements[i]);
            }
        }

        public void apply() throws ConfigurationException {
            for (int j = 0; j < configurableElements.length; j++) {
                configurationsOfElements[j].configure(configurableElements[j]);
            }

        }
    }

    private static class NaiveRuntimeCoreAdapter implements RuntimeCoreAdapter {

        private int adaptedNumberOfCores;
        private double contentionExpAverage;
        private final int maximumCores;

        public NaiveRuntimeCoreAdapter() {
            maximumCores = Runtime.getRuntime().availableProcessors();
            adaptedNumberOfCores = maximumCores;
            contentionExpAverage = 0;
        }

        @Override
        public void update(int round, Scheduler scheduler) {

            contentionExpAverage = contentionExpAverage * DISCOUNT_FACTOR + (1 - DISCOUNT_FACTOR) * scheduler.getContention();

//            System.out.println("contention avarage: " + contentionExpAverage);
            if (round % ADAPTIVE_AVERAGE_AMOUNT == 0) {
                if (contentionExpAverage > 0.25) {
                    if (adaptedNumberOfCores > 1) {
                        adaptedNumberOfCores--;
                    }
                }

                if (contentionExpAverage < 0.18) {
                    if (adaptedNumberOfCores < maximumCores) {
                        adaptedNumberOfCores++;
                    }
                }
            }
        }

        @Override
        public int getAdaptedNumberOfCores() {
            return adaptedNumberOfCores;
        }

    }

    /**
     * @propertyName statistics
     * @return
     */
    public HashSet<StatisticCollector> getStatistics() {
        return statistics;
    }

}
