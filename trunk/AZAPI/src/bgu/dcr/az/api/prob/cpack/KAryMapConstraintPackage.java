/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob.cpack;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.prob.KAryConstraint;
import bgu.dcr.az.api.tools.Assignment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class KAryMapConstraintPackage extends AbstractConstraintPackage {

    private class CostQuery extends HashMap<Set<Integer>, List<KAryConstraint>> {
    }
    private List<Map<Set<Integer>, KAryConstraint>> constraints = new ArrayList<Map<Set<Integer>, KAryConstraint>>();
    private CostQuery[] costQuery;

    public KAryMapConstraintPackage(int numvar) {
        super(numvar);
        costQuery = new CostQuery[numvar];
        for (int i = 0; i < numvar; i++) {
            constraints.add(new HashMap<Set<Integer>, KAryConstraint>());
            costQuery[i] = new CostQuery();
        }
    }

    @Override
    public void setConstraintCost(int owner, int x1, int v1, int x2, int v2, int cost) {
        Agt0DSL.panic("cannot use binary or unary version of constraints in k-ary problem, use the setConstraintCost(int, int[], KAryConstraint) method instead.");
    }

    @Override
    public void setConstraintCost(int owner, int x1, int v1, int cost) {
        Agt0DSL.panic("cannot use binary or unary version of constraints in k-ary problem, use the setConstraintCost(int, int[], KAryConstraint) method instead.");
    }

    @Override
    public int getConstraintCost(int owner, int x1, int v1) {
        return getConstraintCost(owner, new Assignment(x1, v1));
    }

    @Override
    public int getConstraintCost(int owner, int x1, int v1, int x2, int v2) {
        return getConstraintCost(owner, new Assignment(x1, v1, x2, v2));
    }

    @Override
    public int getConstraintCost(int owner, Assignment k) {
        KAryConstraint constraint = constraints.get(owner).get(k.assignedVariables());
        if (constraint == null) {
            return 0;
        }
        return constraint.getCost(k);
    }

    private List<Set<Integer>> participientsPermutations(int[] participients) {
        List<Set<Integer>> result = new LinkedList<Set<Integer>>();
        _participientsPermutations(participients, result, 0, new HashSet<Integer>());
        return result;
    }

    private void _participientsPermutations(int[] participients, List<Set<Integer>> result, int idx, HashSet<Integer> current) {
        if (idx < participients.length) {
            current.add(participients[idx]);
            _participientsPermutations(participients, result, idx + 1, current);
            current.remove(participients[idx]);
            _participientsPermutations(participients, result, idx + 1, current);
        } else {
            result.add(new HashSet<Integer>(current)); //todo can take lass space if use modified version of hashset
        }
    }

    @Override
    public void setConstraintCost(int owner, KAryConstraint constraint) {
        final HashSet<Integer> set = new HashSet<Integer>(constraint.getParicipients().length);
        //update neighbores
        for (int p : constraint.getParicipients()) {
            set.add(p);
            addNeighbor(owner, p);
        }

        //update cost query data structure
        for (Set<Integer> permutation : participientsPermutations(constraint.getParicipients())) {
            List<KAryConstraint> cq = costQuery[owner].get(permutation);
            if (cq == null) {
                cq = new LinkedList<KAryConstraint>();
                costQuery[owner].put(permutation, cq);
            }

            cq.add(constraint);
        }

        constraints.get(owner).put(set, constraint);
    }

    @Override
    public void calculateCost(int owner, Assignment a, int[] result) {
        Set<Integer> participients = a.assignedVariables();
        List<KAryConstraint> constraintsToConsider = costQuery[owner].get(participients);

        int cost = 0;
        for (KAryConstraint constraint : constraintsToConsider) {
            cost += constraint.getCost(a);
        }
        result[0] = cost;
        result[1] = constraintsToConsider.size();
    }

    @Override
    public int calculateGlobalCost(Assignment a) {
        int[] res = new int[2];
        int cost = 0;
        for (int i = 0; i < getNumberOfVariables(); i++) {
            calculateCost(i, a, res);
            cost+=res[0];
        }
        return cost;
    }
}
