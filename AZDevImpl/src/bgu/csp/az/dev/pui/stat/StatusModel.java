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
//import bc.swing.pfrm.viewtypes.ParamType;
import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Experiment.ExperimentResult;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.Experiment.ExperimentListener;
import bgu.csp.az.api.infra.Round;
import bgu.csp.az.api.tools.Assignment;
//import bgu.csp.az.dev.frm.Round;
//import bgu.csp.az.dev.frm.TestExpirement;
import bgu.csp.az.dev.ExecutionUnit;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
@PageDef(icon = "page-test-details", name = "Status", layout = StatusLayout.class)
public class StatusModel extends Model implements ExperimentListener {

    public static final String CURRENT_ROUND_PARAM = "Current Round";
    public static final String EXECUTION_STATUS_PARAM = "Execution Status";
    public static final String EXECUTION_TIME_PARAM = "Execution Time";
    public static final String ROUNDS_PARAM = "Rounds";
    public static final String TESTED_ALGORITHM_PARAM = "Tested Algorithm";
//    @Param(name = EXECUTION_STATUS_PARAM, type = ParamType.LABEL, role = StatusLayout.EXECUTION_STATUS_ROLE)
    String executionStatus;
//    @Param(name = EXECUTION_TIME_PARAM, type = ParamType.LABEL, role = StatusLayout.EXECUTION_TIME_ROLE)
//    @ViewHints(autoSyncEvery = 250)
    TimeDelta execTime = new TimeDelta();
    @Param(name = CURRENT_ROUND_PARAM)
    Round currentRound;
    int problemNumber = 0;

//    @ViewHints(allowSelection = false)
//    @Param(name = ROUNDS_PARAM, type = ParamType.TABLE, role = StatusLayout.ROUNDS_ROLE)
    public List<Round> getRounds() {
        return ExecutionUnit.UNIT.getAllRounds();
    }

//    @Param(name = TESTED_ALGORITHM_PARAM, type = ParamType.LABEL, role = StatusLayout.ALGORITHM_NAME_ROLE)
    public String getTestedAlgorithmName() {
        if (ExecutionUnit.UNIT.getRunningAlgorithm() != null) {
            return ExecutionUnit.UNIT.getRunningAlgorithm().getName();
        }else {
            return "???";
        }
    }

    @DataExtractor(param = ROUNDS_PARAM,
    columns = {"Run Var", "Var Start", "Var End", "Tick", "Tick Size"})
    public String extractRound(String column, Round from) {
        if ("Tick Size".equals(column)) {
            return "" + from.getTickSize();
        } else if ("Tick".equals(column)) {
            return "" + from.getTick();
        } else if ("Run Var".equals(column)) {
            return "" + from.getRunningVarName();
        } else if ("Var Start".equals(column)) {
            return "" + from.getVarStart();
        } else if ("Var End".equals(column)) {
            return "" + from.getVarEnd();
        } else {
            return "????????";
        }
    }

    public void onExpirementEndedSuccessfully() {
        setExecutionEnded("Finished Successfully!");
    }

    private void setExecutionEnded(String status) {
        setExecutionStatus(status);
        execTime.setEnd();
    }

    private void setExecutionStatus(String status) {
        executionStatus = status;
        Page.get(this).syncParameterFromModel(EXECUTION_STATUS_PARAM);
    }

    public void onExecutionEndedWithWrongResult(Execution execution, Assignment wrong, Assignment right) {
        setExecutionEnded("Finished with wrong results");
    }

    public void onExecutionCrushed(Execution ex, Exception exc) {
        setExecutionEnded("Crushed because of " + exc.getClass().getSimpleName());
    }

    @Override
    public void onExpirementStarted(Experiment source) {
        setExecutionStatus("Running...");
        execTime.setStart();
    }

    @Override
    public void onExpirementEnded(Experiment source) {
        execTime.setEnd();

        ExperimentResult result = source.getResult();
        if (result.succeded) {
            onExpirementEndedSuccessfully();
        } else if (result.badRoundResult.finishStatus == Round.FinishStatus.WRONG_RESULT) {
            onExecutionEndedWithWrongResult(result.badRoundResult.badExecution, result.badRoundResult.badExecution.getResult().getAssignment(), result.badRoundResult.goodAssignment);
        } else {
            onExecutionCrushed(result.badRoundResult.badExecution, result.badRoundResult.crushReason);
        }
    }

    @Override
    public void onNewRoundStarted(Experiment source, Round round) {
        currentRound = round;
        problemNumber = 0;
        Page.get(this).syncParameterFromModel(CURRENT_ROUND_PARAM);
    }

    @Override
    public void onNewExecutionStarted(Experiment source, Round round, Execution exec) {
        problemNumber++;
        setExecutionStatus("Solving the " + problemNumber + "/" + currentRound.getLength() + " problem on this round...");
        syncToView(TESTED_ALGORITHM_PARAM);
    }

    @Override
    public void onExecutionEnded(Experiment source, Round round, Execution exec) {
    }
}
