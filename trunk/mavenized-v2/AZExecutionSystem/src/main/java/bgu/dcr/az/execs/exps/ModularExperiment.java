/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.execs.api.statistics.InfoStream;
import bgu.dcr.az.execs.api.statistics.SimpleInfoStream;
import bgu.dcr.az.execs.exps.exe.AdaptiveScheduler;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.execs.orm.H2EmbeddedDatabaseManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * an experiment is a wrapper for an execution. it is intendent to hold modules
 * that are not specific to the execution itself like progress enhancers,
 * databases, etc. it will kick start the containing execution and setup the
 * main progress object.
 *
 * @author bennyl
 */
public final class ModularExperiment extends ModuleContainer {

    ExecutorService pool;

    public ModularExperiment(ExecutorService pool) {
        this.pool = pool;
    }

    public ExecutionTree execution() {
        return get(ExecutionTree.class);
    }

    public void setExecution(ExecutionTree exec) {
        supply(exec);
    }

    public ExperimentProgress execute() {
        ExecutionTree ex = require(ExecutionTree.class);
        startModuleContainer(); //start the container
        final ExperimentProgress experimentProgress = new ExperimentProgress(this);

        pool.execute(() -> {
            ex.execute();
            experimentProgress.setRunning(false);
        });

        return experimentProgress;
    }

    public InfoStream getInfoStream() {
        return get(InfoStream.class);
    }

    /**
     * creates a new modular experiment with default modules preloaded
     * </br>
     * default modules are: </br>
     * <ul>
     * <li> database [EmbeddedDatabaseManager] </li>
     * <li> info-stream [InfoStream] </li>
     * <li> scheduler-provider [***?]</li>
     * </ul>
     *
     * @param es
     * @return the newly constructed experiment
     */
    public static ModularExperiment createDefault(ExecutorService es) {
        H2EmbeddedDatabaseManager manager = new H2EmbeddedDatabaseManager();
        ModularExperiment result = new ModularExperiment(es);

        result.supply(EmbeddedDatabaseManager.class, manager);
        result.supply(InfoStream.class, new SimpleInfoStream());
        result.supply(AdaptiveScheduler.class, new AdaptiveScheduler(es));
        return result;
    }

    public static ModularExperiment createDefault(InputStream executionConfiguration, ExecutorService es) throws IOException, ConfigurationException {
        Configuration conf = ConfigurationUtils.read(executionConfiguration);
        ExecutionTree exec = conf.create();
        ModularExperiment exper = createDefault(es);

        exper.setExecution(exec);
        return exper;
    }

    @Override
    public Collection<Module> getAllModules() {
        return super.getAllModules(); //To change body of generated methods, choose Tools | Templates.
    }
}
