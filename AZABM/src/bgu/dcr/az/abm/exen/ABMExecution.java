/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.exen;

import bgu.dcr.az.abm.api.ABMAgent;
import bgu.dcr.az.abm.api.World;
import bgu.dcr.az.execs.api.Proc;
import bgu.dcr.az.mas.ExecutionEnvironment;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.impl.BaseExecution;
import bgu.dcr.az.mas.impl.InitializationException;
import java.util.Collection;

/**
 *
 * @author bennyl
 */
public class ABMExecution extends BaseExecution<World> {

    public ABMExecution(World data, Experiment containingExperiment) {
        super(data, containingExperiment, ExecutionEnvironment.sync, new BehaviorDistributer());
    }

    @Override
    protected Collection<Proc> createProcesses() throws InitializationException {
        data().agents().forEach(a -> a.setExecution(this));
        return (Collection) data().agents();
    }

    @Override
    protected void initialize() throws InitializationException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void terminate() {
        data().agents().forEach(ABMAgent::kill);
    }

}
