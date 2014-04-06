/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.execution;

import bgu.dcr.az.common.deepcopy.DeepCopyable;
import bgu.dcr.az.dcr.api.Agent;
import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.api.problems.Problem;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author User
 */
public class CPSolution implements DeepCopyable {

    private Integer finalCost;
    private final ConcurrentHashMap<Integer, Integer> assignment;
    private State state;
    private final Problem globalProblem;

    public CPSolution(Problem problem) {
        state = State.SOLUTION;
        assignment = new ConcurrentHashMap<>();
        finalCost = null;
        this.globalProblem = problem;
    }

    public CPSolution(Problem problem, Assignment assignment) {
        this(problem);
        for (Map.Entry<Integer, Integer> a : assignment.getAssignments()) {
            this.assignment.put(a.getKey(), a.getValue());
        }
    }
    
    public void setStateNoSolution() {
        this.state = State.NO_SOLUTION;
    }

    public void setFinalCost(int cost) {
        finalCost = cost;
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
        return finalCost == null ? new Assignment(assignment).calcCost(globalProblem) : finalCost;
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
            default:
                throw new AssertionError(state.name());
        }

        return sb.toString();
    }

    @Override
    public Object deepCopy() {
        CPSolution res = new CPSolution(this.globalProblem);
        res.assignment.putAll(this.assignment);
        res.state = state;
        res.finalCost = finalCost;

        return res;
    }

    public static CPSolution newNoSolution(Problem p){
        CPSolution solution = new CPSolution(p);
        solution.setStateNoSolution();
        return solution;
    }
    
    public enum State {

        NO_SOLUTION,
        SOLUTION,
    }
}
