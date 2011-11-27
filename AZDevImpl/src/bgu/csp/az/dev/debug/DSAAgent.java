package bgu.csp.az.dev.debug;

import bgu.csp.az.api.*;
import bgu.csp.az.api.agt.*;
import bgu.csp.az.api.ano.*;
import bgu.csp.az.api.tools.*;

@Algorithm(name="DSA_ALON",useIdleDetector=false)
public class DSAAgent extends SimpleAgent {
	private Assignment values; 
	private double p;
	private boolean localChange;
	
	@Override
	public void start() {
		values = new Assignment();
		localChange = true;
		if (isFirstAgent()){
			//log(getProblem().toString());
		}
		p = 0;
		int val = random(getDomain());
		submitCurrentAssignment(val);
		send("ValueMessage", val).toNeighbores(getProblem());

	}

	@WhenReceived("ValueMessage") 
	public void handleValueMessage(int value) {
		log("Got message");
		int sender = getCurrentMessage().getSender();
		if (!values.isAssigned(sender) || values.getAssignment(sender) != value){
			localChange = true;
			values.assign(getCurrentMessage().getSender(), value); 
			log("AV ="+values.toString()+", current assignment "+getSubmitedCurrentAssignment());
		}
	}

	@Override 
	public void onMailBoxEmpty() { 
		final long systemTime = getSystemTimeInTicks(); 

		if (systemTime + 1 == 100 && isFirstAgent()) { 
			finishWithAccumulationOfSubmitedPartialAssignments(); 
		} 
		if (localChange){
			Integer newValue = calcDelta();
			if (Math.random() > p && newValue != null) { 
				submitCurrentAssignment(newValue); 
				send("ValueMessage", newValue).toNeighbores(getProblem());
				log("Assigning value and informing neighbors that new assignment is "+newValue);
			}
		}
		else
			log("Did not send a message in tick "+systemTime);
	}


	private Integer calcDelta() {
		log("CALLING CALCDELTA");
		int ans = getSubmitedCurrentAssignment(); 
		double delta = values.calcAddedCost(getId(), ans, getProblem()); 
		double tmpDelta = delta; 
		for (Integer i : this.getDomain()) { 
			if (i != ans){
				double tmp = this.values.calcAddedCost(this.getId(), i, this.getProblem()); 
				log("Cost of assigning "+i+" is "+tmp+" ("+delta+")");
				if (tmp > tmpDelta) { 
					tmpDelta = tmp; 
					ans = i; 
				}
			}
		} 
		if (delta == tmpDelta) {
			localChange = false;
			return null; 
		} 
//		return random(getDomain());
		return ans; 
	}

}
