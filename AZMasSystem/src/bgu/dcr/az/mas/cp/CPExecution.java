/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

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
public class CPExecution extends BaseExecution {

    private final Solution solution;
    private final Problem problem;

    public CPExecution(Scheduler scheduler, AgentSpawner spawner, Problem problem, int numCores) {
        super(scheduler, problem.getAgentDistributer(), spawner, numCores);
        
        this.problem = problem;
        this.solution = new Solution(problem);
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

    public Solution getSolution() {
        return solution;
    }

    @Override
    protected void initialize() {
        //???
    }

    Problem getGlobalProblem() {
        return problem;
    }

}
