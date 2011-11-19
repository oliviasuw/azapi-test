/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.correctness;

import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.api.tools.Assignment;
import java.util.HashSet;

/**
 *
 * @author bennyl
 */
public class BranchAndBound {

    public static Assignment solve(Problem p) {
        return _solve(p, 0, new Assignment(), Double.MAX_VALUE);
    }

    private static Assignment _solve(Problem p, int var, Assignment cpa, double ub) {
        
        if (p.getNumberOfVariables() == var) return cpa;
        HashSet<Integer> cd = new HashSet<Integer>(p.getDomainOf(var));
        Assignment bcpa = null;
        
        while (! cd.isEmpty()){
            Integer best = cpa.findMinimalCostValue(var, cd, p);
            cpa.assign(var, best);
            cd.remove(best);
            
            if (cpa.calcCost(p) >= ub) {
                cpa.unassign(var);
                return bcpa;
            }
            
            Assignment temp = _solve(p, var+1, cpa, ub);
            
            
            if (temp != null){
                double cost = temp.calcCost(p);
                if (cost < ub){
                    ub = cost;
                    bcpa = temp.copy();
                } 
            }
            
            cpa.unassign(var);
        }
        
        return bcpa;
    }
}
