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

/**
 *
 * @author bennyl
 */
@Register("nccc-sc")
public class NCCCStatisticCollector extends AbstractStatisticCollector {

    private long[] lastCCs;
    private long[] currentNccc;

    @Override
    protected void initialize(Execution<CPData> ex, DefinitionDatabase database) {
        database.defineTable("NCCC", NCCCRecord.class);

        lastCCs = new long[ex.data().getProblem().getNumberOfAgents()];
        currentNccc = new long[ex.data().getProblem().getNumberOfAgents()];

        ex.informationStream().listen(MessageSentInfo.class, m -> {
            currentNccc[m.getSender()] += (m.getConstraintChecks() - lastCCs[m.getSender()]);
            lastCCs[m.getSender()] = m.getConstraintChecks();
        });

        ex.informationStream().listen(MessageReceivedInfo.class, m -> {
            currentNccc[m.getRecepient()] = Math.max(currentNccc[m.getSender()], currentNccc[m.getRecepient()]);
        });

        ex.informationStream().listen(ExecutionTerminationInfo.class, t -> {
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
