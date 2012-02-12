/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.stat;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.Hooks.BeforeCallingFinishHook;
import bgu.dcr.az.api.Hooks.BeforeMessageProcessingHook;
import bgu.dcr.az.api.Hooks.BeforeMessageSentHook;
import bgu.dcr.az.api.Hooks.TerminationHook;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.api.infra.stat.DBRecord;
import bgu.dcr.az.api.infra.stat.Database;
import bgu.dcr.az.api.infra.stat.VisualModel;
import bgu.dcr.az.api.infra.stat.vmod.LineVisualModel;
import bgu.dcr.az.impl.stat.NCSCStatisticCollector.NCSCRecord;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name="ncsc-sc")
public class NCSCStatisticCollector extends AbstractStatisticCollector<NCSCRecord> {

    long[] ncsc;
    
    @Override
    public VisualModel analyze(Database db, Test r) {
        String query = "select AVG(ncsc) as avg, rVar, ALGORITHM_INSTANCE from NCSC where TEST = '" + r.getName() + "' group by ALGORITHM_INSTANCE, rVar order by rVar";
        LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "Avg(NCSC)", "NCSC");
        try {
            ResultSet rs = db.query(query);
            while (rs.next()){
                line.setPoint(rs.getString("ALGORITHM_INSTANCE"), rs.getFloat("rVar"), rs.getFloat("avg"));
            }
            return line;
        } catch (SQLException ex) {
            Logger.getLogger(NCCCStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public void hookIn(final Agent[] agents, Execution ex) {
        System.out.println("NCSC Statistic Collector registered");
        
        ncsc = new long[agents.length];
        final double rvar = ex.getTest().getCurrentVarValue();
        
        for (Agent a : agents){
            a.hookIn(new BeforeMessageProcessingHook() {

                @Override
                public void hook(Agent a, Message msg) {
                    long newNcsc = (Long) msg.getMetadata().get("ncsc");
                    ncsc[a.getId()] = Math.max(newNcsc, ncsc[a.getId()]);
                    ncsc[a.getId()]++;
                }
            });
            
            a.hookIn(new BeforeMessageSentHook() {

                @Override
                public void hook(Agent a, Message msg) {
                    msg.getMetadata().put("ncsc", ncsc[a.getId()]);
                }
            });
        }
        
        ex.hookIn(new TerminationHook() {

            @Override
            public void hook() {
                submit(new NCSCRecord(Agt0DSL.max(ncsc), rvar, agents[0].getAlgorithmName()));
            }
        });
        
    }

    @Override
    public String getName() {
        return "Number Of Concurrent Steps Of Computation";
    }
    
    public static class NCSCRecord extends DBRecord{

        float ncsc;
        double rVar;
        String algorithm;
        
        public NCSCRecord(float ncsc, double rVar, String algorithm) {
            this.ncsc = ncsc;
            this.rVar = rVar;
            this.algorithm = algorithm;
        }
        
        @Override
        public String provideTableName() {
            return "NCSC";
        }
        
    }
    
}
