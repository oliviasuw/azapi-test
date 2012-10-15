/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob.cpack;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.prob.KAryConstraint;
import bgu.dcr.az.api.tools.Assignment;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class BinaryMapConstraintPackage extends AbstractConstraintPackage {

    private Object[] map;
    private int biggestDomainSize = 0;
    private final int numvars;

    public BinaryMapConstraintPackage(int numvar, int maxDomainSize) {
        super(numvar);

        this.numvars = numvar;
        this.biggestDomainSize = maxDomainSize;
        this.map = new Object[numvars * numvars];
    }

    protected int calcId(int i, int j) {
        return i * numvars + j;
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

        int id = calcId(var1, var2);
        if (var1 != var2) {
            addNeighbor(var1, var2);
        }

        createMap(id);
        ((int[][]) map[id])[val1][val2] = cost;
    }

    private void createMap(int id) {
        int[][] mapId = (int[][]) map[id];
        if (mapId == null) {
            mapId = new int[biggestDomainSize][biggestDomainSize];
            map[id] = mapId;
        }
    }

    @Override
    public void setConstraintCost(int owner, int x1, int v1, int cost) {
        setConstraintCost(owner, x1, v1, x1, v1, cost);
    }

    @Override
    public int getConstraintCost(int owner, int x1, int v1) {
        return getConstraintCost(owner, x1, v1, x1, v1);
    }

    @Override
    public int getConstraintCost(int owner, int var1, int val1, int var2, int val2) {
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

        int id = calcId(var1, var2);
        if (map[id] == null) {
            return 0;
        }
        return ((int[][]) map[id])[val1][val2];
    }

    @Override
    public int getConstraintCost(int owner, Assignment k) {
        throw new UnsupportedOperationException("Not supported - only Binary and Unary Constraints supported in this problem type.");
    }

    @Override
    public void setConstraintCost(int owner, KAryConstraint constraint) {
        throw new UnsupportedOperationException("Not supported - only Binary and Unary Constraints supported in this problem type.");
    }

    @Override
    public void calculateCost(int owner, Assignment assignment, int[] result) {
        int c = 0;
        int cc = 0;
        
        LinkedList<Map.Entry<Integer, Integer>> past = new LinkedList<Map.Entry<Integer, Integer>>();
        for (Map.Entry<Integer, Integer> e : assignment.getAssignments()) {
            int var = e.getKey();
            int val = e.getValue();
            c += getConstraintCost(var, var, val);
            ++cc;

            for (Map.Entry<Integer, Integer> pe : past) {
                int pvar = pe.getKey();
                int pval = pe.getValue();

                c += getConstraintCost(pvar, pvar, pval, var, val);
                ++cc;
            }
            past.add(e);
        }

        result[0] = c;
        result[1] = cc;
    }

    @Override
    public int calculateGlobalCost(Assignment assignment) {
        int[] res = new int[2];
        calculateCost(-1, assignment, res);
        return res[0];
    }
}
