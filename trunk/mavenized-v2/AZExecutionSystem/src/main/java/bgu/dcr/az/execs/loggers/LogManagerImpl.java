/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.loggers;

import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.execs.api.loggers.LogManager;
import bgu.dcr.az.execs.api.loggers.LogRecord;
import bgu.dcr.az.execs.api.loggers.Logger;
import bgu.dcr.az.execs.exceptions.InitializationException;
import bgu.dcr.az.orm.api.EmbeddedDatabaseManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class LogManagerImpl implements LogManager {

    public static final String LOG_MANAGEMENT_TABLE_NAME = "LOG_MANAGEMENT";

    private final List<Logger> loggers = new ArrayList<>();
    private Execution execution;
    private EmbeddedDatabaseManager db = null;

    @Override
    public void initialize(Execution ex) throws InitializationException {
        this.execution = ex;
        if (db == null) {
            db = (EmbeddedDatabaseManager) ex.require(EmbeddedDatabaseManager.class);
            createLogManagementTable();
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

    private void createLogManagementTable() {
        StringBuilder exe = new StringBuilder("CREATE TABLE ").append(LOG_MANAGEMENT_TABLE_NAME).append(" (");
        exe.append("ID INTEGER NOT NULL AUTO_INCREMENT ");
        exe.append(", EXECUTION_NUMBER BIGINT");
        exe.append(", PROCESS_ID INTEGER");
        exe.append(", EXECUTION_TIME BIGINT");
        for (Logger l : loggers) {
            exe.append(", ").append(l.getClass().getSimpleName()).append(" BIGINT");
        }
        exe.append(", PRIMARY KEY (ID));");
        db.execute(exe.toString());
    }

    @Override
    public void commit(LogRecord record) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
