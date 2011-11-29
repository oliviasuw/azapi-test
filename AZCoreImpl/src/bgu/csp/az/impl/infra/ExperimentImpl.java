/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.exp.InvalidValueException;
import bgu.csp.az.api.infra.Configureable;
import bgu.csp.az.impl.DebugInfo;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Round.RoundResult;
import bgu.csp.az.api.infra.VariableMetadata;
import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.impl.db.DatabaseUnit;
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
    private DebugInfo di;

    @Override
    public void _run() {

        ExecutorService pool = Executors.newCachedThreadPool();
        try {

            fireExperimentStarted();

            List<Round> roundsToRun = new LinkedList<Round>();
            if (di == null) {
                roundsToRun.addAll(rounds);
            } else {
                for (Round r : rounds) {
                    if (r.getName().equals(di.getRoundName())) {
                        roundsToRun.add(r);
                        break;
                    }
                }
            }

            for (Round current : roundsToRun) {
                if (Thread.interrupted()) {
                    result = new ExperimentResult(true);
                }

                current.addListener(this);

                if (current instanceof AbstractRound) {
                    ((AbstractRound) current).setPool(pool);
                }

                if (di == null) {
                    current.run();
                } else {
                    ((AbstractRound) current).debug(di);
                }

                DatabaseUnit.UNIT.signal(current); // SIGNALING - TELLING THAT STATISTICS COLLECTION TO THE CURRENT ROUND IS OVER
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
    public List<Configureable> getConfiguredChilds() {
        LinkedList<Configureable> ret = new LinkedList<Configureable>(rounds);
        if (di != null) {
            ret.add(di);
        } else if (getResult() != null && !getResult().succeded) {
            if (getResult().problematicRound instanceof AbstractRound && ((AbstractRound) getResult().problematicRound).getFailoreDebugInfo() != null) {
                ret.add(((AbstractRound) getResult().problematicRound).getFailoreDebugInfo());
            }
        }

        return ret;
    }

    public DebugInfo getFailureDebugInfo() {
        return di;
    }

    @Override
    public void addRound(Round round) {
        rounds.add(round);
    }

    @Override
    public List<Round> getRounds() {
        if (di == null) {
            return Collections.unmodifiableList(rounds);
        }else {
            List<Round> ret = new LinkedList<Round>();
            for (Round r : rounds){
                if (r.getName().equals(di.getRoundName())){
                    ret.add(r);
                    return ret;
                }
            }
            
            return ret;
        }
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
        ll.add(DebugInfo.class);
        return ll;
    }

    @Override
    public boolean canAccept(Class<? extends Configureable> cls) {
        if (DebugInfo.class.isAssignableFrom(cls)) {
            return di == null;
        }
        return Round.class.isAssignableFrom(cls);
    }

    @Override
    public void addSubConfiguration(Configureable sub) throws InvalidValueException {
        if (!canAccept(sub.getClass())) {
            throw new InvalidValueException("only except rounds");
        } else if (sub instanceof Round) {
            Round r = (Round) sub;
            addRound(r);
        } else {
            di = (DebugInfo) sub;
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

    @Override
    public void bubbleDownVariable(String var, Object val) {
        for (Round r : rounds) {
            r.bubbleDownVariable(var, val);
        }
    }

    @Override
    public int getLength() {
        if (di != null) {
            return 1;
        } else {
            int sum = 0;
            for (Round r : rounds) {
                sum += r.getLength();
            }
            return sum;
        }

    }
}
