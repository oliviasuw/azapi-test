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
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.conf.modules.info.SimpleInfoStream;
import bgu.dcr.az.execs.exps.exe.AdaptiveScheduler;
import bgu.dcr.az.execs.exps.exe.SimulationResult;
import bgu.dcr.az.execs.exps.prog.DefaultExperimentProgress;
import bgu.dcr.az.execs.orm.api.EmbeddedDatabaseManager;
import bgu.dcr.az.execs.orm.H2EmbeddedDatabaseManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * an experiment is a wrapper for an execution. it is intendent to hold modules
 * that are not specific to the execution itself like progress enhancers,
 * databases, etc. it will kick start the containing execution and setup the
 * main progress object.
 *
 * @author bennyl
 */
public final class ModularExperiment extends ModuleContainer {

    private ExecutorService pool;

    public ModularExperiment(ExecutorService pool) {
        this.pool = pool;
    }

    public ExecutionTree execution() {
        return get(ExecutionTree.class);
    }

    public void setExecution(ExecutionTree exec) {
        install(exec);
    }

    public void execute() throws ExperimentFailedException {
        ExecutionTree ex = require(ExecutionTree.class);
        initializeModules(); //start the container if not already started

        System.out.println("Experiment Started!");
        ex.execute();
        System.out.println("Experiment completed!");
    }

    public InfoStream getInfoStream() {
        return get(InfoStream.class);
    }

    @Override
    public void initializeModules() {
        super.initializeModules();
    }

    /**
     * creates a new modular experiment with default modules preloaded
     * </br>
     * default modules are: </br>
     * <ul>
     * <li> {@link EmbeddedDatabaseManager} </li>
     * <li> {@link InfoStream} </li>
     * <li> {@link AdaptiveScheduler} </li>
     * <li> {@link DefaultExperimentProgress} </li>
     * </ul>
     *
     * @param es
     * @return the newly constructed experiment
     */
    public static ModularExperiment createDefault(ExecutorService es, boolean recordProgress) {
        H2EmbeddedDatabaseManager manager = new H2EmbeddedDatabaseManager();
        ModularExperiment result = new ModularExperiment(es);

        result.install(EmbeddedDatabaseManager.class, manager);
        result.install(InfoStream.class, new SimpleInfoStream());
        result.install(AdaptiveScheduler.class, new AdaptiveScheduler(es));
        if (recordProgress) {
            result.install(new DefaultExperimentProgress());
        }
        return result;
    }

    /**
     * same as {@link #createDefault(java.util.concurrent.ExecutorService) } but
     * also set the execution tree to be the one loaded from the given input
     * stream (using the configuration framework)
     *
     * @param executionConfiguration
     * @param es
     * @return
     * @throws IOException
     * @throws ConfigurationException
     */
    public static ModularExperiment createDefault(InputStream executionConfiguration, ExecutorService es, boolean recordProgress) throws IOException, ConfigurationException {
        Configuration conf = ConfigurationUtils.read(executionConfiguration);
        ExecutionTree exec = conf.create();
        ModularExperiment exper = createDefault(es, recordProgress);

        exper.setExecution(exec);
        return exper;
    }

    /**
     * same as
     * {@link #createDefault(java.io.InputStream, java.util.concurrent.ExecutorService)}
     * but the execution service is a new
     * {@link Executors#newCachedThreadPool()}
     *
     * @param executionConfiguration
     * @param recordProgress if true - progress inspectors will be added to the
     * experiment (mainly for UI tools)
     * @return
     * @throws IOException
     * @throws ConfigurationException
     */
    public static ModularExperiment createDefault(InputStream executionConfiguration, boolean recordProgress) throws IOException, ConfigurationException {
        return createDefault(executionConfiguration, Executors.newCachedThreadPool(), recordProgress);
    }

    @Override
    public Collection<Module> getAllModules() {
        return super.getAllModules(); //To change body of generated methods, choose Tools | Templates.
    }
}
