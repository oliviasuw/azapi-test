/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.api.experiment;

import bgu.dcr.az.common.random.RandomSequance;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.dcr.api.modules.CPCorrectnessTester;
import bgu.dcr.az.dcr.api.modules.ProblemGenerator;
import bgu.dcr.az.dcr.api.problems.Problem;
import bgu.dcr.az.dcr.modules.progress.CPProgress;
import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import bgu.dcr.az.execs.exps.ExperimentProgressInspector;
import bgu.dcr.az.execs.exps.exe.ExecutionEnvironment;
import bgu.dcr.az.execs.exps.exe.Looper;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.exps.exe.SimulationConfiguration;
import bgu.dcr.az.execs.exps.exe.BaseStatisticFields;
import bgu.dcr.az.execs.exps.exe.DefaultExperimentRoot;
import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.execs.sim.Agt0DSL;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author bennyl
 */
@Register("cp-test")
public class CPTest extends Test {

    private ProblemGenerator pgen;

    private ExecutionEnvironment env = ExecutionEnvironment.async;
    private RandomSequance seq = new RandomSequance();

    private WeakReference<Problem> cachedLastProblem;
    private int cachedLastProblemId = -1;

    @Override
    public int numChildren() {
        return getLooper().count() * amountInstalled(AlgorithmDef.class);
    }

    @Override
    public Simulation child(int index) {
        List<AlgorithmDef> algorithms = getAlgorithms();

        //note that the iteration number is different than the execution index since each iteration can contain several algorithms execution
        int iteration = index / algorithms.size();

        Problem p = null;
        if (iteration == cachedLastProblemId) {
            p = cachedLastProblem.get();
        }

        if (p == null) {
            p = new Problem();
            getLooper().configure(iteration, pgen);
            pgen.generate(p, new Random(seq.getIthLong(iteration)));
            cachedLastProblem = new WeakReference<>(p);
            cachedLastProblemId = iteration;
        }

        p.resetCC_Count();

        AlgorithmDef adef = algorithms.get(index % algorithms.size());

        CPData data = new CPData(new CPSolution(p), p, adef, getLooper().getRunningVariableName(), getLooper().getRunningVariableValue(iteration));

        BaseCPStatisticFields fields = new BaseCPStatisticFields();
        fields.algorithm_instance = data.getAlgorithm().getInstanceName();
        fields.rvar = data.getRunningVarValue();
        fields.test = getName();

        SimulationConfiguration.Builder conf = p.getInitialConfiguration();
        conf.withAllAgentsOfClass(adef.resolveClass());
        conf.withGlobalInitializationArgs(adef.getAssignments().stream().collect(Collectors.toMap(e -> e.getPropertyName(), e -> e.getValue())));
        conf.withEnvironment(env);
        conf.withBaseStatisticFields(fields);

        return new Simulation(index, data, conf, this);
    }

    @Override
    protected Class<? extends ExperimentProgressInspector>[] supplyProgressInspectors() {
        return new Class[]{CPProgress.class};
    }

    /**
     * @propertyName seed
     * @return
     */
    public long getSeed() {
        return seq.getSeed();
    }

    public void setSeed(long seed) {
        this.seq.reset(seed);
    }

    /**
     * @propertyName algorithms
     * @return
     */
    public List<AlgorithmDef> getAlgorithms() {
        return getDirectList(AlgorithmDef.class);
    }

    /**
     * @propertyName statistics
     * @return
     */
    public List<StatisticCollector> getStatisticCollectors() {
        return getDirectList(StatisticCollector.class);
    }

    /**
     * @propertyName env
     * @return
     */
    public ExecutionEnvironment getExecutionEnvironment() {
        return env;
    }

    public void setExecutionEnvironment(ExecutionEnvironment executionEnvironment) {
        this.env = executionEnvironment;
    }

    /**
     * @propertyName loop
     * @return
     */
    public Looper getLooper() {
        return get(Looper.class);
    }

    public void setLooper(Looper looper) {
        install(looper);
    }

    /**
     * @propertyName problem-generator
     * @return
     */
    public ProblemGenerator getProblemGenerator() {
        return get(ProblemGenerator.class);
    }

    public void setProblemGenerator(ProblemGenerator pgen) {
        install(pgen);
    }

    /**
     * @propertyName correctness-tester
     * @return
     */
    public CPCorrectnessTester getCorrectnessTester() {
        return get(CPCorrectnessTester.class);
    }

    public void setCorrectnessTester(CPCorrectnessTester correctnessTester) {
        install(correctnessTester);
    }

    @Override
    public void initialize(DefaultExperimentRoot root) {
        pgen = require(ProblemGenerator.class);
        if (getAlgorithms().isEmpty()) {
            Agt0DSL.panic("cannot run experiment without any algorithm defined. (please add one in test.xml file)");
        }
    }

    public static class BaseCPStatisticFields extends BaseStatisticFields {

        public String algorithm_instance;
        public double rvar;
        public String test;
    }

}
