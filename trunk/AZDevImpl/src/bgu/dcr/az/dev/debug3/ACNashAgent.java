package bgu.dcr.az.dev.debug3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;


import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.Assignment;

/**
 * 
 * @author alongrub
 *
 * AC Table passing "phase" algorithm
 * NOTE: I assume that all domain sizes are equal!
 */

@Algorithm(name="AC-Nash", useIdleDetector=true)
public class ACNashAgent extends SimpleAgent {

	private HashMap<Integer, int[][]> acTables = null;
	
	private KArySatConstraint myConstraint = null;
	private HashSet<Integer> pStarDomain = null;
	

	@Override
    public void start() {
    	myConstraint = new KArySatConstraint(getProblem(), getId(), false);
    	int d = getDomainSize();
    	
    	acTables = new HashMap<Integer, int[][]>();
    	for (int n : getNeighbors()){
    		int[][] initAC = new int[d][d];
    		for (int i=0; i<d; i++)
    			for (int j=0; j<d; j++)
    				initAC[i][j]=1;
    		acTables.put(n, initAC);
    		send("Table", (Object) initAC).to(n);
    	}
    	
    }

	/**------------ AUX ------------**/
    
    private void reduceDomain(){
    	pStarDomain = new HashSet<Integer>(getDomain());
    	int[] ordering  = new int[getNeighbors().size()];
    	
    	int na=0;
    	for (int n : getNeighbors()){
    		ordering[na]=n;
    		na++;
    	}
    	
    	/* remove all values which are not supported by any assignment of a neighbor */
    	for (int d = 0; d<getDomainSize(); d++){
    		boolean remove = true;
    		ArrayList<HashSet<Integer>> validDomains = new ArrayList<HashSet<Integer>>();
    		
    		for (int n : getNeighbors()){
    			remove = true;
    			for (int i=0; i<getDomainSize(); i++)
    				if (acTables.get(n)[d][i]==1)
    					remove = false;
    			if (remove){
    				pStarDomain.remove(new Integer(d));
    				break;
        		}
    		}
    		if (remove)
    			continue;
    		
    		/* collect all neighbors possible domain */
    		remove = true;
    		for (int n : getNeighbors()){
    			HashSet<Integer> dom = new HashSet<Integer>();
    			for (int i=0; i<getDomainSize(); i++)
    				if (acTables.get(n)[d][i]==1)
    					dom.add(i);
    			validDomains.add(dom);
    		}
    		
    		/* check if the current value (d) is a BR to at least one joint
    		 * valid assignment  */
    		Set<List<Integer>> cart = Sets.cartesianProduct(validDomains);
    		for (List<Integer> profile : cart){
    			Assignment a = new Assignment();
    			int i=0;
    			for (int n : getNeighbors()){
    				a.assign(n, profile.get(i));
    				i++;
    			}
    			if (myConstraint.isConsistentWith(a, d)){
    				remove = false;
    				break;
    			}
    		}
    		if (remove)
    			pStarDomain.remove(d);
    		
    		
    	}
    }

    public HashSet<Integer> getpStarDomain() {
		return pStarDomain;
	}
    
    
    @Override
    public void onIdleDetected() {
    	System.out.println("AGENT "+getId()+":");
    	for (int n : acTables.keySet()){
    		System.out.println("with "+n+": \t\n"+Arrays.deepToString(acTables.get(n)));
    	}
    	reduceDomain();
    	//System.out.println("pStarDomain= "+pStarDomain.toString());
    	finish();
    }
    
    
    /**------------ Message handling ------------**/
    
    
	@WhenReceived("Table")
	public void handleTable(int[][] actab){
		int[][] tr = new int[actab.length][actab[0].length];
		for (int i=0; i<tr.length; i++)
			for (int j=0; j<tr[0].length; j++)
				tr[i][j] = actab[j][i];
		acTables.put(getCurrentMessage().getSender(), tr);
		
		for (int n : getNeighbors()){
    		int[][] tab = myConstraint.acTable(n, acTables);
    		if (!Arrays.deepEquals(tab, acTables.get(n))){
    			send("Table", (Object) tab).to(n);
    			acTables.put(n, tab);
    		}
    	}
		
	}

    
}
