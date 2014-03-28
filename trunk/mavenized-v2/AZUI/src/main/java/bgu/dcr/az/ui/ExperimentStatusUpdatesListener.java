/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.ui;

import bgu.dcr.az.execs.api.experiments.ExperimentStatusSnapshot;

/**
 *
 * @author User
 */
public interface ExperimentStatusUpdatesListener {
    void update(ExperimentStatusSnapshot snapshot);
}
