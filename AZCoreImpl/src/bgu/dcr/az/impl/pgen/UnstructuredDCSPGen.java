/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.pgen;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.pgen.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register(name = "dcsp-unstructured", display = "Unstructured DCSP Problem Generator")
public class UnstructuredDCSPGen extends AbstractProblemGenerator {

    @Variable(name = "n", description = "number of variables", defaultValue = "2")
    int n = 2;
    @Variable(name = "d", description = "domain size", defaultValue = "2")
    int d = 2;
    @Variable(name = "p1", description = "probablity of constraint between two variables", defaultValue = "0.6")
    float p1 = 0.6f;
    @Variable(name = "p2", description = "probablity of conflict between two constrainted variables", defaultValue = "0.4")
    float p2 = 0.4f;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generating : ").append("n = ").append(n).append("\nd = ").append(d);
        return sb.toString();
    }

    @Override
    public void generate(Problem p, Random rand) {
        p.initialize(ProblemType.DCSP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, true, rand, p2);
                }
            }
        }
    }

    private void buildConstraint(int i, int j, Problem p, boolean sym, Random rand, float p2) {
        for (int vi = 0; vi < p.getDomain().size(); vi++) {
            for (int vj = 0; vj < p.getDomain().size(); vj++) {
                if (i == j) {
                    continue;
                }
                if (rand.nextDouble() < p2) {
                    final int cost = rand.nextInt(2);
                    p.setConstraintCost(i, vi, j, vj, cost);
                    if (sym) {
                        p.setConstraintCost(j, vj, i, vi, cost);
                    }
                }
            }
        }
    }
}
