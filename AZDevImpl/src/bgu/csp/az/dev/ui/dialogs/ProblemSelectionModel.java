/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.dialogs;

import bam.utils.FileUtils;
import bam.utils.ui.mvc.GenericListModel;
import bgu.csp.az.api.Problem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class ProblemSelectionModel {
    GenericListModel<File> problemsModel;
    File selectedProblem;
    boolean debugFullTest = false;

    public ProblemSelectionModel(String problemPath) {
        problemsModel = new GenericListModel<File>();
        File dir = new File(problemPath);
        if (dir.exists()){
            for (File f : dir.listFiles()){
                problemsModel.addLast(f);
            }
        }
    }

    public boolean isDebugFullTest() {
        return debugFullTest;
    }

    public void setDebugFullTest(boolean debugFullTest) {
        this.debugFullTest = debugFullTest;
    }

    public GenericListModel<File> getProblemsModel() {
        return problemsModel;
    }

    public File getSelectedProblemFile() {
        return selectedProblem;
    }

    public void setSelectedProblemFile(File selectedProblem) {
        this.selectedProblem = selectedProblem;
    }
    
    public Problem getSelectedProblem() throws IOException{
        try {
            return FileUtils.unPersistObject(selectedProblem, Problem.class);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProblemSelectionModel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
