/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.problems;

import bgu.dcr.az.dcr.api.Assignment;

/**
 *
 * @author bennyl
 */
public interface KAryConstraint {

    void getCost(Assignment a, ConstraintCheckResult result);

    /**
     * @return an array of the variables that concern this constraint
     */
    int[] getParicipients();
}
