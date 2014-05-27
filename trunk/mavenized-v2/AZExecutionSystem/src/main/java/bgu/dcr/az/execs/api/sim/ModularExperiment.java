/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.sim;

import bgu.dcr.az.execs.api.sim.prog.ExperimentProgress;
import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.execs.api.modules.ModuleContainer;
import java.util.concurrent.ExecutorService;

/**
 * a simulation is a base class for most environments that are able to be
 * simulated. it is intended to hold all the needed modules that the simulation
 * needed in order to run, and its contain a "run" method, this class derivation
 * is mostly only adds some setters and getters which only delegates to its
 * supply and require methods. this in order for the configuration framework to
 * provide useful information to the user
 *
 * @author bennyl
 */
public abstract class ModularExperiment extends ModuleContainer {

    public Iterable<ExecutionNode> executions() {
        return requireAll(ExecutionNode.class);
    }

    protected void addExecution(ExecutionNode exec) {
        supply(ExecutionNode.class, exec);
    }

    public ExperimentProgress execute(ExecutorService es){
//        es.execute(()->{
//            executions().forEach(e.exe);
//        });
        return null;
    }
}
