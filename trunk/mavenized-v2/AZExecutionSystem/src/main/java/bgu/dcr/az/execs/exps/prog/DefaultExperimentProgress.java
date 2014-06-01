/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.prog;

import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.execs.exps.ExperimentProgressEnhancer;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.execs.statistics.info.SimulationTerminationInfo;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class DefaultExperimentProgress extends ExperimentProgressEnhancer {

    private int currentExecutedExecutionNumeber = -1;
    private double currentTestProgress = 0;
    private int currentExecutedTestNumber = -1;
    private ModularExperiment experiment;
    private final Set<String> finishedTestNames = new HashSet<>();
    private String currentTestName = null;
    private int numOfExecutionsInCurrentTest;
    private int numOfFinishedExecutionsInCurrentTest;

    @Override
    public void initialize(ModularExperiment experiment) {
        this.experiment = experiment;

        InfoStream info = experiment.execution().infoStream();
        numOfExecutionsInCurrentTest = 0;
        numOfFinishedExecutionsInCurrentTest = 0;

        info.listen(Simulation.class, sim -> {
            incCurrentExecutedExecutionNumber();
        });

        info.listen(SimulationTerminationInfo.class, sti -> {
            numOfFinishedExecutionsInCurrentTest++;
        });

        info.listen(Test.class, test -> {
            if (currentTestName != null) {
                finishedTestNames.add(currentTestName);
            }

            currentTestName = test.getName();
            numOfExecutionsInCurrentTest = test.countExecutions();
            numOfFinishedExecutionsInCurrentTest = 0;

            incCurrentExecutedTestNumber();
        });
    }

    public String getCurrentTestName() {
        return currentTestName;
    }

    public double getCurrentTestProgress() {
        if (numOfExecutionsInCurrentTest == 0) {
            return 0;
        }
        return (double) numOfFinishedExecutionsInCurrentTest / numOfExecutionsInCurrentTest;
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

    public boolean isTestFinished(String testName) {
        return finishedTestNames.contains(testName);
    }

}
