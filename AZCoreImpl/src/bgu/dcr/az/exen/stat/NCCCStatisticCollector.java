/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exenl.stat;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.api.exen.stat.vmod.LineVisualModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "nccc-sc")
public class NCCCStatisticCollector extends AbstractStatisticCollector<NCCCStatisticCollector.NCCCRecord> {

    long[] nccc;
    long[] lastKnownCC;
    String runningVar;

    @Override
    public VisualModel analyze(Database db, Test r) {
        String query = "select AVG(value) as avg, rVar, ALGORITHM_INSTANCE from NCCC where TEST = '" + r.getName() + "' group by ALGORITHM_INSTANCE, rVar order by rVar";
        LineVisualModel line = new LineVisualModel(runningVar, "Avg(NCCC)", "NCCC");
        try {
            ResultSet rs = db.query(query);
            while (rs.next()) {
                line.setPoint(rs.getString("ALGORITHM_INSTANCE"), rs.getFloat("rVar"), rs.getFloat("avg"));
            }
            return line;
        } catch (SQLException ex) {
            Logger.getLogger(NCCCStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void hookIn(final Agent[] agents, final Execution ex) {

        System.out.println("NCCC Statistic Collector registered");

        nccc = new long[agents.length];
        lastKnownCC = new long[agents.length];
        runningVar = ex.getTest().getRunningVarName();
        
        new Hooks.BeforeMessageProcessingHook() {

            @Override
            public void hook(Agent a, Message msg) {
                long newNccc = (Long) msg.getMetadata().get("nccc");

                updateCurrentNccc(a);
                nccc[a.getId()] = max(newNccc, nccc[a.getId()]);
            }
        }.hookInto(ex);

        new Hooks.BeforeMessageSentHook() {

            @Override
            public void hook(Agent a, Message msg) {
                updateCurrentNccc(a);
                msg.getMetadata().put("nccc", nccc[a.getId()]);
            }
        }.hookInto(ex);

        new Hooks.TerminationHook() {

            @Override
            public void hook() {
                submit(new NCCCRecord(ex.getTest().getCurrentVarValue(), max(nccc)));
            }
        }.hookInto(ex);

    }

    @Override
    public String getName() {
        return "Number Of Concurrent Constraint Checks";
    }

    private void updateCurrentNccc(Agent a) {
        long last = lastKnownCC[a.getId()];
        lastKnownCC[a.getId()] = a.getNumberOfConstraintChecks();
        nccc[a.getId()] = nccc[a.getId()] + lastKnownCC[a.getId()] - last;
    }
    
    public long currentNcccOf(int agent){
        return nccc[agent];
    }

    public static class NCCCRecord extends DBRecord {

        double rVar;
        double value;

        public NCCCRecord(double rVar, double value) {
            this.rVar = rVar;
            this.value = value;
        }

        @Override
        public String provideTableName() {
            return "NCCC";
        }
    }
    
}
