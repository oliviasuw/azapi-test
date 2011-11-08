package bgu.csp.az.dev.debug;


import bgu.csp.az.api.ProblemType;
import bgu.csp.az.api.SearchType;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.Algorithm;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.tools.Assignment;

@Algorithm(name = "DSA", searchType = SearchType.SYNCHRONIZED, problemType= ProblemType.ADCOP)
public class DSAAgent extends SimpleAgent {

    private Assignment values;
    private double p;

    @Override
    public void start() {
        log("starting");
        values = new Assignment();
        p = 0.5;
        int value = random(this.getDomain());
        this.submitCurrentAssignment(value);
        send("ValueMessage", value).toNeighbores(this.getProblem());
    }

    @WhenReceived("ValueMessage")
    public void handleValueMessage(int value) {
        log("tick" + getSystemTime());
        values.assign(getCurrentMessage().getSender(), value);
    }

    @Override
    public void onMailBoxEmpty() {
        if (getSystemTime()+1 == 2000 && isFirstAgent()) {
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
