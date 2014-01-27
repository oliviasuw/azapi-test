/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.mas.exp.Experiment;
import java.util.LinkedList;

/**
 *
 * @author bennyl
 */
@Register("experiment")
public class CPExperimentsContainer implements Experiment {

    LinkedList<CPExperimentTest> tests = new LinkedList<>();

    /**
     * @propertyName tests
     * @return
     */
    public LinkedList<CPExperimentTest> getTests() {
        return tests;
    }

    @Override
    public ExecutionResult execute() {
        for (CPExperimentTest t : tests) {
            ExecutionResult result = t.execute();
            
            if (result.getState() != ExecutionResult.State.SUCCESS) {
                return result;
            }
        }
        return new ExecutionResult().toSucceefulState(null);
    }

}
