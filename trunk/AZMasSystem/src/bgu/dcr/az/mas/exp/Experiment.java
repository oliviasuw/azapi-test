/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.exp;

import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.impl.InitializationException;

/**
 *
 * @author User
 */
public interface Experiment {

    /**
     * @return the amount of executions that are needed to execute in order to
     * complete this experiment
     */
    public int numberOfExecutions();

    /**
     * return the i'th execution, this execution is not the object that will
     * actually be executed by the experiment but just a copy of it - so changes
     * will not propagate to the experiment
     *
     * @param i
     * @return
     * @throws bgu.dcr.az.anop.conf.ConfigurationException
     * @throws bgu.dcr.az.mas.impl.InitializationException
     */
    public Execution getExecution(int i) throws ConfigurationException, InitializationException;

    public ExecutionResult execute();
}
