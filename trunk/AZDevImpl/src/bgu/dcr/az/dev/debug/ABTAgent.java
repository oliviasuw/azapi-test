package bgu.dcr.az.dev.debug;

import java.util.HashSet;


import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;

@Algorithm(problemType = ProblemType.DCSP, name = "ABT", useIdleDetector = true)
public class ABTAgent extends SimpleAgent {

    private Assignment agentView;
    private Explanations nogoods;
    private HashSet<Integer> constrainedAgentsAfterCurrent;
    private HashSet<Integer> constrainedAgentsBeforeCurrent;
    private Integer currentValue;

    @Override
    public void start() {
//        if (isFirstAgent()){
//            System.out.println(getProblem().toString());
//        }
        agentView = new Assignment();

        nogoods = new Explanations(this);

        generateConstrainedAgentsLists();

        currentValue = null;

        checkAgentView();
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
        constrainedAgentsAfterCurrent.add(neighbor);

        send("OK?", getId(), currentValue).to(neighbor);
    }

    @WhenReceived("OK?")
    public void handleOK(Integer var, Integer val) {
        updateAgentView(var, val);

        checkAgentView();
    }

    @WhenReceived("NO_GOOD")
    public void handleNO_GOOD(Integer var, Explanation nogood) {
        Integer current = getId();

        if (isCoherent(nogood, true)) {
            checkAddLink(nogood);

            nogoods.getExplanation(nogood.getEliminatedValue()).setExplanation(nogood);

            currentValue = null;

            checkAgentView();
        } else {
            if (nogood.getEliminatedValue().equals(currentValue)) {
                send("OK?", current, currentValue).to(var);
            }
        }
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

    private void checkAgentView() {
        if (currentValue == null || !isConsistent()) {
            Integer newValue = chooseValue();

            if (newValue == null) {
                backtrack();
            } else {
                currentValue = newValue;

                submitCurrentAssignment(currentValue);

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

    private void backtrack() {
        Explanation nogood = nogoods.generateNoGood();

        if (nogood.isEmpty()) {
            finishWithNoSolution();
        } else {
            send("NO_GOOD", getId(), nogood).to(nogood.getEliminatedVariable());

            updateAgentView(nogood.getEliminatedVariable(), null);

            currentValue = null;

            checkAgentView();
        }
    }

    @Override
    public void onIdleDetected() {
        if (isFirstAgent()) {
            finishWithAccumulationOfSubmitedPartialAssignments();
        }
    }
}