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
import bgu.csp.az.dev.frm.TestExecution;
import java.util.List;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.ano.Action;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.viewtypes.ParamType;
import bgu.csp.az.dev.frm.TestExpirement;
import bgu.csp.az.dev.pui.scha.StatisticsModel;
import bgu.csp.az.dev.pui.stat.StatusModel;
import java.util.LinkedList;
import javax.swing.DefaultBoundedRangeModel;

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
        
        
        te.addLogListener(new TestExecution.LogListner() {

            @Override
            public void onLog(int agent, String msg) {
                System.out.println("Agent: " + agent + ": " + msg);
            }
        });
        
        SwingDSL.configureUI();
        PageDSL.showInFrame(this);
    }

    @Action(name=STOP_AND_SAVE_ACTION, icon="cross-circle")
    private void handleStopAndSave(){
        te.stop();
    }

    @Override
    public void onExpirementEndedSuccessfully() {
        SwingDSL.msgbox("GREAT!", "Execution ended successfully.");
    }

    @Override
    public void onExecutionEndedWithWrongResult(TestExecution execution, Assignment wrong, Assignment right) {
        SwingDSL.errbox("Bad News...", "Execution ended with wrong results");
    }

    @Override
    public void onExecutionCrushed(TestExecution ex, Exception exc) {
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
