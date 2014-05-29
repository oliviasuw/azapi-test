/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.sim.net;

import bgu.dcr.az.execs.sim.AgentContext;

/**
 * Agent-Zero-Inner Protocol message
 *
 * @author User
 */
public class AZIPMessage {

    private final Message data;
    private final int controllerRecepient;
    private final int agentRecepient;
    private final AgentContext context;

    public AZIPMessage(Message data, int controllerRecepient, int agentRecepient, AgentContext context) {
        this.data = data;
        this.controllerRecepient = controllerRecepient;
        this.agentRecepient = agentRecepient;
        this.context = context;
    }

    public Message getData() {
        return data;
    }

    public int getControllerRecepient() {
        return controllerRecepient;
    }

    public int getAgentRecepient() {
        return agentRecepient;
    }

    public AgentContext getContext() {
        return context;
    }

}
