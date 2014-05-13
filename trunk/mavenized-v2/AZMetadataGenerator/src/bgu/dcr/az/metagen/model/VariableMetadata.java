package bgu.dcr.az.metagen.model;

import javax.lang.model.element.ExecutableElement;

public class VariableMetadata extends Metadata<ExecutableElement>{

	private String value;

	public VariableMetadata(ExecutableElement e, String value) {
		super(e);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
