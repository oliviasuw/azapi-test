package ext.sim.tools;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.api.ImmutableProblem;
import bgu.dcr.az.api.tools.Assignment;
import ext.sim.agents.DPOPAgent;

public class DPOPUtil implements DeepCopyable{
	public class AgentValue implements DeepCopyable{
		protected int agent;
		protected int value;

		public AgentValue(AgentValue a) {
			this.agent = a.agent;
			this.value = a.value;
		}

		public AgentValue(int agent, int value) {
			this.agent = agent;
			this.value = value;
		}

		public boolean equalAgentValue(AgentValue a) {
			return this.agent == a.agent && this.value == a.value;
		}

		public int getAgent() {
			return this.agent;
		}

		public int getValue() {
			return this.value;
		}

		public String toString() {
			return "<" + this.agent + "," + this.value + ">";
		}

		@Override
		public Object deepCopy() {
			AgentValue av = new AgentValue(this.agent, this.value);
			return av;
		}
	}
	public class AgentUtil implements Comparable<AgentUtil>, DeepCopyable{
		protected int agentID;
		protected HashMap<Integer, AgentValue> agentValues;
		protected int util;
		protected int value;

		protected AgentUtil(AgentUtil u) {
			this(u.agentID);
			this.value = u.value;
			this.util = u.util;
			for (Entry<Integer, AgentValue> a : u.agentValues.entrySet()) {
				this.agentValues.put(a.getKey(), (AgentValue) a.getValue().deepCopy());
			}
		}

		protected AgentUtil(int agent) {
			this.agentID = agent;
			this.value = -1;
			this.util = -1;
			agentValues = new HashMap<Integer, AgentValue>();
		}

		public void add(int agent, int value) {
			AgentValue a = new AgentValue(agent, value);
			agentValues.put(agent, a);
		}

		@Override
		public int compareTo(AgentUtil other) {
			return this.util - other.util;
		}

		private boolean containsAgentValue(AgentValue ao) {
			return agentValues.containsKey(ao.agent) && 
					agentValues.get(ao.agent).value == ao.value;
		}

		public int dim() {
			return agentValues.size();
		}

		public int get_Value(int id) {
			return agentValues.get(id).value;
		}

		protected int getAgentID() {
			return this.agentID;
		}

		public HashMap<Integer, AgentValue> getAgentValues() {
			return this.agentValues;
		}

		protected int getUtil() {
			return this.util;
		}

		public int getValue() {
			return this.value;
		}

		public boolean isConsistent(Assignment cpa) {
			for (Entry<Integer, AgentValue> a : agentValues.entrySet()) {
				if (cpa.isAssigned(a.getKey()) && 
						cpa.getAssignment(a.getKey()) != a.getValue().value) {
					return false;
				}
			}
			return true;
		}

		public boolean isConsistent(AgentUtil u) {
			for (Entry<Integer, AgentValue> a : u.agentValues.entrySet()) {
				if (!containsAgentValue(a.getValue())) {
					return false;
				}
			}
			return true;
		}

		protected void setUtil(int cost) {
			this.util = cost;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String toString() {
			return "Agent: " + this.agentID + " Value: " + this.value
					+ " Util:" + this.util + " AgentValues:"
					+ this.agentValues + "\n";
		}

		@Override
		public Object deepCopy() {
			AgentUtil ans = new AgentUtil(this.agentID);
			ans.agentValues = new HashMap<Integer, AgentValue>();
			ans.util = this.util;
			for (Entry<Integer, AgentValue> av : this.agentValues.entrySet()){
				ans.agentValues.put(av.getKey(), (AgentValue) av.getValue().deepCopy());
			}
			ans.value=this.value;
			return ans;
		}
	}
	protected int agent;
	private TreeMap<Integer, AgentUtil> agentUtils;
	protected LinkedList<DPOPUtil> childrenUtils;
	protected int domainSize;

	protected ImmutableProblem problem;

	protected LinkedList<Integer> relevantAncestors;

