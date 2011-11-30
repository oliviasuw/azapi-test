/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class Constraint {
    private double cost;
    int[] constraintedVarValPairs;
    
    /**
     * represents a single n-ary constraint
     * @param cost
     * @param constraintedVarValPairs
     */
    public Constraint(double cost, int... constraintedVarValPairs){
        this.cost = cost;
        this.constraintedVarValPairs = constraintedVarValPairs;
    }
    
    /**
     * @return true if this constraint involves only one variable
     */
    public boolean isBinary(){
        return numberOfConstraintedVariables() == 2;
    }
    
    /**
     * @return true if this constraint involves only two variables
     */
    public boolean isUnary(){
        return numberOfConstraintedVariables() == 1;
    }
    
    /**
     * @return the number of variables that involved in this constraint
     */
    public int numberOfConstraintedVariables(){
        return this.constraintedVarValPairs.length / 2;
    }

    /**
     * @return the cost of braking this constraint
     */
    public double getCost() {
        return cost;
    }
    
    /**
     * @return list of the constrainted variable + value pairs
     */
    public List<SimpleEntry<Integer, Integer>> getConstrainted(){
        List<SimpleEntry<Integer, Integer>> ret = new LinkedList<SimpleEntry<Integer, Integer>>();
        
        for (int i=0; i<this.constraintedVarValPairs.length; i+=2){
            ret.add(new SimpleEntry<Integer, Integer>(this.constraintedVarValPairs[i], this.constraintedVarValPairs[i+1]));
        }
        
        return ret;
    }
    
}
