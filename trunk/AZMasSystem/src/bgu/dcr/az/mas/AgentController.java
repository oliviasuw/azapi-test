/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.execs.api.Proc;

/**
 *
 * @author User
 */
public interface AgentController extends Proc{

    /**
     * send a message to the given recepient agent
     *
     * @param m
     * @param recepientAgent
     */
    void send(Message m, int recepientAgent);

    /**
     * receive a message from the message router, this message should be
     * distributed eventually to one of your agents
     *
     * @param message
     */
    void receive(AZIPMessage message);

    /**
     * if we are running in a synchronous execution this method can provide the
     * number of tick we are in, note that this method result is undefined for
     * any other execution environment
     *
     * @return
     */
    int getTickNumber();

    /**
     * provides basic logging services for the given agent
     *
     * @param agentId
     * @param msg
     */
    void log(int agentId, String msg);

    /**
     * 
     * @return 
     */
    int getControllerId();
    
}
