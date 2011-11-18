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
public interface Expirement extends Configureable, Process{
    void addRound(Round round);
    List<Round> getRounds();
    ExpirementResult getResult();
    
    public static class ExpirementResult{
        public final boolean succeded;
        public final Round problematicRound;
        public final Round.RoundResult badRoundResult;
        public final boolean interupted;

        /**
         * constract successfull/interupted result
         */
        public ExpirementResult(boolean interupted) {
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
        public ExpirementResult(Round problematicRound, Round.RoundResult badRoundResult) {
            this.succeded = false;
            this.problematicRound = problematicRound;
            this.badRoundResult = badRoundResult;
            this.interupted = false;
        }
        
        
    }
}
