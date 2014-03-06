/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.impl;

import bgu.dcr.az.abm.api.ABMAgent;
import bgu.dcr.az.abm.api.AgentData;
import bgu.dcr.az.abm.api.Behavior;
import bgu.dcr.az.abm.exen.ABMExecution;
import bgu.dcr.az.abm.exen.info.TickInfo;
import bgu.dcr.az.execs.AbstractProc;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class ABMAgentImpl extends AbstractProc implements ABMAgent {

    private final Map<Class<? extends Behavior>, Behavior> behaviors;
    private Iterator<Behavior> handlingBehaviors;
    private Map<Class, AgentData> data;
    private ABMExecution execution;
    private int tickNumber = 0;
    private boolean terminated = false;

    public ABMAgentImpl(int pid) {
        super(pid);

        this.behaviors = new LinkedHashMap<>();
        this.data = new IdentityHashMap<>();
    }

    public void setExecution(ABMExecution execution) {
        this.execution = execution;
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
        if (terminated) {
            terminate();
            return;
        }

        tickNumber++;
        if (pid() == 0) {
            execution.informationStream().write(new TickInfo(tickNumber));
        }

        if (!behaviors.isEmpty()) {
            handlingBehaviors = behaviors.values().iterator();
            wakeup(pid());
        } else {
            sleep();
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

    @Override
    public void kill() {
        terminated = true;
    }

    @Override
    public void setData(AgentData... data) {
        this.data.clear();
        addAllData(data);
    }

    @Override
    public int tickNumber() {
        return tickNumber;
    }

}
