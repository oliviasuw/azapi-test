package bgu.dcr.az.dcr.api;

import bgu.dcr.az.common.deepcopy.DeepCopyable;
import java.util.Map.Entry;

import bgu.dcr.az.dcr.Agt0DSL;
import bgu.dcr.az.dcr.api.exceptions.UnassignedVariableException;
import bgu.dcr.az.dcr.api.problems.ImmutableProblem;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.util.ImmutableIntSetView;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class Assignment implements Serializable, DeepCopyable {

//    private Map<Integer, Integer> assignment = new HashMap<>();
    private Int2IntOpenHashMap assignment = new Int2IntOpenHashMap();

    /**
     * the following fields are used for cost caching
     */
    private int cachedTotalCost = -1;

    /**
     * construction of a new empty assignment
     */
    public Assignment() {
        assignment.defaultReturnValue(-1);
    }

    public Assignment(Assignment other) {
        this();
//        cachedTotalCost = other.cachedTotalCost; not right for asym problems...
        other.foreach((k, v) -> assignment.put(k, v));
    }

    public Assignment(Map<Integer, Integer> assignment) {
        this();
        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            this.assignment.put(e.getKey(), e.getValue());
        }
    }

    public Assignment(int... of) {
        this();
        Agt0DSL.panicIf(of.length % 2 != 0, "for every variable you must supply a value which means that the number of parameters must be even while it: " + of.length);
        for (int i = 0; i < of.length; i += 2) {
            assign(of[i], of[i + 1]);
        }
    }

    public void foreach(IntIntFunction f) {
        IntIterator i = assignment.keySet().iterator();
        while (i.hasNext()) {
            final int nextInt = i.nextInt();
            f.invoke(nextInt, assignment.get(nextInt));
        }
    }

    public int reduce(int initial, IntIntReducer reducer) {
        int result = initial;
        IntIterator i = assignment.keySet().iterator();
        while (i.hasNext()) {
            final int nextInt = i.nextInt();
            result = reducer.reduce(result, nextInt, assignment.get(nextInt));
        }

        return result;
    }

    /**
     * assign val to var if var already been assign then its value will be
     * overwriten
     *
     * @param var
     * @param val
     */
    public void assign(int var, int val) {
        assignment.put(var, val);
        cachedTotalCost = -1;
    }

    /**
     * remove var's assignment
     *
     * @param var
     * @return the removed assigned value
     */
    public Integer unassign(int var) {
        cachedTotalCost = -1;
        int r = assignment.remove(var);
        if (r == -1) {
            return null;
        }
        return r;
    }

    /**
     * same as unassign(agt.getId());
     *
     * @param agt
     * @return the removed assigned value
     */
    public Integer unassign(Agent agt) {
        return unassign(agt.getId());
    }

    /**
     * @param var
     * @return true if var is assigned
     */
    public boolean isAssigned(int var) {
        return assignment.containsKey(var);
    }

    /**
     * @param var
     * @return the value assigned to var or null if no such assignment
     */
    public int getAssignment(int var) {
        final int ass = assignment.get(var);
        if (ass == -1) {
            throw new UnassignedVariableException("calling getAssignment with variable " + var + " while its is not assigned");
        }
        return ass;
    }

    public Set<Entry<Integer, Integer>> getAssignments() {
        return assignment.entrySet();
    }

    /**
     * @param p
     * @return the cost of this assignment - (increase cc checks)
     */
    public int calcCost(ImmutableProblem p) {
        if (p instanceof Agent.AgentProblem) {
            if (cachedTotalCost < 0) {
                cachedTotalCost = p.calculateCost(this);;
            }
            return cachedTotalCost;
        } else {
            return ((Problem) p).calculateGlobalCost(this);
        }
    }

    /**
     * @param var
     * @param val
     * @param p
     * @return the cost that will be added (or all ready added) to this
     * assignment by assigning {@code var <- val} in the problem p this includes
     * binary and unary costs
     *
     * means: costOf(assignment + <var,val>) - costOf(assignment - <var,
     * currentAssignmentOf(var)>)
     *
     * * (increase cc checks)
     */
    public int calcAddedCost(int var, int val, ImmutableProblem p) {
        int oldCache = cachedTotalCost;
        boolean assignend = isAssigned(var);
        int old = (assignend ? getAssignment(var) : 0);
        unassign(var);
        int withoutCost = calcCost(p);

        assign(var, val);
        int ans = calcCost(p) - withoutCost;
        if (assignend) {
            assign(var, old);
            cachedTotalCost = oldCache;
        } else {
            unassign(var);
            cachedTotalCost = withoutCost;
        }

        return ans;
    }

    /**
     * @param var
     * @param p
     * @return the cost of the assignment without the given variable assignment
     */
    public int calcCostWithout(int var, ImmutableProblem p) {
        int oldCache = cachedTotalCost;
        boolean assignend = isAssigned(var);
        int old = (assignend ? getAssignment(var) : 0);

        unassign(var);
        int ans = calcCost(p);
        if (assignend) {
            assign(var, old);
        }

        cachedTotalCost = oldCache; //fix cache

        return ans;
    }

    /**
     * @param var
     * @param domain
     * @param p
     * @return from the given domain the value that assigning var to it will be
     * add the least to the assignment
     */
    public int findMinimalCostValue(int var, Collection<Integer> domain, ImmutableProblem p) {
        boolean first = true;
        int min = 0;
        int c;
        int minv = -1;
        for (Integer dval : domain) {
            c = calcAddedCost(var, dval, p);
            if (first) {
                min = c;
                minv = dval;
                first = false;
            } else {
                if (c < min) {
                    min = c;
                    minv = dval;
                }
            }
        }

        return minv;
    }

    /**
     * searches for the first value that is consistent with this assignment in
     * the given domain of values if no such value found then this function will
     * return the defaultValue
     *
     * @param var
     * @param domain
     * @param p
     * @param defaultValue
     * @return
     */
    public int findConsistentValue(int var, Collection<Integer> domain, ImmutableProblem p, int defaultValue) {
        for (Integer d : domain) {
            if (isConsistentWith(var, d, p)) {
                return d;
            }
        }

        return defaultValue;
    }

    /**
     * find the first assignment to variable - var that keeps the assignment
     * under the given upperbound returns -1 if none found..
     *
     * @param var
     * @param upperbound
     * @param domain
     * @param p
     * @return
     */
    public int findFirstAssignmentUnderUB(int upperbound, int var, Collection<Integer> domain, ImmutableProblem p) {
        int cost = calcCost(p);
        if (cost >= upperbound) {
            return -1;
        }
        for (Integer d : domain) {
            if (cost + calcAddedCost(var, d, p) < upperbound) {
                return d;
            }
        }
        return -1;
    }

    /**
     *
     * @return a deep copy of this assignment (same as calling deepCopy)
     */
    @Deprecated
    public Assignment copy() {
        return deepCopy();
    }

    /**
     * @return the assigned variables in this assignment
     */
    public ImmutableIntSetView assignedVariables() {
        return new ImmutableIntSetView(assignment.keySet());
    }

    /**
     * @return the unassigned variables in this assignemt - same as calling
     */
    public IntSet unassignedVariables(ImmutableProblem p) {
        IntSet result = new IntOpenHashSet();
        IntSet assigned = assignment.keySet();
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            if (!assigned.contains(i)) {
                result.add(i);
            }
        }

        return result;
    }

    /**
     * @return the sum of the assigned variables
     */
    public int getNumberOfAssignedVariables() {
        return assignment.keySet().size();
    }

    public boolean isFull(ImmutableProblem problem) {
        return getNumberOfAssignedVariables() == problem.getNumberOfVariables();
    }

    /**
     * return true if the assignment is consistent with assigning var->val
     *
     * @param var
     * @param val
     * @param p
     * @return
     */
    public boolean isConsistentWith(int var, int val, ImmutableProblem p) {
        boolean assignend = isAssigned(var);
        int old = (assignend ? getAssignment(var) : 0);
        int oldCache = cachedTotalCost;

        assign(var, val);
        boolean ans = calcCost(p) == 0;
        if (assignend) {
            assign(var, old);
        } else {
            unassign(var);
        }

        cachedTotalCost = oldCache; //fix cache
        return ans;
    }

    public boolean isConsistent(ImmutableProblem p) {
        return calcCost(p) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Assignment)) {
            return false;
        } else {
            Assignment otherAssignment = (Assignment) obj;
            return assignment.equals(otherAssignment.assignment);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.assignment != null ? this.assignment.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            sb.append(", ").append(e.toString());
        }
        String str = sb.toString();
        if (str.length() > 2) {
            return "{" + sb.toString().substring(2) + "}";
        }
        return "{}";
    }

    @Override
    public Assignment deepCopy() {
        return new Assignment(this);
    }

    /**
     * @return the number of assigned variables in this assignment
     */
    public int size() {
        return getNumberOfAssignedVariables();
    }

    /**
     * return a new assignment which contains all the assignment in the current
     * one with the additional assignment, the original assignment is not
     * changed!
     *
     * @param i
     * @param vi
     * @return
     */
    public Assignment union(int i, int vi) {
        Assignment a = new Assignment(this);
        a.assign(i, vi);
        return a;
    }

    /**
     * remove all entries from this assignment
     */
    public void clear() {
        assignment.clear();
        cachedTotalCost = -1;
    }

}
