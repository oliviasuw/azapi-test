package bgu.dcr.az.metagen.model;

import javax.lang.model.element.VariableElement;

public class FieldMetadata extends Metadata<VariableElement>{
	
	public FieldMetadata(VariableElement f) {
		super(f);
	}
	
	@Override
	public String getName(){
		return getElement().getSimpleName().toString();
	}
	
}
