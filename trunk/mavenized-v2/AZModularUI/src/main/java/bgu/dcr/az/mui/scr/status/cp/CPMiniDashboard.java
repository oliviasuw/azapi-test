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
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

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

    private Map<String, PieChart.Data> currentAlgorithmDataLookup = new HashMap<>();
    Test selectedTest = null;

    @Override
    protected void onLoadView() {
        progress = require(ModularExperiment.class).require(CPProgress.class);
        infoStream().listen(SyncPulse.Sync.class, sync -> updateCPUTimeChart());
    }

    public static boolean accept(BaseController c) {
        ModularExperiment me = c.get(ModularExperiment.class);
        if (me == null) {
            return false;
        }

        return me.isInstalled(CPProgress.class);
    }

    private void updateCPUTimeChart() {
        Test newlySelectedTest = get(Test.class);

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
        currentAlgorithmDataLookup.clear();
        cputimeChart.getData().clear();

        for (String a : progress.algorithmsIn(selectedTest.getName())) {
            currentAlgorithmDataLookup.put(a, new PieChart.Data(a, progress.stat(selectedTest.getName(), a).timeSpent()));
        }
        cputimeChart.getData().addAll(currentAlgorithmDataLookup.values());
    }

    private void update() {
        for (String a : progress.algorithmsIn(selectedTest.getName())) {
            currentAlgorithmDataLookup.get(a).setPieValue(progress.stat(selectedTest.getName(), a).timeSpent());
        }
    }

    private void clear() {
        currentAlgorithmDataLookup.clear();
        cputimeChart.getData().clear();
    }

}
