/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.execs.experiments;

import bgu.dcr.az.execs.api.experiments.Experiment;
import bgu.dcr.az.conf.api.Configuration;
import bgu.dcr.az.conf.api.ConfigurationException;
import bgu.dcr.az.conf.utils.ConfigurationUtils;
import bgu.dcr.az.execs.api.experiments.ExecutionResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

/**
 *
 * @author User
 */
public class ExperimentUtils {

    public static void executeExperiment(File experimentFile) throws Exception {
        Experiment exp = loadExperiment(experimentFile);
        exp.execute();
    }

    public static Experiment loadExperiment(File experimentFile) throws ConfigurationException, IOException, ParsingException, ClassNotFoundException {
        Builder builder = new Builder();
        Document doc = builder.build(experimentFile);
        Configuration expConf = ConfigurationUtils.fromXML(doc.getRootElement());
        Experiment exp = expConf.create();
        return exp;
    }

    public static Experiment loadExperiment(InputStream experimentStream) throws ConfigurationException, IOException, ParsingException, ClassNotFoundException {
        Builder builder = new Builder();
        Document doc = builder.build(experimentStream);
        Configuration expConf = ConfigurationUtils.fromXML(doc.getRootElement());
        Experiment exp = expConf.create();
        return exp;
    }

    public static void executeExperiment(InputStream experimentInput) throws Exception {
        Builder builder = new Builder();
        Document doc = builder.build(experimentInput);

        Configuration expConf = ConfigurationUtils.fromXML(doc.getRootElement());
        Experiment exp = expConf.create();
        long time = System.currentTimeMillis();
        ExecutionResult result = exp.execute();
        System.out.println("Took: " + (System.currentTimeMillis() - time) + " millis");
        switch (result.getState()) {
            case WRONG:
                System.out.println("WRONG SOLUTION!!!!, got: " + result.getSolution() + ", while the real solution should be: " + result.getCorrectSolution());
                break;
            case CRUSHED:
                System.out.println("EXECUTION ERROR, printing stacktrace:");
                result.getCrushReason().printStackTrace();
                break;
            case LIMITED:
                break;
            case SUCCESS:
                System.out.println("EVERY THING WENT OK WHOOPY!");
                break;
            default:
                throw new AssertionError(result.getState().name());

        }
    }
}
