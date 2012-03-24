package bgu.dcr.az.cpu.server.api;

public interface CPU {
	/**
	 * @return the current user state
	 */
	UserState getUserState();
	
	/**
	 * @return the experiment manager
	 */
	ExperimentManager getExperimentManager();

}
