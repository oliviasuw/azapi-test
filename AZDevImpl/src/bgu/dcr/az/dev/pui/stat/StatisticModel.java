/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.pui.stat;

import bc.dsl.SwingDSL;
import bc.swing.models.chart.AreaChartModel;
import bc.swing.models.chart.ChartModel;
import bc.swing.pfrm.Parameter;
import bc.swing.pfrm.DeltaHint;
//import bc.swing.pfrm.FieldParamModel.ChangeListener;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.ano.Action;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
//import bc.swing.pfrm.models.NoDataModel;
//import bc.swing.pfrm.viewtypes.ParamType;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.Experiment;
import bgu.dcr.az.api.infra.Experiment.ExperimentListener;
import bgu.dcr.az.api.infra.Round;
import bgu.dcr.az.api.infra.stat.StatisticCollector;
import bgu.dcr.az.api.infra.stat.VisualModel;
import bgu.dcr.az.api.infra.stat.vmod.LineVisualModel;
import bgu.dcr.az.impl.db.DatabaseUnit;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author bennyl
 */
@PageDef(icon = "page-execution-statistics", name = "Execution statistics", layout = StatisticsLayout.class)
public class StatisticModel extends Model implements ExperimentListener {

    public static final String COLLECTORS_PARAM = "COLLECTORS PARAM";
    public static final String PASSED_ROUNDS_PARAM = "PASSED ROUNDS PARAM";
    public static final String CHART_PARAM = "CHART PARAM";
    public static final String TABLE_PARAM = "TABLE PARAM";
    private List<Round> passedRounds = new LinkedList<Round>();
    private VisualModel lastVisualModel = null;

//    @Param(type = ParamType.OPTIONS, name = PASSED_ROUNDS_PARAM, role = StatisticsLayout.ROUNDS_ROLE)
    public List<ToStringBox<Round>> getPassedRounds() {
        LinkedList<ToStringBox<Round>> re = new LinkedList<ToStringBox<Round>>();
        for (Round p : passedRounds) {
            re.add(new ToStringBox<Round>(p) {

                @Override
                protected String toString(Round val) {
                    return val.getName();
                }
            });
        }

        return re;
    }

//    @Param(type = ParamType.CHART, name = CHART_PARAM, role = StatisticsLayout.CHART_ROLE)
    public ChartModel getChart() {
        
        return null;

//        if (getPage().param(COLLECTORS_PARAM).getSelectedItem() != null) {
//            StatisticCollector selectedCollector = ((ToStringBox<StatisticCollector>) getPage().param(COLLECTORS_PARAM).getSelectedItem()).getVal();
//            if (selectedCollector != null) {
//                Round selectedRound = ((ToStringBox<Round>) getPage().param(PASSED_ROUNDS_PARAM).getSelectedItem()).getVal();
//                lastVisualModel = selectedCollector.analyze(DatabaseUnit.UNIT.createDatabase(), selectedRound);
//
//                if (lastVisualModel instanceof LineVisualModel) {
//                    syncToView(TABLE_PARAM);
//                    return transform((LineVisualModel) lastVisualModel);
//                } else {
//                    return null;
//                }
//            }
//        }
//
//        return null;
    }

//    @Param(name=TABLE_PARAM, customView=AnalayzedTableView.class, role=StatisticsLayout.TABLE_ROLE)
    public VisualModel getLastVisualModel(){
        return lastVisualModel;
    }
    
//    @Param(type = ParamType.OPTIONS, name = COLLECTORS_PARAM, role = StatisticsLayout.COLLECTORS_ROLE)
    public List<ToStringBox<StatisticCollector>> getCollectors() {
        return null;
        
//        List<ToStringBox<StatisticCollector>> ret = new LinkedList<ToStringBox<StatisticCollector>>();
//        ToStringBox<Round> selectedRoundName = (ToStringBox<Round>) getPage().param(PASSED_ROUNDS_PARAM).getSelectedItem();
//        if (selectedRoundName != null) {
//            Round selectedRound = selectedRoundName.getVal();
//
//            if (selectedRound != null) {
//                for (StatisticCollector sc : selectedRound.getRegisteredStatisticCollectors()) {
//                    ret.add(new ToStringBox<StatisticCollector>(sc) {
//
//                        @Override
//                        protected String toString(StatisticCollector val) {
//                            return val.getName();
//                        }
//                    });
//                }
//            }
//        }
//        return ret;
    }

    @Override
    public void onExpirementStarted(Experiment source) {
    }

    @Override
    public void onExpirementEnded(Experiment source) {
    }

    @Override
    public void onNewRoundStarted(Experiment source, Round round) {
        passedRounds.add(round);
        syncToView(PASSED_ROUNDS_PARAM, DeltaHint.lastItemAdded());
    }

    @Override
    public void onNewExecutionStarted(Experiment source, Round round, Execution exec) {
    }

    @Override
    public void onExecutionEnded(Experiment source, Round round, Execution exec) {
    }

//    @Override
//    public void whenPageCreated(Page page) {
//        page.param(PASSED_ROUNDS_PARAM).addSelectionListner(new ChangeListener() {
//
//            @Override
//            public void onChange(Parameter source, Object newValue, Object deltaHint) {
//                syncToView(COLLECTORS_PARAM);
//            }
//        });
//
//        page.param(COLLECTORS_PARAM).addSelectionListner(new ChangeListener() {
//
//            @Override
//            public void onChange(Parameter source, Object newValue, Object deltaHint) {
//                syncToView(CHART_PARAM);
//            }
//        });
//
//    }
    
    @Action(name=StatisticsLayout.EXPORT_TO_CSV_ACTION)
    void onExportToCSV(){
        if (lastVisualModel != null){
            final File file = new File("temp.csv");
            lastVisualModel.exportToCSV(file);
            SwingDSL.dopen(file);
        }
    }

    private ChartModel transform(LineVisualModel lineVisualModel) {
        bc.swing.models.chart.AreaChartModel model = new AreaChartModel();
        model.setTitle(lineVisualModel.getTitle());
        model.setDomainAxisLabel(lineVisualModel.getxAxisName());
        model.setRangeAxisLabel(lineVisualModel.getyAxisName());

        for (Entry<Double, Double> v : lineVisualModel.getValues().entrySet()) {
            model.add(v.getKey().floatValue(), v.getValue().floatValue());
        }
        return model;
    }

    
    public abstract static class ToStringBox<T> {

        T val;

        public ToStringBox(T val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return toString(val);
        }

        protected abstract String toString(T val);

        public T getVal() {
            return val;
        }
    }
}
