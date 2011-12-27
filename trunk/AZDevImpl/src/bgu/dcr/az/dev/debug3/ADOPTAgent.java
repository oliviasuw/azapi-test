/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import java.util.HashSet;
import java.util.Set;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ProblemType;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;

@Algorithm(problemType = ProblemType.DCOP, name = "__ADOPT", useIdleDetector = true)
public class ADOPTAgent extends SimpleAgent {

    private Set<Integer> neighbors;
    private Set<Integer> childrens;
    private Integer parent;
    private boolean isTerminateReceived;
    private double threshold;
    private Integer currentValue;
    private Assignment currentContext;
    private AdoptBoundsData[][] boundsData;

    @Override
    public void start() {
        calculateChilds();

        System.out.println("Agent: " + getId() + " parent: " + parent + " neighbors: " + neighbors + " childrens: " + childrens);

        isTerminateReceived = false;

        threshold = 0.0;

        currentContext = new Assignment();

        boundsData = new AdoptBoundsData[getDomain().size()][getProblem().getNumberOfVariables()];

        for (Integer val : getDomain()) {
            for (Integer var : childrens) {
                boundsData[val][var] = new AdoptBoundsData();
            }
        }

        currentValue = chooseMinLowerBoundValue();

        backTrack();
    }
    private int[] colors;
    private int[] visitTime;
    private int timer;

    private void calculateChilds() {
        neighbors = new HashSet<Integer>();
        childrens = new HashSet<Integer>();

        parent = null;

        visitTime = new int[getProblem().getNumberOfVariables()];
        colors = new int[getProblem().getNumberOfVariables()];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = 0;
            visitTime[i] = getProblem().getNumberOfVariables() + 1;
        }

