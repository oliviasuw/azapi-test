/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.modules;

import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.dcr.execution.CPExecution;
import bgu.dcr.az.dcr.execution.CPSolution;
import bgu.dcr.az.execs.api.experiments.ExecutionResult;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.execs.exceptions.InitializationException;
import bgu.dcr.az.execs.statistics.info.ExecutionTerminationInfo;

/**
 *
 * @author User
 */
public abstract class CPCorrectnessTester implements ExecutionService<CPData> {

    @Override
    public void initialize(final Execution<CPData> ex) throws InitializationException {
        ex.informationStream().listen(ExecutionTerminationInfo.class, t -> {
            ExecutionResult result = t.getExecutionResult();
            switch (result.getState()) {
                case CRUSHED:
                    break;
                case LIMITED:
                    break;
                case SUCCESS:
                    test((CPExecution) ex, (ExecutionResult<CPSolution>) result);
                    break;
                default:
                    throw new AssertionError(result.getState().name());
            }
        });
    }

    protected abstract void test(CPExecution exec, ExecutionResult<CPSolution> result);
}
