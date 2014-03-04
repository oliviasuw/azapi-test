/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.abm.exen;

import bgu.dcr.az.abm.api.World;
import bgu.dcr.az.abm.exen.info.AgentDataChangedInfo;
import bgu.dcr.az.abm.exen.info.AgentsChangedInfo;
import bgu.dcr.az.abm.exen.info.BehaviorsChangedInfo;
import bgu.dcr.az.abm.exen.info.TickInfo;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.ExecutionService;
import bgu.dcr.az.mas.impl.InitializationException;

/**
 *
 * @author Eran
 */
public class BehaviorDistributer implements ExecutionService<World> {

    World w; 
    
    @Override
    public void initialize(Execution<World> ex) throws InitializationException {

        this.w = ex.data();
        
//        w.agents().forEach();
        
        
        ex.informationStream().listen(AgentsChangedInfo.class, achanged -> {
            
        });
                
        ex.informationStream().listen(BehaviorsChangedInfo.class, bchanged -> {

        });

        ex.informationStream().listen(AgentDataChangedInfo.class, adchanged -> {

        });

        ex.informationStream().listen(TickInfo.class, tinofo -> {

        });

    }

    private void handleAgentAdded(int agentId){
        
    }
    
}
