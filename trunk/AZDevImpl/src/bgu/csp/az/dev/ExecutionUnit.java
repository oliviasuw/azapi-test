/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import bc.dsl.SwingDSL;
import bgu.csp.az.dev.ui.NewUIController;
import bgu.csp.az.api.exp.ConnectionFaildException;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Experiment.ExperimentListener;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.dev.ui.MainWindow;
import bgu.csp.az.impl.AlgorithmMetadata;
import bgu.csp.az.impl.db.DatabaseUnit;
import bgu.csp.az.impl.infra.AbstractExecution;
import bgu.csp.az.impl.infra.AbstractRound;
import bgu.csp.az.impl.infra.LogListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public enum ExecutionUnit implements Experiment.ExperimentListener {

    UNIT;
    List<ExperimentListener> experimentListeners = new LinkedList<ExperimentListener>();
    LinkedBlockingQueue<Runnable> jobs = new LinkedBlockingQueue<Runnable>();
    WorkerThread worker = new WorkerThread();
    Experiment runningExperiment;
    Round currentRound;
    NewUIController ui = null;
    LogListener logListener = null;

    void execute(File xml, boolean withGui) {
        try {
            worker.start();
            runningExperiment = TestXMLReader.read(xml);
            DatabaseUnit.UNIT.delete();
            DatabaseUnit.UNIT.connect();
            DatabaseUnit.UNIT.startCollectorThread();
            runningExperiment.addListener(this);
            SwingDSL.configureUI();
            MainWindow mainW = new MainWindow();
            mainW.startRunning(runningExperiment);

            
            //            if (withGui) {
//                ui = new NewUIController();
//                ui.startUi();
//            }
            //runningExperiment.run();
            runningExperiment.removeListener(this);
            DatabaseUnit.UNIT.stopCollectorThread();
            DatabaseUnit.UNIT.disconnect();
//            System.out.println(runningExperiment.getResult().toString());
        } catch (ConnectionFaildException ex) {
            Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Experiment getRunningExperiment() {
        return runningExperiment;
    }

    public void addExperimentListener(ExperimentListener l) {
        experimentListeners.add(l);
    }

    public void removeExperimentListener(ExperimentListener l) {
        experimentListeners.remove(l);
    }

    public static void main(String[] args) {
        UNIT.execute(new File("exp.xml"), true);
    }

    public AlgorithmMetadata getRunningAlgorithm() {
        if (currentRound == null) {
            return null;
        }
        return ((AbstractRound) currentRound).getAlgorithms().get(0);
    }

    @Override
    public void onExpirementStarted(final Experiment source) {
        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onExpirementStarted(source);
                }
            }
        });

    }

    @Override
    public void onExpirementEnded(final Experiment source) {
        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onExpirementEnded(source);
                }
            }
        });
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    @Override
    public void onNewRoundStarted(final Experiment source, final Round round) {
        currentRound = round;
        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onNewRoundStarted(source, round);
                }
            }
        });
    }

    @Override
    public void onNewExecutionStarted(final Experiment source, final Round round, final Execution exec) {
        if (logListener != null) {
            ((AbstractExecution) exec).addLogListener(logListener);
        }

        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onNewExecutionStarted(source, round, exec);
                }
            }
        });
    }

    @Override
    public void onExecutionEnded(final Experiment source, final Round round, final Execution exec) {
        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onExecutionEnded(source, round, exec);
                }
            }
        });

    }

    public List<Round> getAllRounds() {
        return runningExperiment.getRounds();
    }

    public void setLogListener(LogListener l) {
        logListener = l;
    }

    public class WorkerThread extends Thread {

        public WorkerThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Runnable job = jobs.take();
                    try {
                        job.run();
                    } catch (Exception ex) {
                        if (ex instanceof InterruptedException) {
                            this.interrupt();
                        } else {
                            ex.printStackTrace();
                        }
                    }
                } catch (InterruptedException ex) {
                    this.interrupt();
                }
            }
        }
    }
}
