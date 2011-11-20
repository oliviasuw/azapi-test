package bgu.csp.az.dev.debug;

import bgu.csp.az.api.Message;
import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.SearchType;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.tools.Assignment;
import bgu.csp.az.api.tools.TimeStamp;

@Algorithm(name = "AFB", searchType=SearchType.ASYNCHRONOUS, problemType=ProblemType.DCOP)
public class AFBAgent extends SimpleAgent {
	private int b;
	private Assignment cpa, bestCpa;
	private int[] estimates;
	private int[] h;
	private TimeStamp timeStamp;

	@Override
	public void start() {
		timeStamp = new TimeStamp(this);
		b = Integer.MAX_VALUE;
		estimates = new int[this.getProblem().getNumberOfVariables()];
		h = new int[this.getProblem().getDomainSize(this.getId())];
		fillH();
		if (isFirstAgent()) {
			System.out.println(getProblem().toString());
			generateCPA();
			assignCPA();
		}
	}

	@Override
	protected void beforeMessageSending(Message m) {
		m.getMetadata().put("TIMESTAMP", timeStamp);
	}

	private void assignCPA() {
		clearEstimations();
		int v = -1;
		int lastAssignedValue = (cpa.isAssigned(this.getId()) ? cpa
				.getAssignment(this.getId()) : -1);
		cpa.unassign(this.getId());

		for (int i = lastAssignedValue + 1; i < this.getDomainSize(); i++) {
			if (costOf(cpa) + calcFv(i, cpa) < b) {
				v = i;
				break;
			}
		}

		if (v == -1) {
			backtrack();
		} else {
			cpa.assign(this.getId(), v);
			timeStamp.incLocalTime();
			if (cpa.getNumberOfAssignedVariables() == this.getProblem()
					.getNumberOfVariables()) {
				broadcast("NEW_SOLUTION", cpa);
				b = (int) costOf(cpa);
				assignCPA();
			} else {
				send("CPA_MSG", cpa).toNextAgent();
				send("FB_CPA", this.getId(), cpa).toAllAgentsAfterMe();
			}
		}
	}

	private void clearEstimations() {
		this.estimates = new int[this.getProblem().getNumberOfVariables()];
	}

	private void generateCPA() {
		cpa = new Assignment();

	}

	private void backtrack() {
		clearEstimations();
		// log("backtracking");
		if (this.isFirstAgent()) {
			finish(bestCpa);
		} else {
			cpa.unassign(this.getId());
			send("CPA_MSG", cpa).toPreviousAgent();
		}

	}

	private int calcMinf(Assignment pa) {
		int ans = 0;
		int tmp = Integer.MAX_VALUE;
		int fv = 0;
		for (int v = 0; v < this.getProblem().getDomainSize(this.getId()); v++) {
			fv = calcFv(v, pa);
			if (tmp > fv) {
				tmp = fv;
			}
		}
		ans = tmp;
		return ans;
	}

	private int calcFv(int v, Assignment pa) {
		// log ("H(v) for v = " + v + " is :" + h[v]);
		int ans = (int) (pa.calcAddedCost(this.getId(), v, this.getProblem()) + h[v]);
		return ans;
	}

	private void fillH() {
		for (int v = 0; v < this.getProblem().getDomainSize(this.getId()); v++) {
			h[v] = calculateHv(v);
		}
	}

	private int calculateHv(int v) {
		int ans = 0;
		int cost = 0;
		int tmp = 0;
		for (int aj = this.getId() + 1; aj < this.getProblem()
				.getNumberOfVariables(); aj++) {
			tmp = Integer.MAX_VALUE;
			cost = 0;
			for (int u = 0; u < this.getProblem().getDomainSize(aj); u++) {
				cost = (int) this.getConstraintCost(this.getId(), v, aj, u);
				if (tmp > cost) {
					tmp = cost;
				}
			}
			ans += tmp;
		}
		return ans;
	}

	private double estimatesSum() {
		int ans = 0;
		for (int i = 0; i > estimates.length; i++) {
			ans += estimates[i];
		}
		return ans;
	}

	@Override
	protected Message beforeMessageProcessing(Message msg) {
		if (msg.getName().equals(SYS_TERMINATION_MESSAGE)) {
			return msg;
		}

		TimeStamp hisTimeStamp = (TimeStamp) msg.getMetadata().get("TIMESTAMP");
		if (hisTimeStamp.compare(timeStamp, this) >= 0) {
			timeStamp.copyFrom(hisTimeStamp);
		} else {
			return null;
		}

		return super.beforeMessageProcessing(msg);
	}

	@WhenReceived("FB_CPA")
	public void handleFBCPA(int aj, Assignment pa) {
		int f = calcMinf(pa);
		// log("sending FB_ESTIMATE to " + aj );
		send("FB_ESTIMATE", f, pa, this.getId()).to(aj);
	}

	@WhenReceived("CPA_MSG")
	public void handleCPAMSG(Assignment pa) {
		this.cpa = pa;
		Assignment tempCpa = pa.deepCopy();
		tempCpa.unassign(this.getId());
		if (costOf(tempCpa) >= b) {
			// log("backtracking");
			backtrack();
		} else {
			// log("assigning CPA");
			assignCPA();
		}

	}

	@WhenReceived("FB_ESTIMATE")
	public void handleFBESTIMATE(int estimate, Assignment pa, int aj) {
		estimates[aj] = estimate;

		if (costOf(cpa) + estimatesSum() >= b) {
			assignCPA();
		} 
	}

	@WhenReceived("NEW_SOLUTION")
	public void handleNEWSOLUTION(Assignment pa) {
		bestCpa = pa;
		b = (int) costOf(pa);
	}

}
