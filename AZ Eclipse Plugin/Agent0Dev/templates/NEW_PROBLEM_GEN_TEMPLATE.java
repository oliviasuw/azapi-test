package ext.sim.modules;

import java.util.Random;

import bgu.dcr.az.api.Problem;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.exen.pgen.AbstractProblemGenerator;

@Register(name = "${MODULE_NAME}")
public class ${MODULE_NAME_CC} extends AbstractProblemGenerator {

    //ADD ANY VARIABLES YOU NEED HERE LIKE THIS:
	//@Variable(name = "n", description = "number of variables", defaultValue = "2")
    //int n = 2;
    
    @Override
    public void generate(Problem p, Random rand) {
    	//FIRST INITIALIZE THE PROBLEM LIKE THIS:
    	//p.initialize(ProblemType.DCSP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        
    	//THEN CREATE CONSTRAINTS LIKE THIS:
    	//p.setConstraintCost(i, vi, j, vj, cost);
    	
    	//DONT USE YOUR OWN RANDOM GENERATOR - USER rand INSTEAD
    	//THAT WAY YOUR PROBLEM GENERATOR WILL BE ABLE TO REPRODUCE 
    	//A PROBLEM IF IT IS REQUESTED TO DO SO
    }
}
