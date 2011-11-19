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
    DCSP,
    /**
     * Constraint Optimization Problem
     */
    DCOP,
    ADCOP;
}
