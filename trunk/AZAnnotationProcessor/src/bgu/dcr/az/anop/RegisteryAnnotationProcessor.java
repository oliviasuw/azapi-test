package bgu.dcr.az.anop;

import bgu.dcr.az.anop.utils.StringBuilderWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import resources.templets.ResourcesTemplatesAncor;

/**
 *
 * @author User
 */
@SupportedAnnotationTypes("bgu.dcr.az.anop.Register")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RegisteryAnnotationProcessor extends AbstractProcessor {

    private static boolean run = false;

    public static final String AUTOGEN_PACKAGE = "bgu.dcr.autogen";
    private Messager msg;

    private CompiledTemplate registeryTemplate = TemplateCompiler.compileTemplate(ResourcesTemplatesAncor.class.getResourceAsStream("CompiledRegistery.javat"));
    private CompiledTemplate configurationTemplate = TemplateCompiler.compileTemplate(ResourcesTemplatesAncor.class.getResourceAsStream("AbstractConfigurationTemplete.javat"));

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (run) {
            return false;
        }
        run = true;
        msg = processingEnv.getMessager();

        List<RegisteredClass> registeredClasses = new LinkedList<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(Register.class)) {
            note("Scanning: " + element);

            if (element instanceof TypeElement) {
                TypeElement te = (TypeElement) element;

                createConfiguration(te);

                registeredClasses.add(new RegisteredClass(te.getQualifiedName().toString(), te.getAnnotation(Register.class).value()));
            }
        }

        //create context
        Map context = new HashMap();
        context.put("packageName", AUTOGEN_PACKAGE);
        context.put("registrations", registeredClasses);

        writeClass(AUTOGEN_PACKAGE + ".CompiledRegistery", registeryTemplate, context);

        return true;
    }

    private void writeClass(String classFQN, CompiledTemplate codeTemplate, Map context) {
        String out = (String) TemplateRuntime.execute(codeTemplate, context);
        writeClass(classFQN, out);
    }

    private void writeClass(String classFQN, String code) {
        Filer filler = processingEnv.getFiler();
        try {
            JavaFileObject source = filler.createSourceFile(classFQN);
//            msg.printMessage(Diagnostic.Kind.NOTE, "creating "+ source.toUri());
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

    private void createConfiguration(TypeElement te) {

        final Map ctx = new HashMap();
        ctx.put("typeInfo", te.asType().toString());
        ctx.put("className", te.getQualifiedName().toString().replaceAll("\\.", "_"));
        final List<PropertyInfo> properties = new LinkedList<>();
        ctx.put("properties", properties);

//        Map<String, ExecutableElement> 
        for (Element e : te.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {

//                TypeMirror type = e.asType();
                SimpleElementVisitor7<Void, Void> visitor = new SimpleElementVisitor7<Void, Void>() {

                    @Override
                    public Void visitVariable(VariableElement e, Void p) {
                        note("var: " + e);
                        return null;
                    }

                    @Override
                    public Void visitExecutable(ExecutableElement e, Void p) {
                        note("exec: " + e);
                        note("return type: " + e.getReturnType() + ", params: " + e.getParameters() + ", simple name: " + e.getSimpleName());

                        if (e.getSimpleName().toString().startsWith("get")) {//found getter
                            PropertyInfo info = new PropertyInfo(e.getSimpleName().toString().substring("get".length()), e.getReturnType().toString());
                            properties.add(info);
                        }

                        return null;
                    }

                    @Override
                    public Void visitPackage(PackageElement e, Void p) {
                        note("pack: " + e);
                        return null;
                    }

                    @Override
                    public Void visitType(TypeElement e, Void p) {
                        note("type: " + e);
                        return null;
                    }

                    @Override
                    public Void visitTypeParameter(TypeParameterElement e, Void p) {
                        note("typeparam: " + e);
                        return null;
                    }

                    @Override
                    public Void visitUnknown(Element e, Void p) {
                        note("unknown: " + e);
                        return null;
                    }

                };

                e.accept(visitor, null);

            }
        }

        writeClass(AUTOGEN_PACKAGE + "." + ctx.get("className"), configurationTemplate, ctx);

    }

    public void note(String note) {
        msg.printMessage(Diagnostic.Kind.NOTE, note);
    }

    public static class RegisteredClass {

        public String clazz;
        public String regName;

        public RegisteredClass(String clazz, String regName) {
            this.clazz = clazz;
            this.regName = regName;
        }

    }

    public static class PropertyInfo {

        public String name;
        public String typeInfo;

        public PropertyInfo(String name, String typeInfo) {
            this.name = name;
            this.typeInfo = typeInfo;
        }

    }

}
