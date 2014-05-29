/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.loggers;

import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.execs.exps.exe.ExecutionEnvironment;
import bgu.dcr.az.execs.api.loggers.LogManager;
import bgu.dcr.az.execs.api.loggers.Logger;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.statistics.NCSCStatisticCollector;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.execs.orm.RecordDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
@Register("log-manager")
public class LogManagerImpl implements LogManager {

    public static final String LOG_MANAGEMENT_TABLE_NAME = "LOG_MANAGEMENT";

    private final List<Logger> loggers = new ArrayList<>();
    private Map<Logger, TimedLoggerEntry> logIndex;
    private EmbeddedDatabaseManager db = null;
    private ExecutionEnvironment environment;
    private NCSCStatisticCollector ncsc;
    private Simulation exec;
    

    private boolean saveTimestaps = true;

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
    public List<Logger> getLoggers() {
        return loggers;
    }

    @Override
    public void initialize(Simulation ex) {
        
        this.exec = ex;
        
        if (saveTimestaps) {
            environment = ex.getExecutionEnvironment();
            if (ExecutionEnvironment.async.equals(environment)) {
                ncsc = (NCSCStatisticCollector) ex.require(NCSCStatisticCollector.class);
            }
        }

        if (db == null) {
            db = ex.require(EmbeddedDatabaseManager.class);
            db.defineTable(LOG_MANAGEMENT_TABLE_NAME, new LogRecordDescriptor(loggers));

            logIndex = new HashMap<>();

            loggers.stream().forEach(l -> {
                TimedLoggerEntry entry = new TimedLoggerEntry();
                entry.value = new long[ex.configuration().numAgents()];
                logIndex.put(l, entry);
            });
        }

    }

    @Override
    public Collection<Logger> registered() {
        return loggers;
    }

    @Override
    public void register(Logger logger) {
        if (db != null) {
            throw new UnsupportedOperationException("Cannot register a logger after log manager initialization.");
        }
        loggers.add(logger);
    }

    @Override
    public void commit(Logger logger, LogRecord record) {
        TimedLoggerEntry entry = logIndex.get(logger);
        record.index = entry.time++;
        entry.value[record.aid] = record.index;

        db.insert(record);

        if (saveTimestaps) {
            Object[] args = new Object[loggers.size() + 3];
            args[0] = record.aid;
            args[1] = getTime(record.aid);
            args[2] = exec.getSimulationNumber();

            int i = 3;
            for (Logger l : loggers) {
                args[i++] = logIndex.get(l).value[record.aid];
            }
            db.insert(args, LogRecordDescriptor.class);
        }
    }

    private long getTime(int aid) {
        switch (environment) {
            case async:
                return ncsc.getCurrentNCSC(aid);
            case sync:
                return 0;
            default:
                throw new AssertionError(environment.name());
        }
    }

    @Override
    public String toString() {
        return "Log manager";
    }

    private static class TimedLoggerEntry {

        long time;
        long[] value;
    }

    private static class LogRecordDescriptor implements RecordDescriptor {

        String[] columns;

        public LogRecordDescriptor(Collection<Logger> loggers) {
            columns = new String[3 + loggers.size()];

            columns[0] = "aid";
            columns[1] = "execution_time";
            columns[2] = "execution_number";

            int i = 3;
            for (Logger l : loggers) {
                columns[i++] = l.getClass().getSimpleName();
            }
        }

        @Override
        public String[] fields() {
            return columns;
        }

        @Override
        public Object get(int idx, Object from) {
            return ((Object[]) from)[idx];
        }

        @Override
        public Class type(int idx) {
            return idx == 0 ? int.class : long.class;
        }

        @Override
        public Object identifier() {
            return LogRecordDescriptor.class;
        }
    }
}
