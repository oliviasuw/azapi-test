/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ecoa;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.exen.mdef.CorrectnessTester;
import bgu.dcr.az.api.exen.mdef.Limiter;
import bgu.dcr.az.api.exen.mdef.MessageDelayer;
import bgu.dcr.az.api.exen.mdef.ProblemGenerator;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
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

    //the first argument is the file to which write the output
    //rest of the arguments are the fully qualified class names 
    public static void main(String[] args) throws FileNotFoundException {
        File out = new File(args[0]);
        PrintWriter pw = new PrintWriter(out);
        final ExternalClassesScanner scn = new ExternalClassesScanner();
        Reflections ref = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage("ext.sim")).setScanners(scn));
        Map<String, Set<String>> deps = DependencyEmitter.findDependencies(new HashSet<>(scn.getClassNames()));

        System.out.println("writing results to: " + out.getAbsolutePath());

        for (String sc : scn.getClassNames()) {
            try {
                Class c = Class.forName(sc);
                if (c == null) {
                    pw.println(c.getCanonicalName() + " " + ERROR_TYPE);
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (String d : deps.get(sc)) {
                        sb.append(" ").append(d);
                    }

                    if (Agent.class.isAssignableFrom(c)) {
                        pw.println(c.getCanonicalName() + " " + AGENT_TYPE + sb.toString());
                    } else if (ProblemGenerator.class.isAssignableFrom(c)) {
                        pw.println(c.getCanonicalName() + " " + PROBLEM_GENERATOR_TYPE + sb.toString());
                    } else if (CorrectnessTester.class.isAssignableFrom(c)) {
                        pw.println(c.getCanonicalName() + " " + CORRECTNESS_TESTER_TYPE + sb.toString());
                    } else if (MessageDelayer.class.isAssignableFrom(c)) {
                        pw.println(c.getCanonicalName() + " " + MESSAGE_DELAYER_TYPE + sb.toString());
                    } else if (StatisticCollector.class.isAssignableFrom(c)) {
                        pw.println(c.getCanonicalName() + " " + STATISTIC_COLLECTOR_TYPE + sb.toString());
                    } else if (Limiter.class.isAssignableFrom(c)) {
                        pw.println(c.getCanonicalName() + " " + LIMITER_TYPE + sb.toString());
                    } else {
                        pw.println(c.getCanonicalName() + " " + UNKNOWN_TYPE + sb.toString());
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AZExtCodeAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        pw.close();
    }
}
