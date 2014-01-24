/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas;

import bgu.dcr.az.mas.exp.ExperimentExecutionException;

/**
 *
 * @author User
 */
public interface Execution {

    public void hook(Class hookType, Object hook);

    public <T> T require(Class<T> service);

    public void put(Class<? extends ExecutionService> serviceKey, ExecutionService service);

    public void execute() throws ExperimentExecutionException, InterruptedException;
}
