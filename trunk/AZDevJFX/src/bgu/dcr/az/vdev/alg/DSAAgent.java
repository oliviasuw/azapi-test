package bgu.dcr.az.vdev.alg;

import java.util.Random;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;

/**
 * 
 * @author alongrub
 * A minimal cost search DSA
 */

@Algorithm(name="DSA", useIdleDetector=false)
public class DSAAgent extends SimpleAgent {
	
	
	@Variable(name="changeProbability", description="Probability for swapping an assignment", defaultValue="0.4")
	private double changeProbability=0.4;
	 
	
	@Variable(name="cycles", description="number of clock ticks", defaultValue="100")
	private int cycles=100;
	
	
	private Assignment localView;
	private Random rand;
	
    @Override
    public void start() {
    	localView = new Assignment();
    	rand = new Random(1923 + getId());
    	int val = rand.nextInt(getDomain().size());
    	submitCurrentAssignment(val);
    	send("ValueMessage", val).toNeighbores();
    }

    @WhenReceived("ValueMessage")
	public void handleValueMessage(int value){
		localView.assign(getCurrentMessage().getSender(), value);
		/*log("("+getSystemTimeInTicks()+")    ["+getId()+"="+getSubmitedCurrentAssignment()+"], "+
				localView.toString());*/
	}

    @Override
    public void onMailBoxEmpty() {
    	if (getSystemTimeInTicks() >= cycles){
    		if (isFirstAgent())
    			finishWithAccumulationOfSubmitedPartialAssignments();
    		return;
    	}
    	
    	Integer updatedAssignment = findImprovingAssignment();
    	if (updatedAssignment!=null && rand.nextDouble()<changeProbability){
    		/*log("("+getSystemTimeInTicks()+")    "+getId()+" changed its assignment!");*/
    		submitCurrentAssignment(updatedAssignment);
    		send("ValueMessage", updatedAssignment).toNeighbores();
    	}
    }
    
    
    private Integer findImprovingAssignment(){
    	Integer res = null;
    	int currentAssignment = getSubmitedCurrentAssignment();
    	/*String s = " current assignment is "+currentAssignment;*/
    	double currentCost = localView.calcAddedCost(getId(), currentAssignment, getProblem());
    	/*log("("+getSystemTimeInTicks()+") current cost: "+currentCost);*/
    	double bestCost = currentCost;
    	for (int i : getDomain()){
    		double tmpCost = localView.calcAddedCost(getId(), i, getProblem());
    		/*log("("+getSystemTimeInTicks()+") i="+i+" tmpCost: "+tmpCost);*/
    		if (tmpCost < bestCost){
    			res = i;
    			bestCost = tmpCost;
    		}
    	}
    	/*log("("+getSystemTimeInTicks()+") "+s+" --> will change to "+res);*/
    	return res;
    	
    }
    
    public int getCurrentCost(){
    	return localView.calcAddedCost(getId(), getSubmitedCurrentAssignment(), getProblem());
    }
}
