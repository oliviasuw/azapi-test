package bgu.csp.az.api.pgen;

import bgu.csp.az.api.ImuteableProblem;
import bgu.csp.az.api.ds.ImmutableSet;
import bgu.csp.az.api.tools.Assignment;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * An abstract class for problems that should let you build any type of problem 
 * @author guyafe, edited by bennyl
 */
public abstract class Problem implements Serializable, ImuteableProblem {

    private HashMap<String, Object> metadata = new HashMap<String, Object>();
    protected int numvars;
    protected ImmutableSet<Integer> domain;
    protected HashMap<Integer, List<Integer>> neighbores = new HashMap<Integer, List<Integer>>();
    protected HashMap<Integer, Boolean> constraints = new HashMap<Integer, Boolean>();

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

    protected int calcId(int i, int j) {
        return i * numvars + j;
    }

    /**
     * @param var1
     * @param var2
     * @return true if there is a constraint between var1 and var2
     * operation cost: o(d^2)cc
     */
    @Override
    public boolean isConstrained(int var1, int var2) {
        int id = calcId(var1, var2);
        Boolean ans = constraints.get(id);
        if (ans == null) {

            boolean found = false;
            OUTER_FOR:
            for (Integer d1 : getDomainOf(var1)) {
                for (Integer d2 : getDomainOf(var2)) {
                    if (getConstraintCost(var1, d1, var2, d2) != 0) {
                        found = true;
                        break OUTER_FOR;
                    }
                }
            }

            return found;
        } else {
            return ans;
        }
    }

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return true if var1=val1 consistent with var2=val2
     */
    @Override
    public boolean isConsistent(int var1, int val1, int var2, int val2) {
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
        List<Integer> l = this.neighbores.get(var);
        return l;
    }

    @Override
    public double getConstraintCost(int var, int val, Assignment ass) {
        double sum = 0;
        for (Integer av : ass.assignedVariables()) {
            sum += getConstraintCost(var, val, av, ass.getAssignment(av));
        }

        return sum;
    }

    abstract public void setConstraintCost(int var1, int val1, int var2, int val2, double cost);

    public ImmutableSet<Integer> getDomain() {
        return domain;
    }

    @Override
    public int getNumberOfVariables() {
        return numvars;
    }

    public void initialize(int numberOfVariables, Set<Integer> domain) {
        domain = new ImmutableSet<Integer>(domain);
        numvars = numberOfVariables;
        _initialize();
    }

    protected abstract void _initialize();

    /**
     * we are not yet supports seperated domains but when we do - this function should be useful
     */
    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        return domain;
    }
}
