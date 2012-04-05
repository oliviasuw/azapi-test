package bgu.dcr.az.cpu.shared;

import java.io.Serializable;


public class AlgorithmData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 841843692299679653L;
	private String name;
	private String path;
	
	public AlgorithmData() {
		// FOR SERIALIZATION
	}
	
	public AlgorithmData(String name, String path) {
		this.name = name;
		this.path = path;
	}

	/**
	 * @return the name that this algorithm is registered with
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * @return the path of the class where this algorithm is written
	 */
	public String getPath(){
		return this.path;
	}
	
	
	@Override
	public String toString() {
		return "AlgorithmData [name=" + name + ", path=" + path + "]";
	}
}
