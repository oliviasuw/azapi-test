/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui;

import bgu.dcr.az.anop.utils.EventListeners;
import bgu.dcr.az.mas.exp.ExperimentStatusSnapshot;
import bgu.dcr.az.mas.impl.ExperimentStatusSnapshotImpl;

/**
 *
 * @author User
 */
public class ExperimentStatusEventObserver implements ExperimentStatusUpdatesListener {

    private final ExperimentStatusSnapshotImpl knownStatus = new ExperimentStatusSnapshotImpl();
    private final EventListeners<ExperimentStatusEventListener> listeners = EventListeners.create(ExperimentStatusEventListener.class);

    @Override
    public void update(ExperimentStatusSnapshot snapshot) {
        if (snapshot.isStarted() && !knownStatus.isStarted()) {
            listeners.fire().onExperimentStarted();
            knownStatus.started = true;
        }

        if (snapshot.isEnded() && !knownStatus.isEnded()) {
            listeners.fire().onExperimentEnded();
            knownStatus.ended = true;
        }

        if (snapshot.finishedExecutions() != knownStatus.finishedExecutions()) {
            listeners.fire().onNumberOfFinishedExecutionsChanged(snapshot.finishedExecutions());
            knownStatus.finishedExecutions = snapshot.finishedExecutions();
        }

        if (snapshot.finishedSubExperimentNames().size() != knownStatus.finishedSubExperimentNames().size()) {
            for (String k : snapshot.finishedSubExperimentNames()) {
                if (!knownStatus.finishedSubExperimentNames().contains(k)) {
                    listeners.fire().onSubExperimentEnded(k);
                    knownStatus.finishedExperimentNames.add(k);
                }
            }
        }

        final String currentExecutedSubExperimentName = snapshot.currentExecutedSubExperimentName();
        if (knownStatus.currentExecutedSubExperimentName() == null || (currentExecutedSubExperimentName != null && !currentExecutedSubExperimentName.equals(knownStatus.currentExecutedSubExperimentName()))) {
            knownStatus.currentExecutedSubExperimentName = currentExecutedSubExperimentName;
            knownStatus.currentExecutedSubExperimentStatus = snapshot.currentExecutedSubExperimentStatus();
            listeners.fire().onSubExperimentStarted(currentExecutedSubExperimentName);
        }
    }

    public EventListeners<ExperimentStatusEventListener> listeners() {
        return listeners;
    }

    public ExperimentStatusSnapshot getCurrentlyKnownStatus() {
        return knownStatus;
    }

}
