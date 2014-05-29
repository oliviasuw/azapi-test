/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.exe;

import bgu.dcr.az.execs.sim.Agent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public interface SimulationConfiguration {

    int numMachines();

    int numAgents();

    Class<? extends Agent> agentClass(int id);

    int[] agentsInMachine(int mid);

    ExecutionEnvironment env();

    Map<String, String> agentInitializationArgs(int id);

    BaseStatisticFields baseStatisticFields();
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements SimulationConfiguration {

        private int numMachines = 0;
        private int numAgents = 0;
        private Class allAgentsClass = Agent.class;
        private Map<Integer, Class> specificAgentClass = null;
        private Map<Integer, int[]> agentsInMachine = null;
        private ExecutionEnvironment env = ExecutionEnvironment.async;
        private Map<Integer, Map<String, String>> agentsInitializationArgs = null;
        private Map<String, String> allAgentsInitializationArgs = null;
        private BaseStatisticFields record;

        @Override
        public int numMachines() {
            return numMachines;
        }

        public Builder numMachines(int numMachines) {
            this.numMachines = numMachines;
            return this;
        }

        @Override
        public int numAgents() {
            return numAgents;
        }

        public Builder numAgents(int n) {
            numAgents = n;
            return this;
        }

        @Override
        public Class<? extends Agent> agentClass(int id) {
            if (specificAgentClass == null) {
                return allAgentsClass;
            }
            return specificAgentClass.get(id);
        }

        public Builder withGlobalInitializationArgs(Map<String, String> args) {
            agentsInitializationArgs = null;
            allAgentsInitializationArgs = args;
            return this;
        }

        public Builder withInitializationArgs(int agent, Map<String, String> args) {
            allAgentsInitializationArgs = null;
            if (agentsInitializationArgs == null) {
                agentsInitializationArgs = new HashMap<>();
            }

            agentsInitializationArgs.put(agent, args);
            return this;
        }

        @Override
        public int[] agentsInMachine(int mid) {
            if (agentsInMachine == null) {
                return new int[]{mid};
            }

            return agentsInMachine.get(mid);
        }

        public Builder withAgentsInMachine(int machine, int... agents) {
            if (agentsInMachine == null) {
                agentsInMachine = new HashMap<>();
            }
            agentsInMachine.put(machine, agents);
            return this;
        }

        @Override
        public ExecutionEnvironment env() {
            return env;
        }

        public Builder env(ExecutionEnvironment env) {
            this.env = env;
            return this;
        }

        @Override
        public Map<String, String> agentInitializationArgs(int id) {
            if (agentsInitializationArgs == null) {
                if (allAgentsInitializationArgs == null) {
                    return Collections.EMPTY_MAP;
                }

                return allAgentsInitializationArgs;
            }

            return agentsInitializationArgs.get(id);
        }

        public SimulationConfiguration build() {
            return this;
        }

        public Builder withAllAgentsOfClass(Class c) {
            allAgentsClass = c;
            specificAgentClass = null;
            return this;
        }

        public Builder withAgentOfClass(int id, Class c) {
            allAgentsClass = null;
            if (specificAgentClass == null) {
                specificAgentClass = new HashMap<>();
            }
            specificAgentClass.put(id, c);
            return this;
        }

        public Builder withEnvironment(ExecutionEnvironment env) {
            return env(env);
        }

        public Builder withBaseStatisticFields(BaseStatisticFields record){
            this.record = record;
            return this;
        }
        
        @Override
        public BaseStatisticFields baseStatisticFields() {
            return record;
        }

    }

}
