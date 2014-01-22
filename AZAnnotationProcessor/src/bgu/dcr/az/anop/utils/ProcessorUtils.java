/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import bgu.dcr.az.anop.visitors.QualifiedUnparametrizedNameTypeVisitor;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor7;

/**
 *
 * @author User
 */
public class ProcessorUtils {

    private static ProcessingEnvironment penv = null;
    private static Elements elementUtils;

    public static void initialize(ProcessingEnvironment penv) {
        ProcessorUtils.penv = penv;
        elementUtils = penv.getElementUtils();
    }

    /**
     * extract the methods (ExecutableElements) from the given type element
     * (which represents a class)
     *
     * @param type
     * @return map of method name -> element
     */
    public static Map<String, ExecutableElement> extractMethods(TypeElement type) {
        final Map<String, ExecutableElement> methods = new HashMap<>();
        for (Element e : type.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                SimpleElementVisitor7<Void, Void> visitor = new SimpleElementVisitor7<Void, Void>() {

                    @Override
                    public Void visitExecutable(ExecutableElement e, Void p) {
                        methods.put(e.getSimpleName().toString(), e);
                        return null;
                    }
                };

                e.accept(visitor, null);
            }
        }

        return methods;
    }

    public static String extractTypeUnparametrizedFQN(TypeMirror tm) {
        return tm.accept(new QualifiedUnparametrizedNameTypeVisitor(penv.getMessager()), null);
    }

    public static String extractJavadoc(Element e) {
        final String docComment = elementUtils.getDocComment(e);
        return docComment == null ? "" : docComment;
    }
}
