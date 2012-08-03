package ext.sim.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.Assignment;
import ext.sim.tools.KArySatConstraint;
import ext.sim.tools.NoGood;
import ext.sim.tools.NoGoodStore;

/**
 * Asynchronous Nash BackTracking algorithm
 * 
 * @author alongrub 
 * NOTE: It is assumed that the domain size of all agents is
 * equal!
 */

@Algorithm(name="ANT-new", useIdleDetector=true)
public class ABTNashAgent extends SimpleAgent {
	@Variable(name="cache", description="A boolean value for requesting a caching mechanism", defaultValue="false")
	private boolean cacheState = false;
	
	private KArySatConstraint myConstraint = null;
	private Assignment agentView = null;
	private ImmutableSet<Integer> neighbors = null;
	private HashSet<Integer> extNeighbors = null; // extended set of neighbors
													// (those with added links)
	private NoGoodStore ngStore = null;
	private HashSet<Integer> currentDomain = null;
	private Integer currentAssignment = null;
	private boolean isLastAgentInConstraint = true;
	
	private Random rand;

	//private long learntPrivacyBits = 0;			// used to measure privacy loss
	
    @Override
    public void start() {
    	rand = new Random(getId());
    	
    	myConstraint = new KArySatConstraint(getProblem(), getId(), cacheState);
		
		neighbors = new ImmutableSet<>(getProblem().getNeighbors(getId()));
		
		extNeighbors = new HashSet<>(neighbors);
		for (int i : neighbors)
			if (i > getId()) {
				isLastAgentInConstraint = false;
				break;
			}
		currentDomain = new HashSet<>(getDomain());
		ngStore = new NoGoodStore(getId());
		agentView = new Assignment();
		int val = 0;
		submitCurrentAssignment(val);
		currentAssignment = val;
		currentDomain.remove(currentAssignment);
		send("ok", val).toNeighbores();
		
		//System.out.println("Agent "+getId()+": neighbors="+neighbors.toString());
		//System.out.println("Agent "+getId()+": assigned val="+currentAssignment+", sending ok");
    }

    private void updateAgentView(Integer agent, Integer value){
    	if (value<0)
    		agentView.unassign(agent);
    	else
    		agentView.assign(agent, value);
    	ngStore.makeCoherent(agentView);
    	currentDomain = new HashSet<Integer>(getDomain());
    	currentDomain.removeAll(ngStore.eliminatedVals());
    }
    
    private void checkAgentView(){
    	/* if current assignment is null then we know that checkAgentView was invoked from resolveConflict (handleNoGood) */
    	if (currentAssignment==null || !myConstraint.isConsistentWith(agentView, currentAssignment)){
    		Integer value = chooseValue();
    		//System.out.println("Agent "+getId()+": checkAgentView new value -->"+value);
    		if (value == null)
    			backtrack();
    		else {
    			currentAssignment = value;
    			submitCurrentAssignment(value);
    			
    			send("ok", value).toAll(extNeighbors);
    			//System.out.println("Agent "+getId()+": assigned val="+currentAssignment+", sending ok");
    		}
    		//System.out.println("Agent "+getId()+": ngStore="+ngStore.toString());
    	}
    }

    private void backtrack() {
		NoGood resolvedNG = ngStore.solve();
		
		panicIf(resolvedNG.isEmpty(), "BACKTRACK found an empty nogood!");
		
		send("NoGood", resolvedNG).to(resolvedNG.getR().getKey());
		
		//System.out.println("Agent "+getId()+": sending nogood="+resolvedNG+" to rhs ");
				
		updateAgentView(resolvedNG.getR().getKey(), -1);
		
		//System.out.println("Agent "+getId()+" after sending ng in backtrack: "+ngStore.toString());
		checkAgentView();
	}
    
    
    private boolean coherent(NoGood ng, HashSet<Integer> agentSet) {
		HashSet<Integer> addSet = new HashSet<>(ng.getAgents());
		addSet.addAll(agentSet);
		addSet.add(getId());		/* Since we are only calling coherent once from resolve conflict (handleNoGood) I will abuse... */
		for (Integer var : addSet) {
			if (var.equals(getId())) {
				if (!ng.valueOf(var).equals(currentAssignment))
					return false;
			} else if (agentView.isAssigned(var) && ng.valueOf(var)!=null && !ng.valueOf(var).equals(agentView.getAssignment(var)))
				return false;
		}
		return true;
	}
    
