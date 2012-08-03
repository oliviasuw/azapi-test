package ext.sim.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


import com.google.common.collect.Sets;

import ext.sim.tools.KArySatConstraint;
import ext.sim.tools.NashPropCPA;
import ext.sim.tools.NestableACNash;
import ext.sim.tools.NestablePStarProp;
import ext.sim.tools.NoGoodStore;
import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name="NashPropOld", useIdleDetector=true)
public class OldNashPropAgent extends SimpleAgent {
	
	private HashMap<Integer, HashSet<Integer>> psDomains = null;
	private Assignment agentView;
	
	private HashSet<Integer> reducedDomain = null;
	private ArrayList<Integer> currentDomain = null;
	
	private KArySatConstraint myConstraint = null;
	
	private Set<List<Integer>> nonExhaustedJA = null;
	
    @Override
    public void start() {
    	calcAC(new NestableACNash(), new Continuation() {
			@Override
			public void doContinue() {
				/* Note that to pass all arguments actual agent's code starts in "begin()" */
				return;
			}
		});
    }

    private void calcAC(final NestableACNash algo, final Continuation c){
    	algo.calculate(this).andWhenDoneDo(new Continuation() {
			
			@Override
			public void doContinue() {
				c.doContinue();
				calcProp(new NestablePStarProp(), algo.getReducedDomain(), c);
			}
		});
    }
    
    private void calcProp(final NestablePStarProp algo, HashSet<Integer> doms, final Continuation c){
    	algo.setPStarDomain(doms);
    	reducedDomain = doms;
    	algo.calculate(this).andWhenDoneDo(new Continuation() {
			
			@Override
			public void doContinue() {
				c.doContinue();
				psDomains = algo.getPDomains();
				
				begin();
				return;
			}
		});
    }
    
    private void begin(){
    	agentView = new Assignment();
    	currentDomain = new ArrayList<>(reducedDomain);
    	myConstraint = new KArySatConstraint(getProblem(), getId(), false);
    	nonExhaustedJA = new HashSet<>();
    	
    	if (isFirstAgent()){
	    	NashPropCPA cpa = new NashPropCPA(getNumberOfVariables());
	    	
	    	ArrayList<Integer> agents = new ArrayList<>();
	    	Set<List<Integer>> jointActions = calcJointAction(cpa, agents);
	    	boolean cont = true;
	    	for (Iterator<Integer> iter = currentDomain.iterator(); cont && iter.hasNext(); ){
	    		int myVal = iter.next();
	    		if (assign(myVal, cpa, agents, jointActions)){
		    		send("CPA", cpa).to(cpa.getNext(getId()));
		    		nonExhaustedJA = jointActions;
		    		cont = false;
	    		}
	    		iter.remove();
	    	}
	    	if (cont)
	    		panic("Could not extend the first agent!!!");
    	}
    }
    
    private Set<List<Integer>> calcJointAction(NashPropCPA cpa, ArrayList<Integer> agents){
    	ArrayList<HashSet<Integer>> validDomains = new ArrayList<>();
    	for (int n : getNeighbors()){
    		if (cpa.contains(n))
    			continue;
			validDomains.add(psDomains.get(n));
			agents.add(n);
		}
		
		return Sets.cartesianProduct(validDomains);
    }
    
    private boolean assign(int myVal, NashPropCPA cpa, ArrayList<Integer> agents, Set<List<Integer>> jointActions){
    	if (agents.isEmpty())
    		if (myConstraint.isConsistentWith(agentView, myVal)){
    			cpa.assign(getId(), myVal);
    			return true;
    		}
    		else
    			return false;
    	
		for (List<Integer> ja : jointActions){
			Assignment tmpView = agentView.deepCopy();
			
			int index = 0;
			for (int n : agents){
				tmpView.assign(n, ja.get(index));
				index++;
			}
			
			if (myConstraint.isConsistentWith(tmpView, myVal)){
				tmpView.assign(getId(), myVal);
				cpa.add(tmpView);
				agentView = tmpView;
				return true;
			}
		}
    	return false;
    }
    
    
    @Override
    public void onIdleDetected() {
    	finish();
    }
    
