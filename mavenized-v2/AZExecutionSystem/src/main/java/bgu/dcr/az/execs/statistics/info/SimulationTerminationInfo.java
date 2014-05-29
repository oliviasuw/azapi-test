/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.statistics.info;

import bgu.dcr.az.execs.exps.exe.SimulationResult;

/**
 *
 * @author User
 */
public class SimulationTerminationInfo {

    private final SimulationResult executionResult;

    public SimulationTerminationInfo(SimulationResult executionResult) {
        this.executionResult = executionResult;
    }

    public SimulationResult getExecutionResult() {
        return executionResult;
    }

}
