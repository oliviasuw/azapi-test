/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import bgu.dcr.az.api.Continuation;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.tools.NestableTool;

/**
 *
 * @author Administrator
 */
public class VeryNestedAgent extends SimpleAgent{

    NestableTool[] nts;
    
    
    
    @Override
    public void start() {
        nts = new NestableTool[20]; 
        //TODO: initialize the nesteable tools
        
        calculate(1, new Continuation() {

            @Override
            public void doContinue() {
                System.out.println("Done...");
            }
        });
    }

    private void calculate(final int i, final Continuation k) {
        if (i == nts.length) return;
        
        nts[i].calculate(this).andWhenDoneDo(new Continuation() {

            @Override
            public void doContinue() {
                k.doContinue();
                calculate(i+1, k);
            }
        });
    }
    
    
    
    
    
}
