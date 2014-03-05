/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.exen;

import bgu.dcr.az.abm.api.World;
import bgu.dcr.az.abm.impl.ABMAgentImpl;
import bgu.dcr.az.execs.api.Proc;
import bgu.dcr.az.mas.ExecutionEnvironment;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.impl.BaseExecution;
import bgu.dcr.az.mas.impl.InitializationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class ABMExecution extends BaseExecution<World> {

    int initialNumberOfAgents;

    public ABMExecution(World data, Experiment containingExperiment, int initialNumberOfAgents) {
        super(data, containingExperiment, ExecutionEnvironment.sync, new BehaviorDistributer());
        this.initialNumberOfAgents = initialNumberOfAgents;
    }

    @Override
    protected Collection<Proc> createProcesses() throws InitializationException {
        List<Proc> result = new ArrayList<>();
        for (int i = 0; i < initialNumberOfAgents; i++) {
            result.add(new ABMAgentImpl(i));
        }

        return result;
    }

    @Override
    protected void initialize() throws InitializationException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
