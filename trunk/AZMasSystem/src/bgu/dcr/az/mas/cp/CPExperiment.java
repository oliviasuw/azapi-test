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
import java.io.File;
import java.io.IOException;
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

    private LinkedList<CPExperimentTest> tests = new LinkedList<>();
    private int singleExecutionMode = -1;

    /**
     * @propertyName tests
     * @return
     */
    public LinkedList<CPExperimentTest> getTests() {
        return tests;
    }

    /**
     * @propertyName single-execution-mode
     * @return
     */
    public int getSingleExecutionMode() {
        return singleExecutionMode;
    }

    public void setSingleExecutionMode(int singleExecutionMode) {
        this.singleExecutionMode = singleExecutionMode;
    }

    @Override
    public ExecutionResult execute() {
        if (singleExecutionMode != -1) {
            return executeSelection();
        }

        for (CPExperimentTest t : tests) {
            ExecutionResult result = t.execute();

            if (result.getState() != ExecutionResult.State.SUCCESS) {
                try {
                    saveProblem(result.getLastRunExecution());
                } catch (IOException ex) {
                    Logger.getLogger(CPExperiment.class.getName()).log(Level.SEVERE, null, ex);
                }
                return result;
            }
        }
        return new ExecutionResult().toSucceefulState(null);
    }

    private ExecutionResult executeSelection() {
        ExecutorService pool = Executors.newCachedThreadPool();
        Scheduler sched = new MultithreadedScheduler(pool);

        try {
            ExecutionResult result = getExecution(getSingleExecutionMode()).execute(sched, Runtime.getRuntime().availableProcessors());

            if (result.getState() != ExecutionResult.State.SUCCESS) {
                return result;
            }

            return new ExecutionResult().toSucceefulState(null);
        } catch (ExperimentExecutionException | InterruptedException | ConfigurationException ex) {
            return new ExecutionResult().toCrushState(ex).setLastRunExecution(singleExecutionMode);
        } finally {
            pool.shutdownNow();
        }
    }

    private void saveProblem(int i) throws IOException {
        File problemsPath = new File(failingProblemSavePath);
        problemsPath.mkdirs();
        File problemFile = new File(problemsPath, "" + System.currentTimeMillis() + ".xml");

        int prevMode = this.singleExecutionMode;
        this.singleExecutionMode = i;

        ConfigurationUtils.write(this, problemFile);
        this.singleExecutionMode = prevMode;
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

    @Override
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

}
