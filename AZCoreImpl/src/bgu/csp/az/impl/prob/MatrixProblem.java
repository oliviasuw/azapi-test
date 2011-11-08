/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.prob;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.ds.ImmutableSet;
import bgu.csp.az.api.tools.Assignment;
import java.util.ArrayList;

/**
 * an instance of a problem that save its constraints inside a matrix 
 * use this instance for medium sized problems as this problem is very fast but consumes memory.
 * @author bennyl
 */
public class MatrixProblem extends Problem {

    int[][] matrix;
    int numvars;
    ImmutableSet<Integer> domain;

    public MatrixProblem(int[][] matrix, int numvars) {
        this.matrix = matrix;
        this.numvars = numvars;

        int numDom = matrix.length / numvars;
        ArrayList<Integer> temp = new ArrayList<Integer>(numDom);
        for (int i = 0; i < numDom; i++) {
            temp.add(i);
        }
        domain = new ImmutableSet<Integer>(temp);
    }

    @Override
    public int getNumberOfVariables() {
        return numvars;
    }

    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        return domain;
    }

    @Override
    public double getConstraintCost(int var1, int val1, int var2, int val2) {
        return matrix[var1 * domain.size() + val1][var2 * domain.size() + val2];
    }

    @Override
    public double getConstraintCost(int var1, int val1) {
        return getConstraintCost(var1, val1, var1, val1);
    }

    public void setConstraintCost(int var1, int val1, int var2, int val2, double cost) {
        matrix[var1 * domain.size() + val1][var2 * domain.size() + val2] = (int) cost;
    }

    @Override
    public double getConstraintCost(int var, int val, Assignment ass) {
        double sum = 0;
        for (Integer av : ass.assignedVariables()) {
            sum += getConstraintCost(var, val, av, ass.getAssignment(av));
        }

        return sum;
    }
}
