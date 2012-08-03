/**
 * 
 */
package ext.sim.tools;

import java.util.ArrayList;
import java.util.HashSet;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.tools.Assignment;

/**
 * @author alongrub
 *
 */
public class NashPropCPA {

	private Assignment cpa = null;
	ArrayList<Integer> order = null;
	
	private final int N;
	
	public NashPropCPA(int numOfAgents) {
		N = numOfAgents;
		cpa = new Assignment();
		order = new ArrayList<>(N);
	}
	
	public void assign(int agent, int val){
		cpa.assign(agent, val);
		if (!order.contains(new Integer(agent)))
			order.add(agent);
	}
	
	public void add(Assignment av){
		for (int agent : av.assignedVariables()){
			cpa.assign(agent, av.getAssignment(agent));
			if (!order.contains(new Integer(agent)))
				order.add(agent);
			
		}
	}
	
	public int unassign(int agentId){
		int prev = order.indexOf(new Integer(agentId))-1;
		for (int i= prev+1; i<order.size(); i++)
			cpa.unassign(order.get(i));
		//cpa.unassign(agentId);
		//order.remove(new Integer(agentId));
		if (prev==-1)
			return prev;
		return order.get(prev);
	}
	
	public int last(){
		return order.get(order.size()-1);
	}
	
	public boolean isLast(Integer agentId){
		if (order.size()==N)
			return order.get(order.size()-1).equals(agentId);
		return false;
	}
	
	public Assignment getAssignments(){
		return cpa;
	}
	
	public Integer getAssignmentOf(int agentId){
		if (contains(agentId))
			return cpa.getAssignment(agentId);
		return null;
	}
	
	public boolean contains(int agentId){
		return cpa.isAssigned(agentId);
	}
	
	public void updateOrder(HashSet<Integer> agentNeighbors){
		for (Integer i : agentNeighbors){
			if (!order.contains(i))
				order.add(i);
		}
	}
	
	public boolean isFull(){
		return cpa.getNumberOfAssignedVariables()==N;
	}
	
	public int getNext(int agent){
		Agt0DSL.panicIf(!order.contains(new Integer(agent)), "Attempting to get the agent after "+agent+", but agent "+agent+" is not in CPA");
		int i = order.indexOf(new Integer(agent));
		
		if (i == N-1)
			return -1;
		
		if (i == order.size()-1){
			for (int j=0; j<N; j++)
				if (!order.contains(new Integer(j))){
					order.add(j);
					return j;
				}
		}
		
		return order.get(i+1);
	}
	
	
	public int getPrev(int agent){
		Agt0DSL.panicIf(!order.contains(new Integer(agent)), "Attempting to get the agent before "+agent+", but agent "+agent+" is not in CPA");
		int i = order.indexOf(new Integer(agent));
		
		if (i == 0)
			return -1;
		
		return order.get(i-1);
	}
	
	@Override
	public String toString() {
		return "CPA: \n\tassignments = "+cpa.toString()+"\n\torder = "+order.toString();
	}
}
