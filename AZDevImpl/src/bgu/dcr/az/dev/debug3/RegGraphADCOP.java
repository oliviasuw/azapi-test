package bgu.dcr.az.dev.debug3;

import java.util.ArrayList;
import java.util.Random;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.Problem;
import bgu.dcr.az.exen.pgen.AbstractProblemGenerator;

/**
 * Regular graph ADCOP generator	
 * @author alongrub
 *
 * NOTE: Not necessarily connected! 
 */

@Register(name = "adcop-regular-nash-graph")
public class RegGraphADCOP extends AbstractProblemGenerator {

	@Variable(name = "n", description = "number of variables", defaultValue="2")
    int n = 2;
    @Variable(name = "d", description = "domain size", defaultValue="2")
    int d = 2;
    @Variable(name = "max-cost", description = "maximal cost of constraint", defaultValue="10")
    int maxCost = 10;
    @Variable(name = "deg", description = "degree of all agents", defaultValue="2")
    int deg = 2; 			// deg must by at least 2!
    
    @Override
    public void generate(Problem p, Random rand) {
    	System.out.println("------------ BUILDING K-REG PROBLEM ---------");
    	p.initialize(ProblemType.ADCOP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
    	if (n<=deg)
    		throw new IllegalArgumentException("ERROR: The degree value is equal or larger than the number of agents!");
    	
    	int[] edgesToConnect = new int[n];
    	ArrayList<Integer> v = new ArrayList<Integer>();
		for (int i=0; i<n; i++){
			edgesToConnect[i]=deg;
			v.add(i);
		}
		
		for (Integer i=0; i<n; i++){
			v.remove(i);
			System.out.println("Constraints for "+i+" -->"+edgesToConnect[i]+", v.size="+v.size());
			for (int j=edgesToConnect[i]; j>0; j--){
				boolean cont = false;
				for (Integer vx : v)
					if (!p.isConstrained(i, vx))
						cont=true;
				
				if (!cont)
					continue;
				
				Integer v2 = v.get(rand.nextInt(v.size()));
				if (p.isConstrained(i, v2)){
					j++;
					continue;
				}
				System.out.println("\tBuilding "+i+"<-->"+v2);
				buildConstraint(i, v2, p, rand);
				edgesToConnect[i]--;
	    		edgesToConnect[v2]--;
	    		if (edgesToConnect[v2]==0)
	    			v.remove(v2);
			}
			
				
		}
		for (int i=0; i<n; i++){
			System.out.print(" "+edgesToConnect[i]+" ");
		}
    	/*while (v.size()>2){
    		Integer v1 = v.get(rand.nextInt(v.size()));
    		v.remove(v1);
    		Integer v2 = v.get(rand.nextInt(v.size()));
    		v.remove(v2);
    		
    		buildConstraint(v1, v2, p, rand);
    		
    		edgesToConnect[v1]--;
    		edgesToConnect[v2]--;
    		
    		if (edgesToConnect[v1]>0)
    			v.add(v1);
    		if (edgesToConnect[v2]>0)
    			v.add(v2);
    	}
    	if (!v.isEmpty()){
    		System.err.println("NOTE THAT PROBLEM IS NOT REALLY REGULAR... :)");
    	}*/
    	PSNEGenerator.generatePSNE(p, rand, maxCost);
    }
    
    
    protected void buildConstraint(int i, int j, Problem p, Random rand) {
        for (int vi = 0; vi < p.getDomain().size(); vi++) {
            for (int vj = 0; vj < p.getDomain().size(); vj++) {
                final int cost1 = rand.nextInt(maxCost);
                final int cost2 = rand.nextInt(maxCost);
                p.setConstraintCost(i, vi, j, vj, cost1);
                p.setConstraintCost(j, vj, i, vi, cost2);
                
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generating : ").append("n = ").append(n).append("\nd = ").append(d).append("\nmaxCost = ").append(maxCost);
        return sb.toString();
    }
}
