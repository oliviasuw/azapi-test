/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

import bgu.csp.az.api.ds.ImmutableSet;
import bgu.csp.az.api.tools.Assignment;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public interface ImuteableProblem {

    /**
     *
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return the cost of assigning var1=val1 when var2=val2
     */
    double getConstraintCost(int var1, int val1, int var2, int val2);

    /**
     *
     * @param var1
     * @param val1
     * @return the cost of assigning var1=val1
     */
    double getConstraintCost(int var1, int val1);

    /**
     * @param var
     * @param val
     * @param ass
     * @return the constraint cost of assigning var=val and X=V for each <X=V> in ass
     */
    double getConstraintCost(int var, int val, Assignment ass);

    /**
     * return the domain of the given variable
     * @param var
     * @return
     */
    ImmutableSet<Integer> getDomainOf(int var);

    /**
     * return the domain size of the variable var
     * @param var
     * @return
     */
    int getDomainSize(int var);

    /**
     * @return this problem metadata
     */
    HashMap<String, Object> getMetadata();

    /**
     * @param var
     * @return all the variables that costrainted with the given var
     * operation cost: o(n*d^2)cc
     */
    Set<Integer> getNeighbors(int var);

    /**
     * @return the number of variables defined in this problem
     */
    int getNumberOfVariables();

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return true if var1=val1 consistent with var2=val2
     */
    boolean isConsistent(int var1, int val1, int var2, int val2);

    /**
     * @param var1
     * @param var2
     * @return true if there is a constraint between var1 and var2
     * operation cost: o(d^2)cc
     */
    boolean isConstrained(int var1, int var2);
}
