/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.loggers;

import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.loggers.LogManager;
import bgu.dcr.az.execs.api.loggers.Logger;
import bgu.dcr.az.orm.api.DefinitionDatabase;

/**
 *
 * @author bennyl
 */
public class MessageLogger implements Logger {

    @Override
    public void initialize(LogManager manager, Execution execution, DefinitionDatabase database) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "Message logger";
    }
}
