package bgu.dcr.az.mas.exp;

import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.mas.stat.StatisticCollector;
import bgu.dcr.az.mas.stat.StatisticsManager;
import java.util.Collection;

/**
 * describe common MAS experiment
 *
 * @author Benny Lutati
 */
public interface Experiment {

    /**
     * @return the amount of executions that are needed to execute in order to
     * complete this experiment
     */
    public int numberOfExecutions();

    /**
     * execute this experiment
     *
     * @return result of this experiment
     */
    public ExecutionResult execute();

    /**
     * @return collection of sub experiments
     */
    public Collection<? extends Experiment> subExperiments();

    /**
     * @return snapshot of the current status of the experiment execution
     */
    ExperimentStatusSnapshot status();

    /**
     * @return the experiment name
     */
    public String getName();
    
}
