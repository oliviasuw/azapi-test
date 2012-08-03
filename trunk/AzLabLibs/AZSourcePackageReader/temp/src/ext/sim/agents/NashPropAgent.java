package ext.sim.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import ext.sim.tools.KArySatConstraint;
import ext.sim.tools.NashPropCPA;
import ext.sim.tools.NestableACNash;
import ext.sim.tools.NestablePStarProp;
import bgu.dcr.az.api.*;
import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;

@Algorithm(name="NashProp", useIdleDetector=true)
public class NashPropAgent extends SimpleAgent {

	private HashMap<Integer, HashSet<Integer>> psDomains = null;
	private Assignment agentView;
	
	private HashSet<Integer> reducedDomain = null;
	private ArrayList<Integer> currentDomain = null;
	
	private KArySatConstraint myConstraint = null;
	
	private HashSet<List<Integer>> currentJointAssignment = null; 
	private ArrayList<Integer> currentAgentsInJA = null;			// note: ORDERING IS IMPORTANT!
	
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
    	currentJointAssignment = new HashSet<>();
    	currentAgentsInJA = new ArrayList<>();
    	
    	if (isFirstAgent()){
	    	NashPropCPA cpa = new NashPropCPA(getNumberOfVariables());
	    	currentAgentsInJA.clear();
    		currentJointAssignment = calcJointAction(cpa);
	    	nextValidAssignment(cpa);
	    	send("CPA", cpa).to(cpa.getNext(getId()));
    	}
    	
    }
    
    
    /****------------- AUX  -------------****/
    
    
    /**
     * find the next valid assignment
     * If the CPA contains a value for this agent then will first try to go over  
     * neighbors combination and then replace personal value
     * otherwise will choose a value and then retry
     * @param cpa
     */
    private boolean nextValidAssignment(NashPropCPA cpa){
    	Integer myVal = cpa.getAssignmentOf(getId());
    	boolean isConsistent = false;
    	Assignment tmpView = new Assignment();
    	while (!isConsistent){
    		tmpView = agentView.deepCopy();
    		
    		/* First, get a value for myself */
    		if (myVal == null){
    			if (currentDomain.isEmpty()){
        			cpa.unassign(getId());
        			return false;
        		}
        		myVal = currentDomain.remove(0); // get the first element in current domain
    		}
    		/* Next, get a consistent value for the joint assignment */
        	
    		
    		/*if (currentJointAssignment.isEmpty() && !currentAgentsInJA.isEmpty()){
        		myVal=null;
        		continue;
        	}*/
    		List<Integer> ja;
    		Iterator<List<Integer>> iter= currentJointAssignment.iterator();
    		if (iter.hasNext()){
    			ja = iter.next();
    			iter.remove();
    			//currentJointAssignment.remove(ja);
    			int index = 0;
    			for (int n : currentAgentsInJA){
    				tmpView.assign(n, ja.get(index));
    				index++;
    			}
    		}
    		
    		
    		/* If a consistent joint assignment is found: */
    		if (myConstraint.isConsistentWith(tmpView, myVal))
	    		isConsistent = true;
    		/* otherwise, we check if exhausted domain, unassign all relevant and rebuild a JA */
    		else if (currentJointAssignment.isEmpty()){
    			cpa.unassign(getId());
    			currentAgentsInJA.clear();
    			currentJointAssignment = calcJointAction(cpa);
    			myVal = null;
    		}
    	}
    	cpa.assign(getId(), myVal);
		cpa.add(tmpView);
		agentView = tmpView;    	
		return true;
    }
    
    /**
     * Calculate a joint assignment of all cpa unassigned agents which are compatible with
     * the valid domains
     * @param cpa
     * @return
     */
    private HashSet<List<Integer>> calcJointAction(NashPropCPA cpa){
    	ArrayList<HashSet<Integer>> validDomains = new ArrayList<>();
    	for (int n : getNeighbors()){
    		if (cpa.contains(n))
    			continue;
			validDomains.add(psDomains.get(n));
			currentAgentsInJA.add(n);
		}
    	Set<List<Integer>> cp = Sets.cartesianProduct(validDomains);
		return new HashSet<>(cp);
    	
    }
    
    /**
     * Backtrack function
     * @param cpa
     */
    private void backtrack(NashPropCPA cpa){
    	int prev = cpa.getPrev(getId());
    	currentDomain = new ArrayList<>(reducedDomain);
    	send("BackTrack",cpa).to(prev);
    }    
    
    
    
    @WhenReceived("CPA")
	public void handleCPA(NashPropCPA cpa){
		System.out.println("Agent "+getId()+": got CPA message - "+cpa.toString());
		agentView = cpa.getAssignments().deepCopy();
		agentView.unassign(getId());
		boolean isLast = cpa.isLast(getId());
		
		currentAgentsInJA.clear();
		currentJointAssignment = calcJointAction(cpa);
		
		
		if (nextValidAssignment(cpa)){
			if (isLast){
				if (myConstraint.isConsistentWith(agentView, cpa.getAssignmentOf(getId())))
					send("BackCheckCPA",cpa).to(cpa.getPrev(getId()));
				else{
					handleBackTrack(cpa);
				}
			}
			else
				send("CPA",cpa).to(cpa.getNext(getId()));
		}
		else
			backtrack(cpa);			
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
		//cpa.unassign(getId());
		agentView = cpa.getAssignments().deepCopy();
		agentView.unassign(getId());
		
		boolean returning = true;
		/*for (Integer unassignedNeighbor : currentAgentsInJA){
			if (cpa.contains(unassignedNeighbor)){
				returning = false;
				break;
			}
		}*/
		for (Integer neighbor : getNeighbors()){
			if ((cpa.contains(neighbor) && currentAgentsInJA.contains(neighbor)) ||
					(!cpa.contains(neighbor) && !currentAgentsInJA.contains(neighbor))){
				returning = false;
				break;
			}
		}
		if (!returning){
			currentAgentsInJA.clear();
			currentJointAssignment = calcJointAction(cpa);
		} else if (currentJointAssignment.isEmpty()){
			cpa.unassign(getId());
			currentAgentsInJA.clear();
			currentJointAssignment = calcJointAction(cpa);
		}
			
		
		if (nextValidAssignment(cpa)){
			int next = cpa.getNext(getId());
			if (next<0)
				send("BackCheckCPA",cpa).to(cpa.getPrev(getId()));
			else
				send("CPA",cpa).to(next);
		}
		else
			backtrack(cpa);
		
		
	}
}
