package bgu.dcr.az.vdev.alg;

import java.util.HashMap;
import java.util.Random;

import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

/**
 * MGM2 implementation (ASYMMETRIC ONLY!)
 * @author alongrub
 *
 * Note: This implementation is only correct for the asymmetric case, see note
 * within the accRejStep function.
 */
@Algorithm(name="MGM2", useIdleDetector=false)
public class MGM2Agent extends SimpleAgent {
	
	@Variable(name="offerProb", description="Probability of becoming offerer", defaultValue="0.45")
	private double offerProb=0.45; 
	
	@Variable(name="cycles", description="number of clock ticks", defaultValue="2000")
	private int cycles=2000;
	
	@Variable(name="measurePrivacy", description="Boolean value - turn on or off privacy", defaultValue="true")
	private boolean measurePrivacy=true;
	
	private final int OFFER=0;
	private final int ACCREJ=1;
	private final int GAIN=2;
	private final int GNG=3;
	private final int VALUE=4;
	
	private final int TICKS_PER_CYCLE=5;
	
	private	HashMap<Integer, int[][]> constData; //constraint privacy data
	private int totalKnowledgeBits = 0;
	private int learnedBits = 0;
	
	private Random 		rand;
	private Assignment 	localView=null;
	private double 		mgmGain=0;
	private boolean 	isBestImprovingAgent=false;
	private Integer 	proposedAssignment=null;
	private boolean 	offerer = false;
	private boolean		committed = false;
	private int			committedToAgent = getId();
	private Integer 	committedAssignment=null; 
	
	private int 		numNeighbors;
	
    @Override
    public void start() {
    	report(TICKS_PER_CYCLE).to("ticksPerCycle");			// MGM2 Ticks per Cycles = 5
    	
    	if (measurePrivacy){
    		constData = new HashMap<Integer, int[][]>();
			for (Integer neighbors : getProblem().getNeighbors(getId())){
				constData.put(neighbors, new int[getDomainSize()][getProblem().getDomainSize(neighbors)]);
				totalKnowledgeBits += getDomainSize()*getProblem().getDomainSize(neighbors);
			}
			report(totalKnowledgeBits).to("totalKnowledgeBits");	
    	}
    	
    	localView = new Assignment();
    	rand = new Random(getId()+123);
    	numNeighbors  = (getProblem().getNeighbors(getId())).size();
    	int val = rand.nextInt(getDomain().size());
    	submitCurrentAssignment(val);
    	send("ValueMessage", val).toNeighbores();
    	
    }

    @Override
    public void onMailBoxEmpty() {
    	if (getSystemTimeInTicks() == TICKS_PER_CYCLE*cycles){
    		finish();
    		return;
    	}
    	
    	// dumping all singletons 
    	if (numNeighbors==0)
    		return;
    	
    	int roundtype = (int)((getSystemTimeInTicks()-1) % 5); 
    	switch (roundtype) {
		case OFFER:
			if (measurePrivacy){
    			report(learnedBits).to("LearnedBits");
    		}
			offerStep();
			break;
		case ACCREJ:
			// react to offer messages (CoordinateMessage)
			break;
		case GAIN:
			proposalStep();		// accepted coordination and non-offerer-non-committed agents 
			break;
		case GNG:
			actionStep();
			break;
		case VALUE:
			// update assignment and broadcast
			break;
		default:
			break;
		}
    }
    
    
    private void offerStep(){
    	offerer = false;						// reseting state to false
    	committed = false;						
    	committedAssignment=null;
    	isBestImprovingAgent = true;
    	committedToAgent = getId();
    	   	
		if (rand.nextDouble()<offerProb)
			offerer = true;
		
		/* finding a random neighbor to possibly work with */
		if (offerer){
			//numNeighbors = (getProblem().getNeighbors(getId())).size();
			int offeredAgent = rand.nextInt(numNeighbors);
			for (int agent : getProblem().getNeighbors(getId())){
				if (offeredAgent==0){
					offeredAgent = agent; 		
					break;
				}
				offeredAgent--;
			}
			
			/* Sending to offeredAgent a message which includes the gain from each pair of assignment.
			More specifically, we send all improving assignments and the gain from these as an array 
			of the form [my_assignment, offeredAgent_assignmet] = X,
 			where X=-1 if cost is greater than current cost and otherwise  X=improved_gain_diff */
			send("CoordinateMessage", (Object)allImprovement(offeredAgent)).to(offeredAgent);

		}
    }
    
