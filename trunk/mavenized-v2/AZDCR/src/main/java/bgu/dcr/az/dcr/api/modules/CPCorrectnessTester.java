/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.modules;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.dcr.api.experiment.CPData;
import bgu.dcr.az.dcr.api.experiment.CPSolution;
import bgu.dcr.az.execs.exps.exe.SimulationResult;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.statistics.info.SimulationTerminationInfo;

/**
 *
 * @author User
 */
public abstract class CPCorrectnessTester implements Module<Simulation<CPData, CPSolution>> {

    @Override
    public void initialize(final Simulation<CPData, CPSolution> ex) {
        ex.infoStream().listen(SimulationTerminationInfo.class, t -> {
            SimulationResult result = t.getExecutionResult();
            switch (result.getState()) {
                case CRUSHED:
                    break;
                case LIMITED:
                    break;
                case SUCCESS:
                    test(ex, (SimulationResult<CPSolution>) result);
                    break;
                default:
                    throw new AssertionError(result.getState().name());
            }
        });
    }

    protected abstract void test(Simulation<CPData, CPSolution> sim, SimulationResult<CPSolution> result);
}
