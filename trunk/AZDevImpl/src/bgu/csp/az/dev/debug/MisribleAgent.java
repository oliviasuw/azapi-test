/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.debug;

import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;

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
