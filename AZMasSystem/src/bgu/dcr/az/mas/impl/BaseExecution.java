/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl;

import bgu.dcr.az.execs.ThreadSafeProcTable;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.execs.api.TerminationReason;
import bgu.dcr.az.mas.AgentController;
import bgu.dcr.az.mas.AgentDistributer;
import bgu.dcr.az.mas.AgentSpawner;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionService;
import bgu.dcr.az.mas.UnmetRequirementException;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
import bgu.dcr.az.mas.HookProvider;
import bgu.dcr.az.mas.MessageRouter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public abstract class BaseExecution implements Execution {

    private final Map<Class, HookProvider> hookProviders = new HashMap<>();
    private final Map<Class<? extends ExecutionService>, ExecutionServiceWithInitializationData> services = new HashMap<>();

    private final Scheduler scheduler;
    private final int numCores;

    public BaseExecution(Scheduler scheduler, AgentDistributer distributer, AgentSpawner spawner, int numCores) {
        this.numCores = numCores;
        this.scheduler = scheduler;
        put(AgentDistributer.class, distributer);
        put(AgentSpawner.class, spawner);
    }

    protected void registerHookProvider(Class c, HookProvider provider) {
        hookProviders.put(c, provider);
    }

    @Override
    public TerminationReason execute() throws ExperimentExecutionException, InterruptedException {
        ThreadSafeProcTable table = new ThreadSafeProcTable();
        put(MessageRouter.class, new BaseMessageRouter(table));

        try {

            initialize();

            //initialize rest of services
            for (ExecutionServiceWithInitializationData service : services.values()) {
                service.initialize();
            }

            //create agentcontrollers
            Collection<AgentController> controllers = createControllers();
            for (AgentController c : controllers) {
                table.add(c);
            }
        } catch (InitializationException ex) {
            throw new ExperimentExecutionException("error on experiment initialization, see cause", ex);
        }
        
        TerminationReason result = scheduler.schedule(table, numCores);
        System.out.println("Contention: " + scheduler.getContention());
        return result;
    }

    @Override
    public void hook(Class hookType, Object hook) {
        HookProvider provider = hookProviders.get(hookType);
        if (provider != null) {
            provider.register(hook);
        }
    }

    @Override
    public <T extends ExecutionService> T require(Class<T> service) throws InitializationException {
        ExecutionServiceWithInitializationData result = services.get(service);

        if (result == null) {
            throw new UnmetRequirementException("requrement for " + service.getCanonicalName() + " is unmet");
        }

        result.initialize();
        return (T) result.service;
    }

    @Override
    public final void put(Class<? extends ExecutionService> serviceKey, ExecutionService service) {
        services.put(serviceKey, new ExecutionServiceWithInitializationData(service));
    }

    protected abstract Collection<AgentController> createControllers() throws InitializationException;

    protected abstract void initialize() throws InitializationException;

    private enum InitializatinState {

        INITIALIZED, INITIALIZING, UNINITIALIZED;
    }

    private class ExecutionServiceWithInitializationData {

        ExecutionService service;
        InitializatinState initializatinState;

        public ExecutionServiceWithInitializationData(ExecutionService service) {
            this.service = service;
            this.initializatinState = InitializatinState.UNINITIALIZED;
        }

        public void initialize() throws InitializationException {
            switch (initializatinState) {
                case INITIALIZED:
                    break;
                case INITIALIZING:
                    throw new InitializationException("circular dependency found: " + collectInitializingServices());
                case UNINITIALIZED:
                    this.initializatinState = InitializatinState.INITIALIZING;
                    this.service.initialize(BaseExecution.this);
                    this.initializatinState = InitializatinState.INITIALIZED;
                    break;
                default:
                    throw new AssertionError(initializatinState.name());
            }
        }

        private String collectInitializingServices() {
            StringBuilder sb = new StringBuilder();
            for (ExecutionServiceWithInitializationData s : services.values()) {
                if (s.initializatinState == InitializatinState.INITIALIZING) {
                    sb.append("[").append(s.service.getClass().getSimpleName()).append("]");
                }
            }

            return sb.toString();
        }
    }

}
