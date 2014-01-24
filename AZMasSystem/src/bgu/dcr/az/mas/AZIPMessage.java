/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas;

import bgu.dcr.az.api.Message;

/**
 * Agent-Zero-Inner Protocol message
 *
 * @author User
 */
public class AZIPMessage {

    private final Message data;
    private final int controllerRecepient;
    private final int agentRecepient;

    public AZIPMessage(Message data, int controllerRecepient, int agentRecepient) {
        this.data = data;
        this.controllerRecepient = controllerRecepient;
        this.agentRecepient = agentRecepient;
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

}
