/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.mas.exp;

import bgu.dcr.az.anop.conf.Configuration;
import bgu.dcr.az.anop.conf.ConfigurationUtils;
import bgu.dcr.az.api.exen.ExecutionResult;
import java.io.File;
import java.io.InputStream;
import nu.xom.Builder;
import nu.xom.Document;

/**
 *
 * @author User
 */
public class ExperimentUtils {

    public static void executeExperiment(File experimentFile) throws Exception {
        Builder builder = new Builder();
        Document doc = builder.build(experimentFile);

        Configuration expConf = ConfigurationUtils.fromXML(doc.getRootElement());
        Experiment exp = expConf.create();
        exp.execute();
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
