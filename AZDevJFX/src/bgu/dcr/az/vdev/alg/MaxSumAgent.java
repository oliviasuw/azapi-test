package bgu.dcr.az.vdev.alg;

import java.util.HashMap;
import java.util.HashSet;


import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.ano.WhenReceived;

/**
 * Max-Sum
 * @author alongrub
 * A synchronous Max-Sum implementation solving a minimization problem
 */

@Algorithm(name="MaxSum", useIdleDetector=false)
public class MaxSumAgent extends SimpleAgent {
	
	@Variable(name="cycles", description="number of clock ticks", defaultValue="2000")
	private int cycles=2000;
	
	// Round titles
	private final int SENDR=0;
	private final int SENDQ=1;
	
	private final int TICKS_PER_CYCLE=3;
	
	private HashMap<Integer, int[]> rInfo=null;		// all R messages
	private HashMap<Integer, int[]> qInfo=null;		// all Q messages
	
    @Override
    public void start() {
    	report(TICKS_PER_CYCLE).to("ticksPerCycle");			// Max-Sum Ticks per Cycles = 2
    	/* initiate the data structures */    	
    	rInfo = new HashMap<>();
    	qInfo = new HashMap<>();
    	
    	for (int neighbor : getProblem().getNeighbors(getId())){
    		rInfo.put(neighbor, new int[getDomainOf(neighbor).size()]);
    		qInfo.put(neighbor, new int[getDomainOf(neighbor).size()]);
    	}
    	// add myself (no separation between factor and variables)
    	int id = getId();
    	qInfo.put(id, new int[getDomainSize()]);
    	rInfo.put(id, minSum(id));
    	
    	/* now we need an initial assignment, this is actually a result of the first R_i-->i(x_i) msg, which is simply 
    	the best value the agent can get according to its peers, since no Q message arrived (i.e. all Q's are zero) */
    			
    	int value = assign();
    	submitCurrentAssignment(value);
    }

    @Override
    public void onMailBoxEmpty() {
    	if (getSystemTimeInTicks() == TICKS_PER_CYCLE*cycles){
    		finish();
    		return;
    	}
    	   	
    	int roundtype = (int)((getSystemTimeInTicks()-1) % 5); 
    	switch (roundtype) {
		case SENDR:
			sendR();
			break;
		case SENDQ:
			sendQ();
			break;
		default:
			break;
		}
    }
    
    
    /**
	 * Sending out R messages to neighbors (including to myself)
	 * There is nothing to do here since we already updated all
	 * relevant data after handling all Q messages in mailbox
	 */
	private void sendR(){
		for (Integer agent : rInfo.keySet())
			send("RMessage", minSum(agent)).to(agent);
	}
	
	/**
	 * Send out Q messages to the neighbors
	 */
	private void sendQ(){
		submitCurrentAssignment(assign());
		// we send a message to each neighbor (including myself)
		for (Integer agent : qInfo.keySet()){
			int[] qMsg = new int[getDomainOf(agent).size()];
			HashSet<Integer> others = new HashSet<Integer>(rInfo.keySet());
			others.remove(new Integer(agent));

			// we take the contribution of each R message, but also normalize to avoid explosions (in values) due to feedback loops 
			int sumOfQ = 0;
			for (int other : others)
				for (int i=0; i<qMsg.length; i++){
					qMsg[i] += rInfo.get(other)[i];
					sumOfQ += rInfo.get(other)[i];
				}

			// now we normalize
			for (int i=0; i<qMsg.length; i++)
				qMsg[i] -= sumOfQ;

			// finally, we send the Q message
			send("QMessage", qMsg).to(agent);
		}
	}
    
    /** ----------------- AUX ----------------------- **/
    

	/**
	 * The main function used when building R_m->n(x_n) message
	 * @param targetAgent
	 * @return a vector of values, representing the best value for the target that I can find
	 */
	private int[] minSum(int targetAgent){
		int[] minValues = new int[getDomainSize()];

		// go over all possible assignment of the current agent, excluding the target's values
		int[] currentMinValue = new int[getDomainSize()];
		HashSet<Integer> others = new HashSet<Integer>(qInfo.keySet());
		others.remove(new Integer(targetAgent));
		others.remove(getId());
		
		for (int assignment = 0; assignment < getDomainSize(); assignment++){
			double assignmentValue = 0;
			/* Go over each neighbor and find the best assignment each has (with Q) against
			the value selected for the current agent (assignment) */
			for (Integer neighbor : others){
				double minNeighborConstraintValue = Integer.MAX_VALUE;
				double cost = Integer.MAX_VALUE;
				for (int neighborValue = 0; neighborValue<getDomainOf(neighbor).size() && cost>0; neighborValue++){
					
					cost = getProblem().getConstraintCost(getId(), assignment, neighbor, neighborValue);
					cost += qInfo.get(neighbor)[neighborValue];
					
					if (cost < minNeighborConstraintValue)
						minNeighborConstraintValue =  cost;
				}
				
				assignmentValue += minNeighborConstraintValue;
			} // for neighbors
			currentMinValue[assignment] = (int) assignmentValue;
		
		}
		
		/* now we should have a vector of minimal assignment values (excluding targetAgent), which we call currentMinValue[]
		If targetAgent is the current agent then we can pass currentMinValue, else we have to add 
		the additional cost incurred by the targetAgent+qInfo */
		
		if (targetAgent == getId())
			return currentMinValue;
		
		for (int targetsAssignment = 0; targetsAssignment<getDomainOf(targetAgent).size(); targetsAssignment++){
			double bestAssignmentValue = Integer.MAX_VALUE;
			
			for (int i=0; i < currentMinValue.length; i++){
				double cost = getProblem().getConstraintCost(getId(), i, targetAgent, targetsAssignment);
				cost += qInfo.get(getId())[i];
				
				if (cost < bestAssignmentValue)
					bestAssignmentValue =  cost;
			}
			minValues[targetsAssignment] = (int) bestAssignmentValue;
		}
		
		return minValues;
	}
	
	
	/**
	 * Search by combining all R messages for the best assignment
	 * @return
	 */
	private int assign(){
		int[] assignmentValue = new int[getDomainSize()];
		int bestAssignment = -1;
		int bestGain = Integer.MAX_VALUE;
		for (int i = 0; i<getDomainSize(); i++){
			for (int agent : rInfo.keySet())
				assignmentValue[i]+= rInfo.get(agent)[i];
			
			if (assignmentValue[i]<bestGain){
				bestAssignment = i;
				bestGain = assignmentValue[i];
			}
		}
		return bestAssignment;
	}

	@WhenReceived("QMessage")
	public void handleQMessage(int[] qMsg){
		/* Process all incoming Q messages (nothing to do except for updating the qInfo DS) */
		int sender = getCurrentMessage().getSender();
		qInfo.put(sender, qMsg);
	}

	@WhenReceived("RMessage")
	public void handleRMessage(int[] rMsg){
		/* Process all R messages (update assignment after all messages arrive) */
		int sender = getCurrentMessage().getSender();
		rInfo.put(sender, rMsg);
	}
	
	
}
