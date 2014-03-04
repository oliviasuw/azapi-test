/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.impl;

import bgu.dcr.az.abm.api.ABMAgent;
import bgu.dcr.az.abm.api.Behavior;
import bgu.dcr.az.abm.api.Service;
import bgu.dcr.az.abm.api.World;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author bennyl
 */
public abstract class AbstractBehavior implements Behavior {

    private static ConcurrentHashMap<Class, Requirements> cachedRequirements;

    private ABMAgent agent;

    public AbstractBehavior() {
    }

    protected ABMAgent agent() {
        return agent;
    }

    @Override
    public void init(ABMAgent agent, World w) {
        this.agent = agent;
    }

    @Override
    public Collection<Class> getAgentRequirements() {
        return retreiveRequirements().agentRequirements;
    }

    @Override
    public Collection<Class> getWorldRequirements() {
        return retreiveRequirements().worldRequirements;
    }

    private Requirements retreiveRequirements() {
        Requirements r = cachedRequirements.get(getClass());
        if (r == null) {
            r = new Requirements(getClass());
            r = cachedRequirements.putIfAbsent(getClass(), r);
        }

        return r;
    }

    private static final class Requirements {

        public Collection<Class> agentRequirements;
        public Collection<Class> worldRequirements;

        public Requirements(Class c) {
            agentRequirements = new LinkedList<>();
            worldRequirements = new LinkedList<>();

            Arrays.stream(c.getDeclaredFields()).filter(f -> f.getAnnotation(Require.class) != null).map(f -> {
                f.setAccessible(true);
                return f;
            }).forEach(field -> {
                if (Service.class.isAssignableFrom(field.getType())) {
                    this.worldRequirements.add(c);
                } else {
                    this.agentRequirements.add(c);
                }
            });

        }

    }

}
