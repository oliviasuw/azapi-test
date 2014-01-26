/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
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
    public void execute() throws ExperimentExecutionException, InterruptedException {
        for (CPExperimentTest t : tests) {
            t.execute();
        }
    }

}
