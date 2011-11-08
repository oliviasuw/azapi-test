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
    
    private HashMap<Integer, HashMap<Integer,HashMap<Integer,Double>>> map;


    public MapProblem(int n,int d) {
        
        this.map = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>>();
        this.numvars = n;
        ArrayList<Integer> temp = new ArrayList<Integer>(d);
        for (int i = 0; i < d; i++) {
            temp.add(i);
        }
        this.domain = new ImmutableSet<Integer>(temp);
    }

    public MapProblem(HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> map, int numvars, ImmutableSet<Integer> domain) {
        this.map = map;
        this.numvars = numvars;
        this.domain = domain;
    }

    @Override
    public void setConstraintCost(int var1, int val1, int var2, int val2, double cost) {
        int id = var1*numvars+var2;
        createMap(id,val1,map);
        map.get(id).get(val1).put(val2,cost);
    }

    @Override
    public double getConstraintCost(int var1, int val1, int var2, int val2) {       
        int id = var1*numvars+var2;
        if(checkIfNull(id,val1,val2)){
            return 0;
        }
        return map.get(id).get(val1).get(val2);
    }

    @Override
    public double getConstraintCost(int var1, int val1) {
        int id = var1*numvars+var1;
        if(checkIfNull(id,val1,val1)){
            return 0;
        }
        return map.get(id).get(val1).get(val1);
    }


    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        return this.domain;
    }

    @Override
    public int getNumberOfVariables() {
        return this.numvars;
    }


    private void createMap(int id, int val1, HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> map) {
        HashMap<Integer, HashMap<Integer, Double>> mapId = map.get(id);
        if (mapId == null){
            mapId = new HashMap<Integer, HashMap<Integer, Double>>();
            map.put(id, mapId);
        }
        HashMap<Integer, Double> mapVal1 = mapId.get(val1);
        if(mapVal1 == null){
            map.get(id).put(val1, new HashMap<Integer, Double>());
        }
    }

    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> map) {
        this.map = map;
    }

    private boolean checkIfNull(int id, int val1, int val2) {
        HashMap<Integer, HashMap<Integer, Double>> mapId = map.get(id);
        if(mapId == null) return true;
        HashMap<Integer, Double> mapVal1 = mapId.get(val1);
        if(mapVal1 == null) return true;
        return(mapVal1.get(val2) == null);
    }

    
    
    
}