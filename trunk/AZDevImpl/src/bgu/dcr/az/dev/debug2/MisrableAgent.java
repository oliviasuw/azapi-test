/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug2;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Variable;

/**
 *
 * @author bennyl
 */
@Algorithm(name="MSR")
public class MisrableAgent extends SimpleAgent{

    @Variable(name="b", description="test1")
    boolean b = false;
    
    @Variable(name="i", description="test1")
    int i = 100;
    
    @Variable(name="l", description="test1")
    long l = 999;
    
    @Variable(name="s", description="test1")
    String s = "Hi!";
    
    @Variable(name="c", description="test1")
    char c = '&';
    
    @Variable(name="d", description="test1")
    double d = 13564698.6546;
    
    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
