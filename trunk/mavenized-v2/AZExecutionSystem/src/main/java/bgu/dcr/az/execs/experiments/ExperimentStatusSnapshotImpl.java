/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.experiments;

import bgu.dcr.az.execs.api.experiments.ExperimentStatusSnapshot;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Benny Lutati
 */
public class ExperimentStatusSnapshotImpl implements ExperimentStatusSnapshot {

    public int finishedExecutions = 0;
    public boolean started = false;
    public boolean ended = false;
    public ConcurrentLinkedQueue<String> finishedExperimentNames = new ConcurrentLinkedQueue<>();
    public ExperimentStatusSnapshot currentExecutedSubExperimentStatus = null;
    public String currentExecutedSubExperimentName;

    public ExperimentStatusSnapshotImpl() {
    }

    private ExperimentStatusSnapshotImpl(ExperimentStatusSnapshotImpl copy) {
        this.currentExecutedSubExperimentName = copy.currentExecutedSubExperimentName;
        this.currentExecutedSubExperimentStatus = copy.currentExecutedSubExperimentStatus;
        this.started = copy.started;
        this.ended = copy.ended;
        this.finishedExperimentNames.addAll(copy.finishedExperimentNames);
        this.finishedExecutions = copy.finishedExecutions;
    }

    @Override
    public int finishedExecutions() {
        return finishedExecutions;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    @Override
    public Collection<String> finishedSubExperimentNames() {
        return finishedExperimentNames;
    }

    @Override
    public ExperimentStatusSnapshot currentExecutedSubExperimentStatus() {
        return currentExecutedSubExperimentStatus;
    }

    public void start() {
        this.started = true;
        this.ended = false;
    }

    public void end() {
        this.ended = true;
    }

    @Override
    public String currentExecutedSubExperimentName() {
        return currentExecutedSubExperimentName;
    }

    public ExperimentStatusSnapshotImpl copy(){
        return new ExperimentStatusSnapshotImpl(this);
    }
    
}
