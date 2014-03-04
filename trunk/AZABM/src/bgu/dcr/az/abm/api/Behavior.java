/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.abm.api;

/**
 *
 * @author Eran
 */
public interface Behavior {
    void behave();
    void init(ABMAgent agent, World w);
}
