/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.mas.AgentSpawner;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.impl.InitializationException;

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
