package ext.sim.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import bgu.dcr.az.api.Agent;

public class Explanations implements Iterable<Explanation> {
	private Map<Integer, Explanation>explanations;
	private Integer agentID;
	
	public Explanations(Agent agent) {
		agentID = agent.getId();
		
		explanations = new HashMap<Integer, Explanation>();
		
		for(Integer val : agent.getDomain()) {
			explanations.put(val, new Explanation(agent.getId(), val));
		}		
	}
	
	public int size() {
		return explanations.size();
	}
	
	public Explanation getExplanation(Integer val) {
		return explanations.get(val);
	}	
	
	public Iterable<Integer> getDomain() {
		return explanations.keySet();
	}
	
	public Collection<Integer> getNonEliminatedValues() {
		HashSet<Integer> values = new HashSet<Integer>();
		
		for(Integer val : getDomain()) {
			if (getExplanation(val).isEmpty()) {
				values.add(val);
			}
		}
		
		return values;
	}
		
	public boolean isConsistent(Integer val) {
		return getExplanation(val).isEmpty();
	}
	
	public Explanation generateNoGood() {
		return new Explanation(this);
	}

	@Override
	public Iterator<Explanation> iterator() {
		return explanations.values().iterator();
	}
	
	@Override
	public String toString() {
		String result = "Explanations of agent: " + agentID + "\n";
		
		for(Explanation expl : this) {
			result += expl.getEliminatedValue() + ": " + expl.toString() + "\n";
		}
		
		return result;
	}
		
}