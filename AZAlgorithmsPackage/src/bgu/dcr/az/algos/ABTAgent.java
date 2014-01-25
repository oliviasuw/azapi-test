package bgu.dcr.az.algos;

import bgu.dcr.az.anop.alg.Algorithm;
import bgu.dcr.az.anop.alg.WhenReceived;
import bgu.dcr.az.api.Message;
import java.util.HashSet;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.tools.Explanation;
import bgu.dcr.az.tools.Explanations;

@Algorithm(name = "ABT", useIdleDetector = true)
public class ABTAgent extends SimpleAgent {

    private Assignment agentView;
    private Explanations nogoods;
    private HashSet<Integer> constrainedAgentsAfterCurrent;
    private HashSet<Integer> constrainedAgentsBeforeCurrent;

    private static volatile Boolean isFinished;

    private Integer currentValue;

    @Override
    public void start() {
        if (isFirstAgent()) {
            isFinished = false;
        }

        agentView = new Assignment();

        nogoods = new Explanations(this);

        generateConstrainedAgentsLists();

        currentValue = null;

        checkAgentView(true);
    }

    private void generateConstrainedAgentsLists() {
        Integer current = getId();
        constrainedAgentsAfterCurrent = new HashSet<Integer>();
        constrainedAgentsBeforeCurrent = new HashSet<Integer>();

        for (Integer neighbor : getProblem().getNeighbors(current)) {
            if (current < neighbor) {
                constrainedAgentsAfterCurrent.add(neighbor);
            }
            if (current > neighbor) {
                constrainedAgentsBeforeCurrent.add(neighbor);
            }
        }
    }

    @WhenReceived("ADD_LINK")
    public void handleADD_LINK(Integer neighbor) {
// System.out.println(System.currentTimeMillis() + " Received: ADD_LINK(" + neighbor + ") at " + getId());
        constrainedAgentsAfterCurrent.add(neighbor);
        // TODO: repare!!!
        try {
//System.out.println(System.currentTimeMillis() + " Sent: OK?(" + getId() + "," + currentValue + ") to " + neighbor);
            send("OK?", getId(), currentValue).to(neighbor);
        } catch (Exception e) {
//  System.err.println("Agent: " + getId() + " Value: " + currentValue + " Neighboor: " + neighbor);
            panic("Agent: " + getId() + " Value: " + currentValue + " Neighboor: " + neighbor);
        }
    }

    @WhenReceived("OK?")
    public void handleOK(Integer var, Integer val) {
//System.out.println(System.currentTimeMillis() + " Received: OK?(" + var + "," + val + ") at " + getId());
        updateAgentView(var, val);

        checkAgentView(false);
    }

    @WhenReceived("NO_GOOD")
    public void handleNO_GOOD(Integer var, Explanation nogood) {
        Integer current = getId();
//System.out.println(System.currentTimeMillis() + " Received: NO_GOOD(" + var + "," + nogood + ") at " + current);

//    	System.out.println("NOGOOD START Agent: " + getId() + " value: " + currentValue );
//    	System.out.println("NOGOOD START Agent: " + getId() + "Agent view: " + agentView);
//    	System.out.println("NOGOOD START Agent: " + getId() + "Noogood: " + nogood);
        if (isCoherent(nogood, true)) {
            checkAddLink(nogood);

            nogoods.getExplanation(nogood.getEliminatedValue()).setExplanation(nogood);

    		//submitCurrentAssignment(null);
            checkAgentView(true);
        } else {
            if (nogood.getEliminatedValue().equals(currentValue)) {
//  System.out.println(System.currentTimeMillis() + " Sent: OK?(" + current + "," + currentValue + ") to " + var);
                send("OK?", current, currentValue).to(var);
            }
        }

//    	System.err.println("NOGOOD FINISH Agent: " + getId() + " finished with value: " + currentValue);
//    	System.err.println("NOGOOD FINISH Agent: " + getId() + "Agent view: " + agentView + " finished");
//    	System.err.println("NOGOOD FINISH Agent: " + getId() + "Nogood: " + nogoods.getExplanation(nogood.getEliminatedValue()) + " finished");    	    	
    }