    /**
     * Accepting or rejecting a proposed coordination step
     * @param proposerImprovement - note that the array is of the form arr[proposer's assignment][my assignment]  
     */
    private void accRejStep(int[][] proposerImprovement, int proposer){
    	int[][] personalImprovement = allImprovement(proposer);
    	
    	int maxImprovement = 0;
    	int maxPersonalAssignment = -1;
    	int maxProposerAssignment = -1;
    	
    	if (getProblem().type()==ProblemType.DCOP || getProblem().type()==ProblemType.DCSP){
    		personalImprovement = removeDoubleCounting(personalImprovement, proposer);
    	}
    	
    	for (int i=0; i < personalImprovement.length; i++)
    		for (int j=0; j < personalImprovement[i].length; j++){
    			/** Note that summing both improvement is only correct in the asymmetric case! **/
    			if (personalImprovement[i][j]+proposerImprovement[j][i]>maxImprovement){
    				maxImprovement = personalImprovement[i][j]+proposerImprovement[j][i];
    				maxPersonalAssignment = i;
    				maxProposerAssignment = j;
    			}
    				
    		}
    	
    	if (maxImprovement>0){
    		committed = true;
    		committedToAgent = proposer;
    		committedAssignment = maxPersonalAssignment;
    		mgmGain = maxImprovement;
    		send("AccRejMessage", true, maxProposerAssignment, mgmGain).to(proposer);
    	} else{
    		send("AccRejMessage", false, -1, -1).to(proposer);
    	}
    }
    
    
    
    
    
    /**
	 * Sending an MGM message:
	 * for different cases:
	 * + agent is committed and not offerer ---> send out the committed gain
	 * + agent is offerer --> find out what is the committed gain and send it out, or see that you were
	 *     rejected and search for a non cooperative improvement (do (4))
	 * Note: Search for a non cooperative improvement is handled in the message handler
	 */
	protected void proposalStep(){
		if (committed && offerer)
			panic("MGM2 ERROR: agent is both committed and offerer at the start of the proposal stage!");

		if (committed || offerer){
			for (int other : getProblem().getNeighbors(getId())){
				if (other!= committedToAgent)
					send("MGMMessage", mgmGain).to(other);
			}
		}
		else{ // not committed and not offerer
			if (findImprovingAssignment()!=null){
				send("MGMMessage", mgmGain).toNeighbores();
			}
		}
		
	}
	
	/**
	 * Verify who's gain is maximal/
	 * If there is no attempt at a joint move - change assignment and broadcast
	 * else, notify partner if a move is possible.
	 */
	protected void actionStep(){
		if (offerer || committed){
			if (isBestImprovingAgent)
				send("GoNoGoMessage",true).to(committedToAgent);
		}
		else if (isBestImprovingAgent && proposedAssignment!=null){
			submitCurrentAssignment(proposedAssignment);
			send("ValueMessage", proposedAssignment).toNeighbores();
			proposedAssignment = null;
		}	
	}
    
    /** ----------------- AUX ----------------------- **/
    
    
    /**
     * Find the set of all improving assignment combination and their cost 
     * @param offeredAgent
     * @return a double array [my_assignment, offeredAgent_assignmet] = X,
     * where X=gain_diff
     */
    private int[][] allImprovement(int offeredAgent){
    	int[][] res = new int[getDomainSize()][getDomainOf(offeredAgent).size()];
    	    	
    	int currentAssignment = getSubmitedCurrentAssignment();
    	double currentCost = localView.calcAddedCost(getId(), currentAssignment, getProblem());
    	
    	for (int assignment=0; assignment<getDomainSize(); assignment++){
    		double assignmentValue;
    		localView.assign(getId(), assignment);
    		assignmentValue = localView.calcCostWithout(offeredAgent, getProblem());
    		localView.unassign(getId());
    		for (int propAssignment = 0; propAssignment<getProblem().getDomainSize(offeredAgent); propAssignment++){
    			
    			double cooperativeCost = getProblem().getConstraintCost(getId(), assignment, offeredAgent, propAssignment);
				assignmentValue += cooperativeCost;
				
				res[assignment][propAssignment] = (int) (currentCost - assignmentValue);
	
				assignmentValue -= cooperativeCost;
			}
    	}
    	return res;
    }
    
