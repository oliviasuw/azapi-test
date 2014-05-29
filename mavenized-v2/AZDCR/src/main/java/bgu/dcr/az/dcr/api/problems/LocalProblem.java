/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.dcr.api.problems;

import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.util.ImmutableSet;
import java.util.HashMap;
import java.util.Set;

/**
 * this is a wrap on the given problem - each agent poses a wrap like this
 * instead of the actual problem
 */
public class LocalProblem implements ImmutableProblem {
    private int aid;
    private Problem p;
    private ConstraintCheckResult queryTemp = new ConstraintCheckResult();

    public LocalProblem(int aid, Problem p) {
        this.aid = aid;
        this.p = p;
    }

    public int getAgentId() {
        return aid;
    }

    @Override
    public int getNumberOfVariables() {
        return p.getNumberOfVariables();
    }

    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        return p.getDomainOf(var);
    }

    @Override
    public int getConstraintCost(int var1, int val1) {
        p.getConstraintCost(getAgentId(), var1, val1, queryTemp);
        return queryTemp.getCost();
    }

    @Override
    public int getConstraintCost(int var1, int val1, int var2, int val2) {
        p.getConstraintCost(getAgentId(), var1, val1, var2, val2, queryTemp);
        return queryTemp.getCost();
    }

    @Override
    public String toString() {
        return p.toString();
    }

    @Override
    public int getDomainSize(int var) {
        return p.getDomainSize(var);
    }

    @Override
    public HashMap<String, Object> getMetadata() {
        return p.getMetadata();
    }

    @Override
    public Set<Integer> getNeighbors(int var) {
        return p.getNeighbors(var);
    }

    @Override
    public boolean isConsistent(int var1, int val1, int var2, int val2) {
        p.getConstraintCost(getAgentId(), var1, val1, var2, val2, queryTemp);
        return queryTemp.getCost() == 0;
    }

    @Override
    public boolean isConstrained(int var1, int var2) {
        return p.isConstrained(var1, var2);
    }

    /**
     * @return the type of the problem
     */
    @Override
    public ProblemType type() {
        return p.type();
    }

    @Override
    public int getConstraintCost(Assignment ass) {
        p.getConstraintCost(getAgentId(), ass, queryTemp);
        return queryTemp.getCost();
    }

    @Override
    public int calculateCost(Assignment a) {
        p.calculateCost(getAgentId(), a, queryTemp);
        return queryTemp.getCost();
    }
    
}