        timer = 0;

        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == 0) {
                dfsVisit(i);
            }
        }
    }

    private void dfsVisit(int parnt) {
        Integer current = getId();

        visitTime[parnt] = timer++;
        colors[parnt] = 1;

        for (Integer var : getProblem().getNeighbors(parnt)) {
            if (parnt == current) {
                if (visitTime[current] < visitTime[var]) {
                    neighbors.add(var);
                }
            }

            if (colors[var] == 0) {
                if (var == current) {
                    parent = parnt;
                }

                if (parnt == current) {
                    childrens.add(var);
                }

                dfsVisit(var);
            }
        }
    }

    @WhenReceived("THRESHOLD")
    public void handleTHRESHOLD(Double t, Assignment context) {
        if (compatible(context, currentContext)) {
            threshold = t;

            mantainThresholdInvariant();

            backTrack();
        }
    }

    @WhenReceived("TERMINATE")
    public void handleTERMINATE(Assignment context) {
        isTerminateReceived = true;

        currentContext = context;

        backTrack();
    }

    @WhenReceived("VALUE")
    public void handleVALUE(Integer variable, Integer value) {
        if (!isTerminateReceived) {
            currentContext.assign(variable, value);

            mantainBoundsData();

            mantainThresholdInvariant();

            backTrack();
        }
    }

    @WhenReceived("COST")
    public void handleCOST(Integer variable, Assignment context, Double lb, Double ub) {
        Integer current = getId();
//System.out.println("Received Cost(" + variable + ","+ context + "," + lb + "," + ub + ") at agent " + current + " " + currentContext);
        Integer value = context.getAssignment(current);

        context.unassign(current);

        if (!isTerminateReceived) {
            for (Integer var : context.assignedVariables()) {
                if (!neighbors.contains(var)) {
                    currentContext.assign(var, context.getAssignment(var));
                }
            }

            mantainBoundsData();
        }

        if (compatible(context, currentContext)) {
            AdoptBoundsData data = boundsData[value][variable];

            data.setLowerBound(lb);
            data.setUpperBound(ub);
            data.setContext(context);

            mantainChildThresholdInvariant();

            mantainThresholdInvariant();
        }

        backTrack();
    }

    private void backTrack() {
        Integer current = getId();

        if (threshold == upperBound()) {
            currentValue = chooseMinUpperBoundValue();
        } else {
            if (lowerBound(currentValue) > threshold) {
                currentValue = chooseMinLowerBoundValue();
            }
        }

//System.out.println("Sent VALUE(" + current + "," + currentValue + ") to " + neighbors);		
        send("VALUE", current, currentValue).toAll(neighbors);

        mantainAllocationInvariant();

        if (threshold == upperBound()) {
            if (isTerminateReceived || parent == null) {
                Assignment cpa = currentContext.deepCopy();

                cpa.assign(current, currentValue);

//System.out.println("Sent TERMINATE(" + cpa + ") to " + neighbors + " at agent " + current);				
                send("TERMINATE", cpa).toAll(childrens);
                submitCurrentAssignment(currentValue);
                System.out.println("Submitted: " + current + "=" + currentValue);
                isTerminateReceived = true;
                //finish(currentValue);
                return;
            }
        }

        if (parent != null && currentContext.isAssigned(parent)) {
//System.out.println("Sent COST(" + current + "," + currentContext + "," + lowerBound() + "," + upperBound() + ") to " + parent);			
            send("COST", current, currentContext, lowerBound(), upperBound()).to(parent);
        }
    }

    private void mantainBoundsData() {
        for (Integer val : getDomain()) {
            for (Integer var : childrens) {
                AdoptBoundsData data = boundsData[val][var];

                if (!compatible(data.getContext(), currentContext)) {
                    data.reset();
                }
            }
        }
    }

    private void mantainThresholdInvariant() {
        Double lb = lowerBound();
        Double ub = upperBound();

        if (threshold < lb) {
            threshold = lb;
        }
        if (threshold > ub) {
            threshold = ub;
        }
    }

    private void mantainAllocationInvariant() {
        double accumulatedThreshold;
        AdoptBoundsData[] arr = boundsData[currentValue];
        Set<Integer> changed = new HashSet<Integer>();

        while (threshold > (accumulatedThreshold = localCost(currentValue) + accumulateThreshhold(currentValue))) {
            for (Integer var : childrens) {
                if (arr[var].getUpperBound() > arr[var].getThreshold()) {
                    arr[var].setThreshold(Math.min(arr[var].getUpperBound(), threshold - accumulatedThreshold));
                    changed.add(var);
                    break;
                }
            }
        }

        while (threshold < (accumulatedThreshold = localCost(currentValue) + accumulateThreshhold(currentValue))) {
            for (Integer var : childrens) {
                if (arr[var].getThreshold() > arr[var].getLowerBound()) {
                    arr[var].setThreshold(Math.min(arr[var].getLowerBound(), accumulatedThreshold - threshold));
                    changed.add(var);
                    break;
                }
            }
        }

        for (Integer var : childrens) {
            AdoptBoundsData data = arr[var];
//System.out.println("Sent THRESHOLD(" + data.getThreshold() + "," + currentContext + ") to " + var);			
            send("THRESHOLD", data.getThreshold(), currentContext).to(var);
        }
    }

    private void mantainChildThresholdInvariant() {
        for (Integer value : getDomain()) {
            for (Integer child : childrens) {
                AdoptBoundsData data = boundsData[value][child];
                if (data.getLowerBound() > data.getThreshold()) {
                    data.setThreshold(data.getLowerBound());
                }
            }
        }

        for (Integer value : getDomain()) {
            for (Integer child : childrens) {
                AdoptBoundsData data = boundsData[value][child];
                if (data.getThreshold() > data.getUpperBound()) {
                    data.setThreshold(data.getUpperBound());
                }
            }
        }
    }

    private Integer chooseMinLowerBoundValue() {
        Integer value = null;

        Double min = Double.POSITIVE_INFINITY;

        for (Integer val : getDomain()) {
            Double lb = lowerBound(val);

            if (min > lb) {
                min = lb;

                value = val;
            }
        }

        return value;
    }

    private Integer chooseMinUpperBoundValue() {
        Integer value = null;

        Double min = Double.POSITIVE_INFINITY;

        for (Integer val : getDomain()) {
            Double ub = upperBound(val);

            if (min > ub) {
                min = ub;

                value = val;
            }
        }

        return value;
    }

    private boolean compatible(Assignment ass1, Assignment ass2) {
        for (Integer var : ass1.assignedVariables()) {
            if (ass2.isAssigned(var)
                    && !ass1.getAssignment(var).equals(ass2.getAssignment(var))) {
                return false;
            }
        }

        for (Integer var : ass2.assignedVariables()) {
            if (ass1.isAssigned(var)
                    && !ass1.getAssignment(var).equals(ass2.getAssignment(var))) {
                return false;
            }
        }

        return true;
    }

    private Double accumulateThreshhold(Integer value) {
        Double cost = 0.0;
        AdoptBoundsData[] arr = boundsData[value];

        for (Integer var : childrens) {
            cost += arr[var].getThreshold();
        }

        return cost;
    }

    private Double localCost(Integer value) {
        Integer current = getId();

        Double cost = 0.0;

        for (Integer var : currentContext.assignedVariables()) {
            if (isConstrained(current, var)) {
                cost += getConstraintCost(current, value, var, currentContext.getAssignment(var));
            }
        }

        return cost;
    }

    private Double lowerBound(Integer value) {
//        panic("this is a test");
        Double cost = localCost(value);
        AdoptBoundsData[] arr = boundsData[value];

        for (Integer var : childrens) {
            AdoptBoundsData data = arr[var];
            cost += data.getLowerBound();
        }

        return cost;
    }

    private Double upperBound(Integer value) {
        Double cost = localCost(value);
        AdoptBoundsData[] arr = boundsData[value];

        for (Integer var : childrens) {
            AdoptBoundsData data = arr[var];

            cost += data.getUpperBound();
        }

        return cost;
    }

    private Double lowerBound() {
        Double min = Double.POSITIVE_INFINITY;

        for (Integer val : getDomain()) {
            Double lb = lowerBound(val);

            min = min > lb ? lb : min;
        }

        return min;
    }

    private Double upperBound() {
        Double min = Double.POSITIVE_INFINITY;

        for (Integer val : getDomain()) {
            Double ub = upperBound(val);

            min = min > ub ? ub : min;
        }

        return min;
    }

    @Override
    protected Message beforeMessageProcessing(Message msg) {
        return (isTerminateReceived && !msg.getName().equals(Agent.SYS_TERMINATION_MESSAGE)) ? null : msg;
    }

    @Override
    public void onIdleDetected() {
        if (isFirstAgent()) {
            finishWithAccumulationOfSubmitedPartialAssignments();
        }
    }

    private static class AdoptBoundsData {

        private Double lowerBound;
        private Double upperBound;
        private Double threshold;
        private Assignment context;

        public AdoptBoundsData() {
            reset();
        }

        public void reset() {
            lowerBound = 0.0;
            upperBound = Double.POSITIVE_INFINITY;
            threshold = 0.0;

            context = new Assignment();
        }

        public Double getLowerBound() {
            return lowerBound;
        }

        public void setLowerBound(Double lowerBound) {
            this.lowerBound = lowerBound;
        }

        public Double getUpperBound() {
            return upperBound;
        }

        public void setUpperBound(Double upperBound) {
            this.upperBound = upperBound;
        }

        public Double getThreshold() {
            return threshold;
        }

        public void setThreshold(Double threshold) {
            this.threshold = threshold;
        }

        public Assignment getContext() {
            return context;
        }

        public void setContext(Assignment context) {
            this.context = context;
        }

        @Override
        public String toString() {
            return "Context: " + context + " lb: " + lowerBound + " t: " + threshold + " ub: " + upperBound;
        }
    }
}