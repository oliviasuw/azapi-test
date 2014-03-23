/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui;

import bgu.dcr.az.common.events.EventListeners;
import bgu.dcr.az.mas.exp.Experiment;
import bgu.dcr.az.mas.exp.ExperimentStatusSnapshot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author User
 */
public class ExperimentStatusUpdateServer extends Timer implements ActionListener {

    private EventListeners<ExperimentStatusUpdatesListener> listeners = EventListeners.create(ExperimentStatusUpdatesListener.class);
    private Experiment exp;

    public ExperimentStatusUpdateServer(int delay, Experiment exp) {
        super(delay, null);
        this.exp = exp;
        addActionListener(this);

        this.setRepeats(true);
        this.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final ExperimentStatusSnapshot status = exp.status();
        listeners.fire().update(status);

        if (status.isEnded()) {
            stop();
        }
    }

    public EventListeners<ExperimentStatusUpdatesListener> listeners() {
        return listeners;
    }

}