    private void updateAgentView(Integer var, Integer val) {
        if (val == null) {
            agentView.unassign(var);
        } else {
            agentView.assign(var, val);
        }

        for (Explanation expl : nogoods) {
            if (!isCoherent(expl, false)) {
                expl.clear();
            }
        }
    }

    private void checkAgentView(boolean resetValue) {
        if (resetValue || currentValue == null || !isConsistent()) {
            Integer newValue = chooseValue();

            if (newValue == null) {
                backtrack();
            } else {
                currentValue = newValue;

                if (!isFinished) {
                    assign(newValue);

                    if (!newValue.equals(currentValue)) {
                        panic("Value submittion error at agent: " + getId() + " current value: " + currentValue + " submitted value: " + newValue);
                    }
                }

//System.out.println(System.currentTimeMillis() + " Sent: OK?(" + getId() + "," + currentValue + ") to " + constrainedAgentsAfterCurrent);

                send("OK?", getId(), currentValue).toAll(constrainedAgentsAfterCurrent);
            }
        }
    }

    private Integer chooseValue() {
        Integer current = getId();

        for (Integer val : nogoods.getNonEliminatedValues()) {
            for (Integer var : agentView.assignedVariables()) {
                if (isConstrained(current, var)
                        && getConstraintCost(current, val, var, agentView.getAssignment(var)) != 0) {
                    nogoods.getExplanation(val).setExplanation(var, agentView.getAssignment(var));
                    break;
                }
            }

            if (nogoods.isConsistent(val)) {
                return val;
            }
        }

        return null;
    }

    private void checkAddLink(Explanation expl) {
        Integer current = getId();

        for (Integer var : expl.getExplanationVariables()) {
            if (var.equals(current)) {
                panic("Illegal explanation " + expl + " at agent " + current);
            }

            if (!constrainedAgentsBeforeCurrent.contains(var)) {
//  System.out.println(System.currentTimeMillis() + " Sent: ADD_LINK(" + current + ") to " + var);
                send("ADD_LINK", current).to(var);

                constrainedAgentsBeforeCurrent.add(var);

                updateAgentView(var, expl.getExplanationValue(var));
            }
        }
    }

    private boolean isCoherent(Explanation expl, boolean checkCurrent) {
        Integer current = getId();

        for (Integer var : constrainedAgentsBeforeCurrent) {
            if (expl.contains(var)) {
                if (!agentView.isAssigned(var)
                        || !expl.getExplanationValue(var).equals(agentView.getAssignment(var))) {
                    return false;
                }
            }
        }

        if (expl.contains(current)) {
            panic("Illegal explanation: " + expl + " at agent: " + current);
        }

        if (checkCurrent && !expl.getEliminatedValue().equals(currentValue)) {
            return false;
        }

        return true;
    }

    private boolean isConsistent() {
        Integer current = getId();

        if (!nogoods.isConsistent(currentValue)) {
            return false;
        }

        for (Integer var : agentView.assignedVariables()) {
            if (isConstrained(current, var)
                    && getConstraintCost(current, currentValue, var, agentView.getAssignment(var)) != 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onIdleDetected() {
        finish(currentValue);
    }
    
    private void backtrack() {
        Explanation nogood = nogoods.generateNoGood();

        if (nogood.isEmpty()) {
            isFinished = true;

            finishWithNoSolution();
        } else {
//System.out.println(System.currentTimeMillis() + " Sent: NO_GOOD(" + getId() + "," + nogood + ") to " + nogood.getEliminatedVariable());
            send("NO_GOOD", getId(), nogood).to(nogood.getEliminatedVariable());

//			System.out.println("BACKTRACK START Agent: " + getId() + "remove " + agentView);
//			System.out.println("BACKTRACK START Agent: " + getId() + nogoods);
            updateAgentView(nogood.getEliminatedVariable(), null);

            checkAgentView(true);
//			System.err.println("BACKTRACK FINISH Agent: " + getId() + " after remove " + agentView);
//			System.err.println("BACKTRACK FINISH Agent: " + getId() + nogoods);
//			System.err.println("BACKTRACK FINISH Agent: " + getId() + " value: " + currentValue);
        }
    }

}
