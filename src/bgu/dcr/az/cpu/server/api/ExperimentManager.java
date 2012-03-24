package bgu.dcr.az.cpu.server.api;

import bgu.dcr.az.cpu.server.exp.BadExecutionStateException;
import bgu.dcr.az.cpu.shared.ExperimentData;
import bgu.dcr.az.cpu.shared.ExperimentResultData;

public interface ExperimentManager {
	
	/**
	 * @return true if there is an experiment that currently running
	 */
	boolean isExperimentRunning();
	
	/**
	 * will attempt to execute the given experiment
	 * this method will change the current user state 
	 * @param ex
	 * @throws BadExecutionStateException if an execution is already running 
	 */
	void execute(ExperimentData ex) throws BadExecutionStateException;
	
	/**
	 * will attempt to stop the current execution 
	 * @throws BadExecutionStateException if there is not execution that is currently running
	 */
	void stopExecution() throws BadExecutionStateException;
	
	/**
	 * @return the current executed experiment or null if no such execution is running
	 */
	ExperimentData getCurrentExecutedExperiment();
	
	/**
	 * @return the execution number that the executed experiment is currently running
	 */
	int getCurrentExecutionNumber();
	
	/**
	 * @return the time that the current execution inside the executed experiment take so far
	 */
	int getCurrentExecutionTime();
	
	/**
	 * @return the last experiment execution results
	 * @throws BadExecutionStateException if there is a running execution
	 */
	ExperimentResultData getResult() throws BadExecutionStateException;
}
