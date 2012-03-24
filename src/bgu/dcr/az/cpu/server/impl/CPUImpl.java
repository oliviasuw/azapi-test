package bgu.dcr.az.cpu.server.impl;

import bgu.dcr.az.cpu.server.api.CPU;
import bgu.dcr.az.cpu.server.api.ExperimentManager;
import bgu.dcr.az.cpu.server.api.UserState;
import bgu.dcr.az.cpu.server.utils.Files;

public class CPUImpl implements CPU{

	public static String EXECUTION_DATA_PATH = "execution-data";
	public static String EXPERIMENTS_PATH = "experiments";
	
	private static CPU instance = null;
	private UserState currentUserState;
	private ExperimentManager expManager;
	
	public static CPU get(){
		if (instance == null) instance = new CPUImpl();
		return instance;
	}
	
	public CPUImpl() {
		Files.ensureDirectoryExists(EXECUTION_DATA_PATH);
		Files.ensureDirectoryExists(EXPERIMENTS_PATH);
	}
	
	@Override
	public UserState getUserState() {
		return currentUserState;
	}

	@Override
	public ExperimentManager getExperimentManager() {
		return expManager;
	}

}
