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
import bgu.csp.az.api.ano.Variable;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.stat.DBRecord;
import bgu.csp.az.api.infra.stat.Database;
import bgu.csp.az.api.infra.stat.VisualModel;
import bgu.csp.az.api.infra.stat.vmod.LineVisualModel;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public VisualModel analyze(Database db, Round r) {
        String query = "select AVG(value) as avg, rVar from NCCC where ROUND = '" + r.getName() + "' group by rVar order by rVar";
        LineVisualModel line = new LineVisualModel(runningVar, "Avg(NCCC)", "NCCC");
        try {
            ResultSet rs = db.query(query);
            while (rs.next()) {
                line.setPoint(rs.getFloat("rVar"), rs.getFloat("avg"));
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
        runningVar = ex.getRound().getRunningVarName();

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
                submit(new NCCCRecord(ex.getRound().getCurrentVarValue(), nccc[0]));
            }
        });
    }

    @Override
    public String getName() {
        return "Number Of Concurent Constraint Checks";
    }

    private void updateCurrentNccc(Agent a) {
        long last = lastKnownCC[a.getId()];
        lastKnownCC[a.getId()] = a.getNumberOfConstraintChecks();
        nccc[a.getId()] = nccc[a.getId()] + lastKnownCC[a.getId()] - last;
    }

    public static class NCCCRecord extends DBRecord {

        float rVar;
        long value;

        public NCCCRecord(float rVar, long value) {
            this.rVar = rVar;
            this.value = value;
        }

        @Override
        public String provideTableName() {
            return "NCCC";
        }
    }
}
