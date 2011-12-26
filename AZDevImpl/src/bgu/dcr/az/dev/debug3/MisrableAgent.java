/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Variable;

/**
 *
 * @author bennyl
 */
@Algorithm(name="MSR")
public class MisrableAgent extends SimpleAgent{

    @Variable(name="b", description="test1", defaultValue="false")
    boolean b = false;
    
    @Variable(name="i", description="test1", defaultValue="100")
    int i = 100;
    
    @Variable(name="l", description="test1", defaultValue="999")
    long l = 999;
    
    @Variable(name="s", description="test1", defaultValue="Hi!")
    String s = "Hi!";
    
    @Variable(name="c", description="test1", defaultValue="&")
    char c = '&';
    
    @Variable(name="d", description="test1", defaultValue="13564498.6546")
    double d = 13564698.6546;
    
    @Override
    public void start() {
        log("start!");
        finish();
    }
    
}
