/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.mas.ExecutionEnvironment;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.mas.AgentController;
import bgu.dcr.az.mas.AgentDistributer;
import bgu.dcr.az.mas.AgentSpawner;
import bgu.dcr.az.mas.impl.BaseExecution;
import bgu.dcr.az.mas.impl.InitializationException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author User
 */
public class CPExecution extends BaseExecution<CPSolution> {

    private final CPSolution solution;
    private final Problem problem;

    public CPExecution(Scheduler scheduler, AgentSpawner spawner, Problem problem, ExecutionEnvironment env, int numCores) {
        super(scheduler, problem.getAgentDistributer(), spawner, env, numCores);

        this.problem = problem;
        this.solution = new CPSolution(problem);
    }

    @Override
    protected Collection<AgentController> createControllers() throws InitializationException {
        List<AgentController> controllers = new LinkedList<>();

        AgentDistributer distributer = require(AgentDistributer.class);
        for (int i = 0; i < distributer.getNumberOfAgentControllers(); i++) {
            try {
                controllers.add(new CPAgentController(i, this));
            } catch (ClassNotFoundException | ConfigurationException ex) {
                throw new InitializationException("could not initialize agent, see cause", ex);
            }
        }

        return controllers;
    }

    @Override
    public CPSolution getSolution() {
        return solution;
    }

    public Problem getProblem() {
        return problem;
    }

    @Override
    protected void initialize() {
        //???
    }

    Problem getGlobalProblem() {
        return problem;
    }

}
