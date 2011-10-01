/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

import bgu.csp.az.api.agt.SimpleMessage;

/**
 * this collection of interfaces that the simple agent supports hooking via
 * @author bennyl
 */
public class Hooks {
    
    
    public static interface BeforeMessageSentHook{
        void hook(SimpleMessage msg);
    }
    
    public static interface BeforeMessageProcessingHook{
        void hook(SimpleMessage msg);
    }
}
