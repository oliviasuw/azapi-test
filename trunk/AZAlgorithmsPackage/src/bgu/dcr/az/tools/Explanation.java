package bgu.dcr.az.tools;

import bgu.dcr.az.api.tools.Assignment;

public class Explanation {
	private boolean isEmpty;
	private Assignment explanation;
	
	private Integer eliminatedVariable;
	private Integer eliminatedValue;
	
	public Explanation(Integer var, Integer val) {
		isEmpty = true;
		
		eliminatedVariable = var;
		eliminatedValue = val;
		
		explanation = new Assignment();
	}
	
	public Explanation(Explanations expls) {
		explanation = new Assignment();
		
		for(Explanation expl : expls) {
			for(Integer var : expl.getExplanationVariables()) {
				explanation.assign(var, expl.getExplanationValue(var));
			}
		}
		
		eliminatedVariable = null;
		eliminatedVariable = getLowestPriorityVariable();
		
		if (eliminatedVariable != null) {
			isEmpty = false;
			
			eliminatedValue = explanation.getAssignment(eliminatedVariable);
			explanation.unassign(eliminatedVariable);
		} else {
			isEmpty = true;
			
			eliminatedValue = null;
		}
	}
	
	public Integer getEliminatedVariable() {
		return eliminatedVariable;
	}
	
	public Integer getEliminatedValue() {
		return eliminatedValue;
	}
	
	public void setExplanation(Integer var, Integer val) {
		clear();
		
		isEmpty = false;
		
		explanation.assign(var, val);
	}
	
	public void setExplanation(Explanation expl) {
		clear();
		
		isEmpty = expl.isEmpty;
		
		for(Integer var : expl.getExplanationVariables()) {
			explanation.assign(var, expl.getExplanationValue(var));
		}		
	}
	
	public void setExplanation(Assignment cpa) {
		clear();
		
		isEmpty = false;
		
		for(Integer var : cpa.assignedVariables()) {
			explanation.assign(var, cpa.getAssignment(var));
		}		
	}
	
	public void clear() {
		isEmpty = true;
		
		explanation = new Assignment();		
	}
	
	public boolean contains(Integer var) {
		return explanation.isAssigned(var);
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
		
	private Integer getLowestPriorityVariable() {
		if (explanation.assignedVariables().isEmpty()) {
			return null;
		}
		
		Integer lowest = getExplanationVariables().iterator().next();
		
		for(Integer var : getExplanationVariables()) {
			lowest = var != eliminatedVariable && lowest < var ? var : lowest;
		}
		
		return lowest;
	}
	
	public Iterable<Integer> getExplanationVariables() {
		return explanation.assignedVariables();
	}
	
	public Integer getExplanationValue(Integer var) {
		return explanation.getAssignment(var);
	}
		
	@Override
	public String toString() {
		if (isEmpty()) {
			return "Empty";
		} 
		
		return explanation.toString() + " => " + eliminatedVariable + "<>" + eliminatedValue;
	}
}