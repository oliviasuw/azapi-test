package bgu.dcr.az.cpu.shared;

import java.util.List;

import org.reflections.Reflections;

public class AlgorithmData {
	
	private String name;
	private String path;
	
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
	
	/**
	 * @return all the available algorithms in the loaded experiment pack.
	 */
	public static List<AlgorithmData> list(){
		return null;
		//Reflections ref = new Reflections(arg0)
	}
}
