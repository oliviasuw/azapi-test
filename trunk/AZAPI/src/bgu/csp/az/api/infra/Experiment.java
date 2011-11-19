/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.api.infra;

import java.util.List;

/**
 * expirement is executeable collection of predefined rounds 
 * the expirement responsible to configure its sub executions 
 * based on its loaded rounds and analyzed their statistics via the statistics analayzers defined in the round
 * @author bennyl
 */
public interface Experiment extends Configureable, Process {

    void addRound(Round round);

    List<Round> getRounds();

    ExperimentResult getResult();

    void addListener(ExperimentListener l);

    void removeListener(ExperimentListener l);

    public static class ExperimentResult {

        public final boolean succeded;
        public final Round problematicRound;
        public final Round.RoundResult badRoundResult;
        public final boolean interupted;

        /**
         * constract successfull/interupted result
         */
        public ExperimentResult(boolean interupted) {
            this.succeded = !interupted;
            this.problematicRound = null;
            this.badRoundResult = null;
            this.interupted = interupted;
        }

        /**
         * constract faild result
         * @param problematicRound
         * @param badRoundResult 
         */
        public ExperimentResult(Round problematicRound, Round.RoundResult badRoundResult) {
            this.succeded = false;
            this.problematicRound = problematicRound;
            this.badRoundResult = badRoundResult;
            this.interupted = false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Expirement");
            if (succeded) {
                sb.append(" succeded");
            } else if (!interupted) {
                sb.append(" failed: ");
                sb.append(badRoundResult.toString());
            } else {
                sb.append("interrupted");
            }
            return sb.toString();
        }
    }

    public static interface ExperimentListener {

        void onExpirementStarted(Experiment source);

        void onExpirementEnded(Experiment source);

        void onNewRoundStarted(Experiment source, Round round);

        void onNewExecutionStarted(Experiment source, Round round, Execution exec);

        void onExecutionEnded(Experiment source, Round round, Execution exec);
    }
}
