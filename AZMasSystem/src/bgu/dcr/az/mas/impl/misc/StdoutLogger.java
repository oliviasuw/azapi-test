/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.impl.misc;

import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.impl.InitializationException;
import bgu.dcr.az.mas.misc.Logger;

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
        log("AgentZero", "New Execution started.");
    }

}
