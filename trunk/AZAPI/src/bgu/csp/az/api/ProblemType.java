/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api;

import bgu.csp.az.api.infra.CorrectnessTester;

/**
 *
 * @author bennyl
 */
public enum ProblemType {

    /**
     * Constraint Setisfaction Problem
     */
    DCSP(null),
    /**
     * Constraint Optimization Problem
     */
    DCOP(null),
    CONNECTED_DCOP(null),
    ADCOP(null);
    private CorrectnessTester tester;

    private ProblemType(CorrectnessTester tester) {
        this.tester = tester;
    }

    public CorrectnessTester getCorrectnessTester() {
        return tester;
    }
}
