package bgu.csp.az.api.tools;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.DeepCopyable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.ds.ImmutableSet;
import java.io.Serializable;
import java.util.Collection;

/**
 * 
 * @author bennyl
 */
public class Assignment implements Serializable, DeepCopyable{

    private HashMap<Integer, Integer> assignment;
    private transient double cachedCost = -1;

    /**
     * constracting new empty assignment
     */
    public Assignment() {
        this.assignment = new HashMap<Integer, Integer>();

    }

    private Assignment(Assignment a) {
        this.assignment = new HashMap<Integer, Integer>(a.assignment);
    }

    /**
     * assign val to var
     * @param var
     * @param val
     */
    public void assign(int var, int val) {
        assignment.put(var, val);
        cachedCost = -1;
    }

    /**
     * remove var's assignment
     * @param var
     */
    public void unassign(int var) {
        assignment.remove(var);
        cachedCost = -1;
    }

    
    /**
     * same as unassign(agt.getId());
     * @param agt 
     */
    public void unassign(Agent agt){
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
        return assignment.get(var);
    }

    /**
     * @param p 
     * @return the cost of this assignment
     */
    public double calcCost(Problem p) {
        if (cachedCost >= 0) return cachedCost;
        
        double c = 0;
        LinkedList<Entry<Integer, Integer>> past = new LinkedList<Entry<Integer, Integer>>();

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

        cachedCost = c;
        return c;
    }

    /**
     * @param var
     * @param val
     * @param p 
     * @return the cost that will be added to this assignment by assigning 
     * 		   var <- val in the problem p
     */
    public double calcAddedCost(int var, int val, Problem p) {
        double c = 0;
        c += p.getConstraintCost(var, val);

        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            int var2 = e.getKey();
            int val2 = e.getValue();
            c += p.getConstraintCost(var, val, var2, val2);
        }

        return c;
    }

    /**
     * @param var
     * @param p
     * @return the cost of the assignment without the given variable assignment
     */
    public double calcCostWithout(int var, Problem p){
        double c = 0;
        LinkedList<Entry<Integer, Integer>> past = new LinkedList<Entry<Integer, Integer>>();

        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            int vr = e.getKey();
            int vl = e.getValue();
            
            if (vr == var) continue;
            
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
     * @return from the given domain the value that assigning var to it will be add the least to the assignment
     */
    public int findMinimalCostValue(int var, Collection<Integer> domain, Problem p) {
        boolean first = true;
        double min = 0;
        double c;
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
     * find the first assignment to variable - var that keeps the assignment under the given upperbound
     * returns -1 if none found.. 
     * @param var
     * @param upperbound
     * @param domain
     * @param p
     * @return 
     */
    public int findFirstAssignmentUnderUB(double upperbound, int var, Collection<Integer> domain, Problem p){
        double cost = calcCost(p);
        if (cost >= upperbound) return -1;
        for (Integer d : domain) if (cost + calcAddedCost(var, d, p) < upperbound) return d;
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
     * @return the assignmed variables
     */
    public ImmutableSet<Integer> assignedVariables(){
        return new ImmutableSet<Integer>(assignment.keySet());
    }
    
    /**
     * @return the sum of the assigned variables
     */
    public int getNumberOfAssignedVariables(){
        return assignment.keySet().size();
    }
    
    /**
     * return true if the assignment is consistent with assigning var->val
     * @param var
     * @param val
     * @param p
     * @return 
     */
    public boolean isConsistentWith(int var, int val, Problem p){
        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            int var2 = e.getKey();
            int val2 = e.getValue();
            if (p.getConstraintCost(var, val, var2, val2) != 0) return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Assignment)){
            return false;
        }else {
            Assignment ass = (Assignment) obj;
            for (Entry<Integer, Integer> e : assignment.entrySet()){
                if (ass.getAssignment(e.getKey()) != e.getValue()) return false;
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
