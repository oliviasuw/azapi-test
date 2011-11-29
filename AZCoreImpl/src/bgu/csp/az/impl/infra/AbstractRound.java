
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.infra.CorrectnessTester;
import bgu.csp.az.impl.AlgorithmMetadata;
import bgu.csp.az.api.ano.Variable;
import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.CorrectnessTester.TestResult;
import bgu.csp.az.impl.DebugInfo;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.ExecutionResult;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.Round.RoundResult;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.infra.stat.StatisticCollector;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.api.pgen.ProblemGenerator;
import bgu.csp.az.impl.VarAssign;
import bgu.csp.az.impl.db.DatabaseUnit;
import bgu.csp.az.impl.pgen.MapProblem;
import bgu.csp.az.impl.stat.AbstractStatisticCollector;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author bennyl
 */
public abstract class AbstractRound extends AbstractProcess implements Round {

    public static final String PROBLEM_GENERATOR_PROBLEM_METADATA = "PROBLEM GENERATOR";
    public static final String SEED_PROBLEM_METADATA = "SEED";
    private static final StatisticCollector[] EMPTY_STATISTIC_COLLECTORS_ARRAY = new StatisticCollector[0];
    /**
     * V A R I A B L E S
     */
    @Variable(name = "name", description = "the round name")
    private String name = "";
    @Variable(name = "seed", description = "seed for determining roundiness")
    private long seed = -1;
    //TODO: FOR NOW THIS PARAMETERS ARE GOOD BUT NEED TO TALK WITH ALON AND SEE WHAT TYPE OF EXPIREMENT MORE EXISTS
    @Variable(name = "tick-size", description = "the number of executions in each tick")
    private int tickSize = 100;
    @Variable(name = "run-var", description = "the variable to run")
    private String runVar = "";
    @Variable(name = "start", description = "starting value of the running variable")
    private float start = 0.1f;
    @Variable(name = "end", description = "ending value of the running variable (explicit)")
    private float end = 0.9f;
    @Variable(name = "tick", description = "the runinig variable value increasment")
    private float tick = 0.1f;
    /**
     * F I E L D S
     */
    private List<AlgorithmMetadata> algorithms = new LinkedList<AlgorithmMetadata>();
    private ProblemGenerator pgen = null;
    private List<StatisticCollector> collectors = new LinkedList<StatisticCollector>();
    private RoundResult res = null;
    private ExecutorService pool;
    private Random rand;
    private int current = 0;
    private CorrectnessTester ctester = null;
    private List<RoundListener> listeners = new LinkedList<RoundListener>();
    private float currentVarValue;
    private DebugInfo di = null;
    private long lastProblemSeed = -1; //USED FOR DEBUGING INFORMATION

    public AbstractRound() {
    }

    @Override
    public List<Configureable> getConfiguredChilds() {
        LinkedList<Configureable> ret = new LinkedList<Configureable>();
        ret.addAll(collectors);
        ret.addAll(algorithms);
        if (pgen != null) {
            ret.add(pgen);
        }

        if (ctester != null) {
            ret.add(ctester);
        }

        return ret;
    }

    @Override
    public float getCurrentVarValue() {
        return this.currentVarValue;
    }

    @Override
    public int getLength() {
        if (tick == 0) {
            tick = 0.1f;
        }

        return (int) (((end - start) / tick) + 1.0) * tickSize;//(int) Math.floor((((end - start) / tick ) + 1.0) * (double)tickSize);
    }

    public void setPool(ExecutorService pool) {
        this.pool = pool;
    }

    public ExecutorService getPool() {
        return pool;
    }

