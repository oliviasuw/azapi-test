/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.loggers;

import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.execs.exps.exe.BaseDBFields;
import java.sql.SQLException;

/**
 * represents an entity that responsible for all log related operations for a
 * given experiment
 *
 * @author bennyl
 */
public abstract class LogManager extends ModuleContainer {

    /**
     * saves the latest changes of the experiment (at given time for a given
     * process)
     *
     * @param logger
     * @param record
     */
    public abstract void commit(Logger logger, LogRecord record);
    
    public abstract Iterable<LogRecord> getRecords(String test, int simulation) throws SQLException ;
    
    public static abstract class LogRecord extends BaseDBFields {
        
        public int aid;
        public long time;
        public long sharedIndex;
        
    }
}
