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

    void onExperimentStarted();

    void onSubExperimentStarted(String name);

    void onSubExperimentEnded(String name);

    void onExperimentEnded();

    void onNumberOfFinishedExecutionsChanged(int numberOfFinishedExecutions);
}
