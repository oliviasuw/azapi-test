/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.statistics;

import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.cp.CPData;
import bgu.dcr.az.mas.impl.stat.AbstractStatisticCollector;
import bgu.dcr.az.mas.stat.AdditionalBarChartProperties;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.QueryDatabase;
import bgu.dcr.az.orm.impl.DataUtils;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class NumberOfCoresInUseStatisticCollector extends AbstractStatisticCollector {

    Map<String, Float> coreUsage = new LinkedHashMap<>();
    int maxCores;
    String openingAlgorithmName = null;

    public NumberOfCoresInUseStatisticCollector() {
        maxCores = Runtime.getRuntime().availableProcessors();
    }

    @Override
    protected void initialize(final Execution<CPData> ex, DefinitionDatabase database) {
        final String name = ex.data().getAlgorithm().getInstanceName();

        Float got = coreUsage.get(name);
        if (got == null) {
            got = 0F;
        }

        coreUsage.put(name, (float) (got * 0.5 + ex.getNumberOfCoresInUse() * 0.5));
    }

    @Override
    public String getName() {
        return "Number of cores in use";
    }

    @Override
    public void plot(QueryDatabase database) {
        AdditionalBarChartProperties properties = new AdditionalBarChartProperties();
        properties.setHorizontal(true);
        properties.setMaxValue(maxCores);
        plotBarChart(DataUtils.fromMap(coreUsage,
                String.class, "Algorithm", Integer.class, "Cores"), "Algorithm", "Cores", properties);
    }

}
