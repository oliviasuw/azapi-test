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
import bgu.dcr.az.execs.statistics.info.SimulationTerminationInfo;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.QueryDatabase;

/**
 *
 * @author bennyl
 */
@Register("cc-sc")
public class CCStatisticCollector extends AbstractCPStatisticCollector {

    @Override
    public String getName() {
        return "Constraint Checks";
    }

    @Override
    protected void plot(QueryDatabase database, CPTest test) {
        String sql = "select AVG(cc) as avg, rVar, ALGORITHM_INSTANCE from CC where TEST = ? group by ALGORITHM_INSTANCE, rVar order by rVar";

        AdditionalLineChartProperties properties = new AdditionalLineChartProperties();
        properties.setTitle(getName());
        properties.setLogarithmicScale(true);
        properties.setXAxisLabel(test.getLooper().getRunningVariableName());
        properties.setYAxisLabel("AVG(CC)");
        plotLineChart(database.query(sql, test.getName()), "rvar", "avg", "ALGORITHM_INSTANCE", properties);
    }

    @Override
    protected void initialize(DefinitionDatabase database, final Simulation<CPData, CPSolution> ex) {
        database.defineTable("CC", CCRecord.class);

        ex.infoStream().listen(SimulationTerminationInfo.class, t -> {
            long cc = 0;
            for (long c : ex.data().getProblem().getCC_Count()) {
                cc += c;
            }

            write(new CCRecord(cc));
        });
    }

    public static class CCRecord extends StatisticRecord {

        double cc;

        public CCRecord(double cc) {
            this.cc = cc;
        }

    }
}
