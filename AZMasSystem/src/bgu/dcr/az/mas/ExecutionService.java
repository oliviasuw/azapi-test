/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas;

import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.impl.InitializationException;

/**
 *
 * @author User
 */
public interface ExecutionService<T> {

    /**
     * called before each experiment execution
     *
     * @param ex
     * @throws InitializationException
     */
    default void initialize(Experiment ex) {
    }

    /**
     * called before each execution, an
     * {@link ExecutionService#initialize(bgu.dcr.az.mas.exp.Experiment)} will
     * always get called before sequence of calls to this method
     *
     * @param ex
     * @throws InitializationException
     */
    void initialize(Execution<T> ex) throws InitializationException;
}
