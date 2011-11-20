/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.stat;

import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Hooks.BeforeCallingFinishHook;
import bgu.csp.az.api.ano.Register;
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
@Register(name="cc-sc")
public class CCStatisticCollector extends AbstractStatisticCollector<CCStatisticCollector.CCRecord> {

    @Override
    public VisualModel analyze(Database db, Round r) {
        String query = "select AVG(cc) as avg, rVar from CC where ROUND = '" + r.getName() + "' group by rVar order by rVar";
        LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "Avg(CC)", "CC");
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
    public void hookIn(final Agent[] agents, final Execution ex) {
        agents[0].hookIn(new BeforeCallingFinishHook() {

            @Override
            public void hook(Agent a) {
                int sum = 0;
                for (Agent ag : agents) {
                    sum += ag.getNumberOfConstraintChecks();
                }

                submit(new CCRecord(ex.getRound().getCurrentVarValue(), sum));
            }
        });
    }

    public static class CCRecord extends DBRecord {

        float rVar;
        int cc;

        public CCRecord(float rVal, int cc) {
            this.rVar = rVal;
            this.cc = cc;
        }

        @Override
        public String provideTableName() {
            return "CC";
        }
    }
}
