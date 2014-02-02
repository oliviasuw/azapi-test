package bgu.dcr.az.anop.reg;

import bgu.dcr.az.anop.alg.Algorithm;
import bgu.dcr.az.anop.reg.impl.Registration;
import bgu.dcr.az.anop.conf.JavaDocInfo;
import bgu.dcr.az.anop.conf.Variable;
import bgu.dcr.az.anop.utils.CodeUtils;
import bgu.dcr.az.anop.utils.JavaDocParser;
import bgu.dcr.az.anop.utils.ProcessorUtils;
import bgu.dcr.az.anop.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import resources.templates.ResourcesTemplatesAncor;

/**
 *
 * @author User
 */
@SupportedAnnotationTypes({"bgu.dcr.az.anop.reg.Register", "bgu.dcr.az.anop.alg.Algorithm"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RegisteryAnnotationProcessor extends AbstractProcessor {

    private static boolean run = false;

    public static final String AUTOGEN_PACKAGE = "bgu.dcr.autogen";
    public static final String REGISTRATION_AUTOGEN_PACKAGE = "bgu.dcr.autogen.registry";

    private Messager msg;

    private static CompiledTemplate registrationTemplate;
    private static CompiledTemplate configurationTemplate;
    private final static Map<String, TypeElement> configurableClasses = new HashMap<>();

    public RegisteryAnnotationProcessor() {
        ParserContext ctx = ParserContext.create();
        ctx.addImport(CodeUtils.class);
        registrationTemplate = TemplateCompiler.compileTemplate(ResourcesTemplatesAncor.class.getResourceAsStream("RegistrationTemplate.javat"));
        configurationTemplate = TemplateCompiler.compileTemplate(ResourcesTemplatesAncor.class.getResourceAsStream("AbstractConfigurationTemplete.javat"), ctx);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (run) {
            return false;
        }

        ProcessorUtils.setup(processingEnv);

        run = true;
        msg = processingEnv.getMessager();

        findConfigurableClasses(roundEnv);

        for (TypeElement te : configurableClasses.values()) {
            createConfiguration(te);

            if (te.getAnnotation(Register.class) != null) {
                registerClass(te.getQualifiedName().toString(), te.getAnnotation(Register.class).value(), te);
            } else {
                registerClass(te.getQualifiedName().toString(), "ALGORITHM." + te.getAnnotation(Algorithm.class).name(), te);
            }
        }

        ProcessorUtils.tearDown();

        return false;
    }

    private void findConfigurableClasses(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Register.class)) {
            if (element instanceof TypeElement) {
                configurableClasses.put("" + element, (TypeElement) element);
            }
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(Algorithm.class)) {
            if (element instanceof TypeElement) {
                configurableClasses.put("" + element, (TypeElement) element);
            }
        }
    }

    public void createConfiguration(TypeElement te) {

        final Map ctx = new HashMap();
        final List<PropertyInfo> properties = new LinkedList<>();

        ctx.put("typeInfo", ProcessorUtils.extractClassTypeName(te, true));
        ctx.put("className", fqnToConfigurationClassName(te.getQualifiedName().toString()));
        ctx.put("properties", properties);
        ctx.put("configuredClassName", te.getQualifiedName().toString());
        ctx.put("escapedJavadoc", StringUtils.escapedString(ProcessorUtils.extractJavadoc(te)));
        ctx.put("extension", null);
        ctx.put("extensionConfiurationClass", null);
        ctx.put("haveVariables", haveVariables(te));

        TypeMirror parent = te.getSuperclass();
        final Element parentTypeElement = ProcessorUtils.toDeclaredType(parent).asElement();

//        if (configurableClasses.containsKey(parentFQN)) {
        //very bad when more "configurable types" - need to fix it when i have the time
        if (parentTypeElement.getAnnotation(Register.class) != null || parentTypeElement.getAnnotation(Algorithm.class) != null) {
            String parentFQN = "" + parent;
            ctx.put("extension", parentFQN);
            ctx.put("extensionConfiurationClass", AUTOGEN_PACKAGE + "." + fqnToConfigurationClassName(parentFQN));
        }

        Map<String, ExecutableElement> methods = ProcessorUtils.extractMethods(te);
        for (Map.Entry<String, ExecutableElement> p : methods.entrySet()) {
            if (p.getKey().startsWith("get") && p.getValue().getParameters().isEmpty()) {
                PropertyInfo info = new PropertyInfo();
                info.javadoc = StringUtils.escapedString(ProcessorUtils.extractJavadoc(p.getValue()));
                info.declaredName = p.getValue().getSimpleName().toString().substring("get".length());
                info.name = info.declaredName;
                info.getter = p.getKey();
                info.setter = "";
                info.type = ProcessorUtils.extractTypeUnparametrizedFQN(p.getValue().getReturnType());
                info.typeFQN = ProcessorUtils.extractClassTypeName(p.getValue().getReturnType(), true);

                JavaDocInfo jd = JavaDocParser.parse(ProcessorUtils.extractJavadoc(p.getValue()));
                if (jd.first("propertyName") != null) {
                    info.name = jd.first("propertyName");
                }

                final String setterName = "set" + p.getKey().substring("get".length());
                if (methods.containsKey(setterName)) {
                    info.setter = setterName;
                }

                properties.add(info);
            }
        }

        ProcessorUtils.writeClass(AUTOGEN_PACKAGE + "." + ctx.get("className"), configurationTemplate, ctx, te);

    }

    public static String fqnToConfigurationClassName(String fqn) {
        return fqn.replaceAll("\\.", "_");
    }

    public void note(String note) {
        msg.printMessage(Diagnostic.Kind.NOTE, note);
    }

    private static boolean haveVariables(TypeElement te) {
        for (Element e : te.getEnclosedElements()) {
            if (e.getKind() == ElementKind.FIELD) {
                VariableElement ve = (VariableElement) e;
                if (ve.getAnnotation(Variable.class) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void registerClass(String classFQN, String registration, Element... originatingElements) {
        Map registrationData = new HashMap();
        registrationData.put("registrationPackage", REGISTRATION_AUTOGEN_PACKAGE);
        registrationData.put("registrationClass", fqnToConfigurationClassName(classFQN));
        registrationData.put("classFQN", classFQN);
        registrationData.put("registration", registration);

        final String registrationFQN = registrationData.get("registrationPackage") + "." + registrationData.get("registrationClass");
        ProcessorUtils.writeClass(registrationFQN, registrationTemplate, registrationData, originatingElements);
        ProcessorUtils.appendServiceDefinition(Registration.class.getCanonicalName(), registrationFQN);
    }

    public static class PropertyInfo {

        public String name, declaredName;
        public String type;
        public String typeFQN;
        public String setter;
        public String getter;
        public String javadoc;

    }

}
