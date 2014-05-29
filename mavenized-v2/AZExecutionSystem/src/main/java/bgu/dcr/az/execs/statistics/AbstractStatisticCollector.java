package bgu.dcr.az.execs.statistics;

import bgu.dcr.az.common.reflections.ReflectionUtils;
import bgu.dcr.az.execs.api.statistics.AdditionalBarChartProperties;
import bgu.dcr.az.execs.api.statistics.AdditionalLineChartProperties;
import bgu.dcr.az.execs.api.statistics.Plotter;
import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import bgu.dcr.az.execs.exps.ExecutionTree;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.execs.orm.api.DBRecord;
import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.execs.orm.api.QueryDatabase;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Benny Lutati
 */
public abstract class AbstractStatisticCollector implements StatisticCollector, Plotter {

    private final Set<String> definedTableNames = new HashSet<>();

    private Plotter plotter;
    private EmbeddedDatabaseManager db = null;
    private Simulation sim;

    public Plotter getPlotter() {
        return plotter;
    }

    public void setPlotter(Plotter plotter) {
        this.plotter = plotter;
    }

    public void write(StatisticRecord record) {
        record.executionIndex = sim.getSimulationNumber();
        db.insert(record);
    }

    @Override
    public final void initialize(ExecutionTree exec) {
        db = exec.require(EmbeddedDatabaseManager.class);

        class CachedDDB implements DefinitionDatabase {

            @Override
            public void defineTable(String tableName, Class<? extends DBRecord> recordType) {
                if (definedTableNames.contains(tableName)) {
                    return;
                }
                definedTableNames.add(tableName);

                db.defineTable("RAW_" + tableName, recordType);
                StringBuilder fields = new StringBuilder();
                for (Field f : ReflectionUtils.allFields(recordType)) {
                    if (f.getName().equals("executionIndex")) {
                        continue;
                    }
                    fields.append(", ").append(f.getName());
                }
                for (Field f : ReflectionUtils.allFields(sim.getInfo().getClass())) {
                    if (f.getName().equals("index")) {
                        continue;
                    }
                    fields.append(", ").append(f.getName());
                }

                db.execute("CREATE OR REPLACE VIEW " + tableName + " AS SELECT executionIndex " + fields.toString() + " FROM RAW_" + tableName + " AS t, " + Simulation.EXECUTION_INFO_DATA_TABLE + " AS i WHERE t.executionIndex = i.index;");
            }
        }

        exec.infoStream().listen(Simulation.class, s -> {
            sim = s;
            initialize(sim, new CachedDDB());
        });
    }

    @Override
    public void plot(Plotter ploter, Test ex) {
        this.setPlotter(ploter);
        if (db != null) {
            plot(db.createQueryDatabase(), ex);
        } else {
            plot((QueryDatabase) null, ex);
        }
        this.setPlotter(null);
    }

    protected abstract void plot(QueryDatabase database, Test test);

    protected abstract void initialize(final Simulation ex, DefinitionDatabase database);

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
