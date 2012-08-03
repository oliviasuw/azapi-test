/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ecoa;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exen.mdef.CorrectnessTester;
import bgu.dcr.az.api.exen.mdef.Limiter;
import bgu.dcr.az.api.exen.mdef.MessageDelayer;
import bgu.dcr.az.api.exen.mdef.ProblemGenerator;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import bgu.dcr.az.ecoa.rmodel.ScannedCodeUnit;
import bgu.dcr.az.ecoa.rmodel.ScannedVariable;
import com.thoughtworks.qdox.JavaDocBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author Administrator
 */
public class AZExtCodeAnalyzer {

    public static final String AGENT_TYPE = "AGENT";
    public static final String CORRECTNESS_TESTER_TYPE = "CORRECTNESS_TESTER";
    public static final String ERROR_TYPE = "ERROR";
    public static final String LIMITER_TYPE = "LIMITER";
    public static final String MESSAGE_DELAYER_TYPE = "MESSAGE_DELAYER";
    public static final String PROBLEM_GENERATOR_TYPE = "PROBLEM_GENERATOR";
    public static final String STATISTIC_COLLECTOR_TYPE = "STATISTIC_COLLECTOR";
    public static final String UNKNOWN_TYPE = "UNKNOWN";

    //the first argument is the basedir. 
    //it should include the src and the lib folders.
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String baseFolder = (args.length > 0? args[0]: ".");
        File out = new File(baseFolder + "/out.txt");
        System.out.println("writing results to: " + out.getAbsolutePath());

        //extract libreries.
        LinkedList<File> libs = new LinkedList<>();
        final File lib = new File(baseFolder + "/lib");
        if (lib.exists()) {
            libs.addAll(Arrays.asList(lib.listFiles()));
        }

        //scan binary
        final ExternalClassesScanner scn = new ExternalClassesScanner();
        Reflections ref = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage("ext.sim")).setScanners(scn));
        Map<String, Set<String>> deps = DependencyEmitter.findDependencies(new HashSet<>(scn.getClassNames()));

        //initialize results
        LinkedList<ScannedCodeUnit> results = new LinkedList<>();

        //start scanning for type and dependencies

        for (String sc : scn.getClassNames()) {
            try {
                Class c = Class.forName(sc);
                if (c == null) {
                    System.out.println("problem reading class " + sc);
                } else {
                    ScannedCodeUnit code = new ScannedCodeUnit();

                    //set code type
                    if (Agent.class.isAssignableFrom(c)) {
                        code.type = "AGENT";
                    } else if (ProblemGenerator.class.isAssignableFrom(c)) {
                        code.type = "PROBLEM_GENERATOR";
                    } else if (CorrectnessTester.class.isAssignableFrom(c)) {
                        code.type = "CORRECTNESS_TESTER";
                    } else if (MessageDelayer.class.isAssignableFrom(c)) {
                        code.type = "MESSAGE_DELAYER";
                    } else if (StatisticCollector.class.isAssignableFrom(c)) {
                        code.type = "STATISTIC_COLLECTOR";
                    } else if (Limiter.class.isAssignableFrom(c)) {
                        code.type = "LIMITER";
                    } else {
                        continue;
                    }

                    //fill code dependencies
                    List<File> dependencies = new LinkedList<>(libs);
                    for (String dep : deps.get(sc)) {
                        dependencies.add(new File(baseFolder + "/src/" + dep.replaceAll("\\.", "/") + ".java"));
                    }
                    code.dependencies = dependencies;

                    code.locationOnDisk = new File(baseFolder + "/src/" + sc.replaceAll("\\.", "/") + ".java");

                    //fill variables
                    List<ScannedVariable> vars = new LinkedList<>();
                    for (Field f : c.getFields()) {
                        if (f.isAnnotationPresent(Variable.class)) {
                            Variable v = f.getAnnotation(Variable.class);
                            ScannedVariable vdec = new ScannedVariable(v.name(), f.getType().getSimpleName(), v.defaultValue(), v.description());
                            vars.add(vdec);
                        }
                    }
                    code.variables = vars;

                    //fill registered name
                    if (c.isAnnotationPresent(Algorithm.class)) {
                        Algorithm a = (Algorithm) c.getAnnotation(Algorithm.class);
                        code.registeredName = a.name();
                    } else if (c.isAnnotationPresent(Register.class)) {
                        Register r = (Register) c.getAnnotation(Register.class);
                        code.registeredName = r.name();
                    } else {
                        code.registeredName = c.getSimpleName();
                    }
                    
                    //scan description
                    JavaDocBuilder jdocb = new JavaDocBuilder();
                    jdocb.addSource(code.locationOnDisk);
                    code.description = jdocb.getSources()[0].getClasses()[0].getComment();
                    
                    results.add(code);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AZExtCodeAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        FileOutputStream outs = new FileOutputStream(out);
        try (ObjectOutputStream outos = new ObjectOutputStream(outs)) {
            outos.writeObject(results);
        }
    }
}
