/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.impl;

import bgu.dcr.az.abm.api.ABMAgent;
import bgu.dcr.az.abm.api.Behavior;
import bgu.dcr.az.execs.AbstractProc;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class ABMAgentImpl extends AbstractProc implements ABMAgent {

    private Set<Behavior> behaviors;
    private Iterator<Behavior> handlingBehaviors;

    public ABMAgentImpl(int pid) {
        super(pid);

        behaviors = new LinkedHashSet<>();
    }

    @Override
    protected void start() {
        handlingBehaviors = behaviors.iterator();
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
        behaviors.add(behavior);
    }

    @Override
    public void unregisterBehavior(Behavior behavior) {
        behaviors.remove(behavior);
    }

    @Override
    protected void onIdleDetected() {
        if (!behaviors.isEmpty()) {
            handlingBehaviors = behaviors.iterator();
            wakeup(pid());
        }
    }

    @Override
    public int getId() {
        return pid();
    }

}
