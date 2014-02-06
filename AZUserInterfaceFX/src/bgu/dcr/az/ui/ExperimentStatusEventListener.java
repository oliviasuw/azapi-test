/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui;

/**
 *
 * @author User
 */
public interface ExperimentStatusEventListener {

    default void onExperimentStarted() {
    }

    default void onSubExperimentStarted(String name) {
    }

    default void onSubExperimentEnded(String name) {
    }

    default void onExperimentEnded() {
    }

    default void onNumberOfFinishedExecutionsChanged(int numberOfFinishedExecutions) {
    }
}
