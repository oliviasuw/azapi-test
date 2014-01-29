package bgu.dcr.az.mas.impl.stat;

import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.stat.Plotter;
import bgu.dcr.az.mas.stat.StatisticCollector;
import bgu.dcr.az.mas.stat.StatisticsManager;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.DefinitionDatabase;

/**
 *
 * @author Benny Lutati
 */
public abstract class AbstractStatisticCollector implements StatisticCollector, Plotter {

    private StatisticsManager manager;

    @Override
    public final void initialize(StatisticsManager manager, Execution execution, DefinitionDatabase database) {
        this.manager = manager;
        initialize(execution, database);
    }

    protected abstract void initialize(final Execution ex, DefinitionDatabase database);

    public void plotBarChart(Data data, String lableField, String valueField, String seriesField) {
        manager.plotter().plotBarChart(data, lableField, valueField, seriesField, getName(), lableField, valueField);
    }

    public void plotLineChart(Data data, String xField, String yField, String seriesField) {
        manager.plotter().plotLineChart(data, xField, yField, seriesField, getName(), xField, yField);
    }

    public void plotPieChart(Data data, String lableField, String seriesField) {
        manager.plotter().plotPieChart(data, lableField, seriesField, getName(), lableField, seriesField);
    }

    @Override
    public void plotBarChart(Data data, String lableField, String valueField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
        manager.plotter().plotBarChart(data, lableField, valueField, seriesField, title, xAxisLabel, yAxisLabel);
    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
        manager.plotter().plotLineChart(data, xField, yField, seriesField, title, xAxisLabel, yAxisLabel);
    }

    @Override
    public void plotPieChart(Data data, String lableField, String seriesField, String title, String lableFieldLabel, String seriesFieldLabel) {
        manager.plotter().plotPieChart(data, lableField, seriesField, title, lableFieldLabel, seriesFieldLabel);
    }

}
