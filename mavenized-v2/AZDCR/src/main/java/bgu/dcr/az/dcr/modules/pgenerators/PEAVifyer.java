/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.pgenerators;

import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.execs.sim.Agt0DSL;
import bgu.dcr.az.dcr.api.modules.ProblemGenerator;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.api.problems.ProblemType;
import bgu.dcr.az.dcr.api.problems.constraints.BinaryConstraint;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author User
 */
@Register("peavify")
public class PEAVifyer extends AbstractProblemGenerator {

    ProblemGenerator base;

    @Override
    public void generate(Problem p, Random rand) {
        if (base == null) {
            panic("base problem is not provided, see tutorial for more details.");
        }

        Problem basep = new Problem();
        base.generate(basep, rand);
        
        int[][] distributions = new int[basep.getNumberOfAgents()][];
        List<Set<Integer>> domains = new ArrayList<>();

        //first calculate the number of agents and map to the peav agents
        BiMap<IntIntPair, Integer> variableMapping = HashBiMap.create();
        int numVariables = 0;
        
        for (int i = 0; i < basep.getNumberOfVariables(); i++) {
            distributions[i] = new int[basep.getNeighbors(i).size() + 1];
            distributions[i][basep.getNeighbors(i).size()] = numVariables;
            domains.add(basep.getDomainOf(i));
            variableMapping.put(new IntIntPair(i, i), numVariables++);
        }
        
        for (int i = 0; i < basep.getNumberOfVariables(); i++) {
            int j = 0;
            for (Integer n : basep.getNeighbors(i)) {
                distributions[i][j++] = numVariables;
                variableMapping.put(new IntIntPair(i, n), numVariables++);
                domains.add(basep.getDomainOf(Math.min(i, n)));
            }
        }

        //then initialize the problem based on this calculation
//        p.initialize(ProblemType.DCOP, numVariables, basep.getDomainSize(0));
        p.initialize(ProblemType.DCOP, domains, basep.getNumberOfAgents());
        
        for (int i = 0; i < basep.getNumberOfAgents(); i++) {
            Integer aid = variableMapping.get(new IntIntPair(i, i));
            p.assignVariablesToAgent(aid, distributions[i]);
        }

        //create equility constraints
        EquilityConstraint eq = new EquilityConstraint();
        for (Map.Entry<IntIntPair, Integer> v : variableMapping.entrySet()) {
            final IntIntPair pair = v.getKey();
            if (pair.a != pair.b) {
                Integer variable = v.getValue();
                Integer mirror = variableMapping.get(pair.reverse());

                System.out.println("Constraint between: " + variable + " and " + mirror + " and reverse");

                p.setConstraint(variable, mirror, eq);
                p.setConstraint(mirror, variable, eq);
            }
        }

        //restore original constraints
        for (int i = 0; i < basep.getNumberOfVariables(); i++) {
            Integer meVar = variableMapping.get(new IntIntPair(i, i));
            DelegativeConstraint constraint = new DelegativeConstraint(basep, variableMapping, i);
            for (Integer n : basep.getNeighbors(i)) {
                Integer heVar = variableMapping.get(new IntIntPair(i, n));
                p.setConstraint(meVar, heVar, constraint);
                p.setConstraint(heVar, meVar, constraint);
                System.out.println("Constraint between: " + meVar + " and " + heVar);
            }
        }
    }

    /**
     * @propertyName base
     * @return
     */
    public ProblemGenerator getBase() {
        return base;
    }

    public void setBase(ProblemGenerator base) {
        this.base = base;
    }

    public static class EquilityConstraint implements BinaryConstraint {
        
        @Override
        public int cost(int i, int vi, int j, int vj) {
            if (vi == vj) {
                return 0;
            }
            return Agt0DSL.INFINITY_COST;
        }

    }

    public static class DelegativeConstraint implements BinaryConstraint {

        Problem delegate;
        BiMap<IntIntPair, Integer> variableMapping;
        int owner;

        public DelegativeConstraint(Problem delegate, BiMap<IntIntPair, Integer> variableMapping, int owner) {
            this.delegate = delegate;
            this.variableMapping = variableMapping;
            this.owner = owner;
        }

        @Override
        public int cost(int i, int vi, int j, int vj) {
            IntIntPair original1 = variableMapping.inverse().get(i);
            IntIntPair original2 = variableMapping.inverse().get(j);

            if (original1.a == original1.b) {
                IntIntPair temp = original1;
                original1 = original2;
                original2 = temp;
            }else if (original2.a != original2.b){
                panic("checking unconstrainted agents!");
            }

            return delegate.getConstraintCost(owner, original1.a, vi, original1.b, vj);
        }

    }

    public static class IntIntPair {

        int a, b;

        public IntIntPair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public IntIntPair reverse() {
            return new IntIntPair(b, a);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + this.a;
            hash = 41 * hash + this.b;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IntIntPair other = (IntIntPair) obj;
            if (this.a != other.a) {
                return false;
            }
            if (this.b != other.b) {
                return false;
            }
            return true;
        }

    }

}
