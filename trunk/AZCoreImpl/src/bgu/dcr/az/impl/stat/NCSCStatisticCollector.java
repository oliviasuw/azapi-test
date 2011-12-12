/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.stat;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks.BeforeCallingFinishHook;
import bgu.dcr.az.api.Hooks.BeforeMessageProcessingHook;
import bgu.dcr.az.api.Hooks.BeforeMessageSentHook;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Round;
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
    public VisualModel analyze(Database db, Round r) {
        String query = "select AVG(ncsc) as avg, rVar, algorithm from NCSC where ROUND = '" + r.getName() + "' group by algorithm, rVar order by rVar";
        LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "Avg(NCSC)", "NCSC");
        try {
            ResultSet rs = db.query(query);
            while (rs.next()){
                line.setPoint(rs.getString("algorithm"), rs.getFloat("rVar"), rs.getFloat("avg"));
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
        final double rvar = ex.getRound().getCurrentVarValue();
        
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
                submit(new NCSCRecord(ncsc[0], rvar, a.getAlgorithmName()));
            }
        });
    }

    @Override
    public String getName() {
        return "Number Of Concurent Steps Of Computation";
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
