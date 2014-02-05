/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
import bgu.dcr.az.mas.exp.ExperimentStatusSnapshot;
import bgu.dcr.az.mas.impl.ExperimentStatusSnapshotImpl;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
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

    /**
     * @propertyName tests
     * @return
     */
    public LinkedList<CPExperimentTest> getTests() {
        return tests;
    }

    /**
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

            status.start();

            int singleExecutionMode = failureDescription == null ? -1 : failureDescription.getFailingExecutionNumber();
            if (singleExecutionMode != -1) {
                return executeFailingExecution();
            }

            for (CPExperimentTest t : tests) {
                status.currentExecutedSubExperimentName = t.getName();
                status.currentExecutedSubExperimentStatus = t.status();

                ExecutionResult result = t.execute();

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
            ExecutionResult result = getExecution(failureDescription.getFailingExecutionNumber()).execute(sched, Runtime.getRuntime().availableProcessors());

            if (result.getState() != ExecutionResult.State.SUCCESS) {
                return result;
            }

            return new ExecutionResult().toSucceefulState(null);
        } catch (ExperimentExecutionException | InterruptedException | ConfigurationException ex) {
            return new ExecutionResult().toCrushState(ex).setLastRunExecution(failureDescription.getFailingExecutionNumber());
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

}
