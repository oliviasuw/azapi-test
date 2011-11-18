/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pgen;

import bgu.csp.az.api.Agt0DSL;
import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.ano.Variable;
import bgu.csp.az.api.ds.ImmutableSet;
import bgu.csp.az.api.pgen.Problem;
import com.sun.imageio.plugins.common.I18N;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public class UnstracturedDCOPGen extends AbstractProblemGenerator {

    @Variable(name = "p1", description = "probability of constraint between two variables")
    float p1 = 0.1f;
    @Variable(name = "p2", description = "probability of conflict between two constrainted variables")
    float p2 = 0.1f;
    @Variable(name = "n", description = "number of variables")
    int n = 2;
    @Variable(name = "d", description = "domain size")
    int d = 2;
    @Variable(name = "max-cost", description = "maximal cost of constraint")
    int maxCost = 100;

    public UnstracturedDCOPGen() {
        super("unstractured", ProblemType.DCOP);
    }

    @Override
    protected void _generate(Problem p, Random rand) {
        p.initialize(n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, false, rand);
                }
            }
        }
    }

    protected void buildConstraint(int i, int j, Problem p, boolean sym, Random rand) {
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
