/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra.stat;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.infra.Configurable;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Test;

/**
 *
 * @author bennyl
 */
public interface StatisticCollector<T extends DBRecord> extends Configurable{
    
    VisualModel analyze(Database db, Test r);
    
    void hookIn(Agent[] a, Execution ex); //TODO - REPLACE WITH EXECUTION VIEW
    
    void submit(T record);
    
    String getName();
}
