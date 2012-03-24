package bgu.dcr.az.cpu.shared;

import java.util.List;

public class ExperimentData {
	
	/**
	 * @return this experiment name
	 */
	public String getName(){
		return null;
	}
	
	/**
	 * @return the xml content of the experiment as string
	 */
	public String getContent(){
		return null;
	}
	
	/**
	 * @return true if this experiment was deleted
	 */
	public boolean isDeleted(){
		return false;
	}
	
	/**
	 * delete this experiment from the file system
	 */
	public void delete(){
		
	}
	
	/**
	 * @return the number of executions that this experiment will execute
	 */
	public int getNumberOfExecutions(){
		return -1;
	}
	
	/**
	 * @return all the available experiments
	 */
	public static List<ExperimentData> list(){
		return null;
	}
	
}
