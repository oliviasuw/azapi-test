package bgu.dcr.az.metagen.model;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import bgu.dcr.az.metagen.proc.Processor;
import bgu.dcr.az.metagen.util.JavaDocInfo;
import bgu.dcr.az.metagen.util.JavaDocParser;

public class Metadata<T extends Element> {

    private T element;
    private List<AnnotationMetadata> annotations;
    private JavaDocInfo javadoc;
    private ModifiersMetadata modifiers = null;

    public Metadata(T element) {
        this.element = element;
    }

    public T getElement() {
        return element;
    }

    public JavaDocInfo getJavadoc() {
        if (javadoc == null) {
            String docComment = Processor.ELEMENT_UTILS.getDocComment(element);
            javadoc = JavaDocParser.parse(docComment);
        }

        return javadoc;
    }

    public ModifiersMetadata getModifiers() {
        if (modifiers == null) {
            modifiers = new ModifiersMetadata(getElement());
        }

        return modifiers;
    }

    public String getName() {
        if (element instanceof ExecutableElement) {
            return ((ExecutableElement) element).getSimpleName().toString();
        } else {
            return element.toString();
        }
    }

    public boolean hasAnnotation(String annotationFQN) {
        boolean wild = annotationFQN.startsWith("*");
        if (wild) {
            annotationFQN = "." + annotationFQN.substring(1);
        }

        for (AnnotationMetadata a : getAnnotations()) {
            if (wild) {
                if (a.getName().endsWith(annotationFQN)) {
                    return true;
                }
            } else {
                if (a.getName().equals(annotationFQN)) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<AnnotationMetadata> getAnnotations() {
        if (annotations == null) {
            annotations = new LinkedList<>();

            for (AnnotationMirror a : element.getAnnotationMirrors()) {
                annotations.add(new AnnotationMetadata(a));
            }
        }

        return annotations;
    }

    /**
     * @return the type without generic information
     */
    public String getErasureType() {
        return Processor.TYPE_UTILS.erasure(getTypeMirror()).toString();
    }

    public String getType() {
        return getTypeMirror().toString();
    }

    protected TypeMirror getTypeMirror() {
        if (getElement() instanceof ExecutableElement) {
            return ((ExecutableElement) getElement()).getReturnType();
        }
        return getElement().asType();
    }

    public boolean isInstanceOf(String fqn) {
        List<TypeMirror> q = new LinkedList<>();
        q.add(getTypeMirror());

        while (!q.isEmpty()) {
            TypeMirror t = Processor.TYPE_UTILS.erasure(q.remove(0));
            if (t.toString().equals(fqn)) {
                return true;
            }
            q.addAll(Processor.TYPE_UTILS.directSupertypes(t));
        }

        return false;
    }

    public AnnotationMetadata getAnnotation(String annotationFQN) {
        boolean wild = annotationFQN.startsWith("*");
        if (wild) {
            annotationFQN = "." + annotationFQN.substring(1);
        }

        for (AnnotationMetadata a : getAnnotations()) {
            if (wild) {
                if (a.getName().endsWith(annotationFQN)) {
                    return a;
                }
            } else {
                if (a.getName().equals(annotationFQN)) {
                    return a;
                }
            }
        }

        return null;
    }
}
