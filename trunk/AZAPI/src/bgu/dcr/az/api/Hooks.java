/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api;


/**
 * this collection of interfaces that the simple agent supports hooking via
 * @author bennyl
 */
public class Hooks {
    
    
    /**
     * callback that will get called before message sent - can be attached to simple agent.
     */
    public static interface BeforeMessageSentHook{
        /**
         * callback implementation
         * @param msg
         */
        void hook(Agent a, Message msg);
    }
    
    /**
     * callback that will get called before message processed by the attached agent - can be attachd to simple agent.
     */
    public static interface BeforeMessageProcessingHook{
        /**
         * callback implementation
         * @param msg
         */
        void hook(Agent a, Message msg);
    }
    
    public static interface BeforeCallingFinishHook{
        void hook(Agent a);
    }
    
    
    public static interface ReportHook{
        void hook(Agent a, Object[] args);
    }
    
    public static interface TickHook{
        void hook(SystemClock clock);
    }
}
