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
@Register(name="default-tester")
public class DefaultCorrectnessTester extends AbstractCorrectnessTester {

    @Override
    public TestedResult test(Execution exec, ExecutionResult result) {
        Assignment ass;
        final Problem globalProblem = exec.getGlobalProblem();
        Status stat;
        final MACSolver solver = new MACSolver();
        switch (exec.getGlobalProblem().type()) {
            case ADCOP:
                return new TestedResult(null, true);
            case DCOP:
                ass = BranchAndBound.solve(globalProblem);
                if (ass.calcCost(globalProblem) == result.getAssignment().calcCost(globalProblem)) {
                    return new TestedResult(ass, true);
                } else {
                    return new TestedResult(ass, false);
                }
            case DCSP:
                stat = solver.solve(globalProblem);
                switch (stat) {
                    case imposible:
                        if (result.hasSolution()) {
                            return new TestedResult(null, false);
                        } else {
                            return new TestedResult(null, true);
                        }
                    case solution:
                        ass = solver.getAssignment();
                        if (result.getAssignment() != null && ass.calcCost(globalProblem) == result.getAssignment().calcCost(globalProblem)) {
                            return new TestedResult(ass, true);
                        } else {
                            return new TestedResult(ass, false);
                        }
                    default:
                        return new TestedResult(null, true);
                }
            default:
                return null;
        }
    }
}
