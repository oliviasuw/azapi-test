/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.infra;

import bgu.dcr.az.api.ano.Configuration;
import bgu.dcr.az.api.ano.Register;
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
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "experiment")
public class ExperimentImpl extends AbstractProcess implements Experiment, Test.TestListener {

    private List<Thread> allThreads = new Vector<Thread>();
    private static final VariableMetadata[] EMPTY_VARIABLE_ARRAY = new VariableMetadata[0];
    private List<Test> tests = new ArrayList<Test>();
    private ExperimentResult result;
    private LinkedList<Experiment.ExperimentListener> listeners = new LinkedList<ExperimentListener>();
    private DebugInfo di;
    private ExecutorService pool;

    @Override
    public void _run() {

        pool = Executors.newCachedThreadPool(new ThreadFactory() {

            AtomicInteger ai = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("pool-thread-" + ai.getAndIncrement());
                allThreads.add(t);
                return t;
            }
        });
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

            for (Test currentTest : testsToRun) {
                if (Thread.currentThread().isInterrupted()) {
                    result = new ExperimentResult(true);
                }

                currentTest.addListener(this);

                if (currentTest instanceof AbstractTest) {
                    ((AbstractTest) currentTest).setExperiment(this);
                }


                if (di == null) {
                    currentTest.run();
                } else {
                    ((AbstractTest) currentTest).debug(di);
                }

                DatabaseUnit.UNIT.signal(currentTest); // SIGNALING - TELLING THAT STATISTICS COLLECTION TO THE CURRENT TEST IS OVER
                currentTest.removeListener(this);
                TestResult res = currentTest.getResult();
                switch (res.finishStatus) {
                    case CRUSH:
                    case WRONG_RESULT:
                        result = new ExperimentResult(currentTest, res);
                        setDebugInfo(((AbstractTest) currentTest).getDebugInfo());
                        return;
                }
            }

            result = new ExperimentResult(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            fireExperimentEnded();
            pool.shutdownNow();
        }
    }

    @Configuration(name = "Test", description = "Add new test for the experiment")
    @Override
    public void addTest(Test test) {
        tests.add(test);
    }

    @Override
    public List<Test> getTests() {
        if (di == null) {
            return Collections.unmodifiableList(tests);
        } else {
            List<Test> ret = new LinkedList<Test>();
            for (Test r : tests) {
                if (r.getName().equals(di.getTestName())) {
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

    @Configuration(name = "Debug Information", description = "Required if the test is to be run in debug mode")
    public void setDebugInfo(DebugInfo di) {
        this.di = di;
    }

    public DebugInfo getDebugInfo() {
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

    @Override
    public void stop() {
        pool.shutdownNow();
    }

    @Override
    public ExecutorService getThreadPool() {
        return pool;
    }
}
