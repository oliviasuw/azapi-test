/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.ui.statistics;

import bam.utils.ui.mvc.pages.PagePart;
import bam.utils.ui.graph.BarChartModel;
import bam.utils.ui.graph.BarChartView;
import bgu.csp.az.dev.Round;
import bgu.csp.az.dev.ui.pages.ExecutionStatisticalPage;
import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Statistic;
import java.util.Map.Entry;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public class AgentWorkRatePart extends StatisticalPagePart{

    BarChartView view;
    BarChartModel barChartModel;
    
    public AgentWorkRatePart(int numberOfVariables, Round round) {
        super("Agent Work Rate", "show the avarage agent message handling for the " + round.getNumber() + "'st round");
        barChartModel = new BarChartModel("Agent Work Rate Round #" + round.getNumber(), "", "Work");
        for (int i=0; i<numberOfVariables; i++) barChartModel.set("Agent " + i, 0);
    }

    @Override
    public JPanel getView() {
        if (view == null){
            view = new BarChartView();
            view.setModel(barChartModel);
            
        }
        
        return view;
    }

    @Override
    public void disposeView() {
        view = null;
    }

    @Override
    public void onStatisticalDataReceived(ExecutionStatisticalPage source, Statistic tree) {
        Statistic child = tree.getChild(Agent.MESSAGES_RECEIVED_PER_AGENT_STATISTIC);
        for (Entry<String, Statistic> c : child.getChilds().entrySet()){
            barChartModel.avg(c.getKey(), c.getValue().getValue());
        }
    }
    
}