    private void backtrack(NashPropCPA cpa){
    	int prev = cpa.unassign(getId());
    	currentDomain = new ArrayList<>(reducedDomain);
    	send("BackTrack",cpa).to(prev);
    }

	@WhenReceived("CPA")
	public void handleCPA(NashPropCPA cpa){
		System.out.println("Agent "+getId()+": got CPA message - "+cpa.toString());
		agentView = cpa.getAssignments().deepCopy();
		int myVal;
		if (agentView.isAssigned(getId())){
			myVal = agentView.getAssignment(getId());
			agentView.unassign(getId());
			currentDomain.remove(myVal);
		} else
			myVal = -1;
		
		boolean isLast = true;
		for (int n : getNeighbors())
			if (!agentView.isAssigned(n))
				isLast = false;
		
		if (isLast && myVal!=-1){
			if (!myConstraint.isConsistentWith(agentView, myVal)){
				backtrack(cpa);
				return;
			}
		} else {
			ArrayList<Integer> agents = new ArrayList<>();
	    	Set<List<Integer>> jointActions = calcJointAction(cpa, agents);
	    	
	    	if (myVal!=-1)
	    		if(!assign(myVal, cpa, agents, jointActions)){
	    			backtrack(cpa);
	    			return;
	    		} else
	    			nonExhaustedJA = jointActions;
	    	else{ // unassigned agent
	    		boolean cont = true;
	    		for (Iterator<Integer> iter = currentDomain.iterator(); cont && iter.hasNext(); ){
		    		myVal = iter.next();
		    		if (assign(myVal, cpa, agents, jointActions)){
		    			cont = false;
		    			nonExhaustedJA = jointActions;
		    		}
		    		iter.remove();
		    	}
	    	}
		}
		
		/* at this point the agent should have an assignment which will be sent to the next agent in the order (or backwards) */
		int next = cpa.getNext(getId());
		if (next<0)
			send("BackCheckCPA",cpa).to(cpa.getPrev(getId()));
		else
			send("CPA",cpa).to(next);
	
	}

	@WhenReceived("BackCheckCPA")
	public void handleBackCheckCPA(NashPropCPA cpa){
		System.out.println("Agent "+getId()+": got BACKCHECKCPA message - "+cpa.toString());
		agentView = cpa.getAssignments().deepCopy();
		int myVal = agentView.getAssignment(getId());
		agentView.unassign(getId());
		if (!myConstraint.isConsistentWith(agentView, myVal)){
			send("BackTrack",cpa).to(cpa.last());
			return;
		}
		
		if (isFirstAgent())
			finish(cpa.getAssignments());
		else
			send("BackCheckCPA",cpa).to(cpa.getPrev(getId()));
	}

	@WhenReceived("BackTrack")
	public void handleBackTrack(NashPropCPA cpa){
		System.out.println("Agent "+getId()+": got BACKTRACK message - "+cpa.toString());
		agentView = cpa.getAssignments().deepCopy();
		int myVal = agentView.getAssignment(getId());
		agentView.unassign(getId());
		
		boolean cont = true;
		ArrayList<Integer> agents = new ArrayList<>();
		
		Set<List<Integer>> jointActions;
		if (nonExhaustedJA.isEmpty()){
			jointActions = calcJointAction(cpa, agents);
			currentDomain.remove(myVal);
		} else {
			calcJointAction(cpa, agents);
			jointActions = nonExhaustedJA;
			currentDomain.add(0, myVal);
		}
    	
		for (Iterator<Integer> iter = currentDomain.iterator(); cont && iter.hasNext(); ){
    		myVal = iter.next();
    		if (assign(myVal, cpa, agents, jointActions)){
	    		cont = false;
	    		nonExhaustedJA = jointActions;
    		}
    		iter.remove();
    	}
		if (cont){
			backtrack(cpa);
			return;
		}
		
		int next = cpa.getNext(getId());
		if (next<0)
			send("BackCheckCPA",cpa).to(cpa.getPrev(getId()));
		else
			send("CPA",cpa).to(next);
	}
	
	
}
