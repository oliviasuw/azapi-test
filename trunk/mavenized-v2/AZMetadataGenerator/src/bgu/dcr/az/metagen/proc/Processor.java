package bgu.dcr.az.metagen.proc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import bgu.dcr.az.metagen.model.AnnotationMetadata;
import bgu.dcr.az.metagen.model.ClassMetadata;
import bgu.dcr.az.metagen.model.Metadata;

@SupportedAnnotationTypes({"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Processor extends AbstractProcessor {

    public static Elements ELEMENT_UTILS;
    public static Types TYPE_UTILS;
    public static Messager MESSAGER;
    public static Filer FILER;

    private static CompiledTemplate NONE = new CompiledTemplate(new char[0], null);
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("(public|private)?\\s*(static)?\\s*class\\s*(?<ClassName>[\\w]*)");
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("package\\s*(?<PackageName>[\\w\\.]*)");

    private Map<String, CompiledTemplate> classGenerationTemplates = new HashMap<>();
    private URLClassLoader annotationsLoader;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        OptimizerFactory.setDefaultOptimizer("reflective");
        String annotationsPath = processingEnv.getOptions().get("annotationspath");

        if (annotationsPath != null) {
            try {
                System.out.println("apath: " + new File(annotationsPath).toURI().toURL().toString());
                annotationsLoader = new URLClassLoader(new URL[]{new File(annotationsPath).toURI().toURL()});
            } catch (MalformedURLException e) {
                warn("cannot load annotation lib from given path: " + e.getMessage());
                e.printStackTrace();
            }
        }

        ELEMENT_UTILS = processingEnv.getElementUtils();
        TYPE_UTILS = processingEnv.getTypeUtils();
        MESSAGER = processingEnv.getMessager();
        FILER = processingEnv.getFiler();

        for (Element element : roundEnv.getRootElements()) {
            if (element.getKind() == ElementKind.CLASS) {
                LinkedList<TypeElement> list = new LinkedList<>();
                list.add((TypeElement) element);
                while (!list.isEmpty()) {
                    TypeElement type = list.removeFirst();
                    list.addAll(ElementFilter.typesIn(type.getEnclosedElements()));

                    for (AnnotationMirror a : type.getAnnotationMirrors()) {
                        generateClass(createTemplateContext(type), new AnnotationMetadata(a));
                    }
                }
            }
        }

        return false;
    }

    private void generateClass(ClassGenContext type, AnnotationMetadata a) {
        CompiledTemplate template = lookupTemplate(a.getName());
        if (template != null) { // template found lets create
            // it!
            try {
                // creating the context - this is the class metadata with some
                // additional methods
                String code = (String) TemplateRuntime.execute(template, type, new HashMap());

                String packageName = "";
                Matcher m = PACKAGE_NAME_PATTERN.matcher(code);
                if (m.find()) {
                    packageName = m.group("PackageName") + ".";
                }

                m = CLASS_NAME_PATTERN.matcher(code);
                if (m.find()) {
                    String fqn = packageName + m.group("ClassName");
                    createClass(type.getElement(), fqn, code);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                warn("processing class " + type + " failed, " + ex.getMessage());
            }
        }
    }

    private ClassGenContext createTemplateContext(final TypeElement type) {
        return new ClassGenContext(type);
    }

    private void warn(String string) {
        MESSAGER.printMessage(Kind.WARNING, string);
    }

    private void warn(String string, Element blame) {
        MESSAGER.printMessage(Kind.WARNING, string, blame);
    }

    private void note(String string) {
        MESSAGER.printMessage(Kind.NOTE, string);
    }

    private void error(String string) {
        MESSAGER.printMessage(Kind.ERROR, string);
    }

    private void error(String string, Element e) {
        MESSAGER.printMessage(Kind.ERROR, string, e);
    }

    private CompiledTemplate lookupTemplate(String typeFQN) {
        CompiledTemplate cached = classGenerationTemplates.get(typeFQN);
        if (cached == null) {
            int lastDot = typeFQN.lastIndexOf('.');
            InputStream templateStream = null;
            String templateFileName = typeFQN.substring(lastDot + 1) + ".javat";
//            System.out.println("searching for: " + templateFileName);

            try {
                templateStream = Class.forName(typeFQN).getResourceAsStream(templateFileName);
            } catch (ClassNotFoundException e) {
//                System.out.println("not in class path");
                try {
                    templateStream = FILER.getResource(StandardLocation.CLASS_OUTPUT, typeFQN.substring(0, lastDot), templateFileName).openInputStream();
                } catch (IOException ex) {
//                    System.out.println("not in source path");
                    if (annotationsLoader != null) {
                        try {
                            templateStream = annotationsLoader.loadClass(typeFQN).getResourceAsStream(templateFileName);
                        } catch (ClassNotFoundException e1) {
                            warn("no template file for annotation of type: " + typeFQN);
                            cached = NONE;
                        }
                    }
                }
            }

            if (templateStream != null) {
                note("template found for " + typeFQN + "!!!");
                cached = TemplateCompiler.compileTemplate(templateStream);
            }

            classGenerationTemplates.put(typeFQN, cached);
        }

        if (cached == NONE) {
            return null;
        }

        return cached;
    }

    public String getTypeFQN(TypeMirror type) {
        return type.toString();
    }

    public void createClass(Element originatingElement, String fqn, String code) throws IOException {
        Filer filler = processingEnv.getFiler();

        try {
            System.out.println("creating source file: " + fqn);

            JavaFileObject source = filler.createSourceFile(fqn, originatingElement);

            try (Writer w = source.openWriter()) {
                w.append(code);
                w.flush();
            }
        } catch (Exception ex) {
            warn(ex.getMessage());
        }
    }

    public final class ClassGenContext extends ClassMetadata {

        private boolean error = false;

        public ClassGenContext(Element classElement) {
            super(classElement);
        }

        public void generateClass(String annotationFQN, String value) {
            AnnotationMetadata pseudoAnnotation = AnnotationMetadata.createPseudoAnnotation(annotationFQN, value);
            getAnnotations().add(pseudoAnnotation);
            Processor.this.generateClass(this, pseudoAnnotation);
        }

        public void warn(String warning) {
            Processor.this.warn(warning);
        }

        public void warn(String warning, Metadata blame) {
            Processor.this.warn(warning, blame.getElement());
        }

        public void note(String note) {
            Processor.this.note(note);
        }

        public void error(String error) {
            this.error = true;
            Processor.this.error(error);
        }

        public void error(String error, Metadata m) {
            this.error = true;
            Processor.this.error(error, m.getElement());
        }

        public boolean hasErrors() {
            return error;
        }
    }
}
