package bgu.dcr.az.anop.alg;

import bgu.dcr.az.anop.reg.RegisteryAnnotationProcessor;
import bgu.dcr.az.anop.utils.CodeUtils;
import bgu.dcr.az.anop.utils.ProcessorUtils;
import bgu.dcr.az.anop.utils.StringUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import resources.templates.ResourcesTemplatesAncor;

/**
 *
 * @author User
 */
@SupportedAnnotationTypes("bgu.dcr.az.anop.alg.Algorithm")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AlgorithmAnnotationProcessor extends AbstractProcessor {

    private static boolean run = false;

    public static final String AUTOGEN_PACKAGE = "bgu.dcr.autogen.agents";

    private static final CompiledTemplate agentManipulatorTemplate;

    static {
        ParserContext ctx = ParserContext.create();
        ctx.addImport(CodeUtils.class);
        agentManipulatorTemplate = TemplateCompiler.compileTemplate(ResourcesTemplatesAncor.class.getResourceAsStream("AgentManipulatorTemplete.javat"), ctx);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (run) {
            return false;
        }

        ProcessorUtils.setup(processingEnv);
        run = true;

        Map<String, TypeElement> configurableClasses = ProcessorUtils.extractClassesAnnotatedWith(roundEnv, Algorithm.class);
        for (TypeElement te : configurableClasses.values()) {
            System.err.println("Processing agent: " + te.getQualifiedName().toString());
            createAlgorithmManipulator(te);
        }

        ProcessorUtils.tearDown();

        return false;
    }

    public static void createAlgorithmManipulator(TypeElement te) {

        final Map ctx = new HashMap();
        final LinkedList<HandlerInfo> handlers = new LinkedList<>();
        final String autogenClassName = RegisteryAnnotationProcessor.fqnToConfigurationClassName(te.getQualifiedName().toString());

        ctx.put("className", autogenClassName);
        ctx.put("package", AUTOGEN_PACKAGE);
        ctx.put("classNameFQN", te.getQualifiedName().toString());
        ctx.put("handlers", handlers);
        ctx.put("algorithmName", te.getAnnotation(Algorithm.class).name());
        ctx.put("configurationDelegateClassName", RegisteryAnnotationProcessor.AUTOGEN_PACKAGE + "." + autogenClassName);

        TypeElement scanned = te;
        while (!scanned.getQualifiedName().toString().equals(Object.class.getCanonicalName())) {
            Map<String, ExecutableElement> methods = ProcessorUtils.extractMethods(scanned);
            for (Map.Entry<String, ExecutableElement> p : methods.entrySet()) {
                final WhenReceived ano = p.getValue().getAnnotation(WhenReceived.class);
                if (ano != null) {
                    HandlerInfo hInfo = new HandlerInfo();
                    hInfo.declaredName = p.getValue().getSimpleName().toString();
                    hInfo.javadoc = StringUtils.escapedString(ProcessorUtils.extractJavadoc(p.getValue()));
                    hInfo.of = ano.value();
                    int i = 0;
                    for (VariableElement param : p.getValue().getParameters()) {
                        ParameterInfo pInfo = new ParameterInfo();
                        pInfo.name = param.getSimpleName().toString();
                        
//                        ProcessorUtils.note("modifier of type of " + param + "( " + param.asType() + ") are: " + ProcessorUtils.toDeclaredType(param.asType()).asElement().getModifiers());
                        if (ProcessorUtils.toDeclaredType(param.asType()).asElement().getModifiers().contains(Modifier.PRIVATE)){
                            ProcessorUtils.error("Exposing private class in public API", param);
                        }
                        pInfo.typeFQN = ProcessorUtils.extractClassTypeName(param.asType(), true).replaceAll("\\$", ".");
                        pInfo.index = "" + i;
                        hInfo.params.addLast(pInfo);
                        i++;
                    }

                    handlers.add(hInfo);
                }
            }
            scanned = (TypeElement) ProcessorUtils.toDeclaredType(scanned.getSuperclass()).asElement();
        }
        ProcessorUtils.writeClass(AUTOGEN_PACKAGE + "." + ctx.get("className"), agentManipulatorTemplate, ctx, te);
    }

    public static class HandlerInfo {

        public String javadoc;
        public String declaredName;
        public String of;
        public LinkedList<ParameterInfo> params = new LinkedList<>();
    }

    public static class ParameterInfo {

        public String name;
        public String typeFQN;
        public String index;
    }
}
