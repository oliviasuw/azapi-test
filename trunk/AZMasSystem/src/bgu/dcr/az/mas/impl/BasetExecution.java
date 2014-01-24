/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.execs.ThreadSafeProcTable;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionService;
import bgu.dcr.az.mas.UnmetRequirementException;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
import bgu.dcr.az.mas.exp.executions.AgentControllerProc;
import bgu.dcr.az.mas.exp.executions.HookProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class BasetExecution implements Execution {

    private final Map<Class, HookProvider> hookProviders = new HashMap<>();
    private final Map<Class<? extends ExecutionService>, ExecutionService> services = new HashMap<>();

    private final Collection<Agent> agents;
    private final Scheduler scheduler;

    public BasetExecution(Collection<Agent> agents, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.agents = agents;
    }

    protected void registerHookProvider(Class c, HookProvider provider) {
        hookProviders.put(c, provider);
    }

    @Override
    public void execute() throws ExperimentExecutionException, InterruptedException {
        ThreadSafeProcTable table = new ThreadSafeProcTable();

        for (Agent a : agents) {
            table.add(new AgentControllerProc(a));
        }

        scheduler.schedule(table);
    }

    @Override
    public void hook(Class hookType, Object hook) {
        HookProvider provider = hookProviders.get(hookType);
        if (provider != null) {
            provider.register(hook);
        }
    }

    @Override
    public <T> T require(Class<T> service) {
        T result = (T) services.get(service);
        if (result == null) {
            throw new UnmetRequirementException("requrement for " + service.getCanonicalName() + " is unmet");
        }
        return result;
    }

    @Override
    public void put(Class<? extends ExecutionService> serviceKey, ExecutionService service) {
        services.put(serviceKey, service);
    }

}
