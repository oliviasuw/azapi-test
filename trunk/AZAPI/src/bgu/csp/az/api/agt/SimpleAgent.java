package bgu.csp.az.api.agt;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.Hooks.BeforeMessageProcessingHook;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.infra.Execution;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import bgu.csp.az.api.exp.InternalErrorException;
import bgu.csp.az.api.exp.UnsupportedMessageException;
import bgu.csp.az.utils.DeepCopyUtil;

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
    public Message processNextMessage() throws InterruptedException {
        Message msg = nextMessage();
        Message msgcpy;

        msgcpy = DeepCopyUtil.deepCopy(msg);//msg.copy();
        SimpleMessage smsg = (SimpleMessage) msg;

        for (BeforeMessageProcessingHook hook : beforeMessageProcessingHooks) {
            hook.hook(smsg);
        }
        smsg = beforeMessageProcessing(smsg);
        if (smsg == null) {
            return msgcpy; //DUMPING MESSAGE..
        }
        Method mtd = msgToMethod.get(smsg.getName());
        if (mtd == null) {
            throw new UnsupportedMessageException("no method to handle message: '" + smsg.getName() + "' was found (use @WhenReceived on PUBLIC functions only)");
        }
        try {
            mtd.invoke(this, smsg.getArgs());
            return msgcpy;
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            throw new UnsupportedMessageException("wrong parameters passed with the message " + smsg.getName());
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            throw new InternalErrorException("internal error while processing message: '" + smsg.getName() + "' in agent " + getId(), e);
        } catch (InvocationTargetException e) {
            //e.printStackTrace();
            throw new InternalErrorException("internal error while processing message: '" + smsg.getName() + "' in agent " + getId() + ": " + e.getCause().getMessage() + " (see cause)", e.getCause());
        }
    }

    /**
     * sends a new message 
     * the message should have a name and any number of arguments
     * the message will be sent received by an agent in the method that 
     * defines @WhenReceived with the name of the message (case sensitive!)
     * and the arguments will be inserted to the parameters of that method
     * 
     * usage: send("MESSAGE_NAME", ARG1, ARG2, ..., ARGn).to(OTHER_AGENT_ID)
     * 
     * @param msg the message name
     * @param args the list (variadic) of arguments that belongs to this message
     * @return continuation class 
     */
    public SendbleObject send(String msg, Object... args) {
        final Execution execution = PlatformOperationsExtractor.extract(this).getExecution();
        return new SendbleObject(createMessage(msg, args), execution.getMailer(), execution.getGlobalProblem());
    }

    /**
     * you can override this method to perform preprocessing before messages arrive to their functions
     * you can change the message or even return completly other one - if you will return null 
     * the message is rejected and dumped.
     * @param msg
     * @return 
     */
    protected SimpleMessage beforeMessageProcessing(SimpleMessage msg) {
        if (msg.isFlaged(Message.DISCARDED)) {
            return null;
        }
        return msg;
    }
    
    /**
     * this function called when a SYS_TERMINATION Message Arrived -> it just calls finish on the agent, 
     * you can override it to make your own termination handling.
     */
    @WhenReceived(Agent.SYS_TERMINATION_MESSAGE)
    public void handleTermination(){
        finish();
    }


}
