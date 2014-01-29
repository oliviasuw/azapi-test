/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl.stat;

import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.impl.InitializationException;
import bgu.dcr.az.mas.stat.Plotter;
import bgu.dcr.az.mas.stat.StatisticCollector;
import bgu.dcr.az.mas.stat.StatisticsManager;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.orm.impl.H2EmbeddedDatabaseManager;
import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author User
 */
public class StatisticsManagerImpl implements StatisticsManager {

    Plotter plotter = new NOPlotter();
    H2EmbeddedDatabaseManager db = null;
    Collection<StatisticCollector> registered = new LinkedList<>();
    public static String DATA_BASE_NAME = "agentzero";

    @Override
    public Plotter plotter() {
        return plotter;
    }

    public void setPlotter(Plotter plotter) {
        this.plotter = plotter;
    }

    @Override
    public EmbeddedDatabaseManager database() {
        return db;
    }

    @Override
    public Collection<StatisticCollector> registered() {
        return registered;
    }

    @Override
    public void register(StatisticCollector stat) {
        registered.add(stat);
    }

    @Override
    public void initialize(Execution ex) throws InitializationException {
        if (db == null) {
            db = new H2EmbeddedDatabaseManager();
            try {
                db.start(new File(DATA_BASE_NAME), false);
            } catch (SQLException ex1) {
                throw new InitializationException("cannot initialize database, see cause", ex1);
            }
        }
        
        DefinitionDatabase ddb = db.createDefinitionDatabase();
        for (StatisticCollector r : registered){
            r.initialize(this, ex, ddb);
        }
    }

    private static class NOPlotter implements Plotter {

        @Override
        public void plotLineChart(Data data, String xField, String yField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
        }

        @Override
        public void plotBarChart(Data data, String lableField, String valueField, String seriesField, String title, String xAxisLabel, String yAxisLabel) {
        }

        @Override
        public void plotPieChart(Data data, String lableField, String valueField, String title, String xAxisLabel, String yAxisLabel) {
        }

    }
}
