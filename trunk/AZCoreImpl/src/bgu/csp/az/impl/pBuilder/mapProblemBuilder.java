/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pBuilder;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.pseq.ProblemBuilder;
import bgu.csp.az.impl.prob.MapProblem;

/**
 *
 * @author Inna
 */
public class mapProblemBuilder implements ProblemBuilder{
    
    private int n;
    private int d;

    public mapProblemBuilder(int n, int d) {
        this.n = n;
        this.d = d;
    }
    
    @Override
    public Problem build() {
        return new MapProblem(this.n,this.d);
    }
    
}
