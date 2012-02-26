package bgu.dcr.az.dev.debug3;

import java.util.HashSet;
import java.util.Random;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.ds.ImmutableSet;

//import com.google.common.collect.ImmutableSet;
//import com.google.common.collect.Sets; // guava-libraries
/**
 * Asynchronous Nash BackTracking algorithm
 *
 * @author alongrub NOTE: It is assumed that the domain size of all agents is
 * equal!
 */
@Algorithm(name = "ANT", useIdleDetector = true)
public class ANTAgent extends SimpleAgent {

    @Variable(name = "cache", description = "A boolean value for requesting a caching mechanism", defaultValue = "false")
    private boolean cacheState = false;
    private KArySatConstraint myConstraint = null;
    private Assignment agentView = null;
    private ImmutableSet<Integer> neighbors = null;
    private HashSet<Integer> extNeighbors = null; // extended set of neighbors
    // (those with added links)
    private NoGoodStore ngStore = null;
    private HashSet<Integer> currentDomain = null;
    private Integer currentAssignment = null;
    private boolean isLastAgentInConstraint = true;
    private long learntPrivacyBits = 0;			// used to measure privacy loss

    @Override
    public void start() {
        myConstraint = new KArySatConstraint(getProblem(), getId(), cacheState);
        //log(myConstraint.toString());

        neighbors = new ImmutableSet<>(getProblem().getNeighbors(getId()));
        extNeighbors = new HashSet<>(neighbors);
        for (int i : neighbors) {
            if (i > getId()) {
                isLastAgentInConstraint = false;
                break;
            }
        }
        currentDomain = new HashSet<>(getDomain());
        ngStore = new NoGoodStore(getId());
        agentView = new Assignment();
        Random rand = new Random(1923 + getId());
        int val = rand.nextInt(getDomain().size());
        submitCurrentAssignment(val);
        currentAssignment = val;
        send("ok", val).toNeighbores(getProblem());

        //System.out.println("Agent "+getId()+": assigned val="+currentAssignment+", sending ok");
    }

    private Integer chooseValue() {
        HashSet<Integer> clone = new HashSet<>(currentDomain);
        for (Integer i : clone) {
            if (myConstraint.isConsistentWith(agentView, i)) {
                return i;
            } else {
                NoGood ng = new NoGood(agentView, neighbors);
                ng.addL(ng.removeR());
                ng.addR(getId(), i);
                // ngStore.add(i, ng);
                ngStore.addEE(ng);
                currentDomain.remove(i);
            }
        }
        // At this point we couldn't find one consistent assignment

        return null;
    }

    /**
     * Check the state of the NoGood store with respect to the current agentView
     * and update current domain
     */
    private void updateAgentView() {
        ngStore.makeCoherent(agentView);
        /*
         * add values back to the domain
         */
        for (int i : getDomain()) {
            if (!ngStore.containsEEfor(i) && !currentDomain.contains(i)) {
                currentDomain.add(i);
            } else // assert
            if (ngStore.containsEEfor(i) && currentDomain.contains(i)) {
                panic("Value i="
                        + i
                        + " exist in current domain despit the existance of an explanation."); // +ngStore.getEEfor(i).toString());
            }
        }
    }

    /**
     * Verify consistency of agent view with an assignment and fix if needed
     */
    private void checkAgentView() {

        // if my personal constraint is satisfied I do nothing
        if (currentAssignment != null
                && myConstraint.isConsistentWith(agentView, currentAssignment)) {
            return;
        }

        /*
         * Not consistent - will try to find an alternate assignment which will
         * be consistent, and if I fail - backtrack!
         */
        Integer value = chooseValue();
        if (value == null) {
            backtrack();
        } else {
            currentAssignment = value;
            submitCurrentAssignment(value);
            send("ok", value).toAll(extNeighbors);
            //System.out.println("Agent "+getId()+": assigned val="+currentAssignment+", sending ok");
        }

    }

    /**
     * The backtrack procedure Resolves a new nogood, sends it to the relevant
     * agent, update agent view and find a new assignment
     */
    private void backtrack() {
        NoGood resolvedNG = ngStore.solve();
        if (resolvedNG.isEmpty()) {
            finishWithNoSolution();
            return;
        }
        send("NoGood", resolvedNG).to(resolvedNG.getR().getKey());
        //System.out.println("Agent "+getId()+": sending nogood="+resolvedNG+" to rhs");
        agentView.unassign(resolvedNG.getR().getKey());
        updateAgentView();
        checkAgentView();
    }

