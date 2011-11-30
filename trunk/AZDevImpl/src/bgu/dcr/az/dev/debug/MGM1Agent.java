package bgu.dcr.az.dev.debug;

import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.SearchType;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;

@Algorithm(name = "MGM", problemType = ProblemType.DCOP, searchType = SearchType.SYNCHRONOUS)
public class MGM1Agent extends SimpleAgent {

	private Assignment values;
	private int bestNewValue;
	private double[] gainValues;

	@Override
	public void start() {
		values = new Assignment();
		gainValues = new double[this.getProblem().getNumberOfVariables()];
		for (int i = 0; i < gainValues.length; i++) {
			gainValues[i] = Integer.MAX_VALUE;
		}
		int value = random(this.getDomain());
		this.submitCurrentAssignment(value);
		send("ValueMessage", value).toNeighbores(this.getProblem());
	}

	@WhenReceived("ValueMessage")
	public void handleValueMessage(int value) {
		values.assign(getCurrentMessage().getSender(), value);

	}

	@WhenReceived("GainMessage")
	public void handleGainMessage(double gain) {
		this.gainValues[getCurrentMessage().getSender()] = gain;
	}

	@Override
	public void onMailBoxEmpty() {
		final long systemTime = getSystemTimeInTicks();
		if (systemTime + 1 == 20000 && isFirstAgent()) {
			finishWithAccumulationOfSubmitedPartialAssignments();
		}
		if (systemTime % 2 == 0) {
			double[] returned = calcDelta();
			int newValue = (int) returned[0];
			double gain = returned[1];
			this.gainValues[this.getId()] = gain;
			if (newValue != this.getSubmitedCurrentAssignment()) {
				this.bestNewValue = newValue;
				send("GainMessage", gain).toNeighbores(this.getProblem());
			}
		} else {
			double myGain = this.gainValues[this.getId()];
			boolean best = true;
			for (int i = 0; i < this.gainValues.length; i++) {
				if (i != this.getId() && this.gainValues[i] < myGain) {
					best = false;
					break;
				}
			}
			if (best) {
				send("ValueMessage", this.bestNewValue).toNeighbores(
						this.getProblem());
				submitCurrentAssignment(bestNewValue);
			}
		}
	}

	private double[] calcDelta() {
		double[] ans = new double[2];
		ans[0] = this.getSubmitedCurrentAssignment();
		double delta = this.values.calcAddedCost(this.getId(), (int) ans[0],
				this.getProblem());
		double tmpDelta = delta;
		for (Integer i : this.getDomain()) {
			double tmp = this.values.calcAddedCost(this.getId(), i,
					this.getProblem());
			if (tmp < tmpDelta) {
				tmpDelta = tmp;
				ans[0] = i;
				ans[1] = tmpDelta;
			}
		}
		return ans;
	}
}
