package bgu.dcr.az.dev.debug;

import java.util.LinkedList;
import java.util.List;

import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;

@Algorithm(name="SBB", problemType=ProblemType.DCOP, useIdleDetector=true)
public class SBBAgent extends SimpleAgent {

	Assignment cpa, best;
	List<Integer> currentDomain;
	
    @Override
    public void start() {
//        log("I'm an agent!!!");
        if (isFirstAgent()) {
//            System.out.println(getProblem().toString());
        	cpa = new Assignment();
        	currentDomain = new LinkedList<Integer>(getDomain());
        	assignCpa();
        }
    }

	private void assignCpa() {
		if (currentDomain.isEmpty()) {
			backtrack();
			return;
		}
		
		Integer minimum = cpa.findMinimalCostValue(getId(), currentDomain, getProblem());
		cpa.assign(getId(), minimum);
		currentDomain.remove(minimum);
		
		if (costOf(cpa) < costOf(best)){
			if (isLastAgent()){
				best = cpa.deepCopy();
				backtrack();
			}else {
				send("CPA", cpa).toNextAgent();
			}
		}else {
			backtrack();
		}
	}

	private void backtrack() {
		if (isFirstAgent()){
			finish(best);
		}else {
			cpa.unassign(getId());
			send("BACKTRACK", cpa, best).toPreviousAgent();
		}
	}

	
	@WhenReceived("CPA")
	public void handleCPA(Assignment cpa){
		this.cpa = cpa;
		currentDomain = new LinkedList<Integer>(getDomain());
		assignCpa();
	}

	@WhenReceived("BACKTRACK")
	public void handleBACKTRACK(Assignment cpa, Assignment best){
		this.cpa = cpa;
		this.best = best;
		
		assignCpa();
	}
	
}
