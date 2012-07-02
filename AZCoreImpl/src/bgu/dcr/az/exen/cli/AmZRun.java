/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.cli;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionSelector;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Experiment.ExperimentListener;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.exen.dtp.AzDTPMessage;
import bgu.dcr.az.exen.dtp.Client;
import bgu.dcr.az.exen.escan.ExperimentReader;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import bgu.dcr.az.exen.util.CLIs;
import java.io.File;
import java.io.IOException;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Administrator
 */
public class AmZRun {

    
    @Option(name = "-e", usage = "the experiment file name", required = true)
    File experimentFile;
    @Option(name = "-rdir", usage = "the directory where to put the experiment results", required = true)
    File experimentResultsDir;
    @Option(name = "-tn", usage = "test name to execute", required = true)
    String testName;
    @Option(name = "-an", usage = "algorithm instance name to execute", required = true)
    String algorithmInstanceName;
    @Option(name = "-pn", usage = "problem number/ execution number to execute", required = true)
    int problemNumber;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AmZRun command = new AmZRun();
            CLIs.parseArgs(command, args);
            
            Experiment runningExperiment = ExperimentReader.read(command.experimentFile);

            //RECONFIGURE EXPERIMENT TO REMOVE ANY CORRECTNESS TESTERS
            for (Test t : runningExperiment.getTests()) {
                t.setCorrectnessTester(null);
            }

            //SETUP SELECTOR 
            ExecutionSelector eSel = new ExecutionSelector(command.testName, command.algorithmInstanceName, command.problemNumber);
            
            //RUN
            runningExperiment.run();
            
            System.out.println("Writing results...");
            DatabaseUnit.UNIT.dumpToCsv(command.experimentResultsDir);
            System.out.println("Writing results done.");
            
            System.out.println("Terminating...");
            System.exit(0);
        } catch (IOException ex) {
            CLIs.scream("Cannot find the experiment file specified", ex);
        } catch (InstantiationException ex) {
            CLIs.scream("Experiment contains modules that cannot be resolved", ex);
        } catch (IllegalAccessException ex) {
            CLIs.scream("Experiment curropted modules - contact the module developer", ex);
        }
    }
        
}
