/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.sim.net;

import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.execs.sim.AgentContext;
import bgu.dcr.az.execs.sim.SimulatedMachine;

/**
 * a message router holds the collection of all the agents controllers and is
 * responsible to distribute messages among them (it can support message delay
 * strategies maybe?)
 *
 * @author User
 */
public interface MessageRouter extends Module<Simulation>{

    long[] getMessageReceivedCountPerAgent();
    
    void register(SimulatedMachine controller, int... agentIds);

    void route(Message m, int agent, AgentContext context);

}
