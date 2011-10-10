/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.frm;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Agent.PlatformOps;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.Message;
import bgu.csp.az.impl.ds.MailRegistery;
import bgu.csp.az.api.exp.UnRegisteredAgentException;
import java.util.HashMap;

/**
 *
 * @author bennyl
 */
public class TestMailer implements Mailer {

    public static final String RECEPIENT_MESSAGE_METADATA = "__RECIPIENT__";
    public static final String MESSAGE_ID_METADATA = "__MESSAGE_ID__";
    private MailRegistery registery;
    private final HashMap<Integer, Object> waitingPoll;
    private TestExecution exec;

    public TestMailer(TestExecution exec) {
        registery = new MailRegistery();
        waitingPoll = new HashMap<Integer, Object>();
        this.exec = exec;
    }

    @Override
    public void register(Agent agent) {
        registery.register(agent);
        exec.fire("mailer-agent-registered", "id", "" + agent.getId());

        synchronized (waitingPoll) {
            if (waitingPoll.containsKey(agent.getId())) {
                Object bed = waitingPoll.get(agent.getId());
                synchronized (bed) {
                    bed.notifyAll();
                }
                waitingPoll.remove(agent.getId());
            }
        }
    }

    @Override
    public void unregisterAll() {
        registery.clear();
    }

    @Override
    public void send(Message msg, int to) {
        Agent a = registery.get(to);
        if (a == null) {
            throw new UnRegisteredAgentException("agent " + msg.getSender() + " tries to send a message: " + msg.getName() + " to unregistered agent - '" + to + "'");
        }

        Message mcopy = msg.copy();

        if (TestExpirement.USE_SCENARIO_LOGGER) {
            long mid = exec.getLogger().logMessageSent(msg.getSender(), to, mcopy);
            mcopy.getMetadata().put(MESSAGE_ID_METADATA, mid);
        }

        mcopy.getMetadata().put(RECEPIENT_MESSAGE_METADATA, to);

        PlatformOps apops = Agent.PlatformOperationsExtractor.extract(a);
        apops.receive(mcopy);
    }

    @Override
    public void broadcast(Message msg) {
        registery.startRead();
        for (Agent a : registery.all()) {
            if (a.getId() != msg.getSender()) {
                send(msg, a.getId());
            }
        }
        registery.doneRead();
    }

    @Override
    public void unRegister(Integer id) {
        registery.unRegister(id);
    }

    @Override
    public boolean isRegistered(Integer id) {
        return registery.get(id) != null;
    }

    @Override
    public void waitFor(int id) throws InterruptedException {
        Object bed = null;

        synchronized (waitingPoll) {
            if (isRegistered(id)) {
                return;
            }

            bed = waitingPoll.get(id);
            if (bed == null) {
                bed = new Object();
                waitingPoll.put(id, bed);
            }
        }

        synchronized (bed) {
            while (!isRegistered(id)) {
                bed.wait();
            }
        }
    }

    @Override
    public boolean isAllMailBoxesAreEmpty() {
        for (Agent a : registery.all()) {
            if (a.hasPendingMessages()) {
                return false;
            }
        }

        return true;
    }
}
