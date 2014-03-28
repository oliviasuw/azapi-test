/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution;

import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import bgu.dcr.az.execs.api.experiments.ExecutionResult;
import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.execs.exceptions.ExperimentExecutionException;
import bgu.dcr.az.execs.api.experiments.ExperimentStatusSnapshot;
import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import bgu.dcr.az.execs.experiments.ExperimentStatusSnapshotImpl;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register("experiment")
public class CPExperiment implements Experiment {

    private final String failingProblemSavePath = "failing-problems";

    private final LinkedList<CPExperimentTest> tests = new LinkedList<>();
    private FailureDescription failureDescription = null;
    private final ExperimentStatusSnapshotImpl status = new ExperimentStatusSnapshotImpl();
    private String name;
    private Map<Class, ExecutionService> suppliedServices = new HashMap<>();
    private ExecutionResult result;
    private List<StatisticCollector> statistics = new LinkedList<>();

    /**
     * the set of tests
     *
     * @propertyName tests
     * @return
     */
    public LinkedList<CPExperimentTest> getTests() {
        return tests;
    }

    @Override
    public ExecutionResult lastResult() {
        return result;
    }

    /**
     * @UIVisibility false
     * @propertyName failure-description
     * @return
     */
    public FailureDescription getFailureDescription() {
        return failureDescription;
    }

    public void setFailureDescription(FailureDescription failureDescription) {
        this.failureDescription = failureDescription;
    }

    @Override
    public ExecutionResult execute() {
        try {
            supply(Context.ContextGenerator.class, new Context.ContextGenerator());// for nested agents
            
            status.start();

            int singleExecutionMode = failureDescription == null ? -1 : failureDescription.getFailingExecutionNumber();
            if (singleExecutionMode != -1) {
                return executeFailingExecution();
            }

            suppliedServices.values().stream()
                    .distinct().forEach(s -> s.initialize(this));

            for (CPExperimentTest t : tests) {
                status.currentExecutedSubExperimentName = t.getName();
                status.currentExecutedSubExperimentStatus = t.status();

                for (Map.Entry<Class, ExecutionService> e : suppliedServices.entrySet()) {
                    t.supply(e.getKey(), e.getValue());
                }

                suppliedServices.values().stream()
                        .distinct().forEach(s -> s.initialize(t));

                result = t.execute();

                if (result.getState() != ExecutionResult.State.SUCCESS) {
                    try {
                        saveProblem(new FailureDescription(result.toString(), t.getName(), t.getExecutedAlgorithmName(result.getLastRunExecution()), result.getLastRunExecution()));
                    } catch (IOException ex) {
                        Logger.getLogger(CPExperiment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return result;
                }

                status.finishedExperimentNames.add(t.getName());
            }
            return new ExecutionResult().toSucceefulState(null);
        } finally {
            status.end();
        }
    }

    private ExecutionResult executeFailingExecution() {
        ExecutorService pool = Executors.newCachedThreadPool();
        Scheduler sched = new MultithreadedScheduler(pool);

        try {
            result = getExecution(failureDescription.getFailingExecutionNumber()).execute(sched, Runtime.getRuntime().availableProcessors());

            if (result.getState() != ExecutionResult.State.SUCCESS) {
                return result;
            }

            return result = new ExecutionResult().toSucceefulState(null);
        } catch (ExperimentExecutionException | InterruptedException | ConfigurationException ex) {
            return result = new ExecutionResult().toCrushState(ex).setLastRunExecution(failureDescription.getFailingExecutionNumber());
        } finally {
            pool.shutdownNow();
        }
    }

    private void saveProblem(FailureDescription desc) throws IOException {
        File problemsPath = new File(failingProblemSavePath);
        problemsPath.mkdirs();
        File problemFile = new File(problemsPath, "" + System.currentTimeMillis() + ".xml");

        FailureDescription prevFailure = this.failureDescription;
        this.failureDescription = desc;

        ConfigurationUtils.write(this, problemFile);
        this.failureDescription = prevFailure;
    }

    @Override
    public int numberOfExecutions() {
        int amount = 0;
        for (CPExperimentTest t : tests) {
            amount += t.numberOfExecutions();
        }

        return amount;
    }

    public CPExperimentTest getTestForExectution(int i) {
        int amount = 0;

        for (CPExperimentTest t : tests) {
            int num = t.numberOfExecutions();
            if (amount + num >= i) {
                return t;
            }
            amount += num;
        }

        return null;
    }

    public Execution getExecution(int i) throws ConfigurationException {
        int amount = 0;
        for (CPExperimentTest t : tests) {
            int num = t.numberOfExecutions();
            if (amount + num >= i) {
                return t.getExecution(i - amount);
            }
            amount += num;
        }

        return null;
    }

    @Override
    public Collection<? extends Experiment> subExperiments() {
        return tests;
    }

    @Override
    public ExperimentStatusSnapshot status() {
        status.finishedExecutions = 0;
        for (CPExperimentTest t : tests) {
            status.finishedExecutions += t.status().finishedExecutions();
        }

        return status;
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

    @Override
    public void supply(Class<? extends ExecutionService> serviceType, ExecutionService service) {
        suppliedServices.put(serviceType, service);
    }

    @Override
    public Iterator<Experiment> iterator() {
        return (Iterator) getTests().iterator();
    }

    /**
     * available statistics
     *
     * @icon #remove.png
     * @return
     */
    @Override
    public Collection<StatisticCollector> getStatistics() {
        return statistics;
    }

}
