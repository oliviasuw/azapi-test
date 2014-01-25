/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author User
 */
public class Solution {

    private final ConcurrentHashMap<Integer, Integer> assignment;
    private State state;
    private final Problem globalProblem;
    private Exception crushReason;

    public Solution(Problem problem) {
        state = State.SOLUTION;
        assignment = new ConcurrentHashMap<>();
        this.globalProblem = problem;
    }

    public Exception getCrushReason() {
        return crushReason;
    }

    public void setStateCrushed(Exception crushReason) {
        this.crushReason = crushReason;
        this.state = State.CRUSHED;
    }

    public void setStateNoSolution() {
        this.state = State.NO_SOLUTION;
    }

    public void assign(int i, int vi) {
        assignment.put(i, vi);
    }

    public void unassign(int i) {
        assignment.remove(i);
    }

    public Integer assignmentOf(int id) {
        return this.assignment.get(id);
    }

    public void assignAll(Assignment ass) {
        for (Map.Entry<Integer, Integer> a : ass.getAssignments()) {
            assign(a.getKey(), a.getValue());
        }
    }

    public ConcurrentHashMap<Integer, Integer> getAssignment() {
        return assignment;
    }

    public State getState() {
        return state;
    }

    public double getCost() {
        return globalProblem.calculateGlobalCost(new Assignment(assignment));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (state) {
            case NO_SOLUTION:
                sb.append("No Solution Found");
                break;
            case SOLUTION:
                sb.append("Solution Found With Assignment: ").append(assignment).append(" Cost:").append(getCost());
                break;
            case CRUSHED:
                sb.append("Execution Crushed With reason: ").append(crushReason.getMessage());
                break;
            default:
                throw new AssertionError(state.name());
        }

        return sb.toString();
    }

    public enum State {

        NO_SOLUTION,
        SOLUTION,
        CRUSHED;
    }
}
