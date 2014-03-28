package bgu.dcr.az.dcr.modules.statistics;

import bgu.dcr.az.dcr.execution.CPData;
import bgu.dcr.az.dcr.execution.CPExperimentTest;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.execs.api.statistics.AdditionalBarChartProperties;
import bgu.dcr.az.execs.api.statistics.AdditionalLineChartProperties;
import bgu.dcr.az.execs.api.statistics.Plotter;
import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import bgu.dcr.az.execs.api.statistics.StatisticsManager;
import bgu.dcr.az.execs.statistics.StatisticsManagerImpl;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.QueryDatabase;

/**
 *
 * @author Benny Lutati
 */
public abstract class AbstractStatisticCollector implements StatisticCollector<CPData>, Plotter {

    private StatisticsManager manager;
    private Plotter plotter;
    Execution<CPData> execution;

    @Override
    public final void initialize(StatisticsManager manager, Execution<CPData> execution, DefinitionDatabase database) {
        this.manager = manager;
        this.execution = execution;
        initialize(execution, database);
    }

    public void write(CPRecord record) {
        record.algorithm_instance = execution.data().getAlgorithm().getInstanceName();
        record.rvar = execution.data().getRunningVar();
        record.test = execution.getContainingExperiment().getName();
        manager.database().insert(record);
    }

    @Override
    public void plot(Plotter ploter, Experiment ex) {
//        if (ex == null) return ;

        if (manager == null) {
            manager = StatisticsManagerImpl.getInstance();
        }

        this.plotter = ploter;
        if (manager == null) {
            plot((QueryDatabase) null, (CPExperimentTest) ex);
        } else {
            plot(manager.database().createQueryDatabase(), (CPExperimentTest) ex);
        }
        this.plotter = null;
    }

    protected abstract void plot(QueryDatabase database, CPExperimentTest test);

    protected abstract void initialize(final Execution<CPData> ex, DefinitionDatabase database);

    public void plotLineChart(Data data, String xField, String yField, String seriesField) {
        plotter.plotLineChart(data, xField, yField, seriesField, getName(), xField, yField);
    }

    public void plotPieChart(Data data, String valueField, String seriesField) {
        plotter.plotPieChart(data, valueField, seriesField, getName(), valueField, seriesField);
    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
        plotter.plotLineChart(data, xField, yField, seriesField, title, xAxisLabel, yAxisLabel);
    }

    @Override
    public void plotPieChart(Data data, String lableField, String seriesField, String title, String lableFieldLabel, String seriesFieldLabel) {
        plotter.plotPieChart(data, lableField, seriesField, title, lableFieldLabel, seriesFieldLabel);
    }

    @Override
    public void plotLineChart(Data data, String xField, String yField, String seriesField, AdditionalLineChartProperties properties) {
        plotter.plotLineChart(data, xField, yField, seriesField, properties);
    }

    @Override
    public void plotBarChart(Data data, String categoryField, String valueField, String seriesField, AdditionalBarChartProperties properties) {
        plotter.plotBarChart(data, categoryField, valueField, seriesField, properties);
    }

    @Override
    public String toString() {
        return getName();
    }

}
