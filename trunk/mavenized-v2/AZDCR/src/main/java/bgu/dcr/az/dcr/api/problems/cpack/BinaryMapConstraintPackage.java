/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.problems.cpack;

import bgu.dcr.az.dcr.Agt0DSL;
import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.api.problems.constraints.BinaryConstraint;
import bgu.dcr.az.dcr.api.problems.ConstraintCheckResult;
import bgu.dcr.az.dcr.api.problems.constraints.BinaryConstraintTable;
import bgu.dcr.az.dcr.api.problems.constraints.KAryConstraint;

/**
 *
 * @author bennyl
 */
public class BinaryMapConstraintPackage extends AbstractConstraintPackage {

    private BinaryConstraint[][] map;
    private int biggestDomainSize = 0;
    private final int numvars;

    public BinaryMapConstraintPackage(int numvar, int maxDomainSize) {
        super(numvar);

        this.numvars = numvar;
        this.biggestDomainSize = maxDomainSize;
        this.map = new BinaryConstraint[numvar][numvar];
    }

    @Override
    public void setConstraintCost(int owner, int var1, int val1, int var2, int val2, int cost) {

        if (owner == var2) {
            int t = var1;
            var1 = var2;
            var2 = t;
            t = val1;
            val1 = val2;
            val2 = t;
        }

        if (owner != var1) {
            Agt0DSL.panic("Binary Problem cannot support constraint owners that are not part of the constraints, if you need such a feature use the K-Ary version.");
        }

        if (var1 != var2) {
            addNeighbor(var1, var2);
        }

        createMap(var1, var2);
        if (map[var1][var2] instanceof BinaryConstraintTable) {
            ((BinaryConstraintTable) map[var1][var2]).setCost(val1, val2, cost);
        } else {
            Agt0DSL.panic("Cannot change the constraint cost of a custom constraints");
        }
    }

    private void createMap(int var1, int var2) {
        if (map[var1][var2] == null) {
            map[var1][var2] = new BinaryConstraintTable(biggestDomainSize);
        }
    }

    @Override
    public void setConstraintCost(int owner, int x1, int v1, int cost) {
        setConstraintCost(owner, x1, v1, x1, v1, cost);
    }

    @Override
    public void getConstraintCost(int owner, int x1, int v1, ConstraintCheckResult result) {
        getConstraintCost(owner, x1, v1, x1, v1, result);
    }

    @Override
    public void getConstraintCost(int owner, int var1, int val1, int var2, int val2, ConstraintCheckResult result) {
        if (owner == var2) {
            int t = var1;
            var1 = var2;
            var2 = t;
            t = val1;
            val1 = val2;
            val2 = t;
        }

        if (owner != var1) {
            Agt0DSL.panic("Binary Problem cannot support constraint owners that are not part of the constraints, if you need such a feature use the K-Ary version.");
        }

//        final BinaryConstraint c = map[var1][var2];
        if (map[var1][var2] == null) {
            result.set(0, 1);
        } else {
            result.set(map[var1][var2].cost(var1, val1, var2, val2), 1);
        }
    }

    @Override
    public void getConstraintCost(int owner, Assignment k, ConstraintCheckResult result) {
        throw new UnsupportedOperationException("Not supported - only Binary and Unary Constraints supported in this problem type.");
    }

    @Override
    public void setConstraint(int owner, KAryConstraint constraint) {
        throw new UnsupportedOperationException("Not supported - only Binary and Unary Constraints supported in this problem type.");
    }

    @Override
    public void calculateCost(int owner, Assignment assignment, ConstraintCheckResult result) {
        int c = 0;
        int cc = 0;

        int[] var = assignment.assignedVariables().toIntArray();
        for (int i = 0; i < var.length; i++) {
            int ival = assignment.getAssignment(var[i]);
            getConstraintCost(var[i], var[i], ival, result);
            c = Agt0DSL.boundedSumm(c, result.getCost());
            cc += result.getCheckCost();
            for (int j = i + 1; j < var.length; j++) {
                int jval = assignment.getAssignment(var[j]);
                getConstraintCost(var[j], var[j], jval, var[i], ival, result);
                c = Agt0DSL.boundedSumm(c, result.getCost());
                cc += result.getCheckCost();
            }
        }
        result.set(c, cc);
    }

    @Override
    public int calculateGlobalCost(Assignment assignment) {
        ConstraintCheckResult res = new ConstraintCheckResult();
        calculateCost(-1, assignment, res);
        return res.getCost();
    }

    @Override
    public void addConstraint(int owner, KAryConstraint constraint) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setConstraint(int owner, int participient1, int participient2, BinaryConstraint constraint) {
        if (owner != participient1 && owner != participient2) {
            Agt0DSL.panic("Binary Problem cannot support constraint owners that are not part of the constraints, if you need such a feature use the K-Ary version.");
        }

        if (participient1 != participient2) {
            addNeighbor(participient1, participient2);
        }

        map[participient1][participient2] = constraint;
    }

}
