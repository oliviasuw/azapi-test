package bgu.dcr.az.execs.statistics;

import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.execs.api.statistics.AdditionalBarChartProperties;
import bgu.dcr.az.execs.api.statistics.AdditionalLineChartProperties;
import bgu.dcr.az.execs.api.statistics.Plotter;
import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import bgu.dcr.az.execs.exceptions.InitializationException;
import bgu.dcr.az.orm.api.DBRecord;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.orm.api.QueryDatabase;

/**
 *
 * @author Benny Lutati
 */
public abstract class AbstractStatisticCollector<T> implements StatisticCollector<T>, Plotter {

    private Plotter plotter;
    private EmbeddedDatabaseManager db = null;
    private ExecutionInfoCollector executionInfo;

    public Plotter getPlotter() {
        return plotter;
    }

    public void setPlotter(Plotter plotter) {
        this.plotter = plotter;
    }

    public void write(StatisticRecord record) {
        record.executionIndex = executionInfo.getLastRecordIndex();

        db.insert(record);
    }

    @Override
    public final void initialize(Execution<T> execution) throws InitializationException {

        executionInfo = execution.require(ExecutionInfoCollector.class);

        db = (EmbeddedDatabaseManager) execution.require(EmbeddedDatabaseManager.class);

        initialize(execution, (tableName, recordType) -> {
            db.defineTable("RAW_" + tableName, recordType);
            db.execute("CREATE OR REPLACE VIEW " + tableName + " AS SELECT * FROM RAW_" + tableName + " AS t, " + ExecutionInfoCollector.EXECUTION_INFO_DATA_TABLE + " AS i WHERE t.executionIndex = i.index;");
        });

        System.out.println("Statistic Collector: " + getName() + " initialized");
    }

    @Override
    public void plot(Plotter ploter, Experiment ex) {
//        if (ex == null) return ;

        this.setPlotter(ploter);
        if (db != null) {
            plot(db.createQueryDatabase(), ex);
        } else {
            plot((QueryDatabase) null, ex);
        }
        this.setPlotter(null);
    }

    protected abstract void plot(QueryDatabase database, Experiment test);

    protected abstract void initialize(final Execution<T> ex, DefinitionDatabase database);

    @Override
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

    public static class StatisticRecord implements DBRecord {

        public long executionIndex;
    }
}
