package bgu.dcr.az.api.agt;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.Hooks.BeforeMessageProcessingHook;
import bgu.dcr.az.api.ano.WhenReceived;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import bgu.dcr.az.api.exp.InternalErrorException;
import bgu.dcr.az.api.exp.UnsupportedMessageException;
import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * SimpleAgent is an agent that uses annotations instead of multiple message objects and manages a major parts of the algorithm tasks by its own. 
 * @author bennyl
 */
public abstract class SimpleAgent extends Agent {

    //private HashMap<String, Method> msgToMethod;
    private HashMap<String, Integer> msgToMtdIdx;
    private MethodAccess mtda;
    
    public SimpleAgent() {
        //msgToMethod = new HashMap<String, Method>();
        msgToMtdIdx = new HashMap<String, Integer>();
        mtda = MethodAccess.get(getClass());
        
        scanMethods();
    }

    /**
     * will scan methods that should handle messages
     */
    private void scanMethods() {
        for (Method m : getClass().getMethods()) {
            if (m.isAnnotationPresent(WhenReceived.class)) {
//                m.setAccessible(true); // bypass the security manager rechecking - make reflected calls faster
//                msgToMethod.put(m.getAnnotation(WhenReceived.class).value(), m);
                msgToMtdIdx.put(m.getAnnotation(WhenReceived.class).value(), mtda.getIndex(m.getName(), m.getParameterTypes()));
            }
        }
    }

    @Override
    public void processNextMessage() throws InterruptedException {
        Message msg = nextMessage(); //will block until there will be messages in the q
        for (BeforeMessageProcessingHook hook : beforeMessageProcessingHooks) {
            hook.hook(this, msg);
        }
        msg = beforeMessageProcessing(msg);
        if (msg == null) {
            return; //DUMPING MESSAGE..
        }
//        Method mtd = msgToMethod.get(msg.getName());
//        if (mtd == null) {
//            throw new UnsupportedMessageException("no method to handle message: '" + msg.getName() + "' was found (use @WhenReceived on PUBLIC functions only)");
//        }
        try {
            mtda.invoke(this, msgToMtdIdx.get(msg.getName()), msg.getArgs());
            //mtd.invoke(this, msg.getArgs());
            return;
        } catch (IllegalArgumentException e) {
            throw new UnsupportedMessageException("wrong usage of message " + msg.getName() + " either it not exists or wrong parameters was passed to it");
        } catch (Exception e) {
            throw new InternalErrorException("internal error while processing message: '" + msg.getName() + "' in agent " + getId() + ": " + e.getMessage() + " (see cause)", e);
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
