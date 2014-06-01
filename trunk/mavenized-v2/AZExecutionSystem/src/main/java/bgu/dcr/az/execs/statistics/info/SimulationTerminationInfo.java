/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.statistics.info;

import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.exps.exe.SimulationResult;

/**
 *
 * @author User
 */
public class SimulationTerminationInfo {

    private final SimulationResult executionResult;
    private final Simulation sim;

    public SimulationTerminationInfo(SimulationResult executionResult, Simulation sim) {
        this.executionResult = executionResult;
        this.sim = sim;
    }

    public Simulation getSimulation() {
        return sim;
    }

    public SimulationResult getExecutionResult() {
        return executionResult;
    }

}