    /**
     * Update the personal improvement matrix of an agent so that there is no double counting
     * in DCOPs
     * Note: this is a patch and uses extra CCs (can be avoided by better handling the accRejStep function)
     * @param personalImpMatrix
     * @return the update version of the personal improvement matrix
     */
    private int[][] removeDoubleCounting(int[][] personalImpMatrix, int offeredAgent){
    	int currentAssignment = getSubmitedCurrentAssignment();
    	double currentCost = localView.calcAddedCost(getId(), currentAssignment, getProblem());    	
    	for (int aiVal=0; aiVal<getDomainSize(); aiVal++){
    		for (int proposersAssignment = 0; proposersAssignment<getProblem().getDomainSize(offeredAgent); proposersAssignment++){
    			    			
    			double cooperativeCost = getProblem().getConstraintCost(getId(), aiVal, offeredAgent, proposersAssignment);
    			personalImpMatrix[aiVal][proposersAssignment] -= (currentCost-cooperativeCost);
	
			}
    	}
    	return personalImpMatrix;
    }
    
    /**
     * Find an improving assignment
     * @return
     */
    private Integer findImprovingAssignment(){
    	Integer res = null;
    	int currentAssignment = getSubmitedCurrentAssignment();
    	double currentCost = localView.calcAddedCost(getId(), currentAssignment, getProblem());
    	double bestCost = currentCost;
    	for (int i : getDomain()){
    		double tmpCost = localView.calcAddedCost(getId(), i, getProblem());
    		if (tmpCost < bestCost){
    			res = i;
    			bestCost = tmpCost;
    		}
    	}
    	mgmGain = currentCost - bestCost;   // NOTE: a minimization problem
    	proposedAssignment = res;
    	return res;
    	
    }
    
    /** Message handling **/
	
	@WhenReceived("ValueMessage")
	public void handleValueMessage(int value){
		localView.assign(getCurrentMessage().getSender(), value);
	}

	@WhenReceived("CoordinateMessage")
	public void handleCoordinateMessage(int[][] improvedGains){
		int sender = getCurrentMessage().getSender();
		
		/* MEASURE PRIVACY LOSS: */
		if (measurePrivacy){
			int[][] constraint = constData.get(sender);
			for (int i=0; i< improvedGains.length; i++)
				for (int j=0; j<improvedGains[i].length; j++){
					if (improvedGains[i][j]!=-1 && constraint[j][i]==0){
						constraint[j][i]=1;
						learnedBits++;
					}
				}
			constData.put(sender, constraint);
		}
		
		if (offerer || committed){
			send("AccRejMessage", false, -1, -1).to(sender);
		}
		else   // run the accept/reject step 
			accRejStep(improvedGains, sender);		
	}

	@WhenReceived("AccRejMessage")
	public void handleAccRejMessage(boolean accept, int proposerAcceptedAssignment, double jointGain){
		if (committed)
			panic("MGM2 ERROR: Comitted agent receiving a message in proposal step!");			
		if (!accept){	// rejected offer
			offerer = false;
		}
		else{
			committedToAgent = getCurrentMessage().getSender();
			committedAssignment = proposerAcceptedAssignment;
			mgmGain = jointGain;
		}
	}

	@WhenReceived("MGMMessage")
	public void handleMGMMessage(double gain){
		if (mgmGain < gain)
			isBestImprovingAgent = false;
		if (mgmGain == gain && getCurrentMessage().getSender() < getId())
			isBestImprovingAgent = false;
	}

	@WhenReceived("GoNoGoMessage")
	public void handleGoNoGoMessage(boolean go){
		if (!committed && !offerer)
			panic("MGM2 ERROR: an agent that is not committed and not an offerer received a GoNoGo Message!");
		if (go){
			submitCurrentAssignment(committedAssignment);
			send("ValueMessage", committedAssignment).toNeighbores();
		}
	}
	
    
}
