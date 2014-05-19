/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.execs.api.loggers;

import bgu.dcr.az.execs.api.experiments.ExecutionService;
import java.util.Collection;

/**
 * represents an entity that responsible for all log related
 * operations for a given experiment
 * @author bennyl
 */
public interface LogManager extends ExecutionService {
    
    /**
     * @return all registered loggers (for a given experiment)
     */
    Collection<Logger> registered();
    
    /**
     * register a new logger for current experiment
     * 
     * @param logger 
     */
    void register(Logger logger);
    
    /**
     * saves the latest changes of the experiment (at given time for a given process)
     * @param record
     */
    void commit(LogRecord record);
}