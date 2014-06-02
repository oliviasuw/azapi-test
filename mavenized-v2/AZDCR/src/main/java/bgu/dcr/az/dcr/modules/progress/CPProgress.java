/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.progress;

import bgu.dcr.az.dcr.api.experiment.AlgorithmDef;
import bgu.dcr.az.dcr.api.experiment.CPData;
import bgu.dcr.az.dcr.api.experiment.CPTest;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.execs.exps.ExecutionTree;
import bgu.dcr.az.execs.exps.ExperimentProgressInspector;
import bgu.dcr.az.execs.exps.ModularExperiment;
import bgu.dcr.az.execs.exps.exe.AdaptiveScheduler;
import bgu.dcr.az.execs.exps.exe.Simulation;
import bgu.dcr.az.execs.statistics.info.SimulationTerminationInfo;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class CPProgress extends ExperimentProgressInspector {

    private Map<String, Map<String, RTStat>> statistics = new HashMap<>();

    @Override
    public void initialize(ModularExperiment experiment) {
        for (ExecutionTree test : experiment.execution()) {
            final HashMap<String, RTStat> perAlgorithmStat = new HashMap<>();
            statistics.put(test.getName(), perAlgorithmStat);

            for (AlgorithmDef a : ((CPTest) test).getAlgorithms()) {
                perAlgorithmStat.put(a.getInstanceName(), new RTStat());
            }
        }

        InfoStream infos = experiment.getInfoStream();

        long[] time = {0L};
        RTStat[] currentStat = {null};
        infos.listen(Simulation.class, sim -> {
            time[0] = System.currentTimeMillis();
            currentStat[0] = statistics.get(sim.parent().getName()).get(((CPData) sim.data()).getAlgorithm().getInstanceName());
        });

        infos.listen(SimulationTerminationInfo.class, sti -> {
            currentStat[0].timeSpent += (System.currentTimeMillis() - time[0]);
        });

        infos.listen(AdaptiveScheduler.CoreUsageInfo.class, cui -> {
            currentStat[0].numCoreUsageSamples++;
            currentStat[0].sumCoreUsageSamples += cui.getCoreUsage();
        });
    }

    public RTStat getRuntimeStatistic(String test, String algorithmInstance) {
        return statistics.get(test).get(algorithmInstance);
    }

    public static class RTStat {

        private double sumCoreUsageSamples = 0;
        private int numCoreUsageSamples = 0;
        private long timeSpent = 0;

        public long timeSpent() {
            return timeSpent;
        }

        public double avgCoreUsage() {
            return sumCoreUsageSamples / numCoreUsageSamples;
        }
    }

}
