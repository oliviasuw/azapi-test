package bgu.dcr.az.dev.debug3;

import java.util.HashSet;
import java.util.TreeSet;

import bgu.dcr.az.api.ImmutableProblem;
import bgu.dcr.az.api.tools.Assignment;

/**
 * @author alongrub 
 * A class for building a k-ary satisfaction constraint
 * Note: I assume that all agents share the same domain size
 */
public class KArySatConstraint {
	
	/* The main data structure used is the multi-dim regular "array". 
	 * The array is arranged so that the agent "owning" this DS is 
	 * accessed via the first dimension
	 **/
	private MultiDimRegularArray<Integer> 	consData = null;
	
	private int arity = 0;
	private int domSize = 0;
	private TreeSet<Integer> neighbors = null;
	
	private final int 						CONS_ID;
	private ImmutableProblem 				problem = null;
	
	private boolean 						persistent = true;		// This can be used to speed access but requires more memory
	
	public KArySatConstraint(ImmutableProblem p, int agentId, boolean inMem) {
		neighbors = new TreeSet<>(p.getNeighbors(agentId));
		arity = neighbors.size()+1;
		domSize = p.getDomainSize(agentId);
		CONS_ID = agentId;
		problem = p;
		persistent = inMem;
		if (arity>1 && persistent)
			consData = buildPersonalConstraint();
	}

	/**
	 * Builds a new k-ary satisfaction constraint Note: pre-computing this
	 * matrix is a heavy operation! \Omega(d^n)
	 * Note: index array = [my_assignment, x_i1, x_i2,...]
	 * @return
	 */
	private MultiDimRegularArray<Integer> buildPersonalConstraint() {
		MultiDimRegularArray<Integer> res;
		res = new MultiDimRegularArray<Integer>(arity, domSize);
		int[] jointNeighborsAssignment = new int[arity-1];
		while (jointNeighborsAssignment != null) {
			int[] index = new int[arity];
			System.arraycopy(jointNeighborsAssignment, 0, index, 1, arity-1);
			// now we find out which assignment is a best-response assignment
			HashSet<Integer> br = findBR(jointNeighborsAssignment);
			for (int i = 0; i < domSize; i++) {
				index[0] = i;
				if (br.contains(i))
					res.setValueAt(index, 1);
				else
					res.setValueAt(index, 0);
			}
			jointNeighborsAssignment = inc(jointNeighborsAssignment);
		}
		return res;
	}

	/**
	 * Increment the array, assuming each cell can take exactly d different
	 * values (d= domain size)
	 * 
	 * @param arr
	 * @return
	 */
	private int[] inc(int[] arr) {
		int i = 0;
		boolean cont = true;
		while (cont && i < arr.length) {
			if (arr[i] == domSize - 1)
				arr[i] = 0;
			else {
				cont = false;
				arr[i]++;
			}
			i++;
		}
		if (i == arr.length && arr[i-1]==0)
			return null;
		return arr;
	}

	/**
	 * Finds the set of "Best Responses" to each assignment combination
	 * 
	 * @param neighborsAssignment
	 * @return
	 */
	private HashSet<Integer> findBR(int[] neighborsAssignment) {
		HashSet<Integer> res = null;
		double bestCost = Integer.MAX_VALUE;
		/* First we build an assignment to calculate cost */
		Assignment a = new Assignment();
		int na = 0;
		for (int n : neighbors) {
			a.assign(n, neighborsAssignment[na]);
			na++;
		}

		/*
		 * Next, we find the minimal cost assignments which are the best
		 * response to a
		 */
		for (int i=0; i< domSize; i++) {
			double tmpCost = a.calcAddedCost(CONS_ID, i, problem);
			if (tmpCost == bestCost) {
				res.add(i);
			}
			if (tmpCost < bestCost) {
				res = new HashSet<>();
				res.add(i);
				bestCost = tmpCost;
			}
		}
		return res;
	}
	
	/**
	 * Check an existing agent view against a domain value
	 * @param av
	 * @param domainValue
	 * @return true if not all agents in the constraints have values or if the assignment is consistent
	 * and false otherwise
	 */
	public boolean isConsistentWith(Assignment av, int domainValue){		
		int[] index = new int[arity];
		int na=1;
		for (int n : neighbors) {
			/* if not all relevant neighbors added their assignment the answer to
			 * this consistency test should be true
			*/
			if (!av.isAssigned(n))
				return true;
			
			index[na] = av.getAssignment(n);
			na++;
		}
		index[0] = domainValue;
		if (!persistent){
			int[] jointNeighborsAssignment = new int[arity-1];
			System.arraycopy(index, 1, jointNeighborsAssignment, 0, arity-1);
			HashSet<Integer> br = findBR(jointNeighborsAssignment);
			if (br.contains(new Integer(domainValue)))
				return true;
			else
				return false;
		}
		
		/**
		 * If persistent we can just do a check on the cache. Unfortunately, no way
		 * of increase cc count so we do an empty lookup...
		 */
		av.calcAddedCost(CONS_ID, 0, problem);
		return consData.getValueOf(index)==1; 
	}
	
	public String toString(){
		String s="CONS_ID "+CONS_ID;
		if (consData == null)
			return s+="\tNULL!";
		s+= ": ("+consData.getSize()+")\n";
		int[] index = new int[arity];
		while (index != null) {		
			s += "\t";
			for (int j = 0; j < arity; j++)
				s += " [" + index[j] + "] ";
			s += "==> " + consData.getValueOf(index) + "\n";
			index = inc(index);
		}
		return s;
	}
}
