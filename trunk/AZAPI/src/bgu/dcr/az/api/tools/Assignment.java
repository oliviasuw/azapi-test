package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.exp.UnassignedVariableException;
import java.io.Serializable;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;

/**
 *
 * @author bennyl
 */
public class Assignment implements Serializable, DeepCopyable {

    private LinkedHashMap<Integer, Integer> assignment;
    private transient int cachedCost = -1;

    /**
     * constracting new empty assignment
     */
    public Assignment() {
        this.assignment = new LinkedHashMap<Integer, Integer>();

    }
    
    public Assignment(int... of){
        this();
        Agt0DSL.panicIf(of.length % 2 !=0 , "for every variable you must supply a value which means that the number of parameters must be even while it: " + of.length); 
        for (int i=0; i<of.length; i+=2){
            assign(of[i], of[i+1]);
        }
    }

    private Assignment(Assignment a) {
        this.assignment = new LinkedHashMap<Integer, Integer>();
        for (Entry<Integer, Integer> e : a.assignment.entrySet()) {
            this.assignment.put(e.getKey(), e.getValue());
        }
    }

    /**
     * assign val to var if var already been assign then its value will be
     * overwriten
     *
     * @param var
     * @param val
     */
    public void assign(int var, int val) {
        assignment.put(var, val);
        cachedCost = -1;
    }

    /**
     * remove var's assignment
     *
     * @param var
     */
    public void unassign(int var) {
        assignment.remove(var);
        cachedCost = -1;
    }

    /**
     * same as unassign(agt.getId());
     *
     * @param agt
     */
    public void unassign(Agent agt) {
        unassign(agt.getId());
    }

    /**
     * @param var
     * @return true if var is assigned
     */
    public boolean isAssigned(int var) {
        return assignment.containsKey(var);
    }

    /**
     * @param var
     * @return the value assigned to var or null if no such assignment
     */
    public Integer getAssignment(int var) {
        final Integer ass = assignment.get(var);
        if (ass == null) {
            throw new UnassignedVariableException("calling getAssignment with variable " + var + " while its is not assigned");
        }
        return ass;
    }

    /**
     * @param p
     * @return the cost of this assignment - (increase cc checks)
     */
    public int calcCost(ImmutableProblem p) {
        if (p instanceof Agent.AgentProblem) {
            if (cachedCost >= 0) {
                return cachedCost;
            }
        }

        int c = 0;
        LinkedList<Entry<Integer, Integer>> past = new LinkedList<Entry<Integer, Integer>>();
        if (p.type() == ProblemType.ADCOP) {
            if (p instanceof Agent.AgentProblem) {
                int agent = ((Agent.AgentProblem) p).getAgentId();
                int value = assignment.get(agent);
                for (Entry<Integer, Integer> e : assignment.entrySet()) {
                    int var = e.getKey();
                    int val = e.getValue();
                    c += p.getConstraintCost(agent, value, var, val);
                }
            } else {
                for (Entry<Integer, Integer> e : assignment.entrySet()) {
                    int var = e.getKey();
                    int val = e.getValue();
                    c += p.getConstraintCost(var, val);

                    for (Entry<Integer, Integer> pe : past) {
                        int pvar = pe.getKey();
                        int pval = pe.getValue();

                        c += p.getConstraintCost(pvar, pval, var, val);
                        c += p.getConstraintCost(var, val, pvar, pval);
                    }

                    past.add(e);
                }
            }
        } else {
            for (Entry<Integer, Integer> e : assignment.entrySet()) {
                int var = e.getKey();
                int val = e.getValue();
                c += p.getConstraintCost(var, val);

                for (Entry<Integer, Integer> pe : past) {
                    int pvar = pe.getKey();
                    int pval = pe.getValue();

                    c += p.getConstraintCost(pvar, pval, var, val);
                }
                past.add(e);
            }

        }

        cachedCost = c;
        return c;
    }

