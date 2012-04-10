package bgu.dcr.az.dev.debug3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.Assignment;


/**
 * Asynchronous Nash BackTracking algorithm
 * 
 * @author alongrub NOTE: It is assumed that the domain size of all agents is
 *         equal!
 */

@Algorithm(name = "ANT", useIdleDetector = true)
public class ANTAgent extends SimpleAgent {

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
	private boolean shouldSwapAssignment = false;

	private long learntPrivacyBits = 0;			// used to measure privacy loss
	
	@Override
	public void start() {
		myConstraint = new KArySatConstraint(getProblem(), getId(), cacheState);
		//log(myConstraint.toString());

		neighbors = new ImmutableSet<Integer>(getProblem().getNeighbors(getId()));
		System.out.println("Agent "+getId()+": neighbors="+neighbors.toString());
		extNeighbors = new HashSet<Integer>(neighbors);
		for (int i : neighbors)
			if (i > getId()) {
				isLastAgentInConstraint = false;
				break;
			}
		currentDomain = new HashSet<Integer>(getDomain());
		ngStore = new NoGoodStore(getId());
		agentView = new Assignment();
		Random rand = new Random(1923 + getId());
		int val = rand.nextInt(getDomain().size());
		submitCurrentAssignment(val);
		currentAssignment = val;
		currentDomain.remove(currentAssignment);
		send("ok", val).toNeighbores();
		
		System.out.println("Agent "+getId()+": assigned val="+currentAssignment+", sending ok");
	}

	private Integer chooseValue() {
		//ngStoreGammaMin(currentAssignment);
		HashSet<Integer> clone = new HashSet<Integer>(currentDomain);
		for (Integer i : clone) {
			currentDomain.remove(i);
			if (myConstraint.isConsistentWith(agentView, i))
				return i;
			else {
				/* will consider only agents which are before me in the ordering */
				/*HashSet<Integer> gammaMin = new HashSet<>(extNeighbors);
				for (Iterator<Integer> iter = gammaMin.iterator(); iter.hasNext();){
					int a = iter.next();
					if (a > getId() || !agentView.isAssigned(a))
						iter.remove();
				}
				NoGood ng = new NoGood(agentView, gammaMin);
				if (!ng.isEmpty())
					ng.addL(ng.removeR());
				ng.addR(getId(), i);
				ngStore.addEE(ng);*/
				//ngStoreGammaMin(i);
				if (!isLastAgentInConstraint){
					NoGood ng = new NoGood(agentView, neighbors);
					ng.addL(getId(), i);
					send("ok", i).to(ng.getR().getKey());
					send("NoGood", ng).to(ng.getR().getKey());
					return i;
				}
				else
					ngStoreGammaMin(i);				
			}
		}
		// At this point we couldn't find one consistent assignment

		return null;
	}

	/**
	 * Check the state of the NoGood store with respect to the current agentView
	 * and update current domain
	 */
	private void updateAgentView() {
		ngStore.makeCoherent(agentView);
		/* add values back to the domain */
		for (int i : getDomain()) {
			if (!ngStore.containsEEfor(i) && !currentDomain.contains(i))
				currentDomain.add(i);
			else // assert
			if (ngStore.containsEEfor(i) && currentDomain.contains(i))
				panic("Value i="
						+ i
						+ " exist in current domain despit the existance of an explanation."); // +ngStore.getEEfor(i).toString());
		}
	}

	/**
	 * Verify consistency of agent view with an assignment and fix if needed
	 */
	private void checkAgentView() {

		
		// if my personal constraint is satisfied I do nothing
		if (!shouldSwapAssignment 
				&& myConstraint.isConsistentWith(agentView, currentAssignment))
			return;
		/*
		 * Not consistent -  will try to find an alternate assignment which will be consistent, 
		 * and if I fail - backtrack!
		 */
		
		Integer value = chooseValue();
		System.out.println("Agent "+getId()+": Assigning new value -->"+value);
		if (value == null)
			backtrack();
		else {
			shouldSwapAssignment = false;
			currentAssignment = value;
			submitCurrentAssignment(value);
			
			send("ok", value).toAll(extNeighbors);
			System.out.println("Agent "+getId()+": assigned val="+currentAssignment+", sending ok");
		}
		System.out.println("Agent "+getId()+": ngStore="+ngStore.toString());
	}

	/**
	 * The backtrack procedure Resolves a new nogood, sends it to the relevant
	 * agent, update agent view and find a new assignment
	 */
	private void backtrack() {
		NoGood resolvedNG = ngStore.solve();
		if (resolvedNG.isEmpty()) {
			panic("BACKTRACK found an empty nogood!");
			finishWithNoSolution();
			return;
		}
		send("NoGood", resolvedNG).to(resolvedNG.getR().getKey());
		System.out.println("Agent "+getId()+": sending nogood="+resolvedNG+" to rhs / "+ngStore.toString());
		agentView.unassign(resolvedNG.getR().getKey());
		updateAgentView();
		System.out.println("Agent "+getId()+" after sending ng: "+ngStore.toString());
		checkAgentView();
	}

