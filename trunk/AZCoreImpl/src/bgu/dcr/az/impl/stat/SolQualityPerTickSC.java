/**
 * 
 */
package bgu.dcr.az.impl.stat;

import java.sql.ResultSet;
import java.sql.SQLException;

import bgu.dcr.az.api.Hooks.ReportHook;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks.TickHook;
import bgu.dcr.az.api.SystemClock;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.api.infra.stat.DBRecord;
import bgu.dcr.az.api.infra.stat.Database;
import bgu.dcr.az.api.infra.stat.VisualModel;
import bgu.dcr.az.api.infra.stat.vmod.LineVisualModel;

/**
 * @author alongrub
 *
 */
@Register(name = "sqpt-sc")
public class SolQualityPerTickSC extends AbstractStatisticCollector<SolQualityPerTickSC.Record> {

    private int ticksPerCycle = 1;
    @Variable(name = "sample-rate", description = "The sampling rate for solution quality", defaultValue="1")
    private int samplingRate = 1;
    private double lastCost = -1;

    public static class Record extends DBRecord {

        public final float solQuality;
        public final long tickNum;
        public long cycles;
        public int execution;
        public float prevSolutionQuality;
        private final String algorithm;

        public Record(float solQuality, long tickNum, int tpc, int execution, float prevSolutionQuality, String algo) {
            super();
            this.algorithm = algo;
            this.solQuality = solQuality;
            this.prevSolutionQuality = prevSolutionQuality;
            this.tickNum = tickNum;
            this.cycles = tickNum / tpc;
            this.execution = execution;
        }

        @Override
        public String provideTableName() {
            return "Solution_Quality";
        }
    }

    @Override
    public VisualModel analyze(Database db, Test r) {
        LineVisualModel lvm = new LineVisualModel("Time", "Solution Quality", "Solution Quality Progress");
        try {
            ResultSet res = db.query("SELECT AVG (solQuality) AS s, tickNum, algorithm FROM Solution_Quality where TEST = '" + r.getName() + "' GROUP BY algorithm, tickNum ORDER BY tickNum");
            while (res.next()) {
                lvm.setPoint(res.getString("algorithm"), res.getLong("tickNum"), res.getDouble("s"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lvm;
    }

    @Override
    public void hookIn(final Agent[] a, final Execution e) {

        lastCost = -1;
        
        e.getSystemClock().hookIn(new TickHook() {

            @Override
            public synchronized void hook(SystemClock clock) {
                if (clock.time() % samplingRate == 0) {
                    float cost = (float) e.getResult().getAssignment().calcCost(e.getGlobalProblem());
//                    System.out.println("in tick " + clock.time() + " the cost was " + cost);
                    submit(new Record(cost, clock.time(), ticksPerCycle, e.getTest().getCurrentExecutionNumber(), (float)lastCost, a[0].getAlgorithmName()));
                    lastCost = cost;
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

    @Override
    public String getName() {
        return "Solution Quality Per Ticks";
    }
    
}
