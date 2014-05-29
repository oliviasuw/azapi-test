/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.sim.net;

import bgu.dcr.az.execs.exceptions.InitializationException;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.sim.AgentContext;
import bgu.dcr.az.execs.sim.SimulatedMachine;

/**
 *
 * @author User
 */
public class BaseMessageRouter implements MessageRouter {

    private SimulatedMachine[] routingTable;
    private long[] messageCounts = null;

    @Override
    public void route(Message m, int agent, AgentContext context) {
        SimulatedMachine machine = routingTable[agent];
        machine.receive(new AZIPMessage(m.copy(), machine.getControllerId(), agent, context));
        messageCounts[agent]++;
    }

    @Override
    public void initialize(Simulation ex) throws InitializationException {
        routingTable = new SimulatedMachine[ex.configuration().numMachines()];
        messageCounts = new long[routingTable.length];
        
    }

    @Override
    public void register(SimulatedMachine controller, int... agentIds) {
        for (int a : agentIds) {
            routingTable[a] = controller;
        }
    }

    @Override
    public long[] getMessageReceivedCountPerAgent() {
        return messageCounts;
    }

}