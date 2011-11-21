/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.infra;

import bgu.csp.az.api.ano.Register;
import bgu.csp.az.api.infra.Execution;
import bgu.csp.az.api.infra.ExecutionResult;
import bgu.csp.az.api.pgen.Problem;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.impl.correctness.BranchAndBound;
import bgu.csp.az.impl.correctness.IterativeCSPSolver.Status;
import bgu.csp.az.impl.correctness.MACSolver;

/**
 *
 * @author bennyl
 */
@Register(name="default-tester")
public class DefaultCorrectnessTester extends AbstractCorrectnessTester {

    @Override
    public TestResult test(Execution exec, ExecutionResult result) {
        Assignment ass;
        final Problem globalProblem = exec.getGlobalProblem();
        Status stat;
        final MACSolver solver = new MACSolver();
        switch (exec.getGlobalProblem().type()) {
            case ADCOP:
                return new TestResult(null, true);
            case DCOP:
                ass = BranchAndBound.solve(globalProblem);
                if (ass.calcCost(globalProblem) == result.getAssignment().calcCost(globalProblem)) {
                    return new TestResult(ass, true);
                } else {
                    return new TestResult(ass, false);
                }
            case DCSP:
                stat = solver.solve(globalProblem);
                switch (stat) {
                    case imposible:
                        if (result.hasSolution()) {
                            return new TestResult(null, false);
                        } else {
                            return new TestResult(null, true);
                        }
                    case solution:
                        ass = solver.getAssignment();
                        if (ass.calcCost(globalProblem) == result.getAssignment().calcCost(globalProblem)) {
                            return new TestResult(ass, true);
                        } else {
                            return new TestResult(ass, false);
                        }
                    default:
                        return new TestResult(null, true);
                }
            default:
                return null;
        }
    }
}
