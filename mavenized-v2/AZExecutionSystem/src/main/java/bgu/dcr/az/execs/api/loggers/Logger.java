/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.loggers;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.execs.api.loggers.LogManager.LogRecord;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.exe.DefaultExperimentRoot;
import bgu.dcr.az.execs.orm.api.Data;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author bennyl
 * @param <T>
 */
public abstract class Logger implements Module<LogManager> {

    private LogManager lman;
    private DefaultExperimentRoot experiment;
    private EmbeddedDatabaseManager db;

    @Override
    public final void installInto(LogManager lm) {
        lman = lm;
        experiment = lm.require(DefaultExperimentRoot.class);
        db = lm.require(EmbeddedDatabaseManager.class);
        initialize(db.createDefinitionDatabase());
    }
    
    protected DefaultExperimentRoot experiment() {
        return experiment;
    }

    /**
     * saves the latest changes of the experiment (at given time for a given
     * process)
     *
     * @param record
     */
    protected void commitLog(LogManager.LogRecord record) {
        lman.commit(this, record);
    }
    
    protected Data query(String sql) throws SQLException {
        return db.query(sql, null);
    }

    public abstract List<LogRecord> getRecords(String test, int simulation) throws SQLException;

    public abstract void initialize(DefinitionDatabase database);

    public String getName() {
        return toString();
    }

}
