/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.sim;

import bgu.dcr.az.execs.api.modules.Module;

/**
 *
 * @author bennyl
 */
public interface ExecutionNode extends Iterable<ExecutionNode>, Module {

    default int numOfExecutions() {
        return 0;
    }

    default boolean leaf() {
        return numOfExecutions() == 0;
    }

    int hight();
    
    
}
