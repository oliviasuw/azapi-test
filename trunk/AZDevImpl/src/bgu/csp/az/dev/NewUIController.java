/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev;

import bc.dsl.PageDSL;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.ano.Action;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.viewtypes.ParamType;
import bc.utils.PokedWorker;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Experiment.ExperimentListener;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.dev.pui.AZView;
import bgu.csp.az.dev.pui.AgentLogDocument;
import bgu.csp.az.dev.pui.Console;
import bgu.csp.az.dev.pui.UIController;
import bgu.csp.az.dev.pui.stat.StatisticModel;
import bgu.csp.az.dev.pui.stat.StatusModel;
import bgu.csp.az.impl.infra.LogListener;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.text.BadLocationException;

/**
 *
 * @author bennyl
 */
@PageDef(layout = AZView.class)
public class NewUIController extends Model implements LogListener, ExperimentListener {

    @Param(name = "Execution Progress", type = ParamType.PROGRESS, role = AZView.PROGRESS_BAR_ROLE)
    DefaultBoundedRangeModel progress;
    List<Model> pages = new LinkedList<Model>();
    AgentLogDocument bdoc = new AgentLogDocument();
    private PokedWorker pw;


    @Param(name = "Pages", type = ParamType.TABS, role = AZView.PAGES_ROLE)
    public List<Model> getPages() {
        return pages;
    }

    @Param(name = "console", customView = Console.class, role = AZView.CONSOLE_ROLE)
    public AgentLogDocument getLogDocument() {
        return bdoc;
    }

    @Action(name = AZView.STOP_AND_SAVE_ACTION)
    public void stopAndSave() {
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

        final StatusModel statusModel = new StatusModel();
        ExecutionUnit.UNIT.addExperimentListener(statusModel);
        pages.add(statusModel);
        
        final StatisticModel statisticModel = new StatisticModel();
        ExecutionUnit.UNIT.addExperimentListener(statisticModel);
        pages.add(statisticModel);
        
        PageDSL.showInFrame(this);
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
