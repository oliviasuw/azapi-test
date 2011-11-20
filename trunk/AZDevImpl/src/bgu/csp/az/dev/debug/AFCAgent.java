package bgu.csp.az.dev.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.api.ano.WhenReceived;

@Algorithm(problemType=ProblemType.DCSP, name="AFC")
public class AFCAgent extends SimpleAgent {
	
	public static enum MessageType {
		CPA, Backtrack_CPA, FC_CPA, NOT_OK		
	}
	
	private Assignment agentView;
	private int highestReceivedStepCounter;
	private boolean isConsistent;
	
	private Set<Integer> currentDomain;
		
    @Override
    public void start() {
    	currentDomain = new HashSet<Integer>(getProblem().getDomainOf(getId()));
    	
    	agentView = new Assignment();
    	highestReceivedStepCounter = 0;
    	isConsistent = true;
    	
    	if (isFirstAgent()) {
    		assignCPA();
    	}
    }
        			    
	@WhenReceived("CPA")
	public void handleCPA(Assignment cpa, int cpaStepCounter, MessageType type) {
		highestReceivedStepCounter = Math.max(cpaStepCounter, highestReceivedStepCounter);
		
		if (!isConsistent) {
			if (contains(cpa, agentView)) {			
				backtrack();
			} else {
				adjustAgentView(cpa, cpaStepCounter);
				
				isConsistent = true;
			}
		}
		
		if (isConsistent) {
			if (type.equals(MessageType.Backtrack_CPA)) {				
				removeLastAssignment();
				
				assignCPA();
			} else {
				if(updateAgentView(cpa, cpaStepCounter)) {
					assignCPA();
				} else {
					backtrack();
				}
			}
		}
	}
	
	private void assignCPA() {
		Integer newValue = chooseValue();
		
		if (newValue != null) {
			agentView.assign(getId(), newValue);
			
			if (isFull(agentView)) {
				finish(agentView);
			} else {
				highestReceivedStepCounter++;
				
				Collection<Integer> unassignedAgents = getUnassignedAgents(agentView);
				
				Integer next = unassignedAgents.iterator().next();
				
				unassignedAgents.remove(next);
				
				send("CPA", agentView, highestReceivedStepCounter, MessageType.CPA).to(next);
				
				send("FC_CPA", agentView, highestReceivedStepCounter).toAll(unassignedAgents);
			}
		} else {
			agentView = generateShortestInconsistentPartialAssignment();
			
			backtrack();
		}
	}

	private void backtrack() {		
		if (isFirstAgent()) {
			finishWithNoSolution();
		} else {				
			isConsistent = false;
									
			Integer agent = last(agentView);
						
			send("CPA", agentView, highestReceivedStepCounter, MessageType.Backtrack_CPA).to(agent);
		}		
	}
	
	@WhenReceived("FC_CPA")
	public void handleFC_CPA(Assignment cpa, int cpaStepCounter) {
		if (cpaStepCounter > highestReceivedStepCounter) {
			highestReceivedStepCounter = cpaStepCounter;
			
			if (!isConsistent) {
				if (!contains(cpa, agentView)) {
					adjustAgentView(cpa, cpaStepCounter);
					
					isConsistent = true;
				}
			}
			
			if (isConsistent) {
				if (!updateAgentView(cpa, cpaStepCounter)) {					
					send("NOT_OK", agentView, highestReceivedStepCounter).toAll(getUnassignedAgents(agentView));
				}
			}
		}				
	}
	
	@WhenReceived("NOT_OK")
	public void handleNOT_OK(Assignment cpa, int cpaStepCounter) {
		if (contains(agentView, cpa)) {
			agentView = cpa;
			
			isConsistent = false;
		} else {
			if (!contains(cpa, agentView)) {
				if (cpaStepCounter > highestReceivedStepCounter) {
					agentView = cpa;
					
					isConsistent = false;
				}
			}
		}
		
		highestReceivedStepCounter = Math.max(cpaStepCounter, highestReceivedStepCounter);
	}
	
	private boolean updateAgentView(Assignment cpa, int cpaStepCounter) {
		adjustAgentView(cpa, cpaStepCounter);
		
		if (isEmptyDomain(agentView)) {
			agentView = generateShortestInconsistentPartialAssignment();
			
			return false;
		}
		
		return true;
	}
	
	private Assignment generateShortestInconsistentPartialAssignment() {
		Assignment shortest = agentView;			
		
		return shortest;
	}

	private void adjustAgentView(Assignment cpa, int cpaStepCounter) {
		if (cpaStepCounter >= highestReceivedStepCounter) {
			agentView = cpa;
			
			highestReceivedStepCounter = cpaStepCounter;
			
			currentDomain = generateConsistentDomain(agentView);
		}
	}

	private void removeLastAssignment() {
		Integer current = getId();
		Integer value = agentView.getAssignment(current);		
		
		agentView.unassign(current);
		
		currentDomain.remove(value);
	}
    	
	private boolean contains(Assignment set, Assignment subset) {		
		for(Integer var : subset.assignedVariables()) {
			if (!set.isAssigned(var) || !subset.getAssignment(var).equals(set.getAssignment(var))) {
				return false;
			}
		}
		
		return true;
	}

    private Integer last(Assignment cpa) {
    	Integer current = getId();
		Integer last = 0;
		
		for(Integer var : cpa.assignedVariables()) {
			if (var != current && var > last) {
				last = var;
			}
		}
		
		return last;
	}

	private Collection<Integer> getUnassignedAgents(Assignment cpa) {
		Integer current = getId();
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int var = 0; var < getProblem().getNumberOfVariables(); var++) {
			if (var != current && !cpa.isAssigned(var)) {
				result.add(var);
			}
		}
		
		return result;
	}
	
	private boolean isFull(Assignment cpa) {
		return cpa.assignedVariables().size() == getProblem().getNumberOfVariables();
	}
	
	private boolean isEmptyDomain(Assignment cpa) {
		return currentDomain.isEmpty();
	}

    private Integer chooseValue() {    	
    	return currentDomain.isEmpty() ? null : currentDomain.iterator().next();
    }
        
    private Set<Integer> generateConsistentDomain(Assignment cpa) {    	
    	Integer current = getId();
    	boolean isConsistentValue;

    	Set<Integer> consistentDomain = new HashSet<Integer>();
    	
    	for(Integer val : getProblem().getDomainOf(current)) {
    		isConsistentValue = true;
    		
    		for(Integer var : cpa.assignedVariables()) {
    			if (isConstrained(current, var) && 
    				getConstraintCost(current, val, var, cpa.getAssignment(var)) != 0) {
    				isConsistentValue = false;
    				break;
    			}
    		}
    		
    		if (isConsistentValue) {
    			consistentDomain.add(val);
    		}
    	}
    	
    	return consistentDomain;
    }
        
}