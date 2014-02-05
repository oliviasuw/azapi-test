/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionService;
import bgu.dcr.az.mas.Hooks;
import bgu.dcr.az.mas.impl.InitializationException;

/**
 *
 * @author User
 */
public abstract class CPCorrectnessTester implements ExecutionService<CPData> {

    @Override
    public void initialize(final Execution<CPData> ex) throws InitializationException {
        new Hooks.TerminationHook() {
            @Override
            public void hook(ExecutionResult result) {
                switch (result.getState()) {
                    case CRUSHED:
                        break;
                    case LIMITED:
                        break;
                    case SUCCESS:
                        test((CPExecution) ex, (ExecutionResult<CPSolution>)result);
                        break;
                    default:
                        throw new AssertionError(result.getState().name());
                }
            }
        }.hookInto(ex);
    }

    protected abstract void test(CPExecution exec, ExecutionResult<CPSolution> result);
}
