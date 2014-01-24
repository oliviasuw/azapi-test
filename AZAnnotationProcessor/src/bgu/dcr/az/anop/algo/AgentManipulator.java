/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.algo;

import bgu.dcr.az.anop.conf.Configuration;
import java.util.Collection;

/**
 *
 * @author User
 */
public interface AgentManipulator extends Configuration{

    /**
     * @return the manipulating algorithm name
     */
    String getAlgorithmName();
    
    /**
     * @return information about all the handlers in this algorithm
     */
    Collection<HandlerInfo> handlers();
    
    /**
     * call handler that is defined via the WhenReceived annotation
     *
     * @param agentInstance
     * @param name
     * @param arguments
     */
    void callHandler(Object agentInstance, String name, Object[] arguments);
}
