/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.anop.utils;

import bgu.dcr.az.anop.visitors.QualifiedUnparametrizedNameTypeVisitor;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.Filer;
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
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

/**
 *
 * @author User
 */
public class ProcessorUtils {

    private static ProcessingEnvironment penv = null;
    private static Elements elementUtils;
    private static Messager msg;
    private static Types typeUtils;
    private static Map<String, List<String>> registeredServices = new HashMap<String, List<String>>();

    public static void setup(ProcessingEnvironment penv) {
        System.setProperty("mvel2.disable.jit", "true");
        ProcessorUtils.penv = penv;
        elementUtils = penv.getElementUtils();
        msg = penv.getMessager();
        typeUtils = penv.getTypeUtils();
    }

    public static void tearDown() {
        for (Map.Entry<String, List<String>> r : registeredServices.entrySet()) {
            try {
                FileObject file = penv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/" + r.getKey());
                try (PrintWriter out = new PrintWriter(file.openWriter())) {
                    for (String l : r.getValue()) {
                        out.println(l);
                    }
                }
            } catch (IOException ex) {
                error(ex);
            }
        }
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

    public static void error(Throwable error) {
        StringBuilderWriter w = new StringBuilderWriter();
        error.printStackTrace(new PrintWriter(w));
        msg.printMessage(Diagnostic.Kind.ERROR, w.toString());
    }

    public static void note(String note) {
        msg.printMessage(Diagnostic.Kind.NOTE, note);
    }

    /**
     * write a class with a given template and context of template execution
     *
     * @param classFQN
     * @param codeTemplate
     * @param context
     */
    public static void writeClass(String classFQN, CompiledTemplate codeTemplate, Map context) {
        String out = (String) TemplateRuntime.execute(codeTemplate, context);
        writeClass(classFQN, out);
    }

    /**
     * write a class with a given code
     *
     * @param classFQN
     * @param code
     */
    public static void writeClass(String classFQN, String code) {
        Filer filler = penv.getFiler();

        try {
            JavaFileObject source = filler.createSourceFile(classFQN);
            try (Writer w = source.openWriter()) {
                w.append(code);
                w.flush();
            }

        } catch (IOException ex) {
            StringBuilderWriter writer = new StringBuilderWriter();
            ex.printStackTrace(new PrintWriter(writer));
            msg.printMessage(Diagnostic.Kind.ERROR, "cannot generate source file:\n" + writer.toString());
        }
    }

    /**
     * adds a service definition to META-INF/services
     *
     * @param serviceClassFQN
     * @param implementationClassFQN
     */
    public static void appendServiceDefinition(String serviceClassFQN, String implementationClassFQN) {
        List<String> l = registeredServices.get(serviceClassFQN);
        if (l == null) {
            l = new LinkedList<>();
            registeredServices.put(serviceClassFQN, l);
        }

        l.add(implementationClassFQN);
    }
}
