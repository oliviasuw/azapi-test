/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.pgenerators;

import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.api.modules.ProblemGenerator;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.api.problems.ProblemType;
import bgu.dcr.az.dcr.api.problems.constraints.BinaryConstraint;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author User
 */
@Register("peav-problem")
public class PEAVifyer extends AbstractProblemGenerator {

    ProblemGenerator base;

    @Override
    public void generate(Problem p, Random rand) {
        if (base == null) {
            panic("base problem is not provided, see tutorial for more details.");
        }

        Problem temp = new Problem();
        base.generate(temp, rand);

        //first calculate the number of agents and map to the peav agents
        Map<IntIntPair, Integer> variableMapping = new HashMap<>();
        int numAgents = 0;
        for (int i = 0; i < temp.getNumberOfVariables(); i++) {
            variableMapping.put(new IntIntPair(i, i), numAgents++);
            for (Integer n : temp.getNeighbors(i)) {
                variableMapping.put(new IntIntPair(i, n), numAgents++);
            }
        }

        //then initialize the problem based on this calculation
        p.initialize(ProblemType.DCOP, numAgents, temp.getDomainSize(0));

        //create equility constraints
        EquilityConstraint eq = new EquilityConstraint();
        for (Map.Entry<IntIntPair, Integer> v : variableMapping.entrySet()) {
            final IntIntPair pair = v.getKey();
            if (pair.a != pair.b) {
                Integer variable = v.getValue();
                Integer mirror = variableMapping.get(pair.reverse());

                p.setConstraint(variable, mirror, eq);
            }
        }

        //restore original constraints
        for (int i = 0; i < temp.getNumberOfVariables(); i++) {
            Integer meVar = variableMapping.get(new IntIntPair(i, i));
            DelegativeConstraint constraint = new DelegativeConstraint(temp, i);
            for (Integer n : temp.getNeighbors(i)) {
                Integer heVar = variableMapping.get(new IntIntPair(i, n));
                p.setConstraint(meVar, heVar, constraint);
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

            return Integer.MAX_VALUE;
        }

    }

    public static class DelegativeConstraint implements BinaryConstraint {

        Problem delegate;
        int owner;

        public DelegativeConstraint(Problem delegate, int owner) {
            this.delegate = delegate;
            this.owner = owner;
        }

        @Override
        public int cost(int i, int vi, int j, int vj) {
            return delegate.getConstraintCost(owner, i, vi, j, vj);
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
