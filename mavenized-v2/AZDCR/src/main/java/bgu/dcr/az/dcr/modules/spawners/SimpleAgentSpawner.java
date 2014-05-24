/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.spawners;

import bgu.dcr.az.conf.api.Property;
import bgu.dcr.az.conf.registery.Registery;
import bgu.dcr.az.dcr.Agt0DSL;
import bgu.dcr.az.dcr.api.modules.AgentSpawner;
import bgu.dcr.az.dcr.execution.AlgorithemVariableAssignment;
import bgu.dcr.az.dcr.execution.AlgorithmDef;
import bgu.dcr.az.dcr.execution.manipulators.AgentManipulator;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.exceptions.InitializationException;

/**
 *
 * @author User
 */
public class SimpleAgentSpawner implements AgentSpawner {

    AlgorithmDef def;

    public SimpleAgentSpawner(AlgorithmDef def) {
        this.def = def;
    }

    @Override
    public AgentManipulator createAgentManipulator(int id) {
        
        final Class registeredClassByName = Registery.get().getRegisteredClassByName("ALGORITHM." + def.getName());
        Agt0DSL.panicIf(registeredClassByName == null, "cannot find agent with algorithem name = " + def.getName());
        
        AgentManipulator man = AgentManipulator.lookup(registeredClassByName);
        for (AlgorithemVariableAssignment assignment : def.getAssignments()) {
            Property p = man.get(assignment.getPropertyName());
            if (p != null) {
                p.set(assignment.getValue());
            } else {
                System.err.println("does not know what to do with algorithm assignment : " + assignment + " to algorithm " + def.getInstanceName() + ", ignoring...");
            }
        }
        
        return man;
    }

    @Override
    public void initialize(Execution ex) throws InitializationException {
    }

}
