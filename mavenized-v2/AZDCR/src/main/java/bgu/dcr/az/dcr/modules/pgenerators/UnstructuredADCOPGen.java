/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.pgenerators;

import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.execs.sim.Agt0DSL;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.api.problems.ProblemType;
import bgu.dcr.az.dcr.util.ImmutableSet;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register("adcop-unstructured")
public class UnstructuredADCOPGen extends UnstructuredDCOPGen {

    @Override
    public void generate(Problem p, Random rand) {
        p.initialize(ProblemType.ADCOP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, false, rand);
                }
            }
        }
    }

}
