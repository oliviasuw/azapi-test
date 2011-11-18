/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.impl.AlgorithmMetadata;
import bgu.csp.az.api.ano.Variable;
import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.infra.Round.RoundResult;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.infra.stat.StatisticAnalyzer;
import bgu.csp.az.api.pgen.ProblemGenerator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public abstract class AbstractRound extends AbstractProcess implements Round {

    private static final StatisticAnalyzer[] EMPTY_STATISTIC_ANALAYZER_ARRAY = new StatisticAnalyzer[0];
    
    @Variable(name="name", description="the round name")
    private String name = "";
    @Variable(name="length", description="the number of executions in this round")
    private int length = 100;
    @Variable(name="seed", description="seed for determining roundiness")
    private long seed = -1;
    private List<AlgorithmMetadata> algorithms = new LinkedList<AlgorithmMetadata>();
    
    private ProblemGenerator pgen = null;
    private List<StatisticAnalyzer> analyzers = new LinkedList<StatisticAnalyzer>();

    RoundResult res = null;

    public AbstractRound() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    @Override
    public String name() {
        return name;
    }

    @Override
    public int length() {
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

    @Override
    protected void _run() {
        res = __run();
    }
    
    @Override
    public long seed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public String getConfigurationName() {
        return "round";
    }

    @Override
    public String getConfigurationDescription() {
        return "configurable part of an expirement";
    }

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return VariableMetadata.scan(this);
    }

    @Override
    public boolean canAccept(Class<? extends Configureable> cls) {
        if (ProblemGenerator.class.isAssignableFrom(cls)){
            return pgen != null;
        }else if (StatisticAnalyzer.class.isAssignableFrom(cls)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<Class<? extends Configureable>> provideExpectedSubConfigurations() {
        LinkedList<Class<? extends Configureable>> ret = new LinkedList<Class<? extends Configureable>>();
        ret.add(ProblemGenerator.class);
        ret.add(StatisticAnalyzer.class);
        return ret;
    }

    @Override
    public void addSubConfiguration(Configureable sub) throws InvalidValueException {
        if (canAccept(sub.getClass())){
            if (sub instanceof ProblemGenerator){
                pgen = (ProblemGenerator) sub;
            }else {
                this.analyzers.add((StatisticAnalyzer) sub);
            }
        }else {
            throw new InvalidValueException("can only accept 1 problem generator and statistics analyzers");
        }
    }

    @Override
    public void configure(Map<String, Object> variables) {
        VariableMetadata.assign(this, variables);
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
    
    protected abstract void onConfigurationComplete();

    protected abstract RoundResult __run();

}
