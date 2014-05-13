package bgu.dcr.az.metagen.model;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class MethodMetadata extends Metadata<ExecutableElement> {

	List<ArgumentMetadata> args = null;

	public MethodMetadata(ExecutableElement f) {
		super(f);
	}

	public String getSignature() {
		return getElement().toString();
	}

	public List<ArgumentMetadata> getArguments() {
		if (args == null) {
			args = new LinkedList<ArgumentMetadata>();

			for (VariableElement v : getElement().getParameters()) {
				args.add(new ArgumentMetadata((VariableElement) v));
			}
		}

		return args;
	}

	public String getReturnType() {
		return getElement().getReturnType().toString();
	}

	public boolean hasNoReturnValue() {
		return getElement().getReturnType().toString().equals("void"); 
	}

}
