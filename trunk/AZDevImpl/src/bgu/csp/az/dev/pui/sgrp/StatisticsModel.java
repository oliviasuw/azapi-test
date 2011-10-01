/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.pui.sgrp;

import bc.dsl.SwingDSL;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.ano.ViewHints;
import bc.swing.pfrm.params.ParamType;
import bgu.csp.az.api.Problem;
import bgu.csp.az.api.Statistic;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.dev.Round;
import bgu.csp.az.dev.frm.TestExecution;
import bgu.csp.az.dev.frm.TestExpirement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;


/**
 *
 * @author bennyl
 */
@PageDef(icon="page-execution-statistics", name="Execution statistics", view=StatisticsView.class)
public class StatisticsModel extends Model implements TestExpirement.Listener{
    public static final String AVAILABLE_GRAPHS_PARAM = "Available Graphs";
    
    @Param(name=AVAILABLE_GRAPHS_PARAM, type= ParamType.TREE, role=StatisticsView.GRAPHS_TREE_ROLE)
    Map<Round, Map<String, Object>> graphs;

    public StatisticsModel() {
        graphs = new HashMap<Round, Map<String, Object>>();
    }

    @Override
    public ImageIcon provideParamValueIcon(String param, Object value) {
        if (param.equals(AVAILABLE_GRAPHS_PARAM)){
            if (value instanceof Round){
                return SwingDSL.resIcon("round");
            }
        }
        return super.provideParamValueIcon(param, value);
    }
    
    @Override
    public void onExpirementEndedSuccessfully() {
    }

    @Override
    public void onExecutionEndedWithWrongResult(TestExecution execution, Assignment wrong, Assignment right) {
    }

    @Override
    public void onExecutionCrushed(TestExecution ex, Exception exc) {
    }

    @Override
    public void onExpirementStarted() {
    }

    @Override
    public void onNewProblemExecuted(Problem p) {
    }

    @Override
    public void onNewRoundStarted(Round r) {
        graphs.put(r, new HashMap<String, Object>());
        Page.get(this).syncParameterFromModel(AVAILABLE_GRAPHS_PARAM);
    }

    @Override
    public void onStatisticsRetrived(Statistic root) {
        
    }
    
}
