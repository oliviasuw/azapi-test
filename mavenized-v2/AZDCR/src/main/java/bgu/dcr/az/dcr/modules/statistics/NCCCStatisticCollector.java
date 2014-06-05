/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.dcr.api.modules.AbstractCPStatisticCollector;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.api.experiment.CPData;
import bgu.dcr.az.dcr.api.experiment.CPSolution;
import bgu.dcr.az.dcr.api.experiment.CPTest;
import bgu.dcr.az.execs.api.statistics.AdditionalLineChartProperties;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.statistics.info.SimulationTermination;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.QueryDatabase;
import com.google.common.primitives.Longs;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
@Register("nccc-sc")
public class NCCCStatisticCollector extends AbstractCPStatisticCollector {

    private long[] lastCCs;
    private long[] currentNccc;
    private Map<Long, Long> messageNccc;

    @Override
    protected void initialize(DefinitionDatabase database, Simulation<CPData, CPSolution> ex) {
        database.defineTable("NCCC", NCCCRecord.class);

        lastCCs = new long[ex.data().getProblem().getNumberOfAgents()];
        currentNccc = new long[ex.data().getProblem().getNumberOfAgents()];
        messageNccc = new HashMap<>();

        ex.infoStream().listen(CPMessageInfo.class, m -> {
            if (!m.isInternal()) {
                switch (m.getType()) {
                    case Sent:
                        if (m instanceof CPMessageInfo) {
                            long cc = m.getConstraintChecks();
                            currentNccc[m.getSender()] += (cc - lastCCs[m.getSender()]);
                            lastCCs[m.getSender()] = cc;
                            messageNccc.put(m.getMessageId(), currentNccc[m.getSender()]);
                        }
                        break;
                    case Received:
                        currentNccc[m.getRecepient()] = Math.max(messageNccc.remove(m.getMessageId()), currentNccc[m.getRecepient()]);
                        break;
                    default:
                        throw new AssertionError(m.getType().name());
                }
            }
        });

        ex.infoStream().listen(SimulationTermination.class, t -> {
            long[] ccCount = ex.data().getProblem().getCC_Count();
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
    protected void plot(QueryDatabase database, CPTest test) {
        String sql = "select AVG(nccc) as avg, rVar, ALGORITHM_INSTANCE from NCCC where TEST = ? group by ALGORITHM_INSTANCE, rVar order by rVar";

        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        properties.setTitle(getName());
        properties.setLogarithmicScale(true);
        properties.setXAxisLabel(test.getLooper().getRunningVariableName());
        properties.setYAxisLabel("AVG(NCCC)");
        plotLineChart(database.query(sql, test.getName()), "rvar", "avg", "ALGORITHM_INSTANCE", properties);
    }

    public static class NCCCRecord extends StatisticRecord {

        double nccc;

        public NCCCRecord(double nccc) {
            this.nccc = nccc;
        }

    }
}
