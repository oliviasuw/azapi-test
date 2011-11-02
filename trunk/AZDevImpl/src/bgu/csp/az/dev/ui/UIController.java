/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui;

import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.dev.ui.pages.CrushAnalyzerPage;
import bgu.csp.az.dev.Round;
import bam.utils.SwingUtils;
import bam.utils.evt.EventManager;
import bam.utils.ui.mvc.pages.PageContainerModel;
import bgu.csp.az.dev.Agent0Tester;
import bgu.csp.az.dev.frm.TestExpirement;
import bgu.csp.az.dev.slog.ScenarioLogger;
import bgu.csp.az.dev.ui.pages.CrushInqueryPage;
import bgu.csp.az.dev.ui.pages.ExecutionStatisticalPage;
import bgu.csp.az.dev.ui.pages.TestDetailsPage;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SwingUtilities;

import static bam.utils.SwingUtils.*;

/**
 *
 * @author bennyl
 */
public class UIController extends TestExpirement.Handler {

    TestExpirement expirement;
    int pid = 0;
    private DefaultBoundedRangeModel progressModel;
    private PageContainerModel model;

    public UIController() {
    }

    public static void main(String[] args) {
        Agent0Tester.main(new String[]{"-f", "test.xml", "--gui", "--cp", "bin", "-a", "ext.sim.agents.SBBAgent2", "--emode", "debug", "--sfp", "fails"});
    }

    public void go(TestExpirement exp) {
        this.expirement = exp;
        exp.addListener(this);

        //CONFIGURE UI
        SwingUtils.configureSystemLookAndFeel();

        //CONFIGURE EVENT MANAGER - TODO IS IT NEEDED HERE?
        final EventManager eman = EventManager.INSTANCE;

        //PAGE CONTAINER
        model = new PageContainerModel();
        final MainView view = new MainView();
        view.setModel(model);

        //TEST DETAILS
        final TestDetailsPage testDetailsPage = new TestDetailsPage(exp.getRounds(), exp.getTestedAlgorithmName());
        model.registerPage(testDetailsPage);
        expirement.addListener(testDetailsPage);

        //PROGRESS MONITORING
        progressModel = new DefaultBoundedRangeModel(0, 0, 0, numOfProblems() - 1);
        view.setModel(progressModel);

        //EXECUTION STATISTICS
        final ExecutionStatisticalPage execStatPage = new ExecutionStatisticalPage();
        model.registerPage(execStatPage);
        expirement.addListener(execStatPage);

        //CRUSH ANALYZER SQL
        model.registerPage(new CrushInqueryPage());


        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                view.setVisible(true);
            }
        });


    }

    private void handleCrush(Execution exec) {
        JdbcConnectionSource conn = ScenarioLogger.getNewDataBaseConnection(TestExpirement.TEMP_SCENARIO_LOG_DB_PATH);
        final CrushAnalyzerPage crushAnalyzerPage = new CrushAnalyzerPage();
        crushAnalyzerPage.setDBConnection(conn);
        
        //CRUSH ANALYZER
        model.registerPage(crushAnalyzerPage);

    }

    private int numOfProblems() {
        int np = 0;
        for (Round t : this.expirement.getRounds()) {
            np += t.getLength();
        }

        return np;
    }

    @Override
    public void onExecutionCrushed(Execution ex, Exception exc) {
        errbox("execution status", "Execution Crushed: " + exc.getMessage());
        handleCrush(ex);
    }

    @Override
    public void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right) {
        errbox("execution status", "Execution Ended With Wrong Results.");
        handleCrush(execution);
    }

    @Override
    public void onExpirementEndedSuccessfully() {
        msgbox("execution status", "Execution Ended Successfully.");
    }

    @Override
    public void onNewProblemExecuted(Problem p) {
        progressModel.setValue(pid++);
    }

    @Override
    public void onStatisticsRetrived(Statistic root) {
        super.onStatisticsRetrived(root);
    }
}
