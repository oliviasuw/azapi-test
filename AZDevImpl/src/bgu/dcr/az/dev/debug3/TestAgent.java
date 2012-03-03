/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;

/**
 *
 * @author Inna
 */
@Algorithm(name="__TEST")
public class TestAgent extends SimpleAgent{

    @Override
    public void start() {
        finish(1);
        
    }
    
}
