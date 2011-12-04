package bgu.dcr.az.dev.debug;

import java.util.Random;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name = "DSA_A", searchType = SearchType.SYNCHRONOUS, useIdleDetector = false)
public class DSAA extends SimpleAgent {

    private final double p = 0.5;
    private final int cycles = 10;
    private Assignment localView;

    @Override
    public void start() {
        if (isFirstAgent()) {
            log(getProblem().toString());
        }
        localView = new Assignment();
        Random rand = new Random(123);
        int val = rand.nextInt(getDomain().size());
        submitCurrentAssignment(val);
        send("ValueMessage", val).toNeighbores(getProblem());
    }

    @WhenReceived("ValueMessage")
    public void handleValueMessage(int value) {
        localView.assign(getCurrentMessage().getSender(), value);
        log("(" + getSystemTimeInTicks() + ")    [" + getId() + "=" + getSubmitedCurrentAssignment() + "], "
                + localView.toString());
    }

    @Override
    public void onMailBoxEmpty() {
        if (getSystemTimeInTicks() == cycles && isFirstAgent()) {
            finishWithAccumulationOfSubmitedPartialAssignments();
            return;
        }

        Integer updatedAssignment = findImprovingAssignment1();
        if (updatedAssignment != null && Math.random() < p) {
            log("(" + getSystemTimeInTicks() + ")    " + getId() + " changed its assignment!");
            submitCurrentAssignment(updatedAssignment);
            send("ValueMessage", updatedAssignment).toNeighbores(getProblem());
        }
    }

    @Override
    protected Message beforeMessageProcessing(Message msg) {
        log("got " + msg);
        return msg;
    }

    @Override
    protected void beforeMessageSending(Message m) {
        log("send " + m);
    }
    
    private Integer findImprovingAssignment() {
        Integer res = null;
        int currentAssignment = getSubmitedCurrentAssignment();
        String s = " current assignment is " + currentAssignment;
        double currentCost = localView.calcAddedCost(getId(), currentAssignment, getProblem());
        log("(" + getSystemTimeInTicks() + ") current cost: " + currentCost);
        double bestCost = currentCost;
        for (int i : getDomain()) {
            double tmpCost = localView.calcAddedCost(getId(), i, getProblem());
            log("(" + getSystemTimeInTicks() + ") i=" + i + " tmpCost: " + tmpCost);
            if (tmpCost > bestCost) {
                res = i;
                bestCost = tmpCost;
            }
        }
        log("(" + getSystemTimeInTicks() + ") " + s + " --> will change to " + res);
        return res;

    }

    private Integer findImprovingAssignment1() {
        Integer res = null;
        int currentAssignment = getSubmitedCurrentAssignment();
        String s = " current assignment is " + currentAssignment;
        double currentCost = 0;
        for (int n : getProblem().getNeighbors(getId())) {
            try {
                currentCost += getProblem().getConstraintCost(getId(), currentAssignment, n, localView.getAssignment(n));
            } catch (Exception ex) {
                System.out.println("HERE");
            }
        }
        log("(" + getSystemTimeInTicks() + ") current cost: " + currentCost);
        double bestCost = currentCost;
        for (int i : getDomain()) {
            if (i == currentAssignment) {
                continue;
            }
            double tmpCost = 0;
            for (int n : getProblem().getNeighbors(getId())) {
                tmpCost += getProblem().getConstraintCost(getId(), i, n, localView.getAssignment(n));
            }
            log("(" + getSystemTimeInTicks() + ") i=" + i + " tmpCost: " + tmpCost);
            if (tmpCost > bestCost) {
                res = i;
                bestCost = tmpCost;
            }
        }
        log("(" + getSystemTimeInTicks() + ") " + s + " --> will change to " + res);
        return res;

    }
}
