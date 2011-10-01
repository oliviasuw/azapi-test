/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.canalyzers;

import bam.utils.ui.mvc.pages.PagePart;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class ProblemAnalyzerPagePart extends PagePart{

    ProblemAnalyzerView view;
    
    public ProblemAnalyzerPagePart() {
        super("Problem Analyze", "show the problem and can query assignments cost");
    }

    @Override
    public JPanel getView() {
        if (view == null){
            view = new ProblemAnalyzerView();
        }
        
        return view;
    }

    @Override
    public void disposeView() {
        view = null;
    }
    
}