	/**
	 * The Coherence function Note that unlike the PKC paper I use the
	 * intersection and not union of agent sets. This is thanks to Mohamad
	 * (http:
	 * //hal-lirmm.ccsd.cnrs.fr/docs/00/59/59/21/PDF/TechnicalReport_RR-11017.
	 * pdf) who mentions that two assignment sets are coherent if every common
	 * variable is assigned the same value in both sets.
	 * 
	 * @param ng
	 * @return
	 */
	private boolean coherent(NoGood ng, HashSet<Integer> agentSet) {
		/*HashSet<Integer> intersectSet = new HashSet<>(ng.getAgents());
		intersectSet.retainAll(agentSet);
		for (int var : intersectSet) {
			if (var == getId()) {
				if (!ng.valueOf(var).equals(currentAssignment))
					return false;
			} else if (!agentView.isAssigned(var) || !ng.valueOf(var).equals(agentView.getAssignment(var)))
				return false;
		}*/
		//HashSet<Integer> ngSet = new HashSet<>(ng.getAgents());
		HashSet<Integer> addSet = new HashSet<Integer>(ng.getAgents());
		addSet.addAll(agentSet);
		for (Integer var : addSet) {
			if (var.equals(getId())) {
				if (!ng.valueOf(var).equals(currentAssignment))
					return false;
			} else if (agentView.isAssigned(var) && ng.valueOf(var)!=null && !ng.valueOf(var).equals(agentView.getAssignment(var)))
				return false;
		}
		return true;
	}

	private void addLinks(NoGood ng) {
		for (Integer i : ng.getAgents())
			/* if i is in ng.lhs and is also not in gamma^- */
			if (i != ng.getR().getKey() && i < getId() && !neighbors.contains(i)
					&& !extNeighbors.contains(i)) {
				extNeighbors.add(i);
				send("addLink", getId()).to(i);
				agentView.assign(i, ng.valueOf(i));
				updateAgentView();
			}
	}

	private void ngStoreGammaMin(int val){
		/* will consider only agents which are before me in the ordering */
		HashSet<Integer> gammaMin = new HashSet<Integer>(extNeighbors);
		for (Iterator<Integer> iter = gammaMin.iterator(); iter.hasNext();){
			int a = iter.next();
			if (a > getId() || !agentView.isAssigned(a))
				iter.remove();
		}
		NoGood ng = new NoGood(agentView, gammaMin);
		if (!ng.isEmpty())
			ng.addL(ng.removeR());
		ng.addR(getId(), val);
		ngStore.addEE(ng);
	}
	
	/* ------------- Message Handlers ------------- */

	@WhenReceived("ok")
	public void handleOk(int assignment) {
		//log("Received OK=" + assignment);
		System.out.println("Agent "+getId()+": received ok assignment="+assignment+" from "+getCurrentMessage().getSender()+", AV="+agentView.toString());
		
		int sender = getCurrentMessage().getSender();
		agentView.assign(sender, assignment);

		System.out.println("Agent "+getId()+" in OK, before updateAgentView: "+ngStore.toString());
		
		/* updateAgentView + checkAgentView */
		updateAgentView();
		// if my personal constraint is satisfied I do nothing
		if (!shouldSwapAssignment
				&& myConstraint.isConsistentWith(agentView, currentAssignment))
			return;

		System.out.println("Agent "+getId()+": assignment <"+getCurrentMessage().getSender()+", "+assignment+"> is inconsistent!");
		
		/*
		 * if received an inconsistent set of ok messages from neighboring
		 * agents, and I am not the last agent in the constraint (i.e. at least
		 * one other agent in (\gamma^+), send out a NoGood to the last agent in
		 * the k-ary constraint
		 */
		if (!isLastAgentInConstraint) {
			NoGood ng = new NoGood(agentView, neighbors);
			ng.addL(getId(), currentAssignment);
			send("NoGood", ng).to(ng.getR().getKey());
			System.out.println("Agent "+getId()+": sending nogood="+ng+" to rhs");
		} else{
			//currentAssignment = null;
			shouldSwapAssignment = true;
			checkAgentView();
		}
	}

	@WhenReceived("NoGood")
	public void handleNoGood(NoGood ng) {
		//log("Received NoGood=" + ng);
		System.out.println("Agent "+getId()+": received nogood="+ng+" from "+getCurrentMessage().getSender());
		
		/* Privacy statistics */
		if (getCurrentMessage().getSender()<getId())
			learntPrivacyBits++;
		
		/* get \gamma- */
		HashSet<Integer> tmpSet = new HashSet<Integer>();
		for (int i : agentView.assignedVariables())
			if (i < getId())
				tmpSet.add(i);
		tmpSet.add(getId());
		
		if (coherent(ng, tmpSet)) {
			System.out.println("Agent "+getId()+": my av="+agentView.toString()+", assignment="+currentAssignment+" nogood is coherent!");
			addLinks(ng);
			ngStore.addEE(ng);
			currentDomain.remove(ng.getR().getValue()); // have to re-assign a
														// value!
			//currentAssignment = null;
			shouldSwapAssignment = true;
			checkAgentView();
		} else if (ng.valueOf(getId()).equals(currentAssignment)){
			send("ok", currentAssignment).to(getCurrentMessage().getSender());
			System.out.println("Agent "+getId()+": resending ok with "+currentAssignment);
		}
	}

	@WhenReceived("addLink")
	public void handleAddLink(int agentId) {
		System.out.println("Agent "+getId()+": received addlink agentId="+agentId+" from "+getCurrentMessage().getSender());
		extNeighbors.add(agentId);
		send("ok", currentAssignment).to(agentId);
	}

	@Override
	public void onIdleDetected() {
		finish(currentAssignment);
		/*if (isFirstAgent())
			finish();*/
	}
}
