package bgu.dcr.az.dev.debug3;

import static bgu.dcr.az.api.Agt0DSL.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import bgu.dcr.az.api.exp.PanicedAgentException;
import bgu.dcr.az.api.tools.Assignment;

/**
 * @author alongrub A basic NoGood of the form {a1=x1, a2=x2,...} ==> {an =/=
 * xns} This is implemented as {lhs} ==> {rhs}, where both sides are HashMaps of
 * pairs: <AGENT_VAR, AGENT_VAL>
 */
public class NoGood {

    private HashMap<Integer, Integer> lhs;
    private HashMap<Integer, Integer> rhs;

    /**
     * The default (empty) constructor
     */
    public NoGood() {
        lhs = new HashMap<>();
        rhs = new HashMap<>();
    }

    /**
     * Generate a simple NoGood from two pairs of <variable, value>
     *
     * @param lhAgent
     * @param lhVal
     * @param rhAgent
     * @param rhVal
     */
    public NoGood(int lhAgent, int lhVal, int rhAgent, int rhVal) {
        lhs.put(lhAgent, lhVal);
        rhs.put(rhAgent, rhVal);
    }

    /**
     * Generate a NoGood out of an agent view and a list of neighbors
     *
     * @param agentView
     * @param neighbors This constructor will generate a new NoGood where the
     * lowest priority agent (maximal id) will be in the rhs and the rest are in
     * the lhs
     */
    public NoGood(Assignment agentView, Set<Integer> neighbors) {
        lhs = new HashMap<>();
        rhs = new HashMap<>();

        if (agentView == null || neighbors == null) {
            panic("Attempting to generate a NoGood with illegal neighbors or agentview");
        }

        int maxId = -1;
        for (int ai : neighbors) {
            if (ai > maxId) {
                if (maxId != -1) {
                    lhs.put(maxId, rhs.remove(maxId));
                }
                maxId = ai;
                rhs.put(ai, agentView.getAssignment(ai));
            } else {
                lhs.put(ai, agentView.getAssignment(ai));
            }
        }
    }

    /**
     * Add a pair {ai=xi} to the lhs
     *
     * @param variable - the variable whose value we are adding
     * @param value - the value to add
     */
    public void addL(int variable, int value) {
        if (lhs.containsKey(variable)) {
            throw new PanicedAgentException("Adding an already existing variable to the NoGood.");
        }
        if (rhs.containsKey(variable)) {
            throw new PanicedAgentException("Adding to the lhs a variable from the rhs.");
        }
        lhs.put(variable, value);
    }

    /**
     * Add a pair {ai=xi} to the lhs
     *
     * @param disallowedPair - the variable - value pair we are adding
     */
    public void addL(Map.Entry<Integer, Integer> disallowedPair) {
        addL(disallowedPair.getKey(), disallowedPair.getValue());
    }

    /**
     * Add a pair {ai =/= xi} to the rhs (note that we are just adding a pair
     * and not an inequality sign)
     *
     * @param variable - the variable whose value we are adding
     * @param value - the value to add
     */
    public void addR(int variable, int value) {
        if (rhs.size() > 0) {
            throw new PanicedAgentException("Adding a variable to the rhs when there already exist one.");
        }
        if (lhs.containsKey(variable)) {
            throw new PanicedAgentException("Adding to the rhs a variable from the lhs.");
        }
        rhs.put(variable, value);
    }

    /**
     * Get the value of a specific variable in this nogood
     *
     * @param variable
     * @return
     */
    public Integer valueOf(int variable) {
        Integer res = lhs.get(variable);
        if (res == null) {
            return rhs.get(variable);
        }
        return res;
    }

    /**
     * Get the list of all agents in this nogood
     *
     * @return
     */
    public HashSet<Integer> getAgents() {
        HashSet<Integer> res = new HashSet<>(lhs.keySet());
        res.addAll(rhs.keySet());
        return res;
    }

    /**
     * Get the list of all agents in the lhs of this nogood
     *
     * @return
     */
    public HashSet<Integer> getLhsAgents() {
        return new HashSet<>(lhs.keySet());
    }

    /**
     * Get the only entry in rhs
     *
     * @return
     */
    public Map.Entry<Integer, Integer> getR() {
        if (rhs.size() > 1) {
            throw new PanicedAgentException("rhs contains more than one item.");
        }
        if (rhs.size() == 0) {
            throw new PanicedAgentException("rhs contains no items.");
        }
        return rhs.entrySet().iterator().next();
    }

    /**
     * Remove the only entry in rhs
     *
     * @return
     */
    public Map.Entry<Integer, Integer> removeR() {
        Map.Entry<Integer, Integer> res = getR();
        rhs.clear();
        return res;
    }

    /**
     * Get the lhs of the nogood
     *
     * @return
     */
    public Set<Map.Entry<Integer, Integer>> getL() {
        return lhs.entrySet();
    }

    /**
     * Find the state of the NoGood.
     *
     * @return true if there is an empty nogood explanation on the rhs (should
     * not happen in nash-adcop-unstructured)
     */
    public boolean isEmpty() {
        return (rhs.size() == 0);
    }

    @Override
    public String toString() {
        return "NoGood [lhs=" + lhs + ", rhs=" + rhs + "]";
    }
}
