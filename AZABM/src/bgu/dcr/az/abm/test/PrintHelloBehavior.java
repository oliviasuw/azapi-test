/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.test;

import bgu.dcr.az.abm.impl.AbstractBehavior;
import bgu.dcr.az.abm.impl.Require;

/**
 *
 * @author bennyl
 */
public class PrintHelloBehavior extends AbstractBehavior {

    @Require
    Talker talker;

    @Override
    public void behave() {
        System.out.println("Agent: " + agent().getId() + " Saying hello!");
    }

}
