/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.pages;

import bam.utils.ui.mvc.pages.Page;
import bam.utils.SwingUtils;
import bam.utils.ui.mvc.DataExtractor;
import bam.utils.ui.mvc.GenericTableModel;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.dev.Round;
import bgu.csp.az.dev.frm.TestExpirement;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class TestDetailsPage extends Page implements TestExpirement.Listener {

    GenericTableModel<Round> roundTableModel;
    String testedAlgorithmName;
    long executionTimeStart;
    long executionTimeEnd;
    String executionStatus;
    TestDetailsPageView view;
    Round currentRound = null;
    private LinkedList<Listener> listeners = new LinkedList<Listener>();

    public TestDetailsPage(List<Round> rounds, String algorithm) {
        super("Test Details", SwingUtils.resIcon("resources/img/page-test-details.png"));
        roundTableModel = new GenericTableModel<Round>(
                new DataExtractor.BeanDataExtractor<Round>(
                "Type", "P1", "Number of variables", "Domain size", "Max cost", "Length"));
        roundTableModel.fillWith(rounds);

        testedAlgorithmName = algorithm;
        executionTimeStart = System.currentTimeMillis();
        executionTimeEnd = -1;

    }

    @Override
    public JPanel getView() {
        if (view == null) {
            view = new TestDetailsPageView();
            view.setModel(this);
        }

        return view;
    }

    @Override
    public void disposeView() {
        view = null;
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public GenericTableModel<Round> getRoundTableModel() {
        return roundTableModel;
    }

    private void setExecutionStatus(String status) {
        executionStatus = status;
        fireStatusChanged();
    }

    private void fireStatusChanged() {
        for (Listener l : listeners) {
            l.onExecutionStatusChanged(this, executionStatus);
        }
    }

    public boolean isExecutionDone() {
        return executionTimeEnd > 0;
    }

    public float getExecutionTime() {
        if (isExecutionDone()) {
            return ((float) (executionTimeEnd - executionTimeStart)) / 1000.0f;
        } else {
            return ((float) (System.currentTimeMillis() - executionTimeStart)) / 1000.0f;
        }
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    private void fireCurrentRoundChanged(Round old, Round newr) {
        for (Listener l : listeners) {
            l.onRoundChanged(this, old, newr);
        }
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    @Override
    public void onExpirementEndedSuccessfully() {
        executionTimeEnd = System.currentTimeMillis();
        setExecutionStatus("Compleated Successfully!");
    }

    @Override
    public void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right) {
        executionTimeEnd = System.currentTimeMillis();
        setExecutionStatus("Compleated With Wrong Results :(");
    }

    @Override
    public void onExecutionCrushed(Execution ex, Exception exc) {
        executionTimeEnd = System.currentTimeMillis();
        setExecutionStatus("Crushed!");
    }

    @Override
    public void onExpirementStarted() {
        executionTimeStart = System.currentTimeMillis();
        executionTimeEnd = -1;
        setExecutionStatus("Running...");
    }

    @Override
    public void onNewProblemExecuted(Problem p) {
    }

    @Override
    public void onNewRoundStarted(Round r) {
        Round temp = currentRound;
        currentRound = r;
        fireCurrentRoundChanged(temp, r);
    }

    @Override
    public void onStatisticsRetrived(Statistic root) {
    }

    public static interface Listener {

        void onExecutionStatusChanged(TestDetailsPage source, String newStatus);

        void onRoundChanged(TestDetailsPage source, Round oldTemplate, Round newTemplate);
    }
}
