package bgu.dcr.az.dev.debug3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import bgu.dcr.az.api.tools.Assignment;

/**
 * @author alongrub A NoGood Store
 */
public class NoGoodStore {

    private HashMap<Integer, HashSet<NoGood>> ngStore = null;
    private final int ID;

    public NoGoodStore(int ownerId) {
        ngStore = new HashMap<>();
        ID = ownerId;
    }

    /**
     * Add an eliminating explanation
     *
     * @param value - the value being removed from the agent's domain
     * @param ai - the offending agent
     * @param vali - the offenders value
     */
    public void addEE(int value, int ai, int vali) {
        HashSet<NoGood> hs = ngStore.get(value);
        if (hs == null) {
            hs = new HashSet<>();
        }
        hs.add(new NoGood(ai, vali, ID, value));
        ngStore.put(value, hs);
    }

    /**
     * Add an eliminating explanation
     *
     * @param ng
     */
    public void addEE(NoGood ng) {
        int removedValue = ng.getR().getValue();
        HashSet<NoGood> hs = ngStore.get(removedValue);
        if (hs == null) {
            hs = new HashSet<>();
        }
        hs.add(ng);
        ngStore.put(removedValue, hs);
    }

    /**
     * Check the agentview against existing nogood store if a nogood's lhs
     * contains a var-val pair which is inconsistent with the agentview we
     * remove it. Using iterators to avoid concurrent modification errors
     *
     * @param av
     */
    public void makeCoherent(Assignment av) {
        Iterator<Integer> valuesIter = ngStore.keySet().iterator();
        while (valuesIter.hasNext()) {
            int keys = valuesIter.next();
            Iterator<NoGood> ngIter = ngStore.get(keys).iterator();
            while (ngIter.hasNext()) {
                NoGood ng = ngIter.next();
                for (int vars : ng.getLhsAgents()) {
                    if (!av.isAssigned(vars) || !ng.valueOf(vars).equals(av.getAssignment(vars))) {
                        ngIter.remove();
                        break;
                    }
                }
            }
            if (ngStore.get(keys).isEmpty()) {
                valuesIter.remove();
            }
        }
    }

    /**
     * check if an agent contains a valid explanation for the removal of value
     * from the current domain. Note: an empty (non null) explanation is also
     * valid!
     *
     * @param value
     * @return false if value has no NoGood explanation for the removal of
     * "value" and true otherwise
     */
    public boolean containsEEfor(int value) {
        HashSet<NoGood> hs = ngStore.get(value);
        return hs != null;
    }

    /**
     * Generate a new nogood composed of all agents currently existing in the
     * ngstore The rhs is the highest agent in the set (this is the agent who
     * will receive this new nogood) and the lhs is composed of all remanining
     * agents.
     *
     * @return
     */
    public NoGood solve() {
        NoGood res = new NoGood();
        TreeMap<Integer, Integer> agents = new TreeMap<>();
        for (int keys : ngStore.keySet()) {
            for (NoGood ng : ngStore.get(keys)) {
                for (int i : ng.getLhsAgents()) {
                    agents.put(i, ng.valueOf(i));
                }
            }
        }
        int maxId = agents.lastKey();
        for (int ai : agents.keySet()) {
            if (ai == maxId) {
                res.addR(ai, agents.get(ai));
            } else {
                res.addL(ai, agents.get(ai));
            }
        }
        
        return res;
    }
}
