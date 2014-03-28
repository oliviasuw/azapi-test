/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.dcr.execution.CPExperimentTest;
import bgu.dcr.az.dcr.execution.statistics.ExternalMessageReceivedInfo;
import bgu.dcr.az.dcr.execution.statistics.ExternalMessageSentInfo;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.statistics.AdditionalLineChartProperties;
import bgu.dcr.az.execs.statistics.info.ExecutionTerminationInfo;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.QueryDatabase;
import com.google.common.primitives.Longs;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
@Register("nccc-sc")
public class NCCCStatisticCollector extends AbstractStatisticCollector {

    private long[] lastCCs;
    private long[] currentNccc;
    private Map<Long, Long> messageNccc;

    @Override
    protected void initialize(Execution<CPData> ex, DefinitionDatabase database) {
        database.defineTable("NCCC", NCCCRecord.class);

        lastCCs = new long[ex.data().getProblem().getNumberOfAgents()];
        currentNccc = new long[ex.data().getProblem().getNumberOfAgents()];
        messageNccc = new HashMap<>();

        ex.informationStream().listen(ExternalMessageSentInfo.class, m -> {
            currentNccc[m.getSender()] += (m.getConstraintChecks() - lastCCs[m.getSender()]);
            lastCCs[m.getSender()] = m.getConstraintChecks();
            messageNccc.put(m.getMessageId(), currentNccc[m.getSender()]);
        });

        ex.informationStream().listen(ExternalMessageReceivedInfo.class, m -> {
            currentNccc[m.getRecepient()] = Math.max(messageNccc.remove(m.getMessageId()), currentNccc[m.getRecepient()]);
        });

        ex.informationStream().listen(ExecutionTerminationInfo.class, t -> {
            long[] ccCount = ex.data().getCcCount();
            for (int i = 0; i < ccCount.length; i++) {
                currentNccc[i] += (ccCount[i] - lastCCs[i]);
            }
            write(new NCCCRecord(Longs.max(currentNccc)));
        });
    }

    @Override

    public String getName() {
        return "Non Concurrent Constraint Checks";
    }

    @Override
    protected void plot(QueryDatabase database, CPExperimentTest test) {
        String sql = "select AVG(nccc) as avg, rVar, ALGORITHM_INSTANCE from NCCC where TEST = ? group by ALGORITHM_INSTANCE, rVar order by rVar";

        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        properties.setTitle(getName());
        properties.setLogarithmicScale(true);
        properties.setXAxisLabel(test.getLooper().getRunningVariableName());
        properties.setYAxisLabel("AVG(NCCC)");
        plotLineChart(database.query(sql, test.getName()), "rvar", "avg", "ALGORITHM_INSTANCE", properties);
    }

    public static class NCCCRecord extends CPRecord {

        double nccc;

        public NCCCRecord(double nccc) {
            this.nccc = nccc;
        }

    }
}
