/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.pgen;

import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.api.ds.ImmutableSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Inna
 */
public class MapProblem extends Problem {

    private Object[] map;
    //private HashMap<Integer, double[][]> map;

//    @Override
//    public boolean isConstrained(int var1, int var2) {
//        return super.constraints.containsKey(calcId(var1, var2));
//    }
    @Override
    public void setConstraintCost(int var1, int val1, int var2, int val2, double cost) {
        if (maxCost < cost){
            maxCost = cost;
        }
        
        int id = calcId(var1, var2);
//        if (cost != 0) {
//            super.constraints.put(id, Boolean.TRUE);
        setNeighbor(var1, var2);
//        setNeighbor(var2, var1);
//        }
        createMap(id);
        ((double[][]) map[id])[val1][val2] = cost;
    }

    private void setNeighbor(int var1, int var2) {
        Set<Integer> l = super.neighbores.get(var1);
        l.add(var2);
    }

    @Override
    public double getConstraintCost(int var1, int val1, int var2, int val2) {
        int id = calcId(var1, var2);
        if (map[id] == null) {
            return 0;
        }
        return ((double[][]) map[id])[val1][val2];
    }

    @Override
    public double getConstraintCost(int var1, int val1) {
        int id = calcId(var1, var1);
        if (map[id] == null) {
            return 0;
        }
        return ((double[][]) map[id])[val1][val1];
    }

    private void createMap(int id) {
        double[][] mapId = (double[][]) map[id];
        if (mapId == null) {
            mapId = new double[domain.size()][domain.size()];
            map[id] = mapId;
        }
    }

    @Override
    protected void _initialize() {
        int n = getNumberOfVariables();
//        int d = getDomain().size();

        this.map = new Object[n * n];
//        this.numvars = n;
//        ArrayList<Integer> temp = new ArrayList<Integer>(d);
//        for (int i = 0; i < d; i++) {
//            temp.add(i);
//        }
//        this.domain = new ImmutableSet<Integer>(temp);
    }
}
