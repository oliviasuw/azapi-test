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
import com.esotericsoftware.reflectasm.ConstructorAccess;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
        return retreiveRequirements(getClass());
    }

    public static Behavior create(Class<? extends Behavior> behaviorType, ABMAgent agent, World w) {
        Behavior b = ConstructorAccess.get(behaviorType).newInstance();
        b.init(agent, w);
        return b;
    }

    public static Requirements retreiveRequirements(Class c) {
        Requirements r = cachedRequirements.get(c);
        if (r == null) {
            r = new Requirements(c);
            r = cachedRequirements.putIfAbsent(c, r);
        }

        return r;
    }

    public static final class Requirements {

        public Collection<Class> agentRequirements;
        public Collection<Class> worldRequirements;

        public Requirements(Class c) {
            agentRequirements = new HashSet<>();
            worldRequirements = new HashSet<>();

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

        public Collection<Class> getAgentRequirements() {
            return agentRequirements;
        }

        public Collection<Class> getWorldRequirements() {
            return worldRequirements;
        }

    }

}
