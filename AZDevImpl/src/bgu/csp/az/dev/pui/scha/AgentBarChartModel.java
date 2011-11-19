/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.pui.scha;

import bc.dsl.JavaDSL.Fn1;
import bc.swing.models.chart.BarChartModel;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.layouts.CenterLayout;
import bc.swing.pfrm.viewtypes.ParamType;
import bgu.csp.az.api.infra.stat.Statistic;
import java.util.Map.Entry;

/**
 *
 * @author bennyl
 */
@PageDef(layout = CenterLayout.class)
public class AgentBarChartModel extends Model implements StatisticNode.Listener{

    BarChartModel chart;

    @Param(name = "Chart", type = ParamType.CHART)
    public BarChartModel getChart() {
        return chart;
    }

    public AgentBarChartModel(StatisticNode sn) {
        chart = new BarChartModel();
        chart.setDomainAxisLabel("Agents");
        String y = sn.toString().replace("Per Agent", "");
        chart.setRangeAxisLabel(y);
        sn.safeStatisticsIteration(new Fn1<Void, Statistic>() {

            @Override
            public Void invoke(Statistic arg) {
                onRootAdded(arg);
                return null;
            }
        });
        chart.setTitle("Avarage " + sn.getData().toString());
    }

    @Override
    public void onRootAdded(Statistic root) {
        for (Entry<String, Statistic> child : root.getChildren().entrySet()){
            chart.avg(child.getKey(), child.getValue().getValue());
        }
    }
}