    /**
     * @param var
     * @param val
     * @param p
     * @return the cost that will be added to this assignment by assigning
     * 		   {@code var <- val} in the problem p
     *             this includes binary and unary costs
     * * (increase cc checks)
     */
    public int calcAddedCost(int var, int val, ImmutableProblem p) {
        int c = 0;
        c += p.getConstraintCost(var, val);

        int var2, val2;
        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            var2 = e.getKey();
            val2 = e.getValue();
            c += p.getConstraintCost(var, val, var2, val2);
        }
        return c;
    }

    /**
     * @param var
     * @param p
     * @return the cost of the assignment without the given variable assignment
     */
    public int calcCostWithout(int var, ImmutableProblem p) {
        int c = 0;
        LinkedList<Entry<Integer, Integer>> past = new LinkedList<Entry<Integer, Integer>>();

        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            int vr = e.getKey();
            int vl = e.getValue();

            if (vr == var) {
                continue;
            }

            c += p.getConstraintCost(vr, vl);

            for (Entry<Integer, Integer> pe : past) {
                int pvar = pe.getKey();
                int pval = pe.getValue();

                c += p.getConstraintCost(pvar, pval, vr, vl);
            }

            past.add(e);
        }

        return c;
    }

    /**
     * @param var
     * @param domain
     * @param p
     * @return from the given domain the value that assigning var to it will be
     * add the least to the assignment
     */
    public int findMinimalCostValue(int var, Collection<Integer> domain, ImmutableProblem p) {
        boolean first = true;
        int min = 0;
        int c;
        int minv = -1;
        for (Integer dval : domain) {
            c = calcAddedCost(var, dval, p);
            if (first) {
                min = c;
                minv = dval;
                first = false;
            } else {
                if (c < min) {
                    min = c;
                    minv = dval;
                }
            }
        }

        return minv;
    }

    /**
     * find the first assignment to variable - var that keeps the assignment
     * under the given upperbound returns -1 if none found..
     *
     * @param var
     * @param upperbound
     * @param domain
     * @param p
     * @return
     */
    public int findFirstAssignmentUnderUB(int upperbound, int var, Collection<Integer> domain, ImmutableProblem p) {
        int cost = calcCost(p);
        if (cost >= upperbound) {
            return -1;
        }
        for (Integer d : domain) {
            if (cost + calcAddedCost(var, d, p) < upperbound) {
                return d;
            }
        }
        return -1;
    }

    /**
     *
     * @return a deep copy of this assignment (same as calling deepCopy)
     */
    @Deprecated
    public Assignment copy() {
        return deepCopy();
    }

    /**
     * @return the assigned variables in this assignment
     */
    public ImmutableSet<Integer> assignedVariables() {
        return new ImmutableSet<Integer>(assignment.keySet());
    }
    
    /**
     * @return the unassigned variables in this assignemt - same as calling 
     */
    public ImmutableSet<Integer> unassignedVariables(ImmutableProblem p) {
        List<Integer> all = Agt0DSL.range(0, p.getNumberOfVariables()-1);
        all.removeAll(assignment.keySet());
        return new ImmutableSet<Integer>(all);
    }

    /**
     * @return the sum of the assigned variables
     */
    public int getNumberOfAssignedVariables() {
        return assignment.keySet().size();
    }

    /**
     * return true if the assignment is consistent with assigning var->val
     *
     * @param var
     * @param val
     * @param p
     * @return
     */
    public boolean isConsistentWith(int var, int val, ImmutableProblem p) {
        if (!isConsistent(p)) return false;
        
        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            int var2 = e.getKey();
            int val2 = e.getValue();
            if (p.getConstraintCost(var, val, var2, val2) != 0) {
                return false;
            }
        }

        return true;
    }
    
    public boolean isConsistent(ImmutableProblem p){
        return calcCost(p) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Assignment)) {
            return false;
        } else {
            Assignment ass = (Assignment) obj;
            for (Entry<Integer, Integer> e : assignment.entrySet()) {
                if (!ass.isAssigned(e.getKey()) || ass.getAssignment(e.getKey()) != e.getValue()) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.assignment != null ? this.assignment.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            sb.append(", ").append(e.toString());
        }
        String str = sb.toString();
        if (str.length() > 2) {
            return "{" + sb.toString().substring(2) + "}";
        }
        return "{}";
    }

    @Override
    public Assignment deepCopy() {
        return new Assignment(this);
    }
}
