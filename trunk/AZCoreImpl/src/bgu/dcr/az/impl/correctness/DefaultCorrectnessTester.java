/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.impl.correctness;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.infra.Execution;
import bgu.dcr.az.api.infra.ExecutionResult;
import bgu.dcr.az.api.pgen.Problem;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.impl.correctness.IterativeCSPSolver.Status;

/**
 *
 * @author bennyl
 */
@Register(name="default-tester", display="Default Correctness Tester")
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
