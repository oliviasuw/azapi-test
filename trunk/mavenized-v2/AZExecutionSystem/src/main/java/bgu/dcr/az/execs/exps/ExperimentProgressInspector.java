/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.ModuleContainer;

/**
 *
 * @author bennyl
 */
public abstract class ExperimentProgressInspector implements Module {

    @Override
    public final void installInto(ModuleContainer mc) {
        if (!(mc instanceof ModularExperiment)) {
            throw new UnsupportedOperationException("Progress Enhancers only support ModularExperiment containers!");
        }
        initialize((ModularExperiment) mc);
    }

    public abstract void initialize(ModularExperiment experiment);
}
