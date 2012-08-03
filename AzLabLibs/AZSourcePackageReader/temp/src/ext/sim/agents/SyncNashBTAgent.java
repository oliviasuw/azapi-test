package ext.sim.agents;

import java.util.TreeSet;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;
import ext.sim.tools.KArySatConstraint;

@Algorithm(name="SyncNashBT", useIdleDetector=false)
public class SyncNashBTAgent extends SimpleAgent {
	@Variable(name="cache", description="A boolean value for requesting a caching mechanism", defaultValue="false")
	private boolean cacheState = false;
	
	private KArySatConstraint myConstraint = null;
	private TreeSet<Integer> currentDomain = null;

	private long learntPrivacyBits = 0;			// used to measure privacy loss
	
	@Override
	public void start() {
		myConstraint = new KArySatConstraint(getProblem(), getId(), cacheState);
		currentDomain = new TreeSet<>(getDomain());
				
		if (isFirstAgent()){
			Integer val = currentDomain.pollFirst();
			Assignment cpa = new Assignment();
			cpa.assign(getId(), val);
			send("CPAMessage",cpa).toNextAgent();
		}
	}
	
	private Integer assign(Assignment cpa){
		Integer res = null;
		while (res == null && !currentDomain.isEmpty()){
			res = currentDomain.pollFirst();
			if (myConstraint.isConsistentWith(cpa, res)){
				cpa.assign(getId(), res);
				send("CPABackCheckMessage",cpa, getId(), true).to(0);
			} else 
				res = null;
		}
		return res;
	}
	
	private void backTrack(Assignment cpa){
		cpa.unassign(getId());
		if (assign(cpa) == null){
			if (isFirstAgent()){
				panic("NO SOLUTION???");
				finish();
			}
			currentDomain = new TreeSet<>(getDomain());
			send("BTMessage",cpa).toPreviousAgent();
		}
	}

	@WhenReceived("CPAMessage")
	public void handleCPAMessage(Assignment cpa){
		log("Received CPAMessage");
		if (assign(cpa) == null){
			if (isFirstAgent()){
				panic("NO SOLUTION???");
				finish();
			}
			currentDomain = new TreeSet<>(getDomain());
			send("BTMessage",cpa).toPreviousAgent();
		}
	}

	@WhenReceived("CPABackCheckMessage")
	public void handleCPABackCheckMessage(Assignment cpa, Integer originator, boolean state){
		log("Received CPABackCheckMessage");
		if (getId() == originator){
			if (state){
				if (isLastAgent()){
					finish(cpa);
					return;
				}
				else
					send("CPAMessage",cpa).toNextAgent();
			}
			else
				backTrack(cpa);	
		} 
		else {
			if (myConstraint.isConsistentWith(cpa, cpa.getAssignment(getId())))
				send("CPABackCheckMessage",cpa, originator, true).toNextAgent();
			else
				send("CPABackCheckMessage",cpa, originator, false).to(originator);
		}
		
	}

	@WhenReceived("BTMessage")
	public void handleBTMessage(Assignment cpa){
		log("Received BTMessage");
		backTrack(cpa);
		/*cpa.unassign(getId());
		if (assign(cpa) == null){
			if (isFirstAgent()){
				panic("NO SOLUTION???");
				finish();
			}
			currentDomain = new TreeSet<>(getDomain());
			send("BTMessage",cpa).toPreviousAgent();
		}*/
	}
	
	
	
}
