/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra.stat;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Round;

/**
 *
 * @author bennyl
 */
public interface StatisticCollector<T extends DBRecord> extends Configureable{
    
    VisualModel analyze(Database db, Round r);
    
    void hookIn(Agent[] a, Execution ex); //TODO - REPLACE WITH EXECUTION VIEW
    
    void submit(T record);
    
    String getName();
}
