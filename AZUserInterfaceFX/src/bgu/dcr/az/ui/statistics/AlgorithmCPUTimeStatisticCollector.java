/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.statistics;

import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.cp.CPData;
import bgu.dcr.az.mas.cp.CPExperimentTest;
import bgu.dcr.az.mas.impl.stat.AbstractStatisticCollector;
import bgu.dcr.az.mas.stat.data.ExecutionTerminationInfo;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.QueryDatabase;
import bgu.dcr.az.orm.impl.DataUtils;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class AlgorithmCPUTimeStatisticCollector extends AbstractStatisticCollector {

    Map<String, Long> millisSpent = new LinkedHashMap<>();

    @Override
    protected void initialize(final Execution<CPData> ex, DefinitionDatabase database) {

        final long time = System.currentTimeMillis();
        ex.informationStream().listen(ExecutionTerminationInfo.class, t -> {
            Long oldMillis = millisSpent.get(ex.data().getAlgorithm().getName());
            if (oldMillis == null) {
                oldMillis = 0L;
            }

            oldMillis += (System.currentTimeMillis() - time);
            millisSpent.put(ex.data().getAlgorithm().getName(), oldMillis);
        });

    }

    @Override
    public String getName() {
        return "Algorithm CPU time spending";
    }

    @Override
    public void plot(QueryDatabase database, CPExperimentTest test) {
        plotPieChart(DataUtils.fromMap(millisSpent, String.class, "Algorithm", Long.class, "Time"), "Time", "Algorithm");
    }

}
