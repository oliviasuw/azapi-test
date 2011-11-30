/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.infra;

import bgu.dcr.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
public interface CorrectnessTester extends Configureable{
    
    TestResult test(Execution exec, ExecutionResult result);
    
    public static class TestResult{
        public final Assignment rightAnswer;
        public final boolean passed;

        public TestResult(Assignment rightAnswer, boolean passed) {
            this.rightAnswer = rightAnswer;
            this.passed = passed;
        }
    }
}
