
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
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.ExecutionResult;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.Round.RoundResult;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.infra.stat.StatisticAnalyzer;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.api.pgen.ProblemGenerator;
import bgu.csp.az.impl.pgen.MapProblem;
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

    public static final String P1_PROBLEM_METADATA = "P1";
    public static final String P2_PROBLEM_METADATA = "P2";
    public static final String PROBLEM_GENERATOR_PROBLEM_METADATA = "PROBLEM GENERATOR";
    public static final String SEED_PROBLEM_METADATA = "SEED";
    private static final StatisticAnalyzer[] EMPTY_STATISTIC_ANALAYZER_ARRAY = new StatisticAnalyzer[0];
    @Variable(name = "name", description = "the round name")
    private String name = "";
    @Variable(name = "length", description = "the number of executions in this round")
    private int length = 100;
    @Variable(name = "seed", description = "seed for determining roundiness")
    private long seed = -1;
    //TODO: FOR NOW THIS PARAMETERS ARE GOOD BUT NEED TO TALK WITH ALON AND SEE WHAT TYPE OF EXPIREMENT MORE EXISTS
    @Variable(name = "p1", description = "probability of constraint between two variables")
    private float p1 = 0.6f;
    @Variable(name = "p2-start", description = "probability of conflict between two constrainted variables: at start of the round")
    private float p2Start = 0.1f;
    @Variable(name = "p2-end", description = "probability of conflict between two constrainted variables: at end of the round")
    private float p2End = 0.9f;
    @Variable(name = "p2-tick", description = "probability of conflict between two constrainted variables: increasment size")
    private float p2Tick = 0.1f;
    private List<AlgorithmMetadata> algorithms = new LinkedList<AlgorithmMetadata>();
    private ProblemGenerator pgen = null;
    private List<StatisticAnalyzer> analyzers = new LinkedList<StatisticAnalyzer>();
    private RoundResult res = null;
    private ExecutorService pool;
    private Random rand;
    private int current = 0;
    private CorrectnessTester ctester = null;
    private List<RoundListener> listeners = new LinkedList<RoundListener>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("round:\n").append("name = ").append(name).append("\nlength = ").append(length).append("\nseed = ").append(seed).append("\np1 = ").append(p1).append("\np2-start = ").append(p2Start).append("\np2-end = ").append(p2End).append("\np2-tick = ").append(p2Tick).append("\n");
        if (pgen == null) {
            sb.append("no problem generator defined!\n");
        } else {
            sb.append("pgen = ").append(pgen.toString()).append("\n");
        }

        return sb.toString();
    }

    public AbstractRound() {
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

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void registerStatisticAnalyzer(StatisticAnalyzer analyzer) {
        analyzers.add(analyzer);
    }

    @Override
    public StatisticAnalyzer[] getRegisteredStatisticAnalayzers() {
        return analyzers.toArray(EMPTY_STATISTIC_ANALAYZER_ARRAY);
    }

    public List<AlgorithmMetadata> getAlgorithms() {
        return algorithms;
    }

    @Override
    protected void _run() {
        ExecutionResult r;
        TestResult testRes;
        Execution e = null;
        try {
            fireRoundStarted();
            for (int i = 0; i < getLength(); i++) {
                Problem p = nextProblem();
                for (AlgorithmMetadata alg : getAlgorithms()) {
                    e = provideExecution(p, alg);
                    fireNewExecution(e);
                    e.run();
                    r = e.getResult();
                    if (r.isExecutionCrushed()) {
                        res = new RoundResult(r.getCrushReason(), e);
                        return;
                    } else if (getCorrectnessTester() != null) {
                        testRes = getCorrectnessTester().test(e, r);
                        if (!testRes.passed) {
                            res = new RoundResult(testRes.rightAnswer, e);
                            return;
                        }
                    }
                    fireExecutionEnded(e);
                }
            }

            res = new RoundResult();
        } catch (Exception ex) {
            res = new RoundResult(ex, e);
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

    public float getP2End() {
        return p2End;
    }

    public float getP2Start() {
        return p2Start;
    }

    public float getP2Tick() {
        return p2Tick;
    }

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return VariableMetadata.scan(this);
    }

    @Override
    public boolean canAccept(Class<? extends Configureable> cls) {
        if (ProblemGenerator.class.isAssignableFrom(cls)) {
            return pgen == null;
        } else if (StatisticAnalyzer.class.isAssignableFrom(cls)) {
            return true;
        } else if (AlgorithmMetadata.class.isAssignableFrom(cls)) {
            return true;
        }else if (CorrectnessTester.class.isAssignableFrom(cls)){
            return ctester == null;
        } else {
            return false;
        }
    }

    @Override
    public List<Class<? extends Configureable>> provideExpectedSubConfigurations() {
        LinkedList<Class<? extends Configureable>> ret = new LinkedList<Class<? extends Configureable>>();
        ret.add(ProblemGenerator.class);
        ret.add(StatisticAnalyzer.class);
        ret.add(AlgorithmMetadata.class);
        ret.add(CorrectnessTester.class);
        return ret;
    }

    @Override
    public void addSubConfiguration(Configureable sub) throws InvalidValueException {
        if (canAccept(sub.getClass())) {
            if (sub instanceof ProblemGenerator) {
                pgen = (ProblemGenerator) sub;
            } else if (sub instanceof StatisticAnalyzer) {
                this.analyzers.add((StatisticAnalyzer) sub);
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
        float p2 = getP2For(current++);
        final long nseed = rand.nextLong();
        Random nrand = new Random(nseed);
        MapProblem p = new MapProblem();
        HashMap<String, Object> metadata = p.getMetadata();

        metadata.put(SEED_PROBLEM_METADATA, nseed);
        metadata.put(PROBLEM_GENERATOR_PROBLEM_METADATA, pgen.getClass().getName());
        metadata.put(P1_PROBLEM_METADATA, p1);
        metadata.put(P2_PROBLEM_METADATA, p2);

        pgen.generate(p, nrand, p1, p2);
        return p;
    }

    private float getP2For(int idx) {
        if (p2Tick == 0) {
            return 0;
        } else if (p2Start == p2End) {
            return p2Start;
        } else {
            float delta = p2End - p2Start;
            float changes = delta / p2Tick;
            float tickLength = (float) length / changes;
            return p2Start + p2Tick * (idx % tickLength);
        }
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
        for (RoundListener l : listeners) l.onRoundStarted(this);
    }

    private void fireNewExecution(Execution e) {
        for (RoundListener l : listeners) l.onExecutionStarted(this, e);
    }

    private void fireExecutionEnded(Execution e) {
        for (RoundListener l : listeners) l.onExecutionEnded(this, e);
    }
    
}
