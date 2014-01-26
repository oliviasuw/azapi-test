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
import bgu.dcr.az.api.exen.mdef.ProblemGenerator;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.execs.api.TerminationReason;
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
    private ExecutionEnvironment executionEnvironment;

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

    @Override
    public void execute() throws ExperimentExecutionException, InterruptedException {
        int numCores = Runtime.getRuntime().availableProcessors();
        final ExecutorService pool = Executors.newFixedThreadPool(numCores);
        Scheduler scheduler = new MultithreadedScheduler(pool);

        RuntimeCoreAdapter[] coreAdapters = new RuntimeCoreAdapter[algorithms.size()];

        for (int i = 0; i < coreAdapters.length; i++) {
            coreAdapters[i] = new NaiveRuntimeCoreAdapter();
        }

        if (getProblemGenerator() == null) {
            throw new ExperimentExecutionException("No problem generator is defined");
        }

        if (getAlgorithms().isEmpty()) {
            throw new ExperimentExecutionException("At least one algorithm should be defined in order to execute an experiment");
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
                    System.out.println("Start Running Problem on " + coreAdapters[numAlgo].getAdaptedNumberOfCores() + " cores");

                    TerminationReason result = exec.execute();
                    coreAdapters[numAlgo].update(i, scheduler);

                    if (result.isError()) {
                        result.getErrorDescription().printStackTrace();
                        return;
                    } else {
                        System.out.println("execution " + i + " done: " + exec.getSolution());
                    }
                    numAlgo++;
                }
            }
        } catch (ClassNotFoundException | ConfigurationException ex) {
            throw new ExperimentExecutionException("cannot execute experiment - configuration problem, see cause", ex);
        } finally {
            pool.shutdownNow();
        }
    }

    @Override
    public String toString() {
        return "DCRExperiment{" + "pgen=" + pgen + ", algorithms=" + algorithms + ", looper=" + looper + '}';
    }

    private static interface RuntimeCoreAdapter {

        public static final int ADAPTIVE_AVERAGE_AMOUNT = 3;

        void update(int round, Scheduler scheduler);

        int getAdaptedNumberOfCores();
    }

    private static class NaiveRuntimeCoreAdapter implements RuntimeCoreAdapter {

        private int adaptedNumberOfCores;
        private double lastContentionAverage;
        private final double[] lastContentions;
        private boolean isStabilized;

        public NaiveRuntimeCoreAdapter() {
            adaptedNumberOfCores = Runtime.getRuntime().availableProcessors();
            lastContentionAverage = 1;
            lastContentions = new double[ADAPTIVE_AVERAGE_AMOUNT];
            isStabilized = false;
        }

        @Override
        public void update(int round, Scheduler scheduler) {
            if (isStabilized) {
                return;
            }

            if (round % ADAPTIVE_AVERAGE_AMOUNT == 0 && round != 0) {
                Double avg = calculateAvg(lastContentions);

                if (avg != null) {
                    if (avg < lastContentionAverage) {
                        adaptedNumberOfCores--;
                        if (adaptedNumberOfCores == 1) {
                            isStabilized = true;
                        }
                    }

                    lastContentionAverage = Math.min(lastContentionAverage, avg);
                }

            }

            lastContentions[round % ADAPTIVE_AVERAGE_AMOUNT] = scheduler.getContention();
        }

        @Override
        public int getAdaptedNumberOfCores() {
            return adaptedNumberOfCores;
        }

        /**
         * @param arr
         * @return the average of non-zero elements in a given array. if all
         * elements in array are zero, returns null
         */
        private Double calculateAvg(double[] arr) {
            double avg = 0;
            int nonZero = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != 0) {
                    avg += arr[i];
                    nonZero++;
                }
            }
            return nonZero == 0 ? null : avg / nonZero;
        }

    }

}
