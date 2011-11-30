/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.pui.scha;

//import bc.swing.pfrm.models.NoDataModel;
import bc.swing.pfrm.Model;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.infra.stat.Statistic;
import java.util.List;

/**
 *
 * @author bennyl
 */
public enum ChartModelProvider {

    I;
    StatisticNode lastNode;

    public Model provide(StatisticNode node) {

        if (lastNode != null) {
            lastNode.clearListeners();
        }

        lastNode = node;

        if (node == null) {
//            return new NoDataModel("There is no data to show.");
        }

        StatisticNode.Listener ret = null;
        if (node.isLeaf()) {
            ret = new P2AreaChartModel(node);
        } else if (node.getChildren().get(0).isLeaf()) {
//            ret = new AgentBarChartModel(node);
        } else {
//            return new NoDataModel("There is no data to show.");
        }
//        
//        if (node.getData().equals(Agent.NCCC_STATISTIC) || node.getData().equals(Agent.NCSC_STATISTIC)) {
//        } else if (node.getData().equals(Agent.MESSAGES_RECEIVED_PER_AGENT_STATISTIC)) {
//        } else {
//            return new NoDataModel("There is no data to show.");
//        }

        node.addListener(ret);
        return (Model) ret;
    }
}
