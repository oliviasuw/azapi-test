/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;


/**
 * this collection of interfaces that the simple agent supports hooking via
 * @author bennyl
 */
public class Hooks {
    
    
    /**
     * callback that will get called before message sent - can be attached to simple agent.
     */
    public static interface BeforeMessageSentHook{
        void hook(Message msg);
    }
    
    /**
     * callback that will get called before message processed by the attached agent - can be attachd to simple agent.
     */
    public static interface BeforeMessageProcessingHook{
        void hook(Message msg);
    }
}
