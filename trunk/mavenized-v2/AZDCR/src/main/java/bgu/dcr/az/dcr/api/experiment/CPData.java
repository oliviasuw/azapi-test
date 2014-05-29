/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.experiment;

import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.execs.exps.exe.SimulationData;

/**
 *
 * @author User
 */
public class CPData implements SimulationData {

    private final CPSolution solution;
    private final Problem problem;
    private final AlgorithmDef algorithm;
    private final double rvar;
    private String rvarName;

    public CPData(CPSolution solution, Problem problem, AlgorithmDef alg, String runningVarName, double runningVarValue) {
        this.solution = solution;
        this.problem = problem;
        this.algorithm = alg;
        this.rvar = runningVarValue;
        this.rvarName = runningVarName;
    }

    public double getRunningVarValue() {
        return rvar;
    }

    public String getRunningVar() {
        return rvarName;
    }

    public AlgorithmDef getAlgorithm() {
        return algorithm;
    }

    public Problem getProblem() {
        return problem;
    }

    public CPSolution getSolution() {
        return solution;
    }

    @Override
    public CPSolution currentSolution() {
        return solution;
    }

}
