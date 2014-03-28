package bgu.dcr.az.execs.api.experiments;

import bgu.dcr.az.execs.api.statistics.StatisticCollector;
import java.util.Collection;

/**
 * describe common MAS experiment
 *
 * @author Benny Lutati
 */
public interface Experiment extends Iterable<Experiment> {

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
     * @return the last execution result
     */
    public ExecutionResult lastResult();

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

    /**
     * supply the given execution service to all executions that are created by
     * this experiment or sub experiments
     *
     * @param serviceType
     * @param service
     */
    public void supply(Class<? extends ExecutionService> serviceType, ExecutionService service);

    /**
     * @return mutable list of statistics stored in this execution - modifying
     * the list while the experiment is running may or may not affect the rest
     * of the execution
     */
    Collection<StatisticCollector> getStatistics();
    
}
