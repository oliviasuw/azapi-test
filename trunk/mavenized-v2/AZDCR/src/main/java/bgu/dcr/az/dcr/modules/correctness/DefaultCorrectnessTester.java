/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dcr.modules.correctness;

import bgu.dcr.az.execs.exps.exe.SimulationResult;
import bgu.dcr.az.conf.registery.Register;
import bgu.dcr.az.execs.sim.Agt0DSL;
import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.api.experiment.CPData;
import bgu.dcr.az.dcr.api.modules.CPCorrectnessTester;
import bgu.dcr.az.dcr.api.problems.Problem;
import static bgu.dcr.az.dcr.api.problems.ProblemType.ADCOP;
import static bgu.dcr.az.dcr.api.problems.ProblemType.DCOP;
import static bgu.dcr.az.dcr.api.problems.ProblemType.DCSP;
import bgu.dcr.az.dcr.api.experiment.CPSolution;
import bgu.dcr.az.dcr.modules.correctness.IterativeCSPSolver.Status;
import static bgu.dcr.az.dcr.modules.correctness.IterativeCSPSolver.Status.imposible;
import static bgu.dcr.az.dcr.modules.correctness.IterativeCSPSolver.Status.solution;
import bgu.dcr.az.execs.exps.exe.Simulation;

/**
 *
 * @author bennyl
 */
@Register("default-tester")
public class DefaultCorrectnessTester extends CPCorrectnessTester {

    @Override
    public void test(Simulation<CPData, CPSolution> exec, SimulationResult<CPSolution> result) {
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
