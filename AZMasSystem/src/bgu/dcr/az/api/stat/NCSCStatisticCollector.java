/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.stat;

import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.cp.CPData;
import bgu.dcr.az.mas.cp.CPExperimentTest;
import bgu.dcr.az.mas.cp.CPRecord;
import bgu.dcr.az.mas.impl.stat.AbstractStatisticCollector;
import bgu.dcr.az.mas.stat.AdditionalLineChartProperties;
import bgu.dcr.az.mas.stat.data.ExecutionTerminationInfo;
import bgu.dcr.az.mas.stat.data.MessageReceivedInfo;
import bgu.dcr.az.mas.stat.data.MessageSentInfo;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.QueryDatabase;
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

    @Override
    protected void initialize(Execution<CPData> ex, DefinitionDatabase database) {
        database.defineTable("NCSC", NCSCRecord.class);

        currentNcsc = new long[ex.data().getProblem().getNumberOfAgents()];
        messageNcsc = new HashMap<>();

        ex.informationStream().listen(MessageSentInfo.class, m -> {
            currentNcsc[m.getSender()]++;
            messageNcsc.put(m.getMessageId(), currentNcsc[m.getSender()]);
        });

        ex.informationStream().listen(MessageReceivedInfo.class, m -> {
            currentNcsc[m.getRecepient()] = Math.max(messageNcsc.remove(m.getMessageId()), currentNcsc[m.getRecepient()]);
        });

        ex.informationStream().listen(ExecutionTerminationInfo.class, t -> {
            write(new NCSCRecord(Longs.max(currentNcsc)));
        });
    }

    @Override
    public String getName() {
        return "Non Concurrent Steps of Computation";
    }

    @Override
    protected void plot(QueryDatabase database, CPExperimentTest test) {
        String sql = "select AVG(ncsc) as avg, rVar, ALGORITHM_INSTANCE from NCSC where TEST = ? group by ALGORITHM_INSTANCE, rVar order by rVar";

        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        properties.setTitle(getName());
        properties.setLogarithmicScale(true);
        properties.setXAxisLabel(test.getLooper().getRunningVariableName());
        properties.setYAxisLabel("AVG(NCSC)");
        plotLineChart(database.query(sql, test.getName()), "rvar", "avg", "ALGORITHM_INSTANCE", properties);
    }

    public static class NCSCRecord extends CPRecord {

        double ncsc;

        public NCSCRecord(double nccc) {
            this.ncsc = nccc;
        }

    }
}
