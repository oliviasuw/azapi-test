/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.statistics;

import bgu.dcr.az.common.ui.FXUtils;
import bgu.dcr.az.ui.AppController;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author bennyl
 */
public class MainStatisticScreen extends BorderPane {

    public MainStatisticScreen() {
        toSimpleMode();
    }

    public void toSimpleMode() {
        FXUtils.PaneWithCTL<BasicStatisticsScreenCtl> pane = FXUtils.loadFXML(BasicStatisticsScreenCtl.class);
        pane.getController().setStatisticScreen(this);
        pane.getController().setModel(AppController.getRunningExperiment());
        setCenter(pane.getPane());
    }

    public void toAdvancedMode() {
        FXUtils.PaneWithCTL<AdvancedStatisticScreenController> pane = FXUtils.loadFXML(AdvancedStatisticScreenController.class);
        pane.getController().setStatisticScreen(this);
        setCenter(pane.getPane());
    }
}
