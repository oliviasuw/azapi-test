/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mui.scr.status.cp;

import bgu.dcr.az.dcr.modules.progress.CPProgress;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.exe.Test;
import bgu.dcr.az.mui.BaseController;
import bgu.dcr.az.mui.RegisterController;
import bgu.dcr.az.mui.jfx.FXMLController;
import bgu.dcr.az.mui.modules.SyncPulse;
import bgu.dcr.az.mui.scr.status.StatusPage;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author bennyl
 */
@RegisterController("status.minidash")
public class CPMiniDashboard extends FXMLController {

    CPProgress progress;

    @FXML
    PieChart cputimeChart;

    @FXML
    BarChart coresNumChart;

    private Map<String, PieChart.Data> currentAlgorithmRTDataLookup = new HashMap<>();
    private Map<String, BarChart.Data> currentAlgorithmCoresDataLookup = new HashMap<>();
    Test selectedTest = null;
    Test newlySelectedTest = null;

    @Override
    protected void onLoadView() {
        final NumberAxis xAxis = (NumberAxis) coresNumChart.getXAxis();

        xAxis.setAutoRanging(false);
        xAxis.setUpperBound(Runtime.getRuntime().availableProcessors());
        xAxis.setForceZeroInRange(true);

        progress = require(ModularExperiment.class).require(CPProgress.class);
        infoStream().listen(SyncPulse.Sync.class, sync -> updateCPUTimeChart());
        infoStream().listen(StatusPage.SelectionChangedInfo.class, s -> newlySelectedTest = s.getSelection());
    }

    public static boolean accept(BaseController c) {
        ModularExperiment me = c.get(ModularExperiment.class);
        if (me == null) {
            return false;
        }

        return me.isInstalled(CPProgress.class);
    }

    private void updateCPUTimeChart() {
        if (newlySelectedTest == null) {
            clear();
            return;
        }

        if (selectedTest == newlySelectedTest) {
            update();
        } else {
            selectedTest = newlySelectedTest;
            reload();
        }
    }

    private void reload() {
        clear();

        final String testName = selectedTest.getName();
        for (String a : progress.algorithmsIn(testName)) {
            final CPProgress.RTStat stat = progress.stat(testName, a);
            currentAlgorithmRTDataLookup.put(a, new PieChart.Data(a, stat.timeSpent()));
            currentAlgorithmCoresDataLookup.put(a, new BarChart.Data(stat.avgCoreUsage(), a));
        }

        cputimeChart.getData().addAll(currentAlgorithmRTDataLookup.values());
        ((XYChart.Series) coresNumChart.getData().get(0)).getData().addAll(currentAlgorithmCoresDataLookup.values());
    }

    private void update() {
        for (String a : progress.algorithmsIn(selectedTest.getName())) {
            final CPProgress.RTStat stat = progress.stat(selectedTest.getName(), a);
            currentAlgorithmRTDataLookup.get(a).setPieValue(stat.timeSpent());
            currentAlgorithmCoresDataLookup.get(a).setXValue(stat.avgCoreUsage());
        }
    }

    private void clear() {
        currentAlgorithmRTDataLookup.clear();
        cputimeChart.getData().clear();

        currentAlgorithmCoresDataLookup.clear();
        coresNumChart.getData().clear();
        coresNumChart.getData().add(new XYChart.Series<>());
    }

}
