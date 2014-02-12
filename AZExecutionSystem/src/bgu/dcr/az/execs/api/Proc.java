/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api;

/**
 *
 * @author bennyl
 */
public interface Proc {

    /**
     * @return true if this process is a deamon process - a deamon process is
     * automatically terminate (currently quietly) if all the non deamon
     * processes has terminated
     */
    boolean isDeamon();

    /**
     * @return the state of the process
     */
    ProcState state();

    /**
     * perform one quota of computation, the process may change its state during
     * this quota
     *
     * @param systemCalls
     * @param idleResolvingQuota if this parameter is true then the process
     * received a quota in order to solve idle state
     */
    void quota(SystemCalls systemCalls, boolean idleResolvingQuota);

    /**
     * @return the id of this process - the process should never change its id.
     */
    int pid();
}
