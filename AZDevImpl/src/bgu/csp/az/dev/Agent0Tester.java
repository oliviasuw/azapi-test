/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import com.j256.ormlite.logger.LocalLog;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import nu.xom.ParsingException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 *
 * @author bennyl
 */
public class Agent0Tester  {

    @Option(name = "--es", usage = "opening a [pooling] server that will transmit events in json form.")
    boolean useEventServer;
    @Option(name = "-f", usage = "the file contain the metadata of the test.", required = true)
    File test;
    @Option(name = "--sfp", usage = "suppling directory to save failed problems in", required = false)
    File failedProblemsDir;
    @Option(name = "--gui", usage = "attach gui to view execution status", required = false)
    boolean useGui;
    @Option(name = "--emode", usage = "execution mode (run/debug) default to 'run', debug automaticly use gui and will fail"
    + " if --sfp or --prob option was not given", required = false)
    String executionMode = "run";

    public void go() throws ParsingException, IOException, MalformedURLException, ClassNotFoundException {
        System.setProperty(LocalLog.LOCAL_LOG_FILE_PROPERTY, "log.out");

        ExecutionUnit.UNIT.run(test, true);
    }

//    @Override
//    public void onExecutionCrushed(Execution ex, Exception exc) {
//        handleBadEnding(ex);
//    }
//
//    @Override
//    public void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right) {
//        handleBadEnding(execution);
//    }
//
//    private ProblemSelectionModel selectProblemByGUI() {
//        ProblemSelectionModel selectPModel = new ProblemSelectionModel(failedProblemsDir.getAbsolutePath());
//        ProblemSelectionDialog diag = new ProblemSelectionDialog(null, true);
//        diag.setModel(selectPModel);
//        diag.setVisible(true);
//        return selectPModel;
//    }
//
//    private AlgorithmMetadata loadAlgorithm() throws MalformedURLException, ClassNotFoundException {
//        ClassLoader ldr = ClassLoader.getSystemClassLoader();
//        if (agentClassPath != null) {
//            ldr = new URLClassLoader(new URL[]{new File(agentClassPath).toURI().toURL()});
//        }
//
//        Class agt = ldr.loadClass(agentClass);
//        return new AlgorithmMetadata(agt);
//    }

    public static void main(String[] args) {
        Agent0Tester tester = new Agent0Tester();
        CmdLineParser parser = new CmdLineParser(tester);
        try {
            parser.parseArgument(args);
            tester.go();
        } catch (Exception ex) {
            System.err.print("Error: " + ex.getMessage() + "\n\n");

            System.err.print("usage: ");
            parser.setUsageWidth(80);
            parser.printSingleLineUsage(System.err);
            System.err.print("\nDescription:\n");
            parser.printUsage(System.err);

            ex.printStackTrace();

        }
    }

//    private void handleBadEnding(Execution ex) {
//        //SAVE THE BAD PROBLEM AND STOP ...
//        Problem p = ex.getGlobalProblem();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd'-'MM'-'yyyy' 'HH'.'mm'.'ss");
//        String fileName = sdf.format(new Date()) + " " + p.getNumberOfVariables() + "X" + p.getDomainSize(0) + ".prob";
//        System.err.println("Trying to save problematic probem to file " + fileName);
//        try {
//            FileUtils.persistObject(failedProblemsDir, fileName, p);
//            System.err.println("Problem saved successfully - you can now debug it.");
//        } catch (IOException ex1) {
//            Logger.getLogger(Agent0Tester.class.getName()).log(Level.SEVERE, "while trying to save failed problem.", ex1);
//        }
//    }
}
