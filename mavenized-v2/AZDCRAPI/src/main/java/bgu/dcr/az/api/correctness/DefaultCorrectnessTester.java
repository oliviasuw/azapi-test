/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.correctness;

import bgu.dcr.az.anop.reg.Register;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.prob.Problem;
import static bgu.dcr.az.api.prob.ProblemType.ADCOP;
import static bgu.dcr.az.api.prob.ProblemType.DCOP;
import static bgu.dcr.az.api.prob.ProblemType.DCSP;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.api.correctness.IterativeCSPSolver.Status;
import bgu.dcr.az.mas.cp.CPExecution;
import bgu.dcr.az.mas.cp.CPCorrectnessTester;
import bgu.dcr.az.mas.cp.CPSolution;

/**
 *
 * @author bennyl
 */
@Register("default-tester")
public class DefaultCorrectnessTester extends CPCorrectnessTester {

    @Override
    public void test(CPExecution exec, ExecutionResult<CPSolution> result) {
        Assignment ass;
        final Problem globalProblem = exec.data().getProblem();
        Status stat;
        final MACSolver solver = new MACSolver();
        switch (exec.data().getProblem().type()) {
            case ADCOP:
            case DCOP:
                ass = BranchAndBound.solve(globalProblem);
                if (ass.calcCost(globalProblem) != result.getSolution().getCost()) {
                    result.toWrongState(new CPSolution(globalProblem, ass));
                }
                break;
            case DCSP:
                stat = solver.solve(globalProblem);
                switch (stat) {
                    case imposible:
                        if (result.getSolution().getState() != CPSolution.State.NO_SOLUTION) {
                            result.toWrongState(CPSolution.newNoSolution(globalProblem));
                        }
                        break;
                    case solution:
                        ass = solver.getAssignment();

                        if (result.getSolution().getState() != CPSolution.State.SOLUTION || result.getSolution().getCost() != 0) {
                            result.toWrongState(new CPSolution(globalProblem, ass));
                        }
                        break;
                    default:
                        Agt0DSL.panic("Solution tester cannot check the given problem");
                }

                break;
            default:
                Agt0DSL.panic("Solution tester cannot check the given problem");
        }
    }
}
