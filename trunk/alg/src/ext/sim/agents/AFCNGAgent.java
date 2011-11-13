package ext.sim.agents;

import java.util.ArrayList;
import java.util.Collection;

import ext.sim.tools.Explanation;
import ext.sim.tools.Explanations;

import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.api.ano.WhenReceived;

@Algorithm(problemType=ProblemType.CSP, name="AFC-NG")
public class AFCNGAgent extends SimpleAgent {
		
	private Assignment myAgentViewAssignment;
	private Assignment myAgentViewTimeStamps;
	private int myCtr;
	private boolean isConsistent;
	
	private Explanations myNogoodStore;
		
    @Override
    public void start() {
    	myNogoodStore = new Explanations(this);
    	
    	initMyAgentView();
    	
    	isConsistent = true;
    	
    	if (isFirstAgent()) {
    		assign();    	    		
    	}
    }
	
	private void initMyAgentView() {
		Integer current = getId();
		
		myAgentViewAssignment = new Assignment();
		myAgentViewTimeStamps = new Assignment();
		
		myCtr = 0;
		
		for(int i = 0; i < current; i++) {
			myAgentViewTimeStamps.assign(i, 0);
		}
	}

	private void assign() {
		Integer current = getId();
		Integer newValue = chooseValue();
		
		if (newValue != null) {			
			myCtr++;
						
			myAgentViewTimeStamps.assign(current, myCtr);
			
			myAgentViewAssignment.assign(current, newValue);
			
			sendCPA(myAgentViewAssignment, myAgentViewTimeStamps);
		} else {
			backtrack();
		}
	}

	private Integer chooseValue() {
		Collection<Integer> nonEliminated = myNogoodStore.getNonEliminatedValues();
		
		return nonEliminated.isEmpty() ? null : nonEliminated.iterator().next();
	}

	private void sendCPA(Assignment cpaAssignment, Assignment cpaTimeStamps) {
		Integer next = getNextAgent();
		
		if (next == null) {
			finish(myAgentViewAssignment);
		} else {
			send("CPA", myAgentViewAssignment, myAgentViewTimeStamps, next).toAll(getLowPriorityAgents());
		}
	}
		
	@WhenReceived("CPA")
	public void handleCPA(Assignment cpaAssignmnet, Assignment cpaTimeStamps, Integer next) {		
		if (!isConsistent && contains(cpaAssignmnet, myAgentViewAssignment)) {
			return;
		}
		
		int splitLevel = compareTimeStamp(myAgentViewTimeStamps, cpaTimeStamps);

		if (splitLevel > 0) {
			updateMyAgentView(cpaAssignmnet, cpaTimeStamps, splitLevel);
			
			isConsistent = true;
			
			fcReviceInitialDomain();
			
			if (myNogoodStore.getNonEliminatedValues().isEmpty()) {
				backtrack();
			} else {
				checkAssign(next);
			}
		}
	}	

	private void checkAssign(Integer next) {
		Integer current = getId();
		
		if (next.equals(current)) {
			assign();
		}		
	}
	
	private void backtrack() {
		Explanation nogood = myNogoodStore.generateNoGood();
		
		if (nogood.isEmpty()) {
			finishWithNoSolution();
			return;
		}
		
		for(int var = nogood.getEliminatedVariable() + 1; var < getProblem().getNumberOfVariables(); var++) {
			myAgentViewAssignment.unassign(var);
			myAgentViewTimeStamps.unassign(var);
			
			for(Explanation explanation : myNogoodStore) {
				if (explanation.contains(var)) {
					explanation.clear();
				}
			}
		}
		
		isConsistent = false;
		
		myCtr = 0;
		
		send("BACK_CPA", myAgentViewAssignment, myAgentViewTimeStamps, nogood).to(nogood.getEliminatedVariable());
	}
	
	@WhenReceived("BACK_CPA")
	public void handleBACK_CPA(Assignment cpaAssignment, Assignment cpaTimeStamps, Explanation nogood) {		
		Integer current = getId();
		Integer currentValue = myAgentViewAssignment.getAssignment(current);
		
		if (!isConsistent && contains(cpaAssignment, myAgentViewAssignment)) {
			return;
		}
				
		int splitLevel = compareTimeStamp(myAgentViewTimeStamps, cpaTimeStamps);
		
		if (splitLevel == 0 && nogood.getEliminatedValue().equals(currentValue)) {
			myNogoodStore.getExplanation(currentValue).setExplanation(nogood);
			
			myAgentViewAssignment.unassign(current);
			myAgentViewTimeStamps.unassign(current);
			
			assign();
		}
	}
		
	private void updateMyAgentView(Assignment cpaAssignment, Assignment cpaTimestamps, int splitLevel) {		
		myAgentViewAssignment = cpaAssignment;
		myAgentViewTimeStamps = cpaTimestamps;
		
		for(Explanation explanation : myNogoodStore) {
			if (!isConsistent(explanation, myAgentViewAssignment)) {
				explanation.clear();
			}
		}
	}

	private void fcReviceInitialDomain() {
		Integer current = getId();
		
		for(Integer val : myNogoodStore.getNonEliminatedValues()) {
    		for(Integer var : myAgentViewAssignment.assignedVariables()) {
    			if (isConstrained(current, var) && 
    				getConstraintCost(current, val, var, myAgentViewAssignment.getAssignment(var)) != 0) {
    				myNogoodStore.getExplanation(val).setExplanation(var, myAgentViewAssignment.getAssignment(var));
    			}
    		}
		}
		
	}

	private int compareTimeStamp(Assignment viewTimeStamps, Assignment cpaTimeStamps) {
		for(int var = 0; cpaTimeStamps.isAssigned(var); var++) {
			if (!viewTimeStamps.isAssigned(var)) {
				return var + 1;
			}
			if (cpaTimeStamps.getAssignment(var) > viewTimeStamps.getAssignment(var)) {
				return var + 1;
			}
			if (cpaTimeStamps.getAssignment(var) < viewTimeStamps.getAssignment(var)) {
				return -1;
			}			
		}
		
		return 0;
	}

	private Integer getNextAgent() {
		Integer next = getId() + 1;
		
		return next < getProblem().getNumberOfVariables() ? next : null;
	}

	private Collection<Integer> getLowPriorityAgents() {
		Integer next = getId() + 1;
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int var = next; var < getProblem().getNumberOfVariables(); var++) {
			result.add(var);
		}
		
		return result;
	}
	
	private boolean contains(Assignment set, Assignment subset) {		
		for(Integer var : subset.assignedVariables()) {
			if (!set.isAssigned(var) || !subset.getAssignment(var).equals(set.getAssignment(var))) {
				return false;
			}
		}
		
		return true;
	}
	
    private boolean isConsistent(Explanation expl, Assignment cpa) {
    	for(Integer var : expl.getExplanationVariables()) {
    		if (!cpa.isAssigned(var) ||
    			!expl.getExplanationValue(var).equals(cpa.getAssignment(var))) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
}