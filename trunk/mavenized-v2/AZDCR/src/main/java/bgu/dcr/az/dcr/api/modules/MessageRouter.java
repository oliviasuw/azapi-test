/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.modules;

import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.dcr.api.Message;
import bgu.dcr.az.dcr.execution.AgentController;
import bgu.dcr.az.dcr.execution.Context;

/**
 * a message router holds the collection of all the agents controllers and is
 * responsible to distribute messages among them (it can support message delay
 * strategies maybe?)
 *
 * @author User
 */
public interface MessageRouter extends ExecutionService {

    void register(AgentController controller, int... agentIds);

    void route(Message m, int agent, Context context);

}
