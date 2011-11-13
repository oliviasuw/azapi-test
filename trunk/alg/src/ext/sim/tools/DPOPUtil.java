package ext.sim.tools;


import java.util.LinkedList;
import java.util.List;

import bgu.csp.az.api.DeepCopyable;
import bgu.csp.az.api.ImuteableProblem;
import bgu.csp.az.api.tools.Assignment;
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
		protected LinkedList<AgentValue> agentValues;
		protected int util;
		protected int value;

		protected AgentUtil(AgentUtil u) {
			this(u.agentID);
			this.value = u.value;
			this.util = u.util;
			for (AgentValue a : u.agentValues) {
				this.agentValues.add((AgentValue) a.deepCopy());
			}
		}

		protected AgentUtil(int agent) {
			this.agentID = agent;
			this.value = -1;
			this.util = -1;
			agentValues = new LinkedList<AgentValue>();
		}

		public void add(int agent, int value) {
			AgentValue a = new AgentValue(agent, value);
			agentValues.add(a);
		}

		@Override
		public int compareTo(AgentUtil other) {
			return this.util - other.util;
		}

		private boolean containsAgentValue(AgentValue ao) {
			for (AgentValue a : agentValues) {
				if (a.equalAgentValue(ao)) {
					return true;
				}
			}
			return false;
		}

		public int dim() {
			return agentValues.size();
		}

		public int get_Value(int id) {
			for (AgentValue a : agentValues) {
				if (a.getAgent() == id) {
					return a.getValue();
				}
			}
			return -1;
		}

		protected int getAgentID() {
			return this.agentID;
		}

		public LinkedList<AgentValue> getAgentValues() {
			return this.agentValues;
		}

		protected int getUtil() {
			return this.util;
		}

		public int getValue() {
			return this.value;
		}

		public boolean isConsistent(Assignment cpa) {
			for (AgentValue a : agentValues) {
				if (cpa.isAssigned(a.getAgent()) && cpa.getAssignment(a.getAgent()) != a.getValue()) {
					return false;
				}
			}
			return true;
		}

		public boolean isConsistent(AgentUtil u) {
			for (AgentValue a : u.agentValues) {
				if (!containsAgentValue(a)) {
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
			ans.agentValues = new LinkedList<DPOPUtil.AgentValue>();
			ans.util = this.util;
			for (AgentValue av : this.agentValues){
				ans.agentValues.add((AgentValue) av.deepCopy());
			}
			ans.value=this.value;
			return ans;
		}
	}
	protected int agent;
	protected LinkedList<AgentUtil> agentUtils;
	protected LinkedList<DPOPUtil> childrenUtils;
	protected int domainSize;

	protected ImuteableProblem problem;

	protected LinkedList<Integer> relevantAncestors;

	public DPOPUtil(ImuteableProblem problem, int agentId, int domain) {
		this.problem = problem;
		this.agent = agentId;
		this.domainSize = domain;
		agentUtils = new LinkedList<AgentUtil>();
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
				for (AgentValue ass : au.getAgentValues()) {
					u = (u + da.getConstraintCost(agent, value, ass.agent, ass.value));
					if (u > min_cost)
						continue;
				}
				if (u > min_cost)
					continue;
				for (DPOPUtil cu : childrenUtils) {
					u = u + cu.getUtil(au, value);
					if (u > min_cost)
						break;
				}
				if (u < min_cost) {
					min_cost = u;
					au.setValue(value);
				}
			}
			au.util = (int) min_cost;
			agentUtils.add(au);
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
				for (AgentValue ass : au.getAgentValues()) {
					u = u + da.getConstraintCost(agent, value, ass.agent, ass.value);
					if (u > min_cost)
						continue;
				}
				if (u < min_cost) {
					min_cost = u;
					au.setValue(value);
				}
			}
			au.util = (int) min_cost;
			agentUtils.add(au);
		}
	}

	

	private void computeRootUtil(DPOPAgent da) {
		AgentUtil au = new AgentUtil(agent);
		double min_cost = Integer.MAX_VALUE;
		for (int value = 0; value < this.domainSize; value++) {
			double u = da.getConstraintCost(agent, value);
			for (DPOPUtil cu : childrenUtils) {
				u = u + cu.getUtil(au, value);
				if (u > min_cost)
					break;
			}
			if (u < min_cost) {
				min_cost = u;
				au.setValue(value);
			}
		}
		au.util = (int) min_cost;
		agentUtils.add(au);
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

	private boolean containsAgentValue(LinkedList<AgentValue> v, AgentValue a1) {
		for (AgentValue a2 : v) {
			if (a1.equalAgentValue(a2)) {
				return true;
			}
		}
		return false;
	}

	private boolean equalAgentValues(LinkedList<AgentValue> v1,LinkedList<AgentValue> v2) {
		if (v1.size() != v2.size()) {
			return false;
		}
		for (AgentValue a1 : v1) {
			if (!containsAgentValue(v2, a1)) {
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
		LinkedList<AgentValue> ancestorsValues = new LinkedList<AgentValue>();
		for (int a = 0; a < problem.getNumberOfVariables(); a++) {
			if (cpa.isAssigned(a)) {
				ancestorsValues.add(new AgentValue(a, cpa.getAssignment(a)));
			}
		}
		return getAgentUtil(ancestorsValues);
	}

	private AgentUtil getAgentUtil(LinkedList<AgentValue> ancestorsValues) {
		LinkedList<AgentValue> relevantAgentValues = new LinkedList<AgentValue>();
		for (AgentValue a1 : ancestorsValues) {
			for (AgentValue a2 : agentUtils.get(0).agentValues) {
				if (a1.agent == a2.agent) {
					relevantAgentValues.add(a1);
					break;
				}
			}
		}
		for (AgentUtil u : agentUtils) {
			if (equalAgentValues(u.agentValues, relevantAgentValues)) {
				return u;
			}
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
		LinkedList<AgentValue> v = new LinkedList<AgentValue>();
		v.add(new AgentValue(other.getAgentID(), i));
		v.addAll(other.agentValues);
		return getUtil(v);
	}

	public int getUtil(LinkedList<AgentValue> ancestorsValues) {
		AgentUtil a = getAgentUtil(ancestorsValues);
		assert a != null;
		return a.util;
	}

	public int getValue(LinkedList<AgentValue> ancestorsValues) {
		AgentUtil a = getAgentUtil(ancestorsValues);
		if (a == null) {
			return -1;
		}
		return a.value;
	}

	public String toString() {
		return "Agent " + this.agent + " Utils:\n" + this.agentUtils + "\n";
	}


	@Override
	public Object deepCopy() {
		
		DPOPUtil ans = new DPOPUtil(this.problem, this.agent, this.domainSize);
		
		ans.agentUtils = new LinkedList<AgentUtil>();
		ans.childrenUtils = new LinkedList<DPOPUtil>();
		ans.relevantAncestors = new LinkedList<Integer>();
		
		for (AgentUtil au : this.agentUtils){
			ans.agentUtils.add((AgentUtil) au.deepCopy());
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
