package bgu.dcr.az.metagen.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.sun.mirror.type.ClassType;

import bgu.dcr.az.metagen.proc.Processor;

public class AnnotationMetadata extends Metadata<Element>{

	private AnnotationMirror a;
	private List<VariableMetadata> values;
	
	public AnnotationMetadata(AnnotationMirror a) {
		super(a.getAnnotationType().asElement());
		this.a = a;
	}

	private AnnotationMetadata(TypeElement a, List<VariableMetadata> values) {
		super(a);
		this.values = values;
		this.a = null;
	}

	
	public List<VariableMetadata> getValues() {
		if (values == null) {
			values = new LinkedList<>();

			for (Entry<? extends ExecutableElement, ? extends AnnotationValue> v : a.getElementValues().entrySet()) {
				values.add(new VariableMetadata(v.getKey(), v.getValue().getValue().toString()));
			}
		}

		return values;
	}
	
	public String getValue(){
		for (VariableMetadata v : getValues()){
			if (v.getName().equals("value")){
				return v.getValue();
			}
		}
		
		return "null";
	}
	
	public static AnnotationMetadata createPseudoAnnotation(String annotationFQN, String value){
		TypeElement am = Processor.ELEMENT_UTILS.getTypeElement(annotationFQN);
		List<VariableMetadata> vars = new LinkedList<>();
		for ( Element m : am.getEnclosedElements()){
			if (m.getSimpleName().toString().equals("value")){
				vars.add(new VariableMetadata((ExecutableElement) m, value));		
			}
		}
		
		return new AnnotationMetadata(am, vars);
	}
	
}
