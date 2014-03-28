/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.pgenerators;

import bgu.dcr.az.conf.api.Variable;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.Agt0DSL;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.api.problems.ProblemType;
import bgu.dcr.az.dcr.util.ImmutableSet;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register("mv-dcsp-unstructured")
public class MultiVarUnstructuredDCSPGen extends AbstractProblemGenerator {

    @Variable(name = "n", description = "number of variables", defaultValue = "2")
    public int n = 2;
    @Variable(name = "a-n", description = "number of variables per agent", defaultValue = "2")
    public int a_n = 2;
    @Variable(name = "d", description = "domain size", defaultValue = "2")
    public int d = 2;
    @Variable(name = "p1", description = "probablity of constraint between two variables", defaultValue = "0.6")
    public float p1 = 0.6f;
    @Variable(name = "p2", description = "probablity of conflict between two constrainted variables", defaultValue = "0.4")
    public float p2 = 0.4f;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generating : ").append("n = ").append(n).append("\nd = ").append(d);
        return sb.toString();
    }

    @Override
    public void generate(Problem p, Random rand) {
        int numberOfAgents = n / a_n;
        numberOfAgents = n % a_n == 0 ? numberOfAgents : numberOfAgents + 1;

        p.initialize(ProblemType.DCSP, n, new ImmutableSet<>(Agt0DSL.range(0, d - 1)), numberOfAgents);
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, true, rand, p2);
                }
            }
        }

        for (int i = 0; i < numberOfAgents; i++) {
            int[] controlledVars = new int[(i + 1) * a_n > n ? n % a_n : a_n];
            for (int j = 0; j < controlledVars.length; j++) {
                controlledVars[j] = a_n * i + j;
            }
            p.setVariablesOwnedByAgent(i, controlledVars);
        }
    }

    private void buildConstraint(int i, int j, Problem p, boolean sym, Random rand, float p2) {
        for (int vi = 0; vi < p.getDomain().size(); vi++) {
            for (int vj = 0; vj < p.getDomain().size(); vj++) {
                if (i == j) {
                    continue;
                }
                if (rand.nextDouble() < p2) {
                    final int cost = 1;
                    p.setConstraintCost(i, vi, j, vj, cost);
                    if (sym) {
                        p.setConstraintCost(j, vj, i, vi, cost);
                    }
                }
            }
        }
    }
}
