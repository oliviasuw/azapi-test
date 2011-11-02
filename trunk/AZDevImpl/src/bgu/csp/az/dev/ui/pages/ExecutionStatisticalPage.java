/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.pages;

import bam.utils.SwingUtils;
import bam.utils.ui.mvc.GenericMapModel;
import bam.utils.ui.mvc.pages.Page;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.dev.Round;
import bgu.csp.az.dev.frm.TestExpirement;
import bgu.csp.az.dev.ui.statistics.AgentWorkRatePart;
import bgu.csp.az.dev.ui.statistics.NCCCPart;
import bgu.csp.az.dev.ui.statistics.StatisticalPagePart;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.impl.pseq.RandomProblemSequence;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class ExecutionStatisticalPage extends Page implements TestExpirement.Listener {

    private ExecutionStatisticalView view;
    private List<Listener> listeners;
    private GenericMapModel<Round, List<StatisticalPagePart>> pagePartsModel;
    private AgentWorkRatePart activeAgentWorkRatePart;
    private TestDetailsPage tdModel;
    private float currentP2;
    private List<Listener> innerListeners;

    public ExecutionStatisticalPage() {
        super("Execution Statistics", SwingUtils.resIcon("resources/img/page-execution-statistics.png"));
        listeners = new LinkedList<Listener>();
        pagePartsModel = new GenericMapModel<Round, List<StatisticalPagePart>>();
        innerListeners = new LinkedList<Listener>();
    }

    public GenericMapModel<Round, List<StatisticalPagePart>> getPagePartsModel() {
        return pagePartsModel;
    }

    public Round getCurrentRound() {
        return this.tdModel.getCurrentRound();
    }

    public float getCurrentExecutedProblemP2() {
        return currentP2;
    }

    @Override
    public JPanel getView() {
        if (view == null) {
            view = new ExecutionStatisticalView();
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

    private void addInnerListener(Listener l) {
        listeners.add(l);
        innerListeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

    private void cleanInnerListeners() {
        for (Listener l : innerListeners) {
            removeListener(l);
        }
        innerListeners.clear();
    }

    private void fireStatisticalDataReceived(Statistic tree) {
        for (Listener l : listeners) {
            l.onStatisticalDataReceived(this, tree);
        }
    }

    @Override
    public void onExpirementEndedSuccessfully() {
    }

    @Override
    public void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right) {
    }

    @Override
    public void onExecutionCrushed(Execution ex, Exception exc) {
    }

    @Override
    public void onExpirementStarted() {
    }

    @Override
    public void onNewProblemExecuted(Problem p) {
        currentP2 = (Float) p.getMetadata().get(RandomProblemSequence.P2_PROBLEM_METADATA);
    }

    @Override
    public void onNewRoundStarted(Round newt) {
        System.out.println("Round Changed: " + newt);
        cleanInnerListeners(); //TODO - remove because now we can look at the map to decide what to remove
        final LinkedList<StatisticalPagePart> crl = new LinkedList<StatisticalPagePart>();

        pagePartsModel.put(newt, crl);

        final NCCCPart nCCCPart = new NCCCPart(newt);
        crl.addLast(nCCCPart);
        addInnerListener(nCCCPart);

        activeAgentWorkRatePart = new AgentWorkRatePart(newt.getNumberOfVariables(), newt);
        crl.addLast(activeAgentWorkRatePart);
        addInnerListener(activeAgentWorkRatePart);

        //invalidaing the model..
        pagePartsModel.fireItemChanged(newt);
    }

    @Override
    public void onStatisticsRetrived(Statistic root) {
        fireStatisticalDataReceived(root);
    }

    public static interface Listener {

        void onStatisticalDataReceived(ExecutionStatisticalPage source, Statistic tree);
    }
}