	public DPOPUtil(ImmutableProblem problem, int agentId, int domain) {
		this.problem = problem;
		this.agent = agentId;
		this.domainSize = domain;
		agentUtils = new TreeMap<Integer, DPOPUtil.AgentUtil>();
		childrenUtils = new LinkedList<DPOPUtil>();
		relevantAncestors = new LinkedList<Integer>();
	}
	
	
	public void addChildUtil(DPOPUtil util) {
		if (util==null) System.out.println("childUtil is null!!! in add");
		childrenUtils.add(util);
	}

	private void computeInnerNodeUtil(DPOPAgent da) {
		for (DPOPUtil u : childrenUtils) { // add children's ancestors
			for (int a : u.relevantAncestors) {
				if (a != this.agent && !relevantAncestors.contains(a)) {
					relevantAncestors.add(a);
				}
			}
		}
		LinkedList<AgentUtil> v = getAgentValuesCombinations(relevantAncestors);
		for (AgentUtil au : v) {
			double min_cost = Integer.MAX_VALUE;
			for (int value = 0; value < this.domainSize; value++) {
				double u = da.getConstraintCost(agent, value);
				if (u > min_cost)
					continue;
				for (Entry<Integer, AgentValue> ass : au.getAgentValues().entrySet()) {
					u = (u + da.getConstraintCost(agent, value, ass.getKey(), ass.getValue().value));
					if (u > min_cost)
						continue;
				}
				if (u > min_cost)
					continue;
				for (DPOPUtil cu : childrenUtils) {
					u = u + cu.getUtil(au, value);
					da.getConstraintCost(agent, value); //To raise one NCC
					if (u > min_cost)
						break;
				}
				if (u < min_cost) {
					min_cost = u;
					au.setValue(value);
				}
			}
			au.util = (int) min_cost;
			agentUtils.put(getKey(au), au);
		}
	}

	
	private void computeLeafUtil(DPOPAgent da) {
		LinkedList<AgentUtil> v = getAgentValuesCombinations(relevantAncestors);
		for (AgentUtil au : v) {
			double min_cost = Integer.MAX_VALUE;
			for (int value = 0; value < this.domainSize; value++) {
				double u =  da.getConstraintCost(agent, value);
				if (u > min_cost)
					continue;
				for (Entry<Integer, AgentValue> ass : au.getAgentValues().entrySet()) {
					u = u + da.getConstraintCost(agent, value, ass.getKey(), ass.getValue().value);
					if (u > min_cost)
						continue;
				}
				if (u < min_cost) {
					min_cost = u;
					au.setValue(value);
				}
			}
			au.util = (int) min_cost;
			agentUtils.put(getKey(au), au);
		}
	}

	

	private void computeRootUtil(DPOPAgent da) {
		AgentUtil au = new AgentUtil(agent);
		double min_cost = Integer.MAX_VALUE;
		for (int value = 0; value < this.domainSize; value++) {
			double u = da.getConstraintCost(agent, value);
			for (DPOPUtil cu : childrenUtils) {
				u = u + cu.getUtil(au, value);
				da.getConstraintCost(agent, value); //To raise one NCC				
				if (u > min_cost)
					break;
			}
			if (u < min_cost) {
				min_cost = u;
				au.setValue(value);
			}
		}
		au.util = (int) min_cost;
		agentUtils.put(getKey(au), au);
	}

	public void computeUtil(DPOPAgent a) {
		computeRootUtil(a);
	}

	public void computeUtil(int parent, List<Integer> pseudoParents,DPOPAgent a) {
		this.relevantAncestors.add(parent);
		this.relevantAncestors.addAll(pseudoParents);
		if (childrenUtils.isEmpty()) {
			computeLeafUtil(a);
		} else {
			computeInnerNodeUtil(a);
		}
	}

