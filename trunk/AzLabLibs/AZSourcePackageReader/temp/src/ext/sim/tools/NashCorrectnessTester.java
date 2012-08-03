/**
 * 
 */
package ext.sim.tools;

import java.util.HashSet;

import bgu.dcr.az.api.Problem;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.exen.mdef.CorrectnessTester.CorrectnessTestResult;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.exen.correctness.AbstractCorrectnessTester;

/**
 * @author alongrub
 *
 */
@Register(name="equilibrium-tester")
public class NashCorrectnessTester extends AbstractCorrectnessTester {


	/* (non-Javadoc)
	 * @see bgu.dcr.az.api.infra.CorrectnessTester#test(bgu.dcr.az.api.infra.Execution, bgu.dcr.az.api.infra.ExecutionResult)
	 */
	@Override
	public CorrectnessTestResult test(Execution exec, ExecutionResult result) {
		Problem p = exec.getGlobalProblem();
		Assignment res = result.getAssignment();
		int n = p.getNumberOfVariables();
		
		for (int i=0; i<n; i++){
			HashSet<Integer> bestAssignments = new HashSet<>();
			int bestCost = Integer.MAX_VALUE;
			for (int di=0; di< p.getDomainSize(i); di++){
				int diCost = p.getConstraintCost(i, di, res);
				if (diCost == bestCost)
					bestAssignments.add(di);
				else if (diCost < bestCost){
					bestAssignments.clear();
					bestAssignments.add(di);
					bestCost = diCost;
				}
			}
			if (!bestAssignments.contains(res.getAssignment(i)))
				return new CorrectnessTestResult(null, false); 
		}
		return new CorrectnessTestResult(null, true);
	}

}
