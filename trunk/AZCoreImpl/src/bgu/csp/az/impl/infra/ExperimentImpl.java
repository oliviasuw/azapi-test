/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.api.infra.CorrectnessTester;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Round.RoundResult;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Round;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author bennyl
 */
@Register(name = "experiment")
public class ExperimentImpl extends AbstractProcess implements Experiment, Round.RoundListener {

    private static final VariableMetadata[] EMPTY_VARIABLE_ARRAY = new VariableMetadata[0];
    private List<Round> rounds = new ArrayList<Round>();
    private ExperimentResult result;
    private LinkedList<Experiment.ExperimentListener> listeners = new LinkedList<ExperimentListener>();

    @Override
    public void _run() {
        ExecutorService pool = Executors.newCachedThreadPool();
        try {

            fireExperimentStarted();

            for (Round current : rounds) {
                if (Thread.interrupted()) {
                    result = new ExperimentResult(true);
                }

                current.addListener(this);

                if (current.canAccept(CorrectnessTester.class)) {
                    current.addSubConfiguration(new DefaultCorrectnessTester());
                }

                if (current instanceof AbstractRound) {
                    ((AbstractRound) current).setPool(pool);
                }

                current.run();
                current.removeListener(this);
                RoundResult res = current.getResult();
                switch (res.finishStatus) {
                    case CRUSH:
                    case WRONG_RESULT:
                        result = new ExperimentResult(current, res);
                        return;
                }
            }

            result = new ExperimentResult(false);
        } finally {
            fireExperimentEnded();
            pool.shutdownNow();
        }
    }

    @Override
    public void addRound(Round round) {
        rounds.add(round);
    }

    @Override
    public List<Round> getRounds() {
        return Collections.unmodifiableList(rounds);
    }

    @Override
    public ExperimentResult getResult() {
        return result;
    }

    @Override
    public VariableMetadata[] provideExpectedVariables() {
        return EMPTY_VARIABLE_ARRAY;
    }

    @Override
    public List<Class<? extends Configureable>> provideExpectedSubConfigurations() {
        Class<? extends Configureable> ret = Round.class;
        LinkedList<Class<? extends Configureable>> ll = new LinkedList<Class<? extends Configureable>>();
        ll.add(ret);
        return ll;
    }

    @Override
    public boolean canAccept(Class<? extends Configureable> cls) {
        return Round.class.isAssignableFrom(cls);
    }

    @Override
    public void addSubConfiguration(Configureable sub) throws InvalidValueException {
        if (!canAccept(sub.getClass())) {
            throw new InvalidValueException("only except rounds");
        } else {
            Round r = (Round) sub;
            addRound(r);
        }
    }

    @Override
    public void configure(Map<String, Object> variables) {
        //NO VARIABLES!
    }

    @Override
    public void addListener(ExperimentListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListener(ExperimentListener l) {
        listeners.remove(l);
    }

    @Override
    public void onRoundStarted(Round source) {
        for (ExperimentListener l : listeners) {
            l.onNewRoundStarted(this, source);
        }
    }

    @Override
    public void onExecutionStarted(Round source, Execution exec) {
        for (ExperimentListener l : listeners) {
            l.onNewExecutionStarted(this, source, exec);
        }

    }

    private void fireExperimentStarted() {
        for (ExperimentListener l : listeners) {
            l.onExpirementStarted(this);
        }
    }

    private void fireExperimentEnded() {
        for (ExperimentListener l : listeners) {
            l.onExpirementEnded(this);
        }
    }

    @Override
    public void onExecutionEnded(Round source, Execution exec) {
        for (ExperimentListener l : listeners) {
            l.onExecutionEnded(this, source, exec);
        }
    }
}
