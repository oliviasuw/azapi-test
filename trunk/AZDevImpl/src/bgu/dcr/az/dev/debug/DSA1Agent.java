package bgu.dcr.az.dev.debug;

import bc.ds.TimeDelta;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.SearchType;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;

@Algorithm(name = "DSA", searchType = SearchType.SYNCHRONOUS, problemType = ProblemType.DCOP)
public class DSA1Agent extends SimpleAgent {

    private Assignment values;
    private double p;
    
    @Override
    public void start() {
        values = new Assignment();
        p = 0.5;
        int value = random(this.getDomain());
        this.submitCurrentAssignment(value);
        send("ValueMessage", value).toNeighbores(this.getProblem());
    }

    @WhenReceived("ValueMessage")
    public void handleValueMessage(int value) {
        values.assign(getCurrentMessage().getSender(), value);
    }

    @Override
    public void onMailBoxEmpty() {
        if (getSystemTimeInTicks() + 1 == 20000 && isFirstAgent()) {
            finishWithAccumulationOfSubmitedPartialAssignments();
        }

        Integer newValue = calcDelta();
        if (Math.random() > p && newValue != null) {
            submitCurrentAssignment(newValue);
            send("ValueMessage", newValue).toNeighbores(this.getProblem());
        }
    }

    private Integer calcDelta() {
        int ans = this.getSubmitedCurrentAssignment();
        double delta = this.values.calcAddedCost(this.getId(), ans, this.getProblem());
        double tmpDelta = delta;
        for (Integer i : this.getDomain()) {
            double tmp = this.values.calcAddedCost(this.getId(), i, this.getProblem());
            if (tmp < tmpDelta) {
                tmpDelta = tmp;
                ans = i;
            }
        }
        if (delta == tmpDelta) {
            return null;
        }
        return ans;
    }
}
