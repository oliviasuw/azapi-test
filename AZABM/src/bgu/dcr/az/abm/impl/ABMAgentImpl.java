/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.impl;

import bgu.dcr.az.abm.api.ABMAgent;
import bgu.dcr.az.abm.api.AgentData;
import bgu.dcr.az.abm.api.Behavior;
import bgu.dcr.az.execs.AbstractProc;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author bennyl
 */
public class ABMAgentImpl extends AbstractProc implements ABMAgent {

    private Map<Class<? extends Behavior>, Behavior> behaviors;
    private Iterator<Behavior> handlingBehaviors;
    private Map<Class, AgentData> data;

    public ABMAgentImpl(int pid) {
        super(pid);

        behaviors = new LinkedHashMap<>();
        data = new IdentityHashMap<>();
    }

    @Override
    protected void start() {
        handlingBehaviors = behaviors.values().iterator();
    }

    @Override
    protected void quota() {
        if (!handlingBehaviors.hasNext()) {
            sleep();
        } else {
            handlingBehaviors.next().behave();
        }
    }

    @Override
    public void registerBehavior(Behavior behavior) {
        if (behavior == null) {
            throw new UnsupportedOperationException("cannot add null behavior to an agent");
        }

        behaviors.put(behavior.getClass(), behavior);
    }

    @Override
    public void unregisterBehavior(Class<? extends Behavior> behavior) {
        behaviors.remove(behavior);
    }

    @Override
    protected void onIdleDetected() {
        if (!behaviors.isEmpty()) {
            handlingBehaviors = behaviors.values().iterator();
            wakeup(pid());
        }
    }

    @Override
    public int getId() {
        return pid();
    }

    @Override
    public boolean hasDataOfType(Class<? extends AgentData> type) {
        return this.data.containsKey(type);
    }

    @Override
    public Collection<AgentData> data() {
        return data.values();
    }

    @Override
    public void addData(AgentData data) {
        if (data == null) {
            throw new UnsupportedOperationException("cannot add null data to an agent");
        }

        this.data.put(data.getClass(), data);
    }

    @Override
    public Collection<Class<? extends Behavior>> registeredBehaviors() {
        return behaviors.keySet();
    }

}
