/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.mas.impl.Context;

/**
 * Agent-Zero-Inner Protocol message
 *
 * @author User
 */
public class AZIPMessage {

    private final Message data;
    private final int controllerRecepient;
    private final int agentRecepient;
    private final Context context;

    public AZIPMessage(Message data, int controllerRecepient, int agentRecepient, Context context) {
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

    public Context getContext() {
        return context;
    }

}
