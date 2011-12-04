/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev;

import bc.dsl.SwingDSL;
import bgu.dcr.az.api.exp.ConnectionFaildException;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Experiment;
import bgu.dcr.az.api.infra.Experiment.ExperimentListener;
import bgu.dcr.az.api.infra.Round;
import bgu.dcr.az.dev.ui.MainWindow;
import bgu.dcr.az.impl.AlgorithmMetadata;
import bgu.dcr.az.impl.db.DatabaseUnit;
import bgu.dcr.az.impl.infra.AbstractExecution;
import bgu.dcr.az.impl.infra.AbstractRound;
import bgu.dcr.az.impl.infra.LogListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    LogListener logListener = null;
    boolean running;
    File badProblemStorage = new File("fail-problems");

    void run(File xml, boolean withGui, boolean debug) throws InterruptedException {
        try {
            worker.start();
            runningExperiment = XMLConfigurator.read(xml);
            SwingDSL.configureUI();
            MainWindow mainW = new MainWindow();

            DatabaseUnit.UNIT.delete();
            DatabaseUnit.UNIT.connect();
            DatabaseUnit.UNIT.startCollectorThread();

            if (debug) {
                runningExperiment = mainW.startDebugging(runningExperiment, badProblemStorage);
            }
            
            runningExperiment.addListener(this);

            mainW.startRunning(runningExperiment);
            running = true;
            runningExperiment.run();
            running = false;
            stop();
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

    public void setFailProblemStorage(File path) {
        System.out.println("Failed Problem Dir Set To: " + (path == null? "none!!":  path.getAbsolutePath()));
        badProblemStorage = path;
        badProblemStorage.mkdirs();
    }

    public void stop() {
        if (running) {
            runningExperiment.stop();
        }

//        runningExperiment.removeListener(this);
        DatabaseUnit.UNIT.stopCollectorThread();
        DatabaseUnit.UNIT.disconnect();
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

    public static void main(String[] args) throws InterruptedException {
        UNIT.run(new File("exp.xml"), true, false);
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

        if (!source.getResult().succeded) {
            PrintWriter pw;
            try {
                String fname = newFileName();
                badProblemStorage.mkdirs();
                pw = new PrintWriter(new File(badProblemStorage.getAbsolutePath() + "/" + fname));
                XMLConfigurator.write(source, pw);
                pw.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    private String newFileName() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy'-'DD'-'HH'-'mm'-'ss'.xml'");
        return df.format(new Date());
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
