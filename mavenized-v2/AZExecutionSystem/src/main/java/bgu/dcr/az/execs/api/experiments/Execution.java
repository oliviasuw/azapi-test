/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.experiments;

import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.execs.api.statistics.InfoStream;
import bgu.dcr.az.execs.exceptions.ExperimentExecutionException;
import bgu.dcr.az.execs.exceptions.InitializationException;

/**
 *
 * @author User
 * @param <T>
 */
public interface Execution<T> {

    public <T extends ExecutionService> T require(Class<T> service) throws InitializationException;

    public boolean hasRequirement(Class<? extends ExecutionService> service);
    
    public void supply(Class<? extends ExecutionService> serviceKey, ExecutionService service);

    public ExecutionResult execute(Scheduler sched, int numCores) throws ExperimentExecutionException, InterruptedException;
    
    public ExecutionEnvironment getEnvironment();
    
    public T data();
    
    public Experiment getContainingExperiment();
    
    public InfoStream informationStream();
}
