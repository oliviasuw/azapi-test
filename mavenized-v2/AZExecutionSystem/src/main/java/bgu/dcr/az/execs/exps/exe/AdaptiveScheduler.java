/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.exps.exe;

import bgu.dcr.az.conf.modules.Module;
import bgu.dcr.az.conf.modules.ModuleContainer;
import bgu.dcr.az.conf.modules.info.InfoStream;
import bgu.dcr.az.execs.lowlevel.MultithreadedScheduler;
import bgu.dcr.az.execs.lowlevel.Scheduler;
import bgu.dcr.az.execs.lowlevel.TerminationReason;
import bgu.dcr.az.execs.lowlevel.ThreadSafeProcTable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * adaptive scheduler that supports adaptation by groups of executions. the
 * scheduler will automatically adapt the number of cores that is most sutiable
 * to the execution of processes of the same group.
 *
 * the idea is to use the adaptive scheduler to execute all experiments that are
 * expected to use similar resources on the same group this way the adaptation
 * will make such executions much faster by reduce lock contention.
 *
 * @author bennyl
 */
public class AdaptiveScheduler implements Module {

    public static final Object GENERAL_GROUP_KEY = new Object();

    private MultithreadedScheduler sched;
    private final int maximumCores = Runtime.getRuntime().availableProcessors();
    private final int minimalSampleSize = 4;
    private final double factor = 0.75;

    private final Map<Object, Adaptation> adaptionMap = new HashMap<>();
    private Adaptation currentAdaption;
    private InfoStream istream;

    public AdaptiveScheduler(ExecutorService execs) {
        sched = new MultithreadedScheduler(execs);
        setGroup(GENERAL_GROUP_KEY);
    }

    @Override
    public void installInto(ModuleContainer mc) {
        istream = mc.require(InfoStream.class);
    }

    public final void setGroup(Object groupKey) {
        currentAdaption = adaptionMap.get(groupKey);
        if (currentAdaption == null) {
            currentAdaption = new Adaptation(groupKey);
            adaptionMap.put(groupKey, currentAdaption);
        }
    }

    public TerminationReason schedule(ThreadSafeProcTable table) throws InterruptedException {
        TerminationReason result = sched.schedule(table, currentAdaption.getAdaptedNumberOfCores());
        currentAdaption.update(sched);
        return result;
    }

    private class Adaptation {

        private int adaptedNumberOfCores;
        private double contentionExpAverage;
        private int executionNumner = 0;
        private final Object groupKey;

        public Adaptation(Object groupKey) {
            adaptedNumberOfCores = maximumCores;
            contentionExpAverage = 0;
            this.groupKey = groupKey;
        }

        public void update(Scheduler scheduler) {
            executionNumner++;
            contentionExpAverage = contentionExpAverage * factor + (1 - factor) * scheduler.getContention();

            if (executionNumner % minimalSampleSize == 0) {
                if (contentionExpAverage > 0.25) {
                    if (adaptedNumberOfCores > 1) {
                        adaptedNumberOfCores--;
                    }
                }

                if (contentionExpAverage < 0.18) {
                    if (adaptedNumberOfCores < maximumCores) {
                        adaptedNumberOfCores++;
                    }
                }

                istream.writeIfListening(() -> new CoreUsageInfo(groupKey, adaptedNumberOfCores), CoreUsageInfo.class);
            }
        }

        public int getAdaptedNumberOfCores() {
            return adaptedNumberOfCores;
        }

    }

    public static class CoreUsageInfo {

        private final Object group;
        private final int coreUsage;

        public CoreUsageInfo(Object group, int coreUsage) {
            this.group = group;
            this.coreUsage = coreUsage;
        }

        public Object getGroup() {
            return group;
        }

        public int getCoreUsage() {
            return coreUsage;
        }

    }

}
