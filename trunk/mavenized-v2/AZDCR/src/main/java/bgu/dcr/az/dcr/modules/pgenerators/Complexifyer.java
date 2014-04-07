package bgu.dcr.az.dcr.modules.pgenerators;

import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.api.modules.ProblemGenerator;
import bgu.dcr.az.dcr.api.problems.ConstraintCheckResult;
import bgu.dcr.az.dcr.api.problems.Problem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * given an multi-var base problem this problem generator change it to
 * single-variable domain by creating complex-variables which are variables that
 * has in their domain the cartesian multiplication of all the domains of
 * variables that belongs to multi-variables of the same agent.
 *
 * @author bennyl
 */
@Register("complexify")
public class Complexifyer extends AbstractProblemGenerator {

    ProblemGenerator base;

    @Override
    public void generate(Problem p, Random rand) {
        Problem basep = new Problem();
        base.generate(basep, rand);

        Map<Integer, Map<Integer, int[]>> mapping = new HashMap<>(); //mapping (var->val)->[original-var1, original-val1, ... ]
        List<Set<Integer>> domains = new ArrayList<>(basep.getNumberOfAgents());

        //generates domain mapping per agent
        for (int agent = 0; agent < basep.getNumberOfAgents(); agent++) {
            HashMap<Integer, int[]> currentMapping = new HashMap<>();
            mapping.put(agent, currentMapping);

            int[] vars = basep.getVariablesOwnedByAgent(agent);
            int[] domainSizes = new int[vars.length];
            for (int v = 0; v < vars.length; v++) {
                domainSizes[v] = basep.getDomainSize(vars[v]);
            }

            int[] valMapping = new int[vars.length];
            int valCounter = 0;
            Set<Integer> domain = new HashSet<>();
            domains.add(domain);
            do {
                domain.add(valCounter);
                currentMapping.put(valCounter++, Arrays.copyOf(valMapping, valMapping.length));
            } while (nextValue(valMapping, domainSizes));
        }

        //initialize problems 
        p.initialize(basep.type(), domains);

        //creating constraints for the new variables
        Assignment a = new Assignment();
        ConstraintCheckResult ccr = new ConstraintCheckResult();
        for (Map.Entry<Integer, Map<Integer, int[]>> varNval1 : mapping.entrySet()) {
            int var1 = varNval1.getKey();
            for (Map.Entry<Integer, int[]> valNMapping1 : varNval1.getValue().entrySet()) {
                int val1 = valNMapping1.getKey();

                for (Map.Entry<Integer, Map<Integer, int[]>> varNval2 : mapping.entrySet()) {
                    int var2 = varNval2.getKey();
                    for (Map.Entry<Integer, int[]> valNMapping2 : varNval2.getValue().entrySet()) {
                        int val2 = valNMapping2.getKey();

                        a.clear();
                        final int[] mapping1 = valNMapping1.getValue();
                        final int[] mapping2 = valNMapping2.getValue();
                        for (int i = 0; i < mapping1.length; i++) {
                            a.assign(basep.getVariablesOwnedByAgent(var1)[i], mapping1[i]);
                        }
                        for (int i = 0; i < mapping2.length; i++) {
                            a.assign(basep.getVariablesOwnedByAgent(var2)[i], mapping2[i]);
                        }

                        basep.calculateCost(var1, a, ccr);
                        if (ccr.getCost() != 0) {
                            p.setConstraintCost(var1, var1, val1, var2, val2, ccr.getCost());
                        }
                    }
                }
            }
        }

    }

    /**
     * @propertyName base
     * @return
     */
    public ProblemGenerator getBase() {
        return base;
    }

    public void setBase(ProblemGenerator base) {
        this.base = base;
    }

    private boolean nextValue(int[] d, int[] domainSizes) {
        for (int i = 0; i < d.length; i++) {
            if (++d[i] == domainSizes[i]) {
                d[i] = 0;
            } else {
                return true;
            }
        }

        return false;
    }

}
