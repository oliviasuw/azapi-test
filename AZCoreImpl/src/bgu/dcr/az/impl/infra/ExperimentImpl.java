/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.infra;

import bgu.dcr.az.api.ano.Configuration;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.impl.DebugInfo;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Test.TestResult;
import bgu.dcr.az.api.infra.VariableMetadata;
import bgu.dcr.az.api.infra.Experiment;
import bgu.dcr.az.api.infra.Test;
import bgu.dcr.az.impl.db.DatabaseUnit;
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
@Register(name = "experiment", display="Experiment")
public class ExperimentImpl extends AbstractProcess implements Experiment, Test.TestListener {

    private static final VariableMetadata[] EMPTY_VARIABLE_ARRAY = new VariableMetadata[0];
    private List<Test> tests = new ArrayList<Test>();
    private ExperimentResult result;
    private LinkedList<Experiment.ExperimentListener> listeners = new LinkedList<ExperimentListener>();
    private DebugInfo di;

    @Override
    public void _run() {

        ExecutorService pool = Executors.newCachedThreadPool();
        try {

            fireExperimentStarted();

            List<Test> testsToRun = new LinkedList<Test>();
            if (di == null) {
                testsToRun.addAll(tests);
            } else {
                for (Test r : tests) {
                    if (r.getName().equals(di.getTestName())) {
                        testsToRun.add(r);
                        break;
                    }
                }
            }

            for (Test current : testsToRun) {
                if (Thread.interrupted()) {
                    result = new ExperimentResult(true);
                }

                current.addListener(this);

                if (current instanceof AbstractTest) {
                    ((AbstractTest) current).setPool(pool);
                }

                if (di == null) {
                    current.run();
                } else {
                    ((AbstractTest) current).debug(di);
                }

                DatabaseUnit.UNIT.signal(current); // SIGNALING - TELLING THAT STATISTICS COLLECTION TO THE CURRENT TEST IS OVER
                current.removeListener(this);
                TestResult res = current.getResult();
                switch (res.finishStatus) {
                    case CRUSH:
                    case WRONG_RESULT:
                        result = new ExperimentResult(current, res);
                        return;
                }
            }

            result = new ExperimentResult(false);
        }catch(Exception ex){
            ex.printStackTrace();
        } finally {
            fireExperimentEnded();
            pool.shutdownNow();
        }
    }

    public DebugInfo getFailureDebugInfo() {
        return di;
    }

    @Configuration(name="Test", description="Add new test for the experiment")
    @Override
    public void addTest(Test test) {
        tests.add(test);
    }

    @Override
    public List<Test> getTests() {
        if (di == null) {
            return Collections.unmodifiableList(tests);
        }else {
            List<Test> ret = new LinkedList<Test>();
            for (Test r : tests){
                if (r.getName().equals(di.getTestName())){
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

    @Configuration(name="Debug Information", description="Required if the test is to be run in debug mode")
    public void setDebugInfo(DebugInfo di){
        this.di = di;
    }
    
    public DebugInfo getDebugInfo(){
        return this.di;
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
    public void onTestStarted(Test source) {
        for (ExperimentListener l : listeners) {
            l.onNewTestStarted(this, source);
        }
    }

    @Override
    public void onExecutionStarted(Test source, Execution exec) {
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
    public void onExecutionEnded(Test source, Execution exec) {
        for (ExperimentListener l : listeners) {
            l.onExecutionEnded(this, source, exec);
        }
    }

    @Override
    public int getLength() {
        if (di != null) {
            return 1;
        } else {
            int sum = 0;
            for (Test r : tests) {
                sum += r.getLength();
            }
            return sum;
        }

    }

}
