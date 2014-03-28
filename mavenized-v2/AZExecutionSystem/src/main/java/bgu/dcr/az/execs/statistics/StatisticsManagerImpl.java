/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.statistics;

import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.exceptions.InitializationException;
import bgu.dcr.az.execs.api.statistics.AdditionalBarChartProperties;
import bgu.dcr.az.execs.api.statistics.AdditionalLineChartProperties;
import bgu.dcr.az.execs.api.statistics.Plotter;
import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import bgu.dcr.az.execs.api.statistics.StatisticsManager;
import bgu.dcr.az.orm.api.Data;
import bgu.dcr.az.orm.api.DefinitionDatabase;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.orm.impl.H2EmbeddedDatabaseManager;
import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class StatisticsManagerImpl implements StatisticsManager {

    private static StatisticsManagerImpl instance = null;
    public static String DATA_BASE_NAME = "agentzero";

    private H2EmbeddedDatabaseManager db = null;
    private Collection<StatisticCollector> registered = new LinkedList<>();

    public static StatisticsManagerImpl getInstance() {
        if (instance == null) {
            instance = new StatisticsManagerImpl();
        }
        return instance;
    }

    @Override
    public EmbeddedDatabaseManager database() {
        if (db == null) {
            try {
                initialize((Execution)null);
            } catch (InitializationException ex) {
                Logger.getLogger(StatisticsManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
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
        if (ex != null) {
            registered.forEach(r -> r.initialize(this, ex, ddb));
        }
    }

    public void clearRegistrations() {
        registered.clear();
    }

    private static class NOPlotter implements Plotter {

        @Override
        public void plotLineChart(Data data, String xField, String yField, String seriesField, AdditionalLineChartProperties properties) {
        }

        @Override
        public void plotPieChart(Data data, String valueField, String seriesField, String title, String valueFieldLabel, String categoriesFieldLabel) {
        }

        @Override
        public void plotBarChart(Data data, String categoryField, String valueField, String seriesField, AdditionalBarChartProperties properties) {
        }

    }
}
