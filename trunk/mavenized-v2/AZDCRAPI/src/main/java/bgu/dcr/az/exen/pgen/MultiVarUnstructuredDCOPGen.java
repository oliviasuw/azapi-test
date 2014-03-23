/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.pgen;

import bgu.dcr.az.anop.conf.Variable;
import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.prob.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register("mv-dcop-unstructured")
public class MultiVarUnstructuredDCOPGen extends AbstractProblemGenerator {

    @Variable(name = "n", description = "number of variables", defaultValue = "2")
    int n = 2;
    @Variable(name = "d", description = "domain size", defaultValue = "2")
    int d = 2;
    @Variable(name = "max-cost", description = "maximal cost of constraint", defaultValue = "100")
    int maxCost = 100;
    @Variable(name = "p1", description = "probablity of constraint between two variables", defaultValue = "0.6")
    float p1 = 0.6f;

    @Variable(name = "vars-per-agent", description = "number of variables owned by an agent", defaultValue = "1")
    int vpa = 2;

    @Override
    public void generate(Problem p, Random rand) {
        p.initialize(ProblemType.DCOP, n, new ImmutableSet<>(Agt0DSL.range(0, d - 1)), (n % vpa == 0 ? (n / vpa) : (n / vpa + 1)));
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, true, rand);
                }
            }
        }

        for (int i = 0, vs = n; vs > 0; i++, vs -= vpa) {
            int[] vars = new int[vs > vpa ? vpa : vs];
            for (int j = 0; j < vars.length; j++) {
                vars[j] = vpa * i + j;
            }
            p.setVariablesOwnedByAgent(i, vars);
        }
    }

    protected void buildConstraint(int i, int j, Problem p, boolean sym, Random rand) {
        for (int vi = 0; vi < p.getDomain().size(); vi++) {
            for (int vj = 0; vj < p.getDomain().size(); vj++) {
                if (i == j) {
                    continue;
                }
                final int cost1 = rand.nextInt(maxCost + 1);
                final int cost2 = rand.nextInt(maxCost + 1);
                p.setConstraintCost(i, vi, j, vj, cost1);
                if (sym) {
                    p.setConstraintCost(j, vj, i, vi, cost1);
                } else {
                    p.setConstraintCost(j, vj, i, vi, cost2);
                }
            }
        }
    }
}
