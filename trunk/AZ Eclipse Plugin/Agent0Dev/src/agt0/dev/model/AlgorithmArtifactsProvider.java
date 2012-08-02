package agt0.dev.model;

import java.util.Arrays;
import java.util.List;

public enum AlgorithmArtifactsProvider {
	INSTANCE;
	
	public List<String> getArtifacts(){
		return Arrays.asList("TimeStamp" , "PseudoTree");
	}
	
	public String[] getArtifactsArray(){
		return getArtifacts().toArray(new String[0]);
	}
	
	
}
