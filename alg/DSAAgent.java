package ext.sim.agents;


import ext.sim.tools.ValueMessage;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.SearchType;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.lsearch.Messages;
import bgu.csp.az.api.tools.Assignment;

@Algorithm(name="DSA", searchType=SearchType.LOCAL_SEARCH)
public class DSAAgent extends SimpleAgent {
	
	private Assignment values;
	private double p;

    @Override
    public void start() {
    	log("starting");
    	values = new Assignment();
    	p = 0.5;
    	int value = random(this.getDomain());
    	this.submitCurrentAssignment(value);
    	send(new ValueMessage(value,this.getId())).toNeighbores(this.getProblem());
    }

	@WhenReceived(SYS_TICK_MESSAGE)
	public void handleTICK(long time, Messages m){
		log("tick" + time);
		if (time == 2000 && isFirstAgent()) finishWithAccumulationOfSubmitedPartialAssignments();
		ValueMessage[] messages = m.allOf(ValueMessage.class);
		for(ValueMessage msg : messages){
			values.assign(msg.getSender(), msg.value);
		}
		Integer newValue = calcDelta();
		if (Math.random()>p && newValue != null){
			submitCurrentAssignment(newValue);
			send(new ValueMessage(newValue, this.getId()));
		}		
	}

    private Integer calcDelta() {
    	int ans = this.getSubmitedCurrentAssignment();
    	double delta =  this.values.calcAddedCost(this.getId(), ans, this.getProblem());
    	double tmpDelta = delta;
		for (Integer i : this.getDomain()){
			double tmp = this.values.calcAddedCost(this.getId(), i, this.getProblem());
			if(tmp < tmpDelta){
				tmpDelta = tmp;
				ans = i;
			}
		}
		if (delta == tmpDelta) return null;
		return ans;
	}

}
