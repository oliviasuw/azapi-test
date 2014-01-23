/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import bgu.dcr.az.anop.visitors.QualifiedUnparametrizedNameTypeVisitor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.lang.model.util.SimpleTypeVisitor7;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 *
 * @author User
 */
public class ProcessorUtils {

    private static ProcessingEnvironment penv = null;
    private static Elements elementUtils;
    private static Messager msg;
    private static Types typeUtils;

    public static void initialize(ProcessingEnvironment penv) {
        ProcessorUtils.penv = penv;
        elementUtils = penv.getElementUtils();
        msg = penv.getMessager();
        typeUtils = penv.getTypeUtils();
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

    /**
     * use this method in order to get the class name for the given type - do
     * not call toString on the type as it is not always accurate.
     *
     * @param te
     * @return
     */
    public static String extractClassTypeName(TypeElement te, boolean parametrized) {
        boolean inner = te.getNestingKind() != NestingKind.TOP_LEVEL;
        String type = te.asType().toString();
        final int lastDot = type.lastIndexOf('.');
        if (inner) {
            type = type.substring(0, lastDot) + '$' + type.substring(lastDot + 1);
        }

        final int parametrizationIndex = type.indexOf("<");
        if (!parametrized && parametrizationIndex >= 0) {
            type = type.substring(0, parametrizationIndex);
        }
        return type;
    }

    /**
     * @param parametrized
     * @see
     * ProcessorUtils#extractClassTypeName(javax.lang.model.element.TypeElement)
     * @param te
     * @return
     */
    public static String extractClassTypeName(TypeMirror te, boolean parametrized) {
        final DeclaredType decte = toDeclaredType(te);
        String result = extractClassTypeName((TypeElement) decte.asElement(), false);
        if (parametrized) {
            final String dectes = decte.toString();
            int paramindex = dectes.indexOf("<");
            if (paramindex >= 0) {
                result = result + dectes.substring(paramindex);
            }
        }

        return result;
    }

    /**
     * extract the javadoc from the given element
     *
     * @param e
     * @return
     */
    public static String extractJavadoc(Element e) {
        final String docComment = elementUtils.getDocComment(e);
        return docComment == null ? "" : docComment;
    }

    /**
     * extract all annotations that are declared over the given type
     *
     * @param tm
     * @return
     */
    public static List<? extends AnnotationMirror> extractAnnotations(TypeMirror tm) {
        DeclaredType dt = toDeclaredType(tm);
        return dt.asElement().getAnnotationMirrors();
    }

    public static DeclaredType toDeclaredType(TypeMirror mirror) {
        return mirror.accept(new SimpleTypeVisitor7<DeclaredType, Void>() {

            @Override
            public DeclaredType visitDeclared(DeclaredType t, Void p) {
                return t;
            }

            @Override
            public DeclaredType visitPrimitive(PrimitiveType t, Void p) {
                return toDeclaredType(typeUtils.boxedClass(t).asType());
            }

            @Override
            protected DeclaredType defaultAction(TypeMirror e, Void p) {
                throw new UnsupportedOperationException("given type mirror is not a declared type: " + e);
            }

        }, null);
    }

    public static void error(String error) {
        msg.printMessage(Diagnostic.Kind.ERROR, error);
    }

    public static void note(String note) {
        msg.printMessage(Diagnostic.Kind.NOTE, note);
    }
}
