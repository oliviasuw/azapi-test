/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;

/**
 *
 * @author bennyl
 */
@Algorithm(name="MSR")
public class MisribleAgent extends SimpleAgent {

    @Override
    public void start() {
        System.out.println(getProblem().toString());
        System.out.println("Started");
        finish();
    }
    
}
