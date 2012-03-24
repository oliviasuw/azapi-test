package bgu.dcr.az.cpu.shared;

import java.io.File;
import java.util.List;

public class ExperimentResultData {
	/**
	 * @return true if the experiment crushed
	 */
	public boolean isCrushed(){
		return false;
	}
	
	/**
	 * @return description of the crush reason 
	 */
	public String getCrushReason(){
		return null;
	}
	
	/**
	 * @return true if the user manually canceld the experiment
	 */
	public boolean isCanceled(){
		return false;
	}
	
	/**
	 * @return true if the experiment finished without problems
	 */
	public boolean isSuccess(){
		return false;
	}
	
	/**
	 * @return a list of all the statistic files that were collected during the execution
	 */
	public List<File> listCollectedStatisticFiles(){
		return null;
	}
}


