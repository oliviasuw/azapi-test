/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.pui;

import bc.dsl.PageDSL;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.dev.Round;
import java.util.List;
import bc.dsl.SwingDSL;
import bc.swing.models.BatchDocument;
import bc.swing.models.LimitedBatchDocument;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.ano.Action;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.viewtypes.ParamType;
import bc.utils.PokedWorker;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.dev.frm.TestExpirement;
import bgu.csp.az.dev.pui.scha.StatisticsModel;
import bgu.csp.az.dev.pui.stat.StatusModel;
import bgu.csp.az.impl.infra.LogListener;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.text.BadLocationException;

/**
 *
 * @author bennyl
 */
@PageDef(layout = AZView.class, name = "AgentZero Test Execution Analyzer")
public class UIController extends Model implements TestExpirement.Listener {
    public static final String STOP_AND_SAVE_ACTION = "Stop and save";

    @Param(name = "Pages", type = ParamType.TABS, role = AZView.PAGES_ROLE)
    List<Model> models = new LinkedList<Model>();
    @Param(name = "Execution Progress", type = ParamType.PROGRESS, role = AZView.PROGRESS_BAR_ROLE)
    DefaultBoundedRangeModel progress;
    TestExpirement te;
    //BatchDocument bdoc = new LimitedBatchDocument();
    AgentLogDocument bdoc = new AgentLogDocument();
    
    public void go(TestExpirement te) {
        this.te = te;
        te.addListener(this);
        
        
        progress = new DefaultBoundedRangeModel(0, 0, 0, te.getNumberOfLeftProblems());

        final StatusModel statusModel = new StatusModel();
        statusModel.setExpirement(te);
        models.add(statusModel);
        
        final StatisticsModel statisticsModel = new StatisticsModel();
        te.addListener(statisticsModel);
        models.add(statisticsModel);
        
        
        final PokedWorker pw = new PokedWorker(100) {

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
        
        te.addLogListener(new LogListener() {

            @Override
            public void onLog(int agent, String mailGroupKey, String log) {
                final String lname = "[" + mailGroupKey.substring(mailGroupKey.lastIndexOf(".") +1) + "] " +agent;
                bdoc.addLog(lname,log, Level.INFO);
                System.out.println(lname + ": " + log);
                pw.poke();
            }
        });
        
        SwingDSL.configureUI();
        PageDSL.showInFrame(this);
    }

    @Action(name=STOP_AND_SAVE_ACTION, icon="cross-circle")
    private void handleStopAndSave(){
        te.stop();
    }

    @Param(name="console", customView=Console.class, role=AZView.CONSOLE_ROLE)
    public BatchDocument getBatchdoc() {
        return bdoc;
    }

    @Override
    public void onExpirementEndedSuccessfully() {
        SwingDSL.msgbox("GREAT!", "Execution ended successfully.");
    }

    @Override
    public void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right) {
        SwingDSL.errbox("Bad News...", "Execution ended with wrong results");
    }

    @Override
    public void onExecutionCrushed(Execution ex, Exception exc) {
        SwingDSL.errbox("Bad News...", "Execution crushed with the exception:\n" + exc.getClass().getSimpleName() + ": " + exc.getMessage());
    }

    @Override
    public void onExpirementStarted() {
    }

    @Override
    public void onNewProblemExecuted(Problem p) {
        progress.setValue(progress.getValue() + 1);
    }

    @Override
    public void onNewRoundStarted(Round r) {
    }

    @Override
    public void onStatisticsRetrived(Statistic root) {
    }
}