    /**
     * The Coherence function Note that unlike the PKC paper I use the
     * intersection and not union of agent sets. This is thanks to Mohamad
     * (http:
     * //hal-lirmm.ccsd.cnrs.fr/docs/00/59/59/21/PDF/TechnicalReport_RR-11017.
     * pdf) who mentions that two assignment sets are coherent if every common
     * variable is assigned the same value in both sets.
     *
     * @param ng
     * @return
     */
    private boolean coherent(NoGood ng, HashSet<Integer> agentSet) {
        HashSet<Integer> intersectSet = new HashSet<>(ng.getAgents());
        intersectSet.retainAll(agentSet);
        for (int var : intersectSet) {
            if (var == getId()) {
                if (!ng.valueOf(var).equals(currentAssignment)) {
                    return false;
                }
            } else if (!agentView.isAssigned(var) || !ng.valueOf(var).equals(agentView.getAssignment(var))) {
                return false;
            }
        }
        return true;
    }

    private void addLinks(NoGood ng) {
        for (int i : ng.getAgents()) /*
         * if i is in ng.lhs and is also not in gamma^-
         */ {
            if (i != ng.getR().getKey() && i < getId()
                    && !extNeighbors.contains(i)) {
                extNeighbors.add(i);
                send("addLink", getId()).to(i);
                agentView.assign(i, ng.valueOf(i));
                updateAgentView();
            }
        }
    }

    /*
     * ------------- Message Handlers -------------
     */
    @WhenReceived("ok")
    public void handleOk(int assignment) {
        //log("Received OK=" + assignment);
        //System.out.println("Agent "+getId()+": received ok assignment="+assignment+" from "+getCurrentMessage().getSender());

        int sender = getCurrentMessage().getSender();
        agentView.assign(sender, assignment);

        /*
         * updateAgentView + checkAgentView
         */
        updateAgentView();
        // if my personal constraint is satisfied I do nothing
        if (currentAssignment != null
                && myConstraint.isConsistentWith(agentView, currentAssignment)) {
            return;
        }

        /*
         * if received an inconsistent set of ok messages from neighboring
         * agents, and I am not the last agent in the constraint (i.e. at least
         * one other agent in (\gamma^+), send out a NoGood to the last agent in
         * the k-ary constraint
         */
        if (!isLastAgentInConstraint) {
            NoGood ng = new NoGood(agentView, neighbors);

            try {
                ng.addL(getId(), currentAssignment);
            } catch (Exception ex) {
                System.out.println("HERE");
            }
            send("NoGood", ng).to(ng.getR().getKey());
            //System.out.println("Agent "+getId()+": sending nogood="+ng+" to rhs");
        } else {
            checkAgentView();
        }
    }

    @WhenReceived("NoGood")
    public void handleNoGood(NoGood ng) {
        //log("Received NoGood=" + ng);
        //System.out.println("Agent "+getId()+": received nogood="+ng+" from "+getCurrentMessage().getSender());

        /*
         * Privacy statistics
         */
        if (getCurrentMessage().getSender() < getId()) {
            learntPrivacyBits++;
        }

        /*
         * get \gamma+
         */
        HashSet<Integer> tmpSet = new HashSet<>();
        for (int i : agentView.assignedVariables()) {
            if (i < getId()) {
                tmpSet.add(i);
            }
        }
        tmpSet.add(getId());

        if (coherent(ng, tmpSet)) {
            addLinks(ng);
            ngStore.addEE(ng);
            currentDomain.remove(ng.getR().getValue()); // have to re-assign a
            // value!
            currentAssignment = null;
            checkAgentView();
        } else if (ng.valueOf(getId()).equals(currentAssignment)) {
            send("ok", currentAssignment).to(getCurrentMessage().getSender());
        }
    }

    @WhenReceived("addLink")
    public void handleAddLink(int agentId) {
        //System.out.println("Agent "+getId()+": received addlink agentId="+agentId+" from "+getCurrentMessage().getSender());
        extNeighbors.add(agentId);
        send("ok", currentAssignment).to(agentId);
    }

    @Override
    public void onIdleDetected() {
        //finish(currentAssignment);
        if (isFirstAgent()) {
            finishWithAccumulationOfSubmitedPartialAssignments();
        }
    }
}
