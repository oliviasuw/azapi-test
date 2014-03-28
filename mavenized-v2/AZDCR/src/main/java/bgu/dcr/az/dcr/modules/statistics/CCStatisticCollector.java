/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.dcr.execution.CPExperimentTest;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.statistics.AdditionalLineChartProperties;
import bgu.dcr.az.execs.statistics.info.ExecutionTerminationInfo;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.QueryDatabase;

/**
 *
 * @author bennyl
 */
@Register("cc-sc")
public class CCStatisticCollector extends AbstractStatisticCollector {

    @Override
    public String getName() {
        return "Constraint Checks";
    }

    @Override
    protected void plot(QueryDatabase database, CPExperimentTest test) {
        String sql = "select AVG(cc) as avg, rVar, ALGORITHM_INSTANCE from CC where TEST = ? group by ALGORITHM_INSTANCE, rVar order by rVar";

        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        properties.setTitle(getName());
        properties.setLogarithmicScale(true);
        properties.setXAxisLabel(test.getLooper().getRunningVariableName());
        properties.setYAxisLabel("AVG(CC)");
        plotLineChart(database.query(sql, test.getName()), "rvar", "avg", "ALGORITHM_INSTANCE", properties);
    }

    @Override
    protected void initialize(Execution<CPData> ex, DefinitionDatabase database) {
        database.defineTable("CC", CCRecord.class);

        ex.informationStream().listen(ExecutionTerminationInfo.class, t -> {
            long cc = 0;
            for (long c : ex.data().getCcCount()) {
                cc += c;
            }

            write(new CCRecord(cc));
        });
    }

    public static class CCRecord extends CPRecord {

        double cc;

        public CCRecord(double cc) {
            this.cc = cc;
        }

    }
}
