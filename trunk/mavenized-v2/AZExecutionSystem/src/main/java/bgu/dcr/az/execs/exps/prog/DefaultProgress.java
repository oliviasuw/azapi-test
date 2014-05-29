/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.prog;

import bgu.dcr.az.execs.api.statistics.InfoStream;
import bgu.dcr.az.execs.exps.ExperimentProgressEnhancer;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.exps.exe.Test;

/**
 *
 * @author bennyl
 */
public class DefaultProgress extends ExperimentProgressEnhancer {

    private int currentExecutedExecutionNumeber = -1;
    private int currentExecutedTestNumber = -1;
    private ModularExperiment experiment;

    @Override
    public void initialize(ModularExperiment experiment) {
        this.experiment = experiment;

        InfoStream info = experiment.execution().infoStream();
        info.listen(Simulation.class, sim -> {
            incCurrentExecutedExecutionNumber();
        });
        
        info.listen(Test.class, test -> {
            incCurrentExecutedTestNumber();
        });
    }

    public int getCurrentExecutedExecutionNumeber() {
        return currentExecutedExecutionNumeber;
    }

    public int getCurrentExecutedTestNumber() {
        return currentExecutedTestNumber;
    }

    private void incCurrentExecutedTestNumber() {
        this.currentExecutedTestNumber++;
    }

    private void incCurrentExecutedExecutionNumber() {
        this.currentExecutedExecutionNumeber++;
    }

    public int getNumberOfExecutions() {
        return experiment.execution().countExecutions();
    }

    public int getNumberOfTests() {
        return experiment.execution().numChildren();
    }

}
