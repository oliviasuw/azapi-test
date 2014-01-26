/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.execs.api.ProcTable;
import bgu.dcr.az.mas.AZIPMessage;
import bgu.dcr.az.mas.AgentController;
import bgu.dcr.az.mas.AgentDistributer;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.MessageRouter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author User
 */
public class BaseMessageRouter implements MessageRouter {

    private AgentController[] routingTable;
//    private final ProcTable procTable;

//    public BaseMessageRouter(ProcTable procTable) {
//        this.procTable = procTable;
//    }

    @Override
    public void route(Message m, int agent) {
        AgentController controller = routingTable[agent];
        controller.receive(new AZIPMessage(m.copy(), controller.getControllerId(), agent));
//        procTable.wake(controller.getControllerId());
    }

    @Override
    public void initialize(Execution ex) throws InitializationException {
        AgentDistributer distributer = ex.require(AgentDistributer.class);
        routingTable = new AgentController[distributer.getNumberOfAgents()];
//        messagesQueues = new ConcurrentLinkedQueue[distributer.getNumberOfAgentControllers()];
//
//        for (int d = 0; d < distributer.getNumberOfAgentControllers(); d++) {
//            for (int a : distributer.getControlledAgentsIds(d)) {
//                routingTable[a] = d;
//            }
//
//            messagesQueues[d] = new ConcurrentLinkedQueue();
//        }
    }

    @Override
    public void register(AgentController controller, int... agentIds) {
        for (int a : agentIds){
            routingTable[a] = controller;
        }
    }

}
