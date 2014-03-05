/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.exen;

import bgu.dcr.az.abm.api.ABMAgent;
import bgu.dcr.az.abm.api.Behavior;
import bgu.dcr.az.abm.api.World;
import bgu.dcr.az.abm.exen.info.AgentDataChangedInfo;
import bgu.dcr.az.abm.exen.info.ExistingAgentsChangedInfo;
import bgu.dcr.az.abm.exen.info.BehaviorsChangedInfo;
import bgu.dcr.az.abm.exen.info.TickInfo;
import bgu.dcr.az.abm.impl.AbstractBehavior;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionService;
import bgu.dcr.az.mas.impl.InitializationException;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Eran
 */
public class BehaviorDistributer implements ExecutionService<World> {

    private World world;
    private Map<Class, Collection<ABMAgent>> agentsWithBehaviorMap;
    private List<Runnable> commands;

    @Override
    public void initialize(Execution<World> ex) throws InitializationException {
        this.agentsWithBehaviorMap = new IdentityHashMap<>();
        this.world = ex.data();
        this.world.agents().forEach(this::handleAgentAdded);
        this.commands = new LinkedList<>();

        //when the collection of agents changed
        ex.informationStream().listen(ExistingAgentsChangedInfo.class, achanged -> {
            ABMAgent agent = world.findAgent(achanged.getAgentId());
            switch (achanged.getChangeType()) {
                case ADD:
                    handleAgentAdded(agent);
                    break;
                case REMOVE:
                    handleAgentRemoved(agent);
                    break;
                default:
                    throw new AssertionError(achanged.getChangeType().name());

            }
        });

        //when behaviors are added or removed from the world
        ex.informationStream().listen(BehaviorsChangedInfo.class, bchanged -> {
            switch (bchanged.getChangeType()) {
                case ADD:
                    commands.add(() -> handleBehaviorAdded(bchanged.getChangedBehavior()));
                    break;
                case REMOVE:
                    commands.add(() -> handleBehaviorRemoved(bchanged.getChangedBehavior()));
                    break;
                default:
                    throw new AssertionError(bchanged.getChangeType().name());

            }
        });

        //when data of an agent changed
        ex.informationStream().listen(AgentDataChangedInfo.class, adchanged -> {
            ABMAgent agent = world.findAgent(adchanged.getAgentId());
            switch (adchanged.getChangeType()) {
                case ADD:
                    commands.add(() -> handleAgentDataAdded(agent, adchanged.getDataClass()));
                    break;
                case REMOVE:
                    commands.add(() -> handleAgentDataRemoved(agent, adchanged.getDataClass()));
                    break;
                default:
                    throw new AssertionError(adchanged.getChangeType().name());

            }

        });

        //when new tick received
        ex.informationStream().listen(TickInfo.class, tinofo -> {
            commands.forEach(Runnable::run);
            commands.clear();
        });

    }

    private void handleAgentAdded(ABMAgent addedAgent) {
        for (Class<? extends Behavior> b : world.behaviors()) {
            tryRegisterBehavior(b, addedAgent);
        }
    }

    private boolean tryRegisterBehavior(Class<? extends Behavior> b, ABMAgent addedAgent) {
        AbstractBehavior.Requirements req = AbstractBehavior.retreiveRequirements(b);

        //check that all requirements are met
        for (Class r : req.getAgentRequirements()) {
            if (!addedAgent.hasDataOfType(r)) {
                return false;
            }
        }

        //register to agent
        addedAgent.registerBehavior(AbstractBehavior.create(b, addedAgent, world));

        //register internally
        Collection<ABMAgent> l = agentsWithBehaviorMap.get(b);
        if (l == null) {
            l = new HashSet<>();
            agentsWithBehaviorMap.put(b, l);
        }
        l.add(addedAgent);

        return true;
    }

    private void handleAgentRemoved(ABMAgent removedAgent) {
        agentsWithBehaviorMap.values().forEach(l -> l.remove(removedAgent));
    }

    private void handleBehaviorAdded(Class<? extends Behavior> changedBehavior) {
        for (ABMAgent a : world.agents()) {
            tryRegisterBehavior(changedBehavior, a);
        }
    }

    private void handleBehaviorRemoved(Class<? extends Behavior> changedBehavior) {
        Collection<ABMAgent> agents = agentsWithBehaviorMap.remove(changedBehavior);
        for (ABMAgent a : agents) {
            a.unregisterBehavior(changedBehavior);
        }
    }

    private void handleAgentDataAdded(ABMAgent agent, Class dataClass) {
        world.behaviors().stream()
                .filter(b -> AbstractBehavior.retreiveRequirements(b).getAgentRequirements().contains(dataClass)).forEach(b -> {
                    tryRegisterBehavior(b, agent);
                });
    }

    private void handleAgentDataRemoved(ABMAgent agent, Class dataClass) {
        List<Class<? extends Behavior>> removeList = agent.registeredBehaviors().stream().filter(b -> AbstractBehavior.retreiveRequirements(b).getAgentRequirements().contains(dataClass)).collect(Collectors.toList());
        agent.unregisterBehaviors(removeList);
    }

}
