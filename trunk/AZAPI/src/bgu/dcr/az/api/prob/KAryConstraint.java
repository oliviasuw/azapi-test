/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob;

import bgu.dcr.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
public interface KAryConstraint {
    void getCost(Assignment a, ConstraintCheckResult result);
    int[] getParicipients();
}
