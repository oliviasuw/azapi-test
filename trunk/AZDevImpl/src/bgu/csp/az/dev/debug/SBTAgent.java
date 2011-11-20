package bgu.csp.az.dev.debug;

import java.util.LinkedList;

import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.tools.Assignment;

@Algorithm(problemType=ProblemType.DCSP, name="SBT")
public class SBTAgent extends SimpleAgent {

	Assignment cpa;
	LinkedList<Integer> currentDomain;
	
    @Override
    public void start() {
        if (isFirstAgent()) {
                System.out.println(this.getProblem().toString());
        	cpa = new Assignment();
        	currentDomain = new LinkedList<Integer>(getDomain());
        	assignCpa();
        }
    }

	private void assignCpa() { 
		while (! currentDomain.isEmpty()){
			if (cpa.isConsistentWith(getId(), currentDomain.get(0), getProblem())){
				cpa.assign(getId(), currentDomain.remove(0));
				
				if (isLastAgent()){
					finish(cpa);
				}else {
					send("CPA", cpa).toNextAgent();
				}
				
				return;
			}else {
				currentDomain.remove(0);
			}
		}
		
		backtrack();
	}

	private void backtrack() {
		cpa.unassign(this);
		if (isFirstAgent()) {
			finishWithNoSolution();
		}else {
			send("BACKTRACK", cpa).toPreviousAgent();
		}
	}

	@WhenReceived("CPA")
	public void handleCPA(Assignment cpa){
		currentDomain = new LinkedList<Integer>(getDomain());
		this.cpa = cpa;
		assignCpa();
	}

	@WhenReceived("BACKTRACK")
	public void handleBACKTRACK(Assignment cpa){
		this.cpa = cpa;
		assignCpa();
	}
	
}
