package bgu.dcr.az.cpu.shared;


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
	
	
	@Override
	public String toString() {
		return "AlgorithmData [name=" + name + ", path=" + path + "]";
	}
}
