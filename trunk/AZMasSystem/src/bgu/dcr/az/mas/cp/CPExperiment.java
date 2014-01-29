/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.mas.exp.Experiment;
import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author bennyl
 */
@Register("experiment")
public class CPExperiment implements Experiment {
    private final String failingProblemSavePath = "failing-problems";

    private LinkedList<CPExperimentTest> tests = new LinkedList<>();
    private ExecutionSelector selector = null;

    /**
     * @propertyName tests
     * @return
     */
    public LinkedList<CPExperimentTest> getTests() {
        return tests;
    }

    /**
     * @propertyName single-execution-selector
     * @return
     */
    public ExecutionSelector getExecutionSelector() {
        return selector;
    }

    public void setExecutionSelector(ExecutionSelector selector) {
        this.selector = selector;
    }

    @Override
    public ExecutionResult execute() {
        if (selector != null) {
            return executeSelection();
        }

        for (CPExperimentTest t : tests) {
            ExecutionResult result = t.execute();

            if (result.getState() != ExecutionResult.State.SUCCESS) {
                saveProblem(result.getLastRunExecution());
                return result;
            }
        }
        return new ExecutionResult().toSucceefulState(null);
    }

    private ExecutionResult executeSelection() {
        for (CPExperimentTest t : tests) {
            if (t.getName().equals(selector.getSelectedTest())) {
                ExecutionResult result = t.executeSelection(selector);

                if (result.getState() != ExecutionResult.State.SUCCESS) {
                    return result;
                }

                break;
            }
        }

        return new ExecutionResult().toSucceefulState(null);
    }

    private void saveProblem(ExecutionSelector lastRunExecution) {
        File problemsPath = new File(failingProblemSavePath);
        problemsPath.mkdirs();
        File problemFile = new File(problemsPath, "" + System.currentTimeMillis() + ".xml");
        
        ExecutionSelector prevSelector = this.selector;
        this.selector = lastRunExecution;
        
        ConfigurationUtils.write(this, problemFile);
        this.selector = prevSelector;
    }

}
