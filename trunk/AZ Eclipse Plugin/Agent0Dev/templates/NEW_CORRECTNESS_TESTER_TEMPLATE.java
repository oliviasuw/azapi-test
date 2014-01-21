package ext.sim.modules;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.exen.correctness.AbstractCorrectnessTester;

@Register(name="${MODULE_NAME}")
public class ${MODULE_NAME_CC} extends AbstractCorrectnessTester {

    @Override
	public CorrectnessTestResult test(Execution exec, ExecutionResult result) {
        Assignment ass = result.getAssignment();
        final Problem globalProblem = exec.getGlobalProblem();
        
        //TEST THE RESULT AN RETURN TESTED RESULT OBJECT THAT REPRESENT THE RESOULT CORRECTNESS
        return new CorrectnessTestResult(null, true); //<-- PASS
  	}
}
