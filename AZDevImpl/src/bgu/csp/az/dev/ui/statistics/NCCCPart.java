/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.statistics;

import bam.utils.ui.mvc.pages.PagePart;
import bam.utils.ui.graph.AreaChartModel;
import bam.utils.ui.graph.AreaChartView;
import bgu.csp.az.dev.Round;
import bgu.csp.az.dev.ui.pages.ExecutionStatisticalPage;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.agt.SimpleAgent;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class NCCCPart extends StatisticalPagePart {

    AreaChartView view;
    AreaChartModel model;

    public NCCCPart(Round round) {
        super("NCCC", "shows a graph represents the number of concurent constraint checks");
        model = new AreaChartModel("NCCC Round #" + round.getNumber(), "P2", "NCCC");
        model.setLogarithmicRangeScale(true);
    }

    @Override
    public JPanel getView() {
        if (view == null) {
            view = new AreaChartView();
            view.setModel(model);
        }

        return view;
    }

    @Override
    public void disposeView() {
        view = null;
    }

    @Override
    public void onStatisticalDataReceived(ExecutionStatisticalPage source, Statistic tree) {
        this.model.avg(source.getCurrentExecutedProblemP2(), tree.getChild(SimpleAgent.NCCC_STATISTIC).getValue());
    }
}
