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
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.api.infra.stat.DBRecord;
import bgu.dcr.az.api.infra.stat.Database;
import bgu.dcr.az.api.infra.stat.VisualModel;
import bgu.dcr.az.api.infra.stat.vmod.LineVisualModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "nccc-sc", display="Avarage NCCC")
public class NCCCStatisticCollector extends AbstractStatisticCollector<NCCCStatisticCollector.NCCCRecord> {

    long[] nccc;
    long[] lastKnownCC;
    String runningVar;

    @Override
    public VisualModel analyze(Database db, Test r) {
        String query = "select AVG(value) as avg, rVar, algorithm from NCCC where TEST = '" + r.getName() + "' group by algorithm, rVar order by rVar";
        LineVisualModel line = new LineVisualModel(runningVar, "Avg(NCCC)", "NCCC");
        try {
            ResultSet rs = db.query(query);
            while (rs.next()) {
                line.setPoint(rs.getString("algorithm"), rs.getFloat("rVar"), rs.getFloat("avg"));
            }
            return line;
        } catch (SQLException ex) {
            Logger.getLogger(NCCCStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void hookIn(Agent[] agents, final Execution ex) {

        System.out.println("NCCC Statistic Collector registered");

        nccc = new long[agents.length];
        lastKnownCC = new long[agents.length];
        runningVar = ex.getTest().getRunningVarName();

        for (Agent a : agents) {
            a.hookIn(new BeforeMessageSentHook() {

                @Override
                public void hook(Agent a, Message msg) {
                    updateCurrentNccc(a);
                    msg.getMetadata().put("nccc", nccc[a.getId()]);
                }
            });

            a.hookIn(new BeforeMessageProcessingHook() {

                @Override
                public void hook(Agent a, Message msg) {
                    long newNccc = (Long) msg.getMetadata().get("nccc");
                    updateCurrentNccc(a);
                    nccc[a.getId()] = Math.max(newNccc, nccc[a.getId()]);
                }
            });
        }

        agents[0].hookIn(new BeforeCallingFinishHook() {

            @Override
            public void hook(Agent a) {
                submit(new NCCCRecord(ex.getTest().getCurrentVarValue(), nccc[0], a.getAlgorithmName()));
            }
        });
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

    public static class NCCCRecord extends DBRecord {

        float rVar;
        float value;
        String algorithm;

        public NCCCRecord(float rVar, float value, String algorithm) {
            this.rVar = rVar;
            this.value = value;
            this.algorithm = algorithm;
        }

        @Override
        public String provideTableName() {
            return "NCCC";
        }
    }
}
