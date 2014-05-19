/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.loggers;

import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.orm.api.DefinitionDatabase;

/**
 *
 * @author bennyl
 */
public interface Logger<T> {

    void initialize(LogManager manager, Execution<T> execution, DefinitionDatabase database);

    String getName();
}
