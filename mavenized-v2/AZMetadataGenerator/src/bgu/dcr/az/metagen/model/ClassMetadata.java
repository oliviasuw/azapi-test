package bgu.dcr.az.metagen.model;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import bgu.dcr.az.metagen.proc.Processor;

public class ClassMetadata extends Metadata<TypeElement> {
	private List<FieldMetadata> fields = null;
	private List<MethodMetadata> methods = null;
	private ClassMetadata parent = null;

	public ClassMetadata(Element classElement) {
		super((TypeElement) classElement);
	}
	
	public ClassMetadata getSuper(){
		if (getFQN().equals("java.lang.Object")) return null;
		
		if (parent == null){
			parent = new ClassMetadata(Processor.TYPE_UTILS.asElement(getElement().getSuperclass()));			
		}
		
		return parent;
	}
	
	/**
	 * search for the first super class in the chain to object that has the given annotation
	 * @param annotationFQN
	 * @return
	 */
	public ClassMetadata lookupSuperClassWithAnnotation(String annotationFQN){
		ClassMetadata s = getSuper();
		while (s != null){
			if (s.hasAnnotation(annotationFQN)) return s;
			s = s.getSuper();
		}
		
		return null;
	}

	public List<FieldMetadata> getFields() {
		if (fields == null) {
			fields = new LinkedList<FieldMetadata>();

			for (VariableElement f : ElementFilter.fieldsIn(getElement().getEnclosedElements())) {
				fields.add(new FieldMetadata(f));
			}
		}

		return fields;
	}

	public List<MethodMetadata> getMethods() {
		if (methods == null) {
			methods = new LinkedList<MethodMetadata>();

			for (ExecutableElement f : ElementFilter.methodsIn(getElement().getEnclosedElements())) {
				methods.add(new MethodMetadata(f));
			}
		}

		return methods;
	}
	
	public List<MethodMetadata> lookupMethodWithAnnotation(String annotationFQN){
		LinkedList<MethodMetadata> result = new LinkedList<>();
		for (MethodMetadata m : getMethods()){
			if (m.hasAnnotation(annotationFQN)){
				result.add(m);
			}
		}
		
		return result;
	}
	
	public List<FieldMetadata> lookupFieldsWithAnnotation(String annotationFQN){
		LinkedList<FieldMetadata> result = new LinkedList<>();
		for (FieldMetadata f : getFields()){
			if (f.hasAnnotation(annotationFQN)){
				result.add(f);
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @return list of property names in this class (property = one that has getter..)
	 */
	public List<PropertyMetadata> getProperties() {
		return PropertyMetadata.findProperties(this);
	}
	
	public String getFQN(){
		return getName();
	}
	
	public String getSimpleName() {
		return getElement().getSimpleName().toString();
	}

	/**
	 * @return the fully quilified name of the class but with underscore instead
	 *         of '.' for example a.b.c.Class => a_b_c_Class
	 */
	public String getUnderscoredFQN() {
		return getName().replace('.', '_');
	}

}
