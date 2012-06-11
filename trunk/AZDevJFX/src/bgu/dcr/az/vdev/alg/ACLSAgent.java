package ext.sim.agents;

import java.util.HashMap;
import java.util.Random;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

/**
 * Asymmetric Local Search
 * @author alongrub
 *
 * In this algorithm the agent inspects its local state and seeks ANY improving value
 */

@Algorithm(name="ACLS", useIdleDetector=false)
public class ACLSAgent extends SimpleAgent {
	
	@Variable(name="cycles", description="number of clock ticks", defaultValue="2000")
	private int cycles=2000;
	
	@Variable(name="probToImprove", description="Probability for taking an improving assignment", defaultValue="0.78")
	private double probToImprove=0.78; 
	
	@Variable(name="wariness", description="Wariness of reported impact", defaultValue="0.25")
	private double wariness=0.25; 
		
	@Variable(name="measurePrivacy", description="Boolean value - turn on or off privacy", defaultValue="true")
	private boolean measurePrivacy=true;
	
	// Round titles
	private final int PROPOSAL=0;
	private final int IMPACT=1;
	private final int UPDATE=2;
	
	private final int TICKS_PER_CYCLE=3;
	
	private	HashMap<Integer, int[][]> constData; //constraint privacy data
	private int totalKnowledgeBits = 0;
	private int learnedBits = 0;
	
	private boolean canImprove = false;
	private Integer	myProposedAssignment = null;
	private double myProposalGain=Integer.MAX_VALUE;
	private double proposalImpact=0;
	private   Random	rand;
	private Assignment 	localView=null;
	
	private int 		numNeighbors;
	
    @Override
    public void start() {
    	report(TICKS_PER_CYCLE).to("ticksPerCycle");			// ACLS Ticks per Cycles = 5
    	
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
    	int val = rand.nextInt(getDomain().size());
    	submitCurrentAssignment(val);
    	send("ValueMessage", val).toNeighbores();
    	numNeighbors = (getProblem().getNeighbors(getId())).size();
    	canImprove = false;
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
    	
    	/* REPORT PRIVACY LOSS : */
		if (measurePrivacy)	
			report(learnedBits).to("LearnedBits");
		
    	
    	int roundtype = (int)((getSystemTimeInTicks()-1) % 3); 
    	switch (roundtype) {
		case PROPOSAL:
			
    		proposalStep();
			break;
		case IMPACT:
			// react 
			break;
		case UPDATE:
			updateStep();		// accepted coordination and non-offerer-non-committed agents 
			break;
		default:
			break;
		}
    }

    /**
	 * proposalStep: (after receiving all new assignment from neighbors) 
	 * If a neighbor changed its assignment, there's a chance we can improve 
	 * the local state, so a new proposal is created and sent to all neighbors 
	 * (to check impact).
	 */
	private void proposalStep(){
		proposalImpact=0;
		myProposedAssignment=null;
		myProposalGain=Integer.MAX_VALUE;
		
		if (!canImprove) // No neighbor changed its value
			return;
		if (findImprovement())
			send("ProposedAssignmentMessage", myProposedAssignment).toNeighbores();
		
		canImprove = false;		// reset the value
	}
	
	/**
	 * updated step: 
	 * see if I want to update and notify neighbors when committing to a new value
	 */
	private void updateStep(){
		int currentAssignment = getSubmitedCurrentAssignment();
    	double currentCost = localView.calcAddedCost(getId(), currentAssignment, getProblem());
    	
		if (myProposalGain + proposalImpact*wariness< currentCost &&
				rand.nextDouble()<probToImprove){
			submitCurrentAssignment(myProposedAssignment);
			send("ValueMessage", myProposedAssignment).toNeighbores();
		}
	}
	
	
	/** ----------------- AUX ----------------------- **/
	
	/**
	 * Find an improving assignment
	 * @return TRUE if an assignment (different than current assignment) can improve
	 * the local state and FALSE otherwise.
	 */
	protected boolean findImprovement(){

		HashMap<Integer, Double> improvements = new HashMap<Integer, Double>();
		int currentAssignment = getSubmitedCurrentAssignment();
    	double currentCost = localView.calcAddedCost(getId(), currentAssignment, getProblem());
    	
		for (int assignment = 0; assignment < getDomainSize(); assignment++){
			double assignmentValue = 0;
			assignmentValue = localView.calcAddedCost(getId(), assignment, getProblem());
    		
			if (assignmentValue < currentCost){
				improvements.put(assignment, assignmentValue);
			}
		}

		if ( improvements.isEmpty())
			return false;

		/* create an inverse distribution of values */
		double sumOfAssignment = 0;
		double sumReNormalized = 0;
		for (Double val : improvements.values())
			sumOfAssignment+=val;

		//for (Double val : improvements.values())
		sumReNormalized = (improvements.size()-1)*sumOfAssignment;

		double prob = rand.nextDouble();
		double accumulatedProb = 0;
		for (Integer assign : improvements.keySet()){
			if (sumReNormalized>0)
				accumulatedProb += (sumOfAssignment-improvements.get(assign))/(double)sumReNormalized;
			else
				accumulatedProb = 1.0;
			//System.out.println("Agent "+id+": proposal phase, prob="+prob);
			if (prob < accumulatedProb ){
				myProposalGain = /*stateVal - */improvements.get(assign);
				myProposedAssignment = assign;
				break;
			}
		}
		
		return true;
	}

	
	/** Message handling **/
	
	@WhenReceived("ValueMessage")
	public void handleValueMessage(int value){
		localView.assign(getCurrentMessage().getSender(), value);
		canImprove = true;
	}
	
	@WhenReceived("ProposedAssignmentMessage")
	public void handleProposedAssignmentMessage(int sendersAssignment){
		int sender = getCurrentMessage().getSender();
		double cost = getConstraintCost(getId(), getSubmitedCurrentAssignment(), 
				sender, sendersAssignment); 
		send("costMessage", cost).to(sender);		
	}

	@WhenReceived("costMessage")
	public void handleCostMessage(double cost){
		proposalImpact +=cost;
		
		/* MEASURE PRIVACY LOSS: */
		if (measurePrivacy){
			int sender = getCurrentMessage().getSender();
			int[][] constraint = constData.get(sender);
			if (constraint[getSubmitedCurrentAssignment()][localView.getAssignment(sender)]==0){
				constraint[getSubmitedCurrentAssignment()][localView.getAssignment(sender)] = 1;
				learnedBits++;
			}
			constData.put(sender, constraint);
		}
		
	}
	
}
