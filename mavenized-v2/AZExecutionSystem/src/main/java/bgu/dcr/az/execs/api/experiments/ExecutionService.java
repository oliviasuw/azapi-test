/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.experiments;

import bgu.dcr.az.execs.exceptions.InitializationException;

/**
 *
 * @author User
 * @param <T>
 */
public interface ExecutionService<T> {

    /**
     * called before each experiment execution
     *
     * @param ex
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
