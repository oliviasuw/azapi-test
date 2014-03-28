/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.distributers;

import bgu.dcr.az.dcr.api.modules.AgentDistributer;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.exceptions.InitializationException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Zovadi
 */
public class OneToManyDistributor implements AgentDistributer {

    private final int numberOfAgents;
    private final int numberOfVars;
    private final int[][] controlledVars;

    public OneToManyDistributor(int numberOfAgents, int numberOfVars) {
        this.numberOfAgents = numberOfAgents;
        this.numberOfVars = numberOfVars;
        this.controlledVars = new int[numberOfAgents][];

        if (numberOfVars == numberOfVars) {
            for (int i = 0; i < controlledVars.length; i++) {
                controlledVars[i] = new int[]{i};
            }
        }
    }

    /**
     *
     * @param controllerId
     * @return returns the set of variables owned by a given agent
     */
    @Override
    public int[] getControlledAgentsIds(int controllerId) {
        return controlledVars[controllerId];
    }

    /**
     * Changes the allocation of variables to the agents. After performing this
     * operation the agent with given id will own given set of variables.
     * IndexOutOfBoundsException exception will be thrown in the case that
     * illegal agent id provided (agent id is grater/equal to number of agents).
     *
     * @param agentId
     * @param variableIds
     */
    public void assignVariablesToAgent(int agentId, int... variableIds) {
        if (agentId >= numberOfAgents) {
            throw new IndexOutOfBoundsException("Agent id must be less than number of agents");
        }
        controlledVars[agentId] = variableIds;
    }

    /**
     * Must be called after all variable to agent allocations are performed.
     * Will ensure the legal allocation: -every agents owns at least one
     * variable -every variable must be owned by a single agent -every variable
     * must be owned by an agent In the case that current variables allocation
     * does not meet the legality criteria InitializationException exception
     * will be thrown.
     *
     * @param ex
     * @throws InitializationException
     */
    @Override
    public void initialize(Execution ex) throws InitializationException {
//        System.out.println("Checking distribution correctness");
        Set<Integer> variables = new HashSet<>();

        for (int i = 0; i < controlledVars.length; i++) {
            if (controlledVars[i] == null) {
                throw new InitializationException("The agent ids must be sequential and start from zero");
            }
            if (controlledVars[i].length == 0) {
                throw new InitializationException("Every agent must controll at least one variable");
            }
            for (int j = 0; j < controlledVars[i].length; j++) {
                if (variables.contains(controlledVars[i][j])) {
                    throw new InitializationException("Every variable can be controlled only by one agent");
                }
                variables.add(controlledVars[i][j]);
            }
        }

        if (variables.size() != numberOfVars) {
            throw new InitializationException("Every variable must be controlled by an agent");
        }
        
        for (int i = 0; i < numberOfVars; i++) {
            if (!variables.contains(i)) {
                throw new InitializationException("Every variable must be controlled by an agent");
            }
        }
    }

    @Override
    public int getNumberOfAgents() {
        return numberOfVars;
    }

    @Override
    public int getNumberOfAgentControllers() {
        return numberOfAgents;
    }
}
