package bgu.dcr.az.mas.exp;

import bgu.sonar.util.collections.CategoryMap;
import java.util.Collection;

/**
 * this object represents a query result for the experiment status
 *
 * @author Benny Lutati
 */
public interface ExperimentStatusSnapshot {

    /**
     * @return the amount of finished executions
     */
    int finishedExecutions();

    /**
     * @return true if the experiment already started
     */
    boolean isStarted();

    /**
     * @return true if the experiment already ended
     */
    boolean isEnded();

    /**
     * @return a collection of all the finished sub experiment names;
     */
    Collection<String> finishedSubExperimentNames();

    /**
     * @return the currently running sub experiment status, may return null in
     * the case where the execution is not started, not ended or has no sub
     * experiments
     */
    ExperimentStatusSnapshot currentExecutedSubExperimentStatus();

    /**
     * @return the name of the currently sub executed status
     */
    String currentExecutedSubExperimentName();
    
}
