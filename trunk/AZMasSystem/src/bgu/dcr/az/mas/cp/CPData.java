/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.mas.exp.AlgorithmDef;
import bgu.dcr.az.mas.impl.HasSolution;

/**
 *
 * @author User
 */
public class CPData implements HasSolution {

    private final CPSolution solution;
    private final Problem problem;
    private final AlgorithmDef algorithm;

    public CPData(CPSolution solution, Problem problem, AlgorithmDef alg) {
        this.solution = solution;
        this.problem = problem;
        this.algorithm = alg;
    }

    public AlgorithmDef getAlgorithm() {
        return algorithm;
    }

    @Override
    public DeepCopyable solution() {
        return solution;
    }

    public Problem getProblem() {
        return problem;
    }

    public CPSolution getSolution() {
        return solution;
    }

}