	private boolean equalAgentValues(HashMap<Integer, AgentValue> a1, HashMap<Integer, AgentValue> a2) {	
		if (a1.size() != a2.size()) {
			return false;
		}
		for (int i : a1.keySet()) {
			if (!a2.containsKey(i) || a1.get(i).value != a2.get(i).value) {
				return false;
			}
		}
		return true;
	}
	protected LinkedList<AgentUtil> getAgentValuesCombinations(LinkedList<Integer> list) {
		LinkedList<Integer> agents = new LinkedList<Integer>();
		agents.addAll(list);
		LinkedList<AgentUtil> assignments = new LinkedList<AgentUtil>();
		for (int value = 0; value < problem.getDomainSize(agents.get(0)); value++) {
			AgentUtil u = new AgentUtil(agent);
			u.add(agents.get(0), value);
			assignments.add(u);
		}
		agents.remove(0);
		LinkedList<AgentUtil> tmp_assignments = new LinkedList<AgentUtil>();
		// add pseudo parents combinations
		for (int agent : agents) {
			for (AgentUtil u : assignments) {
				for (int value = 0; value < problem.getDomainSize(agent); value++) {
					AgentUtil new_u = new AgentUtil(u);
					new_u.add(agent, value);
					tmp_assignments.add(new_u);
				}
			}
			assignments.clear();
			for (AgentUtil a : tmp_assignments) {
				assignments.add(a);
			}
			tmp_assignments.clear();
		}
		return assignments;
	}

	public AgentUtil getBestUtil(Assignment cpa) {
		HashMap<Integer, AgentValue> ancestorsValues = new HashMap<Integer, AgentValue>();
		for (int a = 0; a < problem.getNumberOfVariables(); a++) {
			if (cpa.isAssigned(a)) {
				ancestorsValues.put(a, new AgentValue(a, cpa.getAssignment(a)));
			}
		}
		return getAgentUtil(ancestorsValues);
	}

	private AgentUtil getAgentUtil(HashMap<Integer, AgentValue> ancestorsValues) {
		HashMap<Integer, AgentValue> relevantAssignments = new HashMap<Integer, AgentValue>();
		for (int i: ancestorsValues.keySet()) {
			if (relevantAncestors.contains(i)) {
				relevantAssignments.put(i, ancestorsValues.get(i));
			}
		}	
		int i = getKey(relevantAssignments);
		if (agentUtils.containsKey(i)) {
			return agentUtils.get(i);
		}
		return null;
	}

	public int getNoOfChildrenUtils() {
		return childrenUtils.size();
	}

	public AgentUtil getRootUtil() {
		return agentUtils.get(0);
	}

	public long getSize() {
		long size = 1;
		for (int i : relevantAncestors) {
			size = size * problem.getDomainSize(i);
		}
		return size;
	}

	public int getUtil(AgentUtil other, int i) {
		HashMap<Integer, AgentValue> v = new HashMap<Integer, AgentValue>();
		v.put(other.getAgentID(), new AgentValue(other.getAgentID(), i));
		v.putAll(other.agentValues);
		return getUtil(v);
	}

	public int getUtil(HashMap<Integer, AgentValue> v) {
		AgentUtil a = getAgentUtil(v);
		assert a != null;
		return a.util;
	}

	public int getValue(HashMap<Integer, AgentValue> ancestorsValues) {
		AgentUtil a = getAgentUtil(ancestorsValues);
		if (a == null) {
			return -1;
		}
		return a.value;
	}
	
	private int getKey(HashMap<Integer, AgentValue> h) {
		int sum = 0;
		for (int i=0; i<problem.getNumberOfVariables(); i++) {
			if (h.containsKey(i)) {
				sum = sum + (int)Math.pow(problem.getDomainSize(0), i)*h.get(i).value;
			}
		}
		return sum;	
	}
	
	private int getKey(AgentUtil au) {
		return getKey(au.agentValues);
	}
	
	public String toString() {
		return "Agent " + this.agent + " Utils:\n" + this.agentUtils + "\n";
	}


	@Override
	public Object deepCopy() {
		
		DPOPUtil ans = new DPOPUtil(this.problem, this.agent, this.domainSize);
		
		ans.agentUtils = new TreeMap<Integer, AgentUtil>();
		ans.childrenUtils = new LinkedList<DPOPUtil>();
		ans.relevantAncestors = new LinkedList<Integer>();
		
		for (Entry<Integer, AgentUtil> au : this.agentUtils.entrySet()){
			ans.agentUtils.put(au.getKey(), au.getValue());
		}
		for (DPOPUtil u : this.childrenUtils){
			ans.childrenUtils.add((DPOPUtil) u.deepCopy());
		}
		for (Integer i : this.relevantAncestors){
			ans.relevantAncestors.add(i);
		}
		return ans;
	}
		


}
