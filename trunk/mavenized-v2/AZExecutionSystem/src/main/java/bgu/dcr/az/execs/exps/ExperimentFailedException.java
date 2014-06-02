/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps;

import bgu.dcr.az.execs.exps.exe.Simulation;

/**
 *
 * @author bennyl
 */
public class ExperimentFailedException extends Exception {

    private Simulation failingSimulation;

    public ExperimentFailedException(Simulation failingSimulation, String message, Throwable cause) {
        super(message, cause);
        this.failingSimulation = failingSimulation;
    }

    public ExperimentFailedException(Simulation failingSimulation, Throwable cause) {
        super(cause);
        this.failingSimulation = failingSimulation;
    }

    public ExperimentFailedException(Simulation failingSimulation, String message) {
        super(message);
        this.failingSimulation = failingSimulation;
    }

    public ExperimentFailedException(Simulation failingSimulation) {
        this.failingSimulation = failingSimulation;
    }

    public Simulation getFailingSimulation() {
        return failingSimulation;
    }

    @Override
    public String getMessage() {
        return failingSimulation.result().toString();
    }
    
    

}
