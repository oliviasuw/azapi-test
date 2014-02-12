/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.stat.data;

import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.api.exen.ExecutionResult;

/**
 *
 * @author User
 */
public class ExecutionTerminationInfo<T extends DeepCopyable> {

    private final ExecutionResult<T> executionResult;

    public ExecutionTerminationInfo(ExecutionResult<T> executionResult) {
        this.executionResult = executionResult;
    }

    public ExecutionResult<T> getExecutionResult() {
        return executionResult;
    }

}
