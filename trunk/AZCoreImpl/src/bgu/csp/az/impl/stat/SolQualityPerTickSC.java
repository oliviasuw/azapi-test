/**
 * 
 */
package bgu.csp.az.impl.stat;

import java.sql.ResultSet;
import java.sql.SQLException;

import bgu.csp.az.api.Hooks.ReportHook;
import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Hooks.TickHook;
import bgu.csp.az.api.SystemClock;
import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.ano.Variable;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.stat.DBRecord;
import bgu.csp.az.api.infra.stat.Database;
import bgu.csp.az.api.infra.stat.VisualModel;
import bgu.csp.az.api.infra.stat.vmod.LineVisualModel;

/**
 * @author alongrub
 *
 */
@Register(name = "SQPT")
public class SolQualityPerTickSC extends AbstractStatisticCollector<SolQualityPerTickSC.Record> {

    private int ticksPerCycle = 1;
    @Variable(name = "sample-rate", description = "The sampling rate for solution quality")
    private int samplingRate = 1;

    public static class Record extends DBRecord {

        public final float solQuality;
        public final long tickNum;
        public long cycles;

        public Record(float solQuality, long tickNum, int tpc) {
            super();
            this.solQuality = solQuality;
            this.tickNum = tickNum;
            this.cycles = tickNum / tpc;
        }

        @Override
        public String provideTableName() {
            return "Solution_Quality";
        }
    }

    @Override
    public VisualModel analyze(Database db, Round r) {
        LineVisualModel lvm = new LineVisualModel("Time", "Solution Quality", "Solution Quality Progress");
        try {
            ResultSet res = db.query("SELECT AVG (solQuality) AS s, tickNum FROM Solution_Quality where ROUND = '" + r.getName() + "' GROUP BY tickNum ORDER BY tickNum");
            while (res.next()) {
                lvm.setPoint(res.getLong("tickNum"), res.getDouble("s"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lvm;
    }

    @Override
    public void hookIn(Agent[] arg0, final Execution e) {

        e.getSystemClock().hookIn(new TickHook() {

            @Override
            public void hook(SystemClock clock) {
                if (clock.time() % samplingRate == 0) {
                    float cost = (float) e.getPartialResult().getAssignment().calcCost(e.getGlobalProblem());
                    submit(new Record(cost, clock.time(), ticksPerCycle));
                    System.out.println("Tick " + clock.time() + " with assignment value of " + cost);
                }
            }
        });

        e.hookIn("ticksPerCycle", new ReportHook() {

            @Override
            public void hook(Agent ai, Object[] report) {
                ticksPerCycle = (Integer) report[0];
            }
        });
    }
}
