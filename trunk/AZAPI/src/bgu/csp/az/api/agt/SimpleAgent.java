package bgu.csp.az.api.agt;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.Hooks.BeforeMessageProcessingHook;
import bgu.csp.az.api.ano.WhenReceived;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import bgu.csp.az.api.exp.InternalErrorException;
import bgu.csp.az.api.exp.UnsupportedMessageException;

/**
 * SimpleAgent is an agent that uses annotations instead of multiple message objects and manages a major parts of the algorithm tasks by its own. 
 * @author bennyl
 */
public abstract class SimpleAgent extends Agent {

    private HashMap<String, Method> msgToMethod;

    public SimpleAgent() {
        msgToMethod = new HashMap<String, Method>();
        scanMethods();
    }

    /**
     * will scan methods that should handle messages
     */
    private void scanMethods() {
        for (Method m : getClass().getMethods()) {
            if (m.isAnnotationPresent(WhenReceived.class)) {
                m.setAccessible(true); // bypass the security manager rechecking - make reflected calls faster
                msgToMethod.put(m.getAnnotation(WhenReceived.class).value(), m);
            }
        }
    }

    @Override
    public void processNextMessage() throws InterruptedException {
        Message msg = nextMessage();
        for (BeforeMessageProcessingHook hook : beforeMessageProcessingHooks) {
            hook.hook(this, msg);
        }
        msg = beforeMessageProcessing(msg);
        if (msg == null) {
            return; //DUMPING MESSAGE..
        }
        Method mtd = msgToMethod.get(msg.getName());
        if (mtd == null) {
            throw new UnsupportedMessageException("no method to handle message: '" + msg.getName() + "' was found (use @WhenReceived on PUBLIC functions only)");
        }
        try {
            mtd.invoke(this, msg.getArgs());
            return;
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            throw new UnsupportedMessageException("wrong parameters passed with the message " + msg.getName());
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            throw new InternalErrorException("internal error while processing message: '" + msg.getName() + "' in agent " + getId(), e);
        } catch (InvocationTargetException e) {
            //e.printStackTrace();
            throw new InternalErrorException("internal error while processing message: '" + msg.getName() + "' in agent " + getId() + ": " + e.getCause().getMessage() + " (see cause)", e.getCause());
        }
    }

    /**
     * you can override this method to perform preprocessing before messages arrive to their functions
     * you can change the message or even return completly other one - if you will return null 
     * the message is rejected and dumped.
     * @param msg
     * @return 
     */
    protected Message beforeMessageProcessing(Message msg) {
        return msg;
    }
    


}
