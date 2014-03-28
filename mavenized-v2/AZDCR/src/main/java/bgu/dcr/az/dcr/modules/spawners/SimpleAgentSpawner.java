/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.spawners;

import bgu.dcr.az.dcr.api.modules.AgentSpawner;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.exceptions.InitializationException;

/**
 *
 * @author User
 */
public class SimpleAgentSpawner implements AgentSpawner {

    Class agentType;

    public SimpleAgentSpawner(Class agentType) {
        this.agentType = agentType;
    }

    @Override
    public Class getAgentType(int id) {
        return agentType;
    }

    @Override
    public void initialize(Execution ex) throws InitializationException {
    }

}
