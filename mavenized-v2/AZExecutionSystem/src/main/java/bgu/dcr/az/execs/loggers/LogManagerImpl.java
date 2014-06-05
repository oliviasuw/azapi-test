/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.loggers;

import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.execs.api.loggers.LogManager;
import bgu.dcr.az.execs.api.loggers.Logger;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.statistics.NCSCStatisticCollector;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author bennyl
 */
@Register("log-manager")
public class LogManagerImpl extends LogManager {

    private long sharedIndex;
    private EmbeddedDatabaseManager db = null;
    private Simulation currentSimulation;
    private TimeRetriever timer;

    private final ConcurrentLinkedQueue<LogRecord> lastSimulationRecords = new ConcurrentLinkedQueue<>();

    private boolean saveTimestaps = true;
    private Iterable<Logger> loggers;

    public boolean getSaveTimestaps() {
        return saveTimestaps;
    }

    public void setSaveTimestaps(boolean saveTimestaps) {
        this.saveTimestaps = saveTimestaps;
    }

    /**
     * @propertyName loggers
     * @return
     */
    public Iterable<Logger> getLoggers() {
        return loggers;
    }

    @Override
    public void installInto(ModuleContainer mc) {
        if (!(mc instanceof ModularExperiment)) {
            throw new UnsupportedOperationException("Progress Enhancers only support ModularExperiment containers!");
        }
        super.installInto(mc);
        
        loggers = requireAll(Logger.class);
        
        
        ModularExperiment ex = (ModularExperiment) mc;
        
        if (db == null) {
            db = ex.require(EmbeddedDatabaseManager.class);
        }

        ex.execution().infoStream().listen(Simulation.class, s -> {
//            lastSimulationRecords.clear();

            currentSimulation = s;

            switch (currentSimulation.getExecutionEnvironment()) {
                case async:
                    NCSCStatisticCollector ncsc = (NCSCStatisticCollector) currentSimulation.require(NCSCStatisticCollector.class);
                    timer = aid -> ncsc.getCurrentNCSC(aid);
                    break;
                case sync:
                    timer = aid -> {
                        throw new UnsupportedOperationException("Not supported yet.");
                    };
                    break;
                default:
                    throw new AssertionError(currentSimulation.getExecutionEnvironment().name());
            }
        });
    }

    @Override
    public void commit(Logger logger, LogRecord record) {

        record.test = currentSimulation.configuration().baseStatisticFields().test;
        record.simulation_index = currentSimulation.configuration().baseStatisticFields().simulation_index;

        record.sharedIndex = sharedIndex++;
        record.time = timer.getTime(record.aid);

//        db.insert(record);

        lastSimulationRecords.add(record);
    }

    @Override
    public Iterable<LogRecord> getRecords(String test, int simulation) throws SQLException {
        if (currentSimulation == null) {
            return Collections.EMPTY_LIST;
        }

//        if (currentSimulation.configuration().baseStatisticFields().test.equals(test)
//                && simulation == currentSimulation.configuration().baseStatisticFields().simulation_index) {
            return lastSimulationRecords;
//        }

//        LinkedList<LogRecord> result = new LinkedList<>();
//
//        for (Logger l : getLoggers()) {
//            result.addAll(l.getRecords(test, simulation));
//        }
//
//        return result;
    }

    @Override
    public String toString() {
        return "Log manager";
    }

    private static interface TimeRetriever {

        long getTime(int aid);
    }
}
