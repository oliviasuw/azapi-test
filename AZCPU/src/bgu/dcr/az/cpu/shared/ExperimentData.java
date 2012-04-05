package bgu.dcr.az.cpu.shared;

import java.io.Serializable;

public class ExperimentData implements Serializable{

	private String name;
	private String content;
	
	/**
	 * @return this experiment name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the xml content of the experiment as string
	 */
	public String getContent() {
		return this.content;
	}

	public ExperimentData(String name, String content) {
		this.name = name;
		this.content = content;
	}

	public ExperimentData() {
		//FOR SERIALIZATION - DO NOT DELETE!
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ExperimentData [name=" + name + ", content=" + content.substring(0,  Math.min(10, content.length())) + "]";
	}
	
}
