/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.pui.scha;

import bc.dsl.JavaDSL.Fn1;
import bc.swing.models.GenericTreeModel.Node;
import bc.swing.models.chart.AreaChartModel;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
//import bc.swing.pfrm.layouts.CenterLayout;
//import bc.swing.pfrm.viewtypes.ParamType;
import bgu.dcr.az.api.infra.stat.Statistic;

import java.util.List;

/**
 *
 * @author bennyl
 */
//@PageDef(layout = CenterLayout.class)
public class P2AreaChartModel extends Model implements StatisticNode.Listener {

    AreaChartModel chart;

//    @Param(name = "Chart", type = ParamType.CHART)
    public AreaChartModel getChart() {
        return chart;
    }

    public P2AreaChartModel(StatisticNode sn) {
        chart = new AreaChartModel();
        chart.setDomainAxisLabel("P2");
        final Node parent = sn.getParent();
        
        chart.setRangeAxisLabel((!parent.toString().startsWith("Round")? parent.toString().replace("Per Agent", ""):sn.toString()));
        sn.safeStatisticsIteration(new Fn1<Void, Statistic>() {

            @Override
            public Void invoke(Statistic arg) {
                onRootAdded(arg);
                return null;
            }
        });
        chart.setTitle(sn.getData().toString());
    }

    @Override
    public synchronized final void onRootAdded(Statistic s) {
//        double p2 = (Float) s.getProblemMetadata().get(RandomProblemSequence.P2_PROBLEM_METADATA);
//        chart.add(p2, s.getValue());
    }
}
