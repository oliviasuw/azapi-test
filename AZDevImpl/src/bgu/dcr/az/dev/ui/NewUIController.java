/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.ui;

//import bgu.csp.az.dev.swing.AZScreen;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.ano.Action;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.utils.PokedWorker;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Experiment;
import bgu.dcr.az.api.infra.Experiment.ExperimentListener;
import bgu.dcr.az.api.infra.Round;
import bgu.dcr.az.dev.ExecutionUnit;
import bgu.dcr.az.dev.pui.AZView;
import bgu.dcr.az.dev.pui.AgentLogDocument;
import bgu.dcr.az.dev.pui.UIController;
import bgu.dcr.az.dev.pui.stat.StatisticModel;
import bgu.dcr.az.dev.pui.stat.StatusModel;
import bgu.dcr.az.impl.infra.LogListener;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;

/**
 *
 * @author bennyl
 */
//@PageDef(layout = AZScreen.class)
public class NewUIController extends Model implements LogListener, ExperimentListener {

    @Param(name = "Execution Progress", role = AZView.PROGRESS_BAR_ROLE)
    DefaultBoundedRangeModel progress;
    List<Model> pages = new LinkedList<Model>();
    AgentLogDocument bdoc = new AgentLogDocument();
    private PokedWorker pw;


    @Param(name = "Pages", role = AZView.PAGES_ROLE)
    public List<Model> getPages() {
        return pages;
    }

    @Param(name = "console", role = AZView.CONSOLE_ROLE)
    public AgentLogDocument getLogDocument() {
        return bdoc;
    }

    @Action(name = AZView.STOP_AND_SAVE_ACTION)
    public void stopAndSave() {
    }
    
    public static void main(String[] args){
        new NewUIController().startUi();
    }

    public void startUi() {
        SwingDSL.configureUI();

        int sum = 0;
        for (Round r : ExecutionUnit.UNIT.getAllRounds()){
            sum += r.getLength();
        }
        
        progress = new DefaultBoundedRangeModel(0, 1, 0, sum);
        
        ExecutionUnit.UNIT.addExperimentListener(this);
        ExecutionUnit.UNIT.setLogListener(this);
        pw = new PokedWorker(100) {

            @Override
            public void work() {
                try {
                    bdoc.processBatchUpdates();

                } catch (BadLocationException ex) {
                    Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        new Thread(pw).start();

//        final StatusModel statusModel = new StatusModel();
//        ExecutionUnit.UNIT.addExperimentListener(statusModel);
//        pages.add(statusModel);
//        
//        final StatisticModel statisticModel = new StatisticModel();
//        ExecutionUnit.UNIT.addExperimentListener(statisticModel);
//        pages.add(statisticModel);
        
        JFrame jf = new JFrame("test");
        jf.setContentPane(Page.get(this).getView());
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.setVisible(true);
    }

    @Override
    public void onLog(int agent, String mailGroupKey, String log) {
        final String lname = "[" + mailGroupKey.substring(mailGroupKey.lastIndexOf(".") +1) + "] " +agent;
        bdoc.addLog(lname, log, Level.INFO);
        pw.poke();
    }

    @Override
    public void onExpirementStarted(Experiment source) {
    }

    @Override
    public void onExpirementEnded(Experiment source) {
    }

    @Override
    public void onNewRoundStarted(Experiment source, Round round) {
    }

    @Override
    public void onNewExecutionStarted(Experiment source, Round round, Execution exec) {
        progress.setValue(progress.getValue()+1);
    }

    @Override
    public void onExecutionEnded(Experiment source, Round round, Execution exec) {
    }
}
