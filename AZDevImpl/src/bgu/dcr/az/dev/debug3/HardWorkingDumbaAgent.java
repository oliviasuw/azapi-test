/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
@Algorithm(name="HARD_WORKING_DUMBA", useIdleDetector=true)
public class HardWorkingDumbaAgent extends SimpleAgent{
    Assignment cpa;
    
    @Override
    public void start() {
        if (isFirstAgent()){
            cpa = new Assignment();
            assignCpa();
        }
    }

    @Override
    public void onIdleDetected() {
        panic("ONOES!!!");
    }
    
    

    private void assignCpa() {
        cpa.assign(getId(), random(getDomain()));
        if (isLastAgent()){
            finish(cpa);
        }else {
            send("CPA", cpa).toNextAgent();
        }
    }
    
    @WhenReceived("CPA")
    public void handleCPA(Assignment sentCpa){
        this.cpa = sentCpa;
        assignCpa();
    }
    
}
