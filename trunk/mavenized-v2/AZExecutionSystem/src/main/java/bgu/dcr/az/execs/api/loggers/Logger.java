/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.api.loggers;

import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.execs.orm.api.DefinitionDatabase;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;

/**
 *
 * @author bennyl
 * @param <T>
 */
public abstract class Logger<T extends Simulation> implements Module<T> {

    private ModuleContainer mc;
    private LogManager lman;

    @Override
    public final void installInto(T mc) {
        this.mc = mc;
        lman = mc.require(LogManager.class);
        EmbeddedDatabaseManager db = mc.require(EmbeddedDatabaseManager.class);
        initialize(db.createDefinitionDatabase());
        
    }

    protected T execution() {
        return (T) mc;
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

    public abstract void initialize(DefinitionDatabase database);

    public String getName() {
        return toString();
    }

}
