/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.logger;

import bgu.dcr.az.dcr.api.modules.Logger;
import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.exceptions.InitializationException;

/**
 *
 * @author User
 */
public class StdoutLogger implements Logger {

    @Override
    public void log(String logger, String msg) {
        System.out.printf("%s: %s\n", logger, msg);
    }

    @Override
    public void initialize(Execution ex) throws InitializationException {
        
        log("AgentZero", "New Execution started");
    }

}
