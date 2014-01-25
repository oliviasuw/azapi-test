/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas;

import bgu.dcr.az.execs.api.ProcTable;
import bgu.dcr.az.execs.api.TerminationReason;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
import bgu.dcr.az.mas.impl.InitializationException;

/**
 *
 * @author User
 */
public interface Execution {

    public void hook(Class hookType, Object hook);

    public <T extends ExecutionService> T require(Class<T> service) throws InitializationException;

    public void put(Class<? extends ExecutionService> serviceKey, ExecutionService service);

    public TerminationReason execute() throws ExperimentExecutionException, InterruptedException;
    
}
