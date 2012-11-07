/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob.cpack;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.prob.ConstraintCheckResult;
import bgu.dcr.az.api.prob.KAryConstraint;
import bgu.dcr.az.api.prob.RandomKAryConstraint;
import bgu.dcr.az.api.tools.Assignment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class KAryTreeConstraintPackage extends AbstractConstraintPackage {

    private Node[] roots;

    public KAryTreeConstraintPackage(int numvar) {
        super(numvar);
        roots = new Node[numvar];
        for (int i = 0; i < numvar; i++) {
            roots[i] = new Node();
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
    public void getConstraintCost(int owner, int x1, int v1, ConstraintCheckResult result) {
        getConstraintCost(owner, new Assignment(x1, v1), result);
    }

    @Override
    public void getConstraintCost(int owner, int x1, int v1, int x2, int v2, ConstraintCheckResult result) {
        getConstraintCost(owner, new Assignment(x1, v1, x2, v2), result);
    }

    @Override
    public void getConstraintCost(int owner, Assignment k, ConstraintCheckResult result) {

        KAryConstraint constraint = roots[owner].getConstraint(k);
        if (constraint == null) {
            result.set(0, 0);
        } else {
            constraint.getCost(k, result);
        }
    }

    @Override
    public void setConstraintCost(int owner, KAryConstraint constraint) {
//        System.out.println("Adding constraint: " + constraint + " to " + owner);
        roots[owner].add(constraint);

        //update neighbores
        for (int p : constraint.getParicipients()) {
            if (owner != p) {
                addNeighbor(owner, p);
            }
        }
    }

    @Override
    public void calculateCost(int owner, Assignment a, ConstraintCheckResult result) {
        Set<Integer> participients = a.assignedVariables();
        List<KAryConstraint> constraintsToConsider = roots[owner].collectAllSubConstraints(a);

//        //TEST CODE.
//        if (constraintsToConsider != null) {
//            System.out.println("owner: " + owner + "\nparticipients: " + participients.toString() + "\nconsidering constraints:");
//            for (KAryConstraint c : constraintsToConsider) {
//                System.out.println(c.toString());
//            }
//
//            System.out.println("---------------------");
//        }
        int cost = 0;
        int cc = 0;
        if (constraintsToConsider != null) {
            for (KAryConstraint constraint : constraintsToConsider) {
                constraint.getCost(a, result);
                cost += result.getCost();
                cc += result.getCheckCost();
            }
        }

        result.set(cost, cc);
    }

    @Override
    public int calculateGlobalCost(Assignment a) {
        ConstraintCheckResult res = new ConstraintCheckResult();
        int cost = 0;
        for (int i = 0; i < getNumberOfVariables(); i++) {
            calculateCost(i, a, res);
            cost += res.getCost();
        }
        return cost;
    }

    private static class Node {

        KAryConstraint constraint;
        Map<Integer, Node> childrens;

        public Node() {
            this.constraint = null;
            this.childrens = new HashMap<Integer, Node>();
        }

        public void add(KAryConstraint constraint) {
            int[] participients = constraint.getParicipients();
            Arrays.sort(participients);
            _add(constraint, participients, 0);
        }

        private void _add(KAryConstraint constraint, int[] participients, int idx) {
            if (idx < participients.length) {
                Node children = childrens.get(participients[idx]);
                if (children == null) {
                    children = new Node();
                    childrens.put(participients[idx], children);
                }

                children._add(constraint, participients, idx + 1);
            } else {
                this.constraint = constraint;
            }
        }

        public KAryConstraint getConstraint(Assignment a) {
            List<Integer> participients = extractParticipients(a);
            Node currentNode = this;
            for (Integer p : participients) {
                currentNode = childrens.get(p);
                if (currentNode == null) {
                    return null;
                }
            }

            return currentNode.constraint;
        }

        public List<KAryConstraint> collectAllSubConstraints(Assignment a) {
            LinkedList<KAryConstraint> ret = new LinkedList<KAryConstraint>();
            _collectAllSubConstraints(a.assignedVariables(), ret);
            return ret;
        }

        private void _collectAllSubConstraints(Set<Integer> participients, List<KAryConstraint> result) {
            if (constraint != null) {
                result.add(constraint);
            }

            for (Entry<Integer, Node> children : childrens.entrySet()) {
                if (participients.contains(children.getKey())) {
                    children.getValue()._collectAllSubConstraints(participients, result);
                }
            }
        }

        private List<Integer> extractParticipients(Assignment a) {
            List<Integer> participients = new ArrayList<Integer>(a.assignedVariables());
            Collections.sort(participients);
            return participients;
        }
    }
}
