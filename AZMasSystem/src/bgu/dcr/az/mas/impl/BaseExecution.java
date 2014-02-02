/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl;

import bgu.dcr.az.anop.utils.EventListeners;
import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.api.exen.ExecutionResult;
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
import bgu.dcr.az.mas.ExecutionEnvironment;
import bgu.dcr.az.mas.Hooks;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public abstract class BaseExecution<SOLUTION_TYPE extends DeepCopyable> implements Execution, HookProvider {

    private final Map<Class, HookProvider> hookProviders = new HashMap<>();
    private final Map<Class<? extends ExecutionService>, ExecutionServiceWithInitializationData> services = new HashMap<>();

    private final ExecutionEnvironment env;
    private final EventListeners<Hooks.TerminationHook> terminationHooks = EventListeners.create(Hooks.TerminationHook.class);

    public BaseExecution(AgentDistributer distributer, AgentSpawner spawner, ExecutionEnvironment env) {
        supply(AgentDistributer.class, distributer);
        supply(AgentSpawner.class, spawner);
        this.env = env;

        registerHookProvider(Hooks.TerminationHook.class, this);
    }

    protected void registerHookProvider(Class c, HookProvider provider) {
        hookProviders.put(c, provider);
    }

    protected abstract SOLUTION_TYPE getSolution();

    @Override
    public ExecutionResult execute(Scheduler sched, int numCores) throws ExperimentExecutionException, InterruptedException {
        ThreadSafeProcTable table = new ThreadSafeProcTable();
        supply(MessageRouter.class, new BaseMessageRouter());

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

        TerminationReason terminationResult = sched.schedule(table, numCores);

        ExecutionResult<SOLUTION_TYPE> result = new ExecutionResult<>();
        if (terminationResult.isError()) {
            result.toCrushState(terminationResult.getErrorDescription());
        } else {
            result.toSucceefulState(getSolution());
        }

        terminationHooks.fire().hook(this, result);

        System.out.println("Contention: " + sched.getContention());
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
    public final void supply(Class<? extends ExecutionService> serviceKey, ExecutionService service) {
        services.put(serviceKey, new ExecutionServiceWithInitializationData(service));
    }

    protected abstract Collection<AgentController> createControllers() throws InitializationException;

    protected abstract void initialize() throws InitializationException;

    @Override
    public ExecutionEnvironment getEnvironment() {
        return env;
    }

    @Override
    public void register(Object hook) {
        terminationHooks.add((Hooks.TerminationHook) hook);
    }

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
