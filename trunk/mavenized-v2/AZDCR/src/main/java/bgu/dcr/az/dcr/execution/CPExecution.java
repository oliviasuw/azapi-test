/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution;

import bgu.dcr.az.execs.api.experiments.ExecutionEnvironment;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.dcr.api.modules.AgentDistributer;
import bgu.dcr.az.dcr.api.modules.AgentSpawner;
import bgu.dcr.az.dcr.api.modules.Logger;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.modules.logger.StdoutLogger;
import bgu.dcr.az.execs.api.Proc;
import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.execs.experiments.BaseExecution;
import bgu.dcr.az.execs.exceptions.InitializationException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author User
 */
public class CPExecution extends BaseExecution<CPData> {

    @Override
    public ExecutionService getExecutionInfoCollector() {
        return ((CPExperimentTest) getContainingExperiment()).getInfoCollector();
    }

    public CPExecution(CPExperimentTest containingExperiment, AlgorithmDef a, double runningVariable, AgentSpawner spawner, Problem problem, ExecutionEnvironment environment) {
        super(new CPData(new CPSolution(problem), problem, a, runningVariable), containingExperiment, environment, problem.getAgentDistribution(), spawner, new BaseMessageRouter());
    }

    @Override
    protected Collection<Proc> createProcesses() throws InitializationException {
        List<Proc> controllers = new LinkedList<>();

        AgentDistributer distributer = require(AgentDistributer.class);
        for (int i = 0; i < distributer.getNumberOfAgentControllers(); i++) {
            try {
                final CPAgentController controller = new CPAgentController(i, this);
                controller.setGiveupBeforeComplete(getEnvironment() == ExecutionEnvironment.async);
                controllers.add(controller);
            } catch (ClassNotFoundException | ConfigurationException ex) {
                throw new InitializationException("could not initialize agent, see cause", ex);
            }
        }

        return controllers;
    }

    @Override
    public int numberOfAgents() {
        return data().getProblem().getNumberOfAgents();
    }

    @Override
    protected void initialize() {
        if (!hasRequirement(Logger.class)) {
            supply(Logger.class, new StdoutLogger());
        }
    }

}