    @Override
    public int getCurrentExecutionNumber() {
        return current;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void registerStatisticCollector(StatisticCollector collector) {
        if (collector instanceof AbstractStatisticCollector) {
            ((AbstractStatisticCollector) collector).setRound(this);
        }
        collectors.add(collector);
    }

    @Override
    public StatisticCollector[] getRegisteredStatisticCollectors() {
        return collectors.toArray(EMPTY_STATISTIC_COLLECTORS_ARRAY);
    }

    public List<AlgorithmMetadata> getAlgorithms() {
        return algorithms;
    }

    public void addAlgorithm(AlgorithmMetadata alg) {
        algorithms.add(alg);
    }

    @Override
    public void bubbleDownVariable(String var, Object val) {
        for (AlgorithmMetadata a : algorithms) {
            a.bubbleDownVariable(var, val);
        }

        for (StatisticCollector c : this.collectors) {
            c.bubbleDownVariable(var, val);
        }

        if (ctester != null) {
            ctester.bubbleDownVariable(var, val);
        }

        pgen.bubbleDownVariable(var, val);
    }

    public void debug(DebugInfo di) {
        fireRoundStarted();
        try {
            for (VarAssign v : di.getProblemGeneratorConfiguration()) {
                pgen.bubbleDownVariable(v.getVarName(), v.getValue());
            }

            Problem p = new MapProblem();
            pgen.generate(p, rand);

            AlgorithmMetadata alg = null;
            for (AlgorithmMetadata a : algorithms) {
                if (a.getName().equals(di.getAlgorithmName())) {
                    alg = a;
                    break;
                }
            }

            if (alg != null) {

                if (!execute(p, alg)) {
                    return;
                }
            }

            res = new RoundResult();
        } catch (Exception ex) {
            res = new RoundResult(ex, null);
        } finally {
            DatabaseUnit.UNIT.signal(this);
        }
    }

    @Override
    protected void _run() {
        try {
            fireRoundStarted();

            if (tick == 0) {
                tick = 0.1f;
            }

            for (currentVarValue = start; currentVarValue <= end; currentVarValue += tick) {
                bubbleDownVariable(runVar, currentVarValue);
                for (int i = 0; i < tickSize; i++) {
                    Problem p = nextProblem();
                    for (AlgorithmMetadata alg : getAlgorithms()) {
                        if (!execute(p, alg)) {
                            di = new DebugInfo(this.getName(), alg.getName(), lastProblemSeed);
                            return;
                        }
                    }
                }
            }
            res = new RoundResult();

        } catch (Exception ex) {
            res = new RoundResult(ex, null);
        } finally {
            DatabaseUnit.UNIT.signal(this);
        }

    }
    
    public DebugInfo getFailoreDebugInfo(){
        return di;
    }

    private boolean execute(Problem p, AlgorithmMetadata alg) {
        Execution e = provideExecution(p, alg);
        try {
            e.setStatisticCollectors(collectors);
            fireNewExecution(e);
            e.run();
            ExecutionResult r = e.getResult();
            if (r.isExecutionCrushed()) {
                res = new RoundResult(r.getCrushReason(), e);
                return false;
            } else if (getCorrectnessTester() != null) {
                TestResult testRes = getCorrectnessTester().test(e, r);
                if (!testRes.passed) {
                    res = new RoundResult(testRes.rightAnswer, e);
                    return false;
                }
            }
            fireExecutionEnded(e);
            return true;
        } catch (Exception ex) {
            res = new RoundResult(ex, e);
            return false;
        }
    }

    protected abstract Execution provideExecution(Problem p, AlgorithmMetadata alg);

    @Override
    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return VariableMetadata.scan(this);
    }

    @Override
    public boolean canAccept(Class<? extends Configureable> cls) {
        if (ProblemGenerator.class.isAssignableFrom(cls)) {
            return pgen == null;
        } else if (StatisticCollector.class.isAssignableFrom(cls)) {
            return true;
        } else if (AlgorithmMetadata.class.isAssignableFrom(cls)) {
            return true;
        } else if (CorrectnessTester.class.isAssignableFrom(cls)) {
            return ctester == null;
        } else {
            return false;
        }
    }

    @Override
    public List<Class<? extends Configureable>> provideExpectedSubConfigurations() {
        LinkedList<Class<? extends Configureable>> ret = new LinkedList<Class<? extends Configureable>>();
        ret.add(ProblemGenerator.class);
        ret.add(StatisticCollector.class);
        ret.add(AlgorithmMetadata.class);
        ret.add(CorrectnessTester.class);
        return ret;
    }

    @Override
    public void addSubConfiguration(Configureable sub) throws InvalidValueException {
        if (canAccept(sub.getClass())) {
            if (sub instanceof ProblemGenerator) {
                pgen = (ProblemGenerator) sub;
            } else if (sub instanceof StatisticCollector) {
                registerStatisticCollector((StatisticCollector) sub);
            } else if (sub instanceof AlgorithmMetadata) {
                this.algorithms.add((AlgorithmMetadata) sub);
            } else {
                this.setCorrectnessTester((CorrectnessTester) sub);
            }
        } else {
            throw new InvalidValueException("can only accept 1 problem generator and statistics analyzers");
        }
    }

    @Override
    public void configure(Map<String, Object> variables) {
        VariableMetadata.assign(this, variables);
        rand = new Random(seed);
        onConfigurationComplete();
    }

    @Override
    public RoundResult getResult() {
        return res;
    }

    @Override
    public ProblemGenerator getProblemGenerator() {
        return pgen;
    }

    protected Problem nextProblem() {
        lastProblemSeed = rand.nextLong();
        Random nrand = new Random(lastProblemSeed);
        MapProblem p = new MapProblem();
        HashMap<String, Object> metadata = p.getMetadata();

        metadata.put(SEED_PROBLEM_METADATA, lastProblemSeed);
        metadata.put(PROBLEM_GENERATOR_PROBLEM_METADATA, pgen.getClass().getName());

        pgen.generate(p, nrand);
        return p;
    }

    protected abstract void onConfigurationComplete();

    @Override
    public CorrectnessTester getCorrectnessTester() {
        return this.ctester;
    }

    @Override
    public void setCorrectnessTester(CorrectnessTester ctester) {
        this.ctester = ctester;
    }

    @Override
    public void addListener(RoundListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListener(RoundListener l) {
        listeners.remove(l);
    }

    private void fireRoundStarted() {
        for (RoundListener l : listeners) {
            l.onRoundStarted(this);
        }
    }

    private void fireNewExecution(Execution e) {
        for (RoundListener l : listeners) {
            l.onExecutionStarted(this, e);
        }
    }

    private void fireExecutionEnded(Execution e) {
        for (RoundListener l : listeners) {
            l.onExecutionEnded(this, e);
        }
    }

    @Override
    public String getRunningVarName() {
        return runVar;
    }

    @Override
    public float getVarStart() {
        return this.start;
    }

    @Override
    public float getVarEnd() {
        return this.end;
    }

    @Override
    public float getTick() {
        return this.tick;
    }

    @Override
    public int getTickSize() {
        return this.tickSize;
    }
}
