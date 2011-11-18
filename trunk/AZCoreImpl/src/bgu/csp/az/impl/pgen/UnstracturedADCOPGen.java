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
import java.util.Random;

/**
 *
 * @author bennyl
 */
public class UnstracturedADCOPGen extends UnstracturedDCOPGen {

    public UnstracturedADCOPGen() {
        type = ProblemType.ADCOP;
    }

    @Override
    protected void _generate(Problem p, Random rand) {
        p.initialize(n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = i + 1; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, true, rand);
                }
            }
        }
    }

}
