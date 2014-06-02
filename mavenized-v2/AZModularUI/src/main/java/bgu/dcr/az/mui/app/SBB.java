package bgu.dcr.az.mui.app;

import java.util.LinkedList;
import java.util.List;

import bgu.dcr.az.dcr.api.Assignment;
import bgu.dcr.az.dcr.api.SimpleAgent;
import bgu.dcr.az.dcr.api.annotations.Algorithm;
import bgu.dcr.az.dcr.api.annotations.WhenReceived;

@Algorithm("_SBB")
public class SBB extends SimpleAgent {

    Assignment cpa, best;
    List<Integer> currentDomain;

    @Override
    public void start() {
        if (isFirstAgent()) {
            cpa = new Assignment();
            currentDomain = new LinkedList<>(getDomain());
            assignCpa();
        }
    }

    private void assignCpa() {
        if (currentDomain.isEmpty()) {
            backtrack();
            return;
        }

        Integer minimum = cpa.findMinimalCostValue(getId(), currentDomain, getProblem());
        cpa.assign(getId(), minimum);
        currentDomain.remove(minimum);

        if (costOf(cpa) < costOf(best)) {
            if (isLastAgent()) {
                best = cpa.deepCopy();
                backtrack();
            } else {
                send("CPA", cpa).toNextAgent();
            }
        } else {
            backtrack();
        }
    }

    private void backtrack() {
        if (isFirstAgent()) {
            finish(best);
        } else {
            cpa.unassign(getId());
            send("BACKTRACK", cpa, best).toPreviousAgent();
        }
    }

    @WhenReceived("CPA")
    public void handleCPA(Assignment cpa) {
        this.cpa = cpa;
        currentDomain = new LinkedList<>(getDomain());
        assignCpa();
    }

    @WhenReceived("BACKTRACK")
    public void handleBACKTRACK(Assignment cpa, Assignment best) {
        this.cpa = cpa;
        this.best = best;

        assignCpa();
    }

}
