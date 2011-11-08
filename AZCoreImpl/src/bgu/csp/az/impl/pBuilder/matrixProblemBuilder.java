/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.pBuilder;

import bgu.csp.az.api.Problem;
import bgu.csp.az.api.pseq.ProblemBuilder;
import bgu.csp.az.impl.prob.MatrixProblem;

/**
 *
 * @author Inna
 */
public class matrixProblemBuilder implements ProblemBuilder {

    private int n;
    private int d;
    private double[][] matrix;

    public matrixProblemBuilder(int n, int d) {
        this.n = n;
        this.d = d;
        this.matrix = new double[n*d][n*d];
        for (int i = 0; i < n * d; i++) {
            for (int j = 0; j < n * d; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    @Override
    public Problem build() {
        return new MatrixProblem(this.matrix, n);
    }
}
