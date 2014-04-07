/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.pgenerators;

import bgu.dcr.az.conf.api.Variable;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.api.modules.ProblemGenerator;
import bgu.dcr.az.dcr.api.problems.Problem;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register("multi-varify")
public class MultiVariableProblem extends AbstractProblemGenerator {

    private ProblemGenerator base;

    @Variable(name = "vars-per-agent", description = "number of variables owned by an agent", defaultValue = "1")
    int vpa = 2;

    @Variable(name = "cluster-variables", description = "if cluster variables is true then the agents will get assigned sequental variables otherwise they will get assigned random variables", defaultValue = "true")
    boolean clusterVariables = true;

    @Override
    public void generate(Problem p, Random rand) {
        base.generate(p, rand);

        int numberOfAgents = p.getNumberOfVariables() / vpa;
        numberOfAgents = p.getNumberOfVariables() % vpa == 0 ? numberOfAgents : numberOfAgents + 1;
        p.setNumberOfAgents(numberOfAgents);

        Integer[] order = new Integer[p.getNumberOfVariables()];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        if (!clusterVariables) {
            Collections.shuffle(Arrays.asList(order));
        }

        for (int i = 0, vs = order.length; vs > 0; i++, vs -= vpa) {
            int[] vars = new int[vs > vpa ? vpa : vs];
            for (int j = 0; j < vars.length; j++) {
                vars[j] = order[vpa * i + j];
            }
            p.setVariablesOwnedByAgent(i, vars);
        }
    }

    /**
     *
     * @propertyName base
     */
    public ProblemGenerator getBase() {
        return base;
    }

    public void setBase(ProblemGenerator base) {
        this.base = base;
    }

}
