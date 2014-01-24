package bgu.dcr.az.anop.alg;

import bgu.dcr.az.anop.*;
import bgu.dcr.az.anop.utils.CodeUtils;
import bgu.dcr.az.anop.utils.ProcessorUtils;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import resources.templates.ResourcesTemplatesAncor;

/**
 *
 * @author User
 */
@SupportedAnnotationTypes("bgu.dcr.az.anop.alg.Algorithm")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AlgorithmAnnotationProcessor extends AbstractProcessor {

    private static boolean run = false;

    public static final String AUTOGEN_PACKAGE = "bgu.dcr.autogen.agents";

    private final CompiledTemplate agentManipulatorTemplate;

    public AlgorithmAnnotationProcessor() {
        ParserContext ctx = ParserContext.create();
        ctx.addImport(CodeUtils.class);
        agentManipulatorTemplate = TemplateCompiler.compileTemplate(ResourcesTemplatesAncor.class.getResourceAsStream("AgentManipulatorTemplete.javat"));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (run) {
            return false;
        }

        ProcessorUtils.initialize(processingEnv);
        run = true;

        for (Element element : roundEnv.getElementsAnnotatedWith(Algorithm.class)) {
            if (element instanceof TypeElement) {
                RegisteryAnnotationProcessor.createConfiguration((TypeElement) element);
            }
        }

        return true;
    }

}