    /**------------ AUX ------------**/
    
    private Integer chooseValue() {
		ArrayList<Integer> clone = new ArrayList<Integer>(currentDomain);
		Collections.shuffle(clone,rand);
		for (Integer i : clone) {
			currentDomain.remove(i);
			if (myConstraint.isConsistentWith(agentView, i))
				return i;
			else {
				NoGood ng = new NoGood(agentView, neighbors);
				if (!isLastAgentInConstraint){
					ng.addL(getId(), i);
					send("ok", i).to(ng.getR().getKey());
					send("NoGood", ng).to(ng.getR().getKey());
					//System.out.println("Agent "+getId()+": chooseValue, not lastAgentInConstraint, assigning i="+i+" and sending nogood="+ng.toString()); 
					return i;
				}
				else{
					if (!ng.isEmpty())
						ng.addL(ng.removeR());
					ng.addR(getId(), i);
					ngStore.addEE(ng);
					//System.out.println("Agent "+getId()+": chooseValue, last agent in constraint adding EE="+ng.toString());
				}
									
			}
		}
		// At this point we couldn't find one consistent assignment
		return null;
	}
    
    private void addLinks(NoGood ng) {
		for (Integer i : ng.getAgents())
			/* Not supposed to receive a NoGood from agents after me in the ordering  */
			if (i != ng.getR().getKey() && !neighbors.contains(i) && !extNeighbors.contains(i)) {
				extNeighbors.add(i);
				send("addLink", getId()).to(i);
				updateAgentView(i, ng.valueOf(i));
			}
	}
    
    /**------------ Message handling ------------**/

    @WhenReceived("ok")
	public void handleOk(int val){
    	/* Process info */
    	//System.out.println("Agent "+getId()+": received ok assignment="+val+" from "+getCurrentMessage().getSender()+", AV="+agentView.toString());
		    	
    	updateAgentView(getCurrentMessage().getSender(), val);
    	
    	if (!myConstraint.isConsistentWith(agentView, currentAssignment)){
    		if (!isLastAgentInConstraint){
    			NoGood ng = new NoGood(agentView, neighbors);
    			ng.addL(getId(), currentAssignment);
    			
    			    			
    			send("NoGood", ng).to(ng.getR().getKey());
    			//System.out.println("Agent "+getId()+": sending nogood="+ng+" to rhs");
    		}
    		else{
    			//System.out.println("Agent "+getId()+": last in constraint, calling checkAgentView()");
    			checkAgentView();
    		}
    	}
	}

	@WhenReceived("NoGood")
	public void handleNoGood(NoGood ng){
		/* Resolve Conflict */
		//System.out.println("Agent "+getId()+": received nogood="+ng+" from "+getCurrentMessage().getSender());
		
		if (coherent(ng,extNeighbors)){
			//System.out.println("Agent "+getId()+": my av="+agentView.toString()+", assignment="+currentAssignment+" nogood is coherent!");
			addLinks(ng);
			ngStore.addEE(ng);
			currentAssignment = null;
			checkAgentView();
		}  else if (ng.valueOf(getId()).equals(currentAssignment)){
			send("ok", currentAssignment).to(getCurrentMessage().getSender());
			//System.out.println("Agent "+getId()+": resending ok with "+currentAssignment);
		}
	}

	@WhenReceived("addLink")
	public void handleAddLink(int agentId){
		//System.out.println("Agent "+getId()+": received addlink agentId="+agentId+" from "+getCurrentMessage().getSender());
		extNeighbors.add(agentId);
		send("ok", currentAssignment).to(agentId);
	}
	
	@Override
	public void onIdleDetected() {
		finish(currentAssignment);
	}
}
