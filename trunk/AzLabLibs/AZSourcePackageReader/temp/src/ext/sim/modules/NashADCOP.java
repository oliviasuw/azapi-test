package ext.sim.modules;

import java.util.Random;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.Problem;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.exen.pgen.AbstractProblemGenerator;

@Register(name = "nash-adcop-unstructured")
public class NashADCOP extends AbstractProblemGenerator{

	private int[] psne=null;			// There may be more than just this PSNE....
	
	@Variable(name = "n", description = "number of variables", defaultValue="2")
    int n = 2;
    @Variable(name = "d", description = "domain size", defaultValue="2")
    int d = 2;
    @Variable(name = "max-cost", description = "maximal cost of constraint", defaultValue="10")
    int maxCost = 10;
    @Variable(name = "p1", description = "probablity of constraint between two variables", defaultValue="0.6f")
    float p1 = 0.6f;
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generating : ").append("n = ").append(n).append("\nd = ").append(d).append("\nmaxCost = ").append(maxCost);
        /*String s = "PSNE=";
        for (int i=0; i<n; i++)
        	s+= psne[i]+", ";
        s+="\n";
        sb.append(s);*/
        return sb.toString();
    }
    
    @Override
    public void generate(Problem p, Random rand) {
    	p.initialize(ProblemType.ADCOP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        psne = new int[n];
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = i+1; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, true, rand);
                }
            }
        }
        generatePSNE(p, rand);
        System.out.println("---- PSNE ------");
        for (int i=0; i<n; i++)
        	System.out.print(psne[i]+", ");
        System.out.println();
    }
    
    protected void buildConstraint(int i, int j, Problem p, boolean sym, Random rand) {
        for (int vi = 0; vi < p.getDomain().size(); vi++) {
            for (int vj = 0; vj < p.getDomain().size(); vj++) {
                final int cost1 = rand.nextInt(maxCost+ 1);
                final int cost2 = rand.nextInt(maxCost+ 1);
                p.setConstraintCost(i, vi, j, vj, cost1);
                p.setConstraintCost(j, vj, i, vi, cost2);
            }
        }
    }
    

	private void generatePSNE(Problem p, Random rand){
		for (int i=0; i< n; i++)
			psne[i] = rand.nextInt(d);
		
		// First, get the costs of each agent in the would be PSNE
		int[] psneCosts = solToGains(psne, p);
				
		// Next, go over each agent and see how to perturb all of its other actions  
		for (int i=0; i< n; i++){
			Integer[] neighbors = p.getNeighbors(i).toArray(new Integer[p.getNeighbors(i).size()]);
			if (neighbors.length == 0)
				continue;
			// if we come to an agent whose cost is maximal (can't make sure its better than other assignments)
			// we restart the process
			
			if (psneCosts[i]==(maxCost)*neighbors.length){
				int vi = rand.nextInt(neighbors.length);
				int updatedConsCost = (int) (p.getConstraintCost(i, psne[i], neighbors[vi], psne[neighbors[vi]])-1);
				p.setConstraintCost(i, psne[i], neighbors[vi], psne[neighbors[vi]], updatedConsCost);
				
				psne[i] = rand.nextInt(d);
				psneCosts = solToGains(psne, p);
				i=-1;
				continue;
			}
			// save psne assignment
			int i_psne = psne[i];
			for (int dom=0; dom<d; dom++)
				if (dom != i_psne){
					psne[i] = dom;
					int[] testCosts = solToGains(psne, p);
					int dCost = testCosts[i];
										
					if (psneCosts[i] > dCost){
						// this assignment (with dom) is "better" so we
						// must make sure its cost increases
						
						int inc = 0;
						while(psneCosts[i]-dCost-inc >-1){
							// get the list of neighbors, and choose randomly an agent with increased costs
							int vi = rand.nextInt(neighbors.length);
							int viCostInc = rand.nextInt(maxCost - (int) (p.getConstraintCost(i, dom, neighbors[vi], psne[neighbors[vi]])-1));
							inc +=viCostInc;
							int updatedConsCost = viCostInc + (int) (p.getConstraintCost(i, dom, neighbors[vi], psne[neighbors[vi]]));
							p.setConstraintCost(i, dom, neighbors[vi], psne[neighbors[vi]], updatedConsCost);
							
						}
					}
				}
			
			psne[i] = i_psne;
		}
	}
    
	private int[] solToGains(int[] fullAssignment, Problem p){
		int[] res = new int[n];
		
		for (int i=0; i< n; i++)
			for (int neighbor : p.getNeighbors(i)){
				res[i] += p.getConstraintCost(i, fullAssignment[i], neighbor, fullAssignment[neighbor]);
			}
		return res;
	}
	
	
}
