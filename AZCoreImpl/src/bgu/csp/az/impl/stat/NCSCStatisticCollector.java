/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.stat;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Hooks.BeforeCallingFinishHook;
import bgu.csp.az.api.Hooks.BeforeMessageProcessingHook;
import bgu.csp.az.api.Hooks.BeforeMessageSentHook;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.stat.DBRecord;
import bgu.csp.az.api.infra.stat.Database;
import bgu.csp.az.api.infra.stat.VisualModel;
import bgu.csp.az.api.infra.stat.vmod.LineVisualModel;
import bgu.csp.az.impl.stat.NCSCStatisticCollector.NCSCRecord;
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
    public VisualModel analyze(Database db, Round r) {
        String query = "select AVG(ncsc) as avg, rVar from NCSC where ROUND = '" + r.getName() + "' group by rVar order by rVar";
        LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "Avg(NCSC)", "NCSC");
        try {
            ResultSet rs = db.query(query);
            while (rs.next()){
                line.setPoint(rs.getFloat("rVar"), rs.getFloat("avg"));
            }
            return line;
        } catch (SQLException ex) {
            Logger.getLogger(NCCCStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public void hookIn(Agent[] agents, Execution ex) {
        System.out.println("NCSC Statistic Collector registered");
        
        ncsc = new long[agents.length];
        final float rvar = ex.getRound().getCurrentVarValue();
        
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
        
        agents[0].hookIn(new BeforeCallingFinishHook() {

            @Override
            public void hook(Agent a) {
                submit(new NCSCRecord(ncsc[0], rvar));
            }
        });
    }
    
    public static class NCSCRecord extends DBRecord{

        long ncsc;
        float rVar;

        public NCSCRecord(long ncsc, float rVar) {
            this.ncsc = ncsc;
            this.rVar = rVar;
        }
        
        @Override
        public String provideTableName() {
            return "NCSC";
        }
        
    }
    
}
