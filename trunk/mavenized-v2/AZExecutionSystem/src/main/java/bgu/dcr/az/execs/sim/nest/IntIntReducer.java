/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.execs.sim.nest;

/**
 *
 * @author User
 */
@FunctionalInterface
public interface IntIntReducer {
    int reduce(int initial, int i, int vi);
}
