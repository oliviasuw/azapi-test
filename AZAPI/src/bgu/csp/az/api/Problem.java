package bgu.csp.az.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * An abstract class for problems that should let you build any type of problem 
 * @author guyafe, edited by bennyl
 */
public abstract class Problem implements Serializable, ProblemView {

    private HashMap<String, Object> metadata = new HashMap<String, Object>();
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumberOfVariables(); i++) {
            for (Integer di : getDomainOf(i)) {
                for (int j = 0; j < getNumberOfVariables(); j++) {
                    for (Integer dj : getDomainOf(j)) {
                        sb.append((int) getConstraintCost(i, di, j, dj)).append(" ");
                    }
                }

                sb.append("\n");
            }
        }

        return sb.toString();
    }

    
    /**
     * @param var1
     * @param var2
     * @return true if there is a constraint between var1 and var2
     * operation cost: o(d^2)cc
     */
    @Override
    public boolean isConstrained(int var1, int var2){
        for (Integer d1 : getDomainOf(var1)){
            for (Integer d2: getDomainOf(var2)){
                if (getConstraintCost(var1, d1, var2, d2) != 0) return true;
            }
        }
        
        return false;
    }

    
    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return true if var1=val1 consistent with var2=val2
     */
    @Override
    public boolean isConsistent(int var1, int val1, int var2, int val2){
        return getConstraintCost(var1, val1, var2, val2) == 0;
    }
    
    /**
     * return the domain size of the variable var
     * @param var
     * @return
     */
    @Override
    public int getDomainSize(int var) {
        return getDomainOf(var).size();
    }

    /**
     * @return this problem metadata
     */
    @Override
    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * @param var
     * @return all the variables that costrainted with the given var 
     * operation cost: o(n*d^2)cc
     */
    @Override
    public List<Integer> getNeighbors(int var) {
        List<Integer> l = new LinkedList<Integer>();
        for (int v = 0; v<getNumberOfVariables(); v++){
            if (v != var && isConstrained(var, v)) l.add(v); 
        }
        return l;
    }
    
    /**
     * @return list of constraints that exist in this problem - the list is not part of the problem
     *         so regenerating it will create a new list every time and changing it will not change the 
     *         problem
     *  operation cost: o(n^2*d^2)cc
     */
    @Override
    public List<Constraint> getConstraints(){
        LinkedList<Constraint> constraints = new LinkedList<Constraint>();
        
        for (int v1=0; v1<getNumberOfVariables(); v1++){
            for (Integer v2 : getNeighbors(v1)){
                for (Integer d1 : getDomainOf(v1)){
                    for (Integer d2 : getDomainOf(v2)){
                        final double cost = getConstraintCost(v1,d1,v2,d2);
                        if (cost != 0){
                            if (v1 == v2){
                                constraints.add(new Constraint(cost, v1, d1, v2, d2));
                            }else {
                                constraints.add(new Constraint(cost, v1, d1));
                            }
                        }
                    }
                }
            }
        }
        
        return constraints;
    }
        
}
