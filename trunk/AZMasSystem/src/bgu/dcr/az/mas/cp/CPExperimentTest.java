/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.anop.reg.RegisteryUtils;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.exen.mdef.ProblemGenerator;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.mas.AgentSpawner;
import bgu.dcr.az.mas.ExecutionEnvironment;
import bgu.dcr.az.mas.exp.AlgorithmDef;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
import bgu.dcr.az.mas.exp.Looper;
import bgu.dcr.az.mas.exp.loopers.SingleExecutionLooper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author User
 */
@Register("test")
public class CPExperimentTest implements Experiment {

    private ProblemGenerator pgen;
    private final List<AlgorithmDef> algorithms = new LinkedList<>();
    private Looper looper = new SingleExecutionLooper();
    private ExecutionEnvironment executionEnvironment = ExecutionEnvironment.async;
    private CPCorrectnessTester correctnessTester;

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
        int numCores = Runtime.getRuntime().availableProcessors();
        final ExecutorService pool = Executors.newFixedThreadPool(numCores);
        Scheduler scheduler = new MultithreadedScheduler(pool);

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

        Object[] configurableElements = {
            getProblemGenerator()
        };

        try {

            Random seedGenerator = new Random(10);

            List<Configuration> configurationOfElements = new ArrayList<>(configurableElements.length);
            for (Object o : configurableElements) {
                configurationOfElements.add(ConfigurationUtils.createConfigurationFor(o));
            }

            for (int i = 0; i < looper.count(); i++) {
                looper.configure(i, configurationOfElements);
                for (int j = 0; j < configurableElements.length; j++) {
                    configurationOfElements.get(j).configure(configurableElements[j]);
                }

                System.out.println("Start Generating Problem");
                Problem p = new Problem();
                pgen.generate(p, new Random(seedGenerator.nextLong()));
                int numAlgo = 0;
                for (AlgorithmDef adef : algorithms) {
                    AgentSpawner spawner = new SimpleAgentSpawner(RegisteryUtils.getDefaultRegistery().getRegisteredClassByName("ALGORITHM." + adef.getName()));
                    CPExecution exec = new CPExecution(scheduler, spawner, p, executionEnvironment, coreAdapters[numAlgo].getAdaptedNumberOfCores());
                    
                    if (correctnessTester != null) {
                        exec.put(CPCorrectnessTester.class, correctnessTester);
                    }
                    
                    System.out.println("Start Running Problem on " + coreAdapters[numAlgo].getAdaptedNumberOfCores() + " cores");

                    ExecutionResult result = exec.execute();

                    if (result.getState() != ExecutionResult.State.SUCCESS) {
                        return result;
                    }

                    coreAdapters[numAlgo].update(i, scheduler);
                    numAlgo++;
                }
            }
        } catch (Exception ex) {
            return new ExecutionResult().toCrushState(new ExperimentExecutionException("cannot execute experiment - configuration problem, see cause", ex));
        } finally {
            pool.shutdownNow();
        }

        return new ExecutionResult().toSucceefulState(null);
    }

    @Override
    public String toString() {
        return "DCRExperiment{" + "pgen=" + pgen + ", algorithms=" + algorithms + ", looper=" + looper + '}';
    }

    private static interface RuntimeCoreAdapter {

        public static final int ADAPTIVE_AVERAGE_AMOUNT = 4;
        public static final double DISCOUNT_FACTOR = 0.75;

        void update(int round, Scheduler scheduler);

        int getAdaptedNumberOfCores();
    }

    private static class NaiveRuntimeCoreAdapter implements RuntimeCoreAdapter {

        private int adaptedNumberOfCores;
        private double contentionExpAverage;
        private boolean isStabilized;
        private final int maximumCores;

        public NaiveRuntimeCoreAdapter() {
            maximumCores = Runtime.getRuntime().availableProcessors();
            adaptedNumberOfCores = maximumCores;
            contentionExpAverage = 0;
            isStabilized = false;
        }

        @Override
        public void update(int round, Scheduler scheduler) {
            if (isStabilized) {
                return;
            }

            contentionExpAverage = contentionExpAverage * DISCOUNT_FACTOR + (1 - DISCOUNT_FACTOR) * scheduler.getContention();

            System.out.println("contention avarage: " + contentionExpAverage);

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

}
