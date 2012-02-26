/**
 * 
 */
package bgu.dcr.az.dev.debug3;

import java.util.Random;

import bgu.dcr.az.api.pgen.Problem;

/**
 * @author alongrub
 *
 * This class will generate at least one Pure Strategy Nash Equilibrium from 
 * a given problem instance 
 * 
 * Assumption:
 * 1. Domain size is equal to all agents
 * 2. 
 */
public class PSNEGenerator {
	
	
	public static Problem generatePSNE(Problem p, Random rand, int maxCost){
		int n = p.getNumberOfVariables();
		int d = p.getDomain().size();
		
		int[] psne = new int[n];
		
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
		return p;
	}
    
	private static int[] solToGains(int[] fullAssignment, Problem p){
		int[] res = new int[fullAssignment.length];
		
		for (int i=0; i< res.length; i++)
			for (int neighbor : p.getNeighbors(i)){
				res[i] += p.getConstraintCost(i, fullAssignment[i], neighbor, fullAssignment[neighbor]);
			}
		return res;
	}
}
