/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.prob;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.ds.ImmutableSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Inna
 */
public class MapProblem extends Problem {

    private Object[] map;
    //private HashMap<Integer, double[][]> map;

    public MapProblem(int n, int d) {
        //this.map = new HashMap<Integer, double[][]>();
        this.map = new Object[n*n];
        this.numvars = n;
        ArrayList<Integer> temp = new ArrayList<Integer>(d);
        for (int i = 0; i < d; i++) {
            temp.add(i);
        }
        this.domain = new ImmutableSet<Integer>(temp);
    }

    @Override
    public boolean isConstrained(int var1, int var2) {
        return super.constraints.containsKey(calcId(var1, var2));
    }

    @Override
    public void setConstraintCost(int var1, int val1, int var2, int val2, double cost) {
        int id = calcId(var1, var2);
        if (cost != 0) {
            super.constraints.put(id, Boolean.TRUE);
        }
        createMap(id);
        ((double[][])map[id])[val1][val2] = cost;
    }

    @Override
    public double getConstraintCost(int var1, int val1, int var2, int val2) {
        int id = calcId(var1, var2);
        if (map[id]==null) {
            return 0;
        }
        return ((double[][])map[id])[val1][val2];
    }

    @Override
    public double getConstraintCost(int var1, int val1) {
        int id = calcId(var1, var1);
        if (map[id] == null) {
            return 0;
        }
        return ((double[][])map[id])[val1][val1];
    }

    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        return this.domain;
    }

    @Override
    public int getNumberOfVariables() {
        return this.numvars;
    }

    private void createMap(int id) {
        double[][] mapId = (double[][]) map[id];
        if (mapId == null) {
            mapId = new double[domain.size()][domain.size()];
            map[id] = mapId;
        }
    }

}