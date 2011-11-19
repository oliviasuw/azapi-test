/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pgen;

import bgu.csp.az.api.Agt0DSL;
import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.ano.Variable;
import bgu.csp.az.api.ds.ImmutableSet;
import bgu.csp.az.api.pgen.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register(name="dcop-unstructured")
public class UnstructuredDCOPGen extends AbstractProblemGenerator {

    @Variable(name = "n", description = "number of variables")
    int n = 2;
    @Variable(name = "d", description = "domain size")
    int d = 2;
    @Variable(name = "max-cost", description = "maximal cost of constraint")
    int maxCost = 100;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generating : ").append("n = ").append(n).append("\nd = ").append(d).append("\nmaxCost = ").append(maxCost);
        return sb.toString();
    }
    
    
    @Override
    public void generate(Problem p, Random rand, float p1, float p2) {
        p.initialize(ProblemType.DCOP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, true, rand, p2);
                }
            }
        }
    }

    protected void buildConstraint(int i, int j, Problem p, boolean sym, Random rand, float p2) {
        for (int vi = 0; vi < p.getDomain().size(); vi++) {
            for (int vj = 0; vj < p.getDomain().size(); vj++) {
                if (i == j && vi != vj) {
                    continue;
                }
                if (rand.nextDouble() < p2) {
                    final int cost = rand.nextInt(maxCost) + 1;
                    p.setConstraintCost(i, vi, j, vj, cost);
                    if (sym) {
                        p.setConstraintCost(j, vj, i, vi, cost);
                    }
                }
            }
        }
    }

    
}
