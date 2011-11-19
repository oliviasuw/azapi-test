/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.dev.pui.stat;

import bc.ds.TimeDelta;
import bc.swing.pfrm.Model;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.ano.DataExtractor;
import bc.swing.pfrm.ano.PageDef;
import bc.swing.pfrm.ano.Param;
import bc.swing.pfrm.ano.ViewHints;
import bc.swing.pfrm.viewtypes.ParamType;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.api.infra.stat.Statistic;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.tools.Assignment;
//import bgu.csp.az.dev.frm.Round;
//import bgu.csp.az.dev.frm.TestExpirement;
import java.util.List;

/**
 *
 * @author bennyl
 */
@PageDef(icon = "page-test-details", name = "Status", layout = StatusView.class)
public class StatusModel extends Model {
//
//    public static final String CURRENT_ROUND_PARAM = "Current Round";
//    public static final String EXECUTION_STATUS_PARAM = "Execution Status";
//    public static final String EXECUTION_TIME_PARAM = "Execution Time";
//    public static final String ROUNDS_PARAM = "Rounds";
//   
////    private List<Round> rounds;
//    @Param(name = EXECUTION_STATUS_PARAM, type = ParamType.LABEL, role = StatusView.EXECUTION_STATUS_ROLE)
//    String executionStatus;
//    @Param(name = EXECUTION_TIME_PARAM, type = ParamType.LABEL, role = StatusView.EXECUTION_TIME_ROLE)
//    @ViewHints(autoSyncEvery = 250)
//    TimeDelta execTime = new TimeDelta();
//    @Param(name = CURRENT_ROUND_PARAM)
//    Round currentRound;
//    TestExpirement te;
//    int problemNumber = 0;
//    
//    public void setExpirement(TestExpirement te) {
//        this.te = te;
//        rounds = te.getRounds();
//        te.addListener(this);
//    }
//
//    @ViewHints(allowSelection = false)
//    @Param(name = ROUNDS_PARAM, type = ParamType.TABLE, role = StatusView.ROUNDS_ROLE)
//    public List<Round> getRounds() {
//        return rounds;
//    }
//
//    @Param(name = "Tested Algorithm", type = ParamType.LABEL, role = StatusView.ALGORITHM_NAME_ROLE)
//    public String getTestedAlgorithmName() {
//        return te.getTestedAlgorithmName();
//    }
//
//    @DataExtractor(param = ROUNDS_PARAM,
//    columns = {"Length", "#Vars", "|Domain|", "Max Cost", "P(Constraint Between Two Variables)"})
//    public String extractRound(String column, Round from) {
//        if ("Length".equals(column)) {
//            return "" + from.getLength();
//        } else if ("#Vars".equals(column)) {
//            return "" + from.getNumberOfVariables();
//        } else if ("|Domain|".equals(column)) {
//            return "" + from.getDomainSize();
//        } else if ("Max Cost".equals(column)) {
//            return "" + from.getMaxCost();
//        } else if ("P(Constraint Between Two Variables)".equals(column)) {
//            return "" + from.getP1();
//        } else {
//            return "????????";
//        }
//    }
//
//    @Override
//    public void onExpirementEndedSuccessfully() {
//        setExecutionEnded("Finished Successfully!");
//    }
//
//    private void setExecutionEnded(String status) {
//        setExecutionStatus(status);
//        execTime.setEnd();
//    }
//
//    private void setExecutionStatus(String status) {
//        executionStatus = status;
//        Page.get(this).syncParameterFromModel(EXECUTION_STATUS_PARAM);
//    }
//
//    @Override
//    public void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right) {
//        setExecutionEnded("Finished with wrong results");
//    }
//
//    @Override
//    public void onExecutionCrushed(Execution ex, Exception exc) {
//        setExecutionEnded("Crushed because of " + exc.getClass().getSimpleName());
//    }
//
//    @Override
//    public void onExpirementStarted() {
//        setExecutionStatus("Running...");
//        execTime.setStart();
//    }
//
//    @Override
//    public void onNewProblemExecuted(Problem p) {
//        problemNumber++;
//        setExecutionStatus("Solving the " + problemNumber + "/" + currentRound.getLength() + " problem on this round...");
//    }
//
//    @Override
//    public void onNewRoundStarted(Round r) {
//        currentRound = r;
//        problemNumber = 0;
//        Page.get(this).syncParameterFromModel(CURRENT_ROUND_PARAM);
//    }
//
//    @Override
//    public void onStatisticsRetrived(Statistic root) {
//    }
}
