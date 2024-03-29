/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.statistics;

import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.execs.exps.exe.Looper;
import bgu.dcr.az.execs.api.statistics.AdditionalLineChartProperties;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.execs.statistics.AbstractStatisticCollector.StatisticRecord;
import bgu.dcr.az.execs.statistics.info.SimulationTermination;
import bgu.dcr.az.execs.statistics.info.MessageInfo;
import static bgu.dcr.az.execs.statistics.info.MessageInfo.OperationType.Received;
import static bgu.dcr.az.execs.statistics.info.MessageInfo.OperationType.Sent;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.QueryDatabase;
import com.google.common.primitives.Longs;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
@Register("ncsc-sc")
public class NCSCStatisticCollector extends AbstractStatisticCollector {

    private long[] currentNcsc;
    private Map<Long, Long> messageNcsc;

    public long getCurrentNCSC(int aid) {
        return currentNcsc[aid];
    }

    @Override
    protected void initialize(Simulation ex, DefinitionDatabase database) {
        database.defineTable("NCSC", NCSCRecord.class);

        currentNcsc = new long[ex.configuration().numAgents()];
        messageNcsc = new HashMap<>();

        ex.infoStream().listen(MessageInfo.class, m -> {
            switch (m.getType()) {
                case Sent:
                    messageNcsc.put(m.getMessageId(), currentNcsc[m.getSender()]);
                    break;
                case Received:
                    currentNcsc[m.getRecepient()] = Math.max(messageNcsc.remove(m.getMessageId()), currentNcsc[m.getRecepient()]) + 1;
                    break;
                default:
                    throw new AssertionError(m.getType().name());
            }
        });

        ex.infoStream().listen(SimulationTermination.class, t -> {
            write(new NCSCRecord(Longs.max(currentNcsc)));
        });
    }

    @Override
    public String getName() {
        return "Non Concurrent Steps of Computation";
    }

    @Override
    protected void plot(QueryDatabase database, Test test) {
        Looper hlop = test.require(Looper.class);
        String sql = "select AVG(ncsc) as avg, rVar, ALGORITHM_INSTANCE from NCSC where TEST = ? group by ALGORITHM_INSTANCE, rVar order by rVar";

        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        properties.setTitle(getName());
        properties.setLogarithmicScale(true);
        properties.setXAxisLabel(hlop.getRunningVariableName());
        properties.setYAxisLabel("AVG(NCSC)");
        plotLineChart(database.query(sql, test.getName()), "rvar", "avg", "ALGORITHM_INSTANCE", properties);
    }

    public static class NCSCRecord extends StatisticRecord {

        double ncsc;

        public NCSCRecord(double ncsc) {
            this.ncsc = ncsc;
        }

    }
}
