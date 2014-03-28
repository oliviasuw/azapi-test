/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.status;

import bgu.dcr.az.execs.api.experiments.Execution;
import bgu.dcr.az.execs.api.experiments.ExecutionService;
import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.execs.exceptions.InitializationException;
import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class RuntimeStatisticsService implements ExecutionService {

    private static final String NUM_CORES_PREFIX = "NUMC";
    private static final String CPU_TIME_PREFIX = "CPUT";

    private Map<String, StatisticCollector> collectors = new HashMap<>();

    @Override
    public void initialize(Execution ex) throws InitializationException {
        collectors.get(CPU_TIME_PREFIX + ex.getContainingExperiment().getName()).initialize(null, ex, null);
        collectors.get(NUM_CORES_PREFIX + ex.getContainingExperiment().getName()).initialize(null, ex, null);
    }

    @Override
    public void initialize(Experiment ex) {
        for (Experiment t : ex) {
            AlgorithmCPUTimeStatisticCollector c1 = new AlgorithmCPUTimeStatisticCollector();
            NumberOfCoresInUseStatisticCollector c2 = new NumberOfCoresInUseStatisticCollector();

            collectors.put(CPU_TIME_PREFIX + t.getName(), c1);
            collectors.put(NUM_CORES_PREFIX + t.getName(), c2);
        }
    }

    public AlgorithmCPUTimeStatisticCollector getAlgorithmCPUTimeStatistic(String expName) {
        return (AlgorithmCPUTimeStatisticCollector) collectors.get(CPU_TIME_PREFIX + expName);
    }

    public NumberOfCoresInUseStatisticCollector getNumberOfCoresInUseStatistic(String expName) {
        return (NumberOfCoresInUseStatisticCollector) collectors.get(NUM_CORES_PREFIX + expName);
    }

}
