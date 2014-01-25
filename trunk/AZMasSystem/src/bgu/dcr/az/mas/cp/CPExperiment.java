/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.cp;

import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.anop.reg.RegisteryUtils;
import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationException;
import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.api.exen.mdef.ProblemGenerator;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.execs.MultithreadedScheduler;
import bgu.dcr.az.execs.api.Scheduler;
import bgu.dcr.az.execs.api.TerminationReason;
import bgu.dcr.az.mas.AgentSpawner;
import bgu.dcr.az.mas.Execution;
import bgu.dcr.az.mas.exp.AlgorithmDef;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.exp.ExperimentExecutionException;
import bgu.dcr.az.mas.exp.Looper;
import bgu.dcr.az.mas.exp.loopers.SingleExecutionLooper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author User
 */
@Register("experiment")
public class CPExperiment implements Experiment {

    private ProblemGenerator pgen;
    private final List<AlgorithmDef> algorithms = new LinkedList<>();
    private Looper looper = new SingleExecutionLooper();

    /**
     * @propertyName algorithms
     * @return
     */
    public List<AlgorithmDef> getAlgorithms() {
        return algorithms;
    }

    /**
     * @propertyName loop
     * @return
     */
    public Looper getLooper() {
        return looper;
    }

    public void setLooper(Looper looper) {
        this.looper = looper;
    }

    /**
     * @propertyName problem-generator
     * @return
     */
    public ProblemGenerator getProblemGenerator() {
        return pgen;
    }

    public void setProblemGenerator(ProblemGenerator pgen) {
        this.pgen = pgen;
    }

    @Override
    public void execute() throws ExperimentExecutionException, InterruptedException {
        final ExecutorService pool = Executors.newFixedThreadPool(8);
        Scheduler scheduler = new MultithreadedScheduler(pool, 4);

        if (getProblemGenerator() == null) {
            throw new ExperimentExecutionException("No problem generator is defined");
        }

        if (getAlgorithms().isEmpty()) {
            throw new ExperimentExecutionException("At least one algorithm should be defined in order to execute an experiment");
        }

        Object[] configurableElements = {
            getProblemGenerator()
        };

        try {
            
            Random seedGenerator = new Random(10);
            
            List<Configuration> configurationOfElements = new ArrayList<>(configurableElements.length);
            for (Object o : configurableElements) {
                configurationOfElements.add(ConfigurationUtils.createConfigurationFor(o));
            }

            for (int i = 0; i < looper.count(); i++) {
                looper.configure(i, configurationOfElements);
                for (int j = 0; j < configurableElements.length; j++) {
                    configurationOfElements.get(j).configure(configurableElements[j]);
                }

                System.out.println("Start Generating Problem");
                Problem p = new Problem();
                pgen.generate(p, new Random(seedGenerator.nextLong()));
                for (AlgorithmDef adef : algorithms) {
                    AgentSpawner spawner = new SimpleAgentSpawner(RegisteryUtils.getDefaultRegistery().getRegisteredClassByName("ALGORITHM." + adef.getName()));
                    CPExecution exec = new CPExecution(scheduler, spawner, p);
                    System.out.println("Start Running Problem");
                    TerminationReason result = exec.execute();
                    if (result.isError()) {
                        result.getErrorDescription().printStackTrace();
                        return;
                    } else {
                        System.out.println("execution " + i + " done: " + exec.getSolution());
                    }
                }
            }
        } catch (ClassNotFoundException | ConfigurationException ex) {
            throw new ExperimentExecutionException("cannot execute experiment - configuration problem, see cause", ex);
        } finally {
            pool.shutdownNow();
        }
    }

    @Override
    public String toString() {
        return "DCRExperiment{" + "pgen=" + pgen + ", algorithms=" + algorithms + ", looper=" + looper + '}';
    }

}
