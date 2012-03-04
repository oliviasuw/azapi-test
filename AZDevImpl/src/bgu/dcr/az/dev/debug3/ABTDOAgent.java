/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import bgu.dcr.az.api.Message;
import java.util.HashMap;
import java.util.LinkedList;


import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.*;
import bgu.dcr.az.api.ano.WhenReceived;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Algorithm(name = "ABTDO", useIdleDetector = true)
public class ABTDOAgent extends SimpleAgent {

    Map<Integer, Integer> currentOrder;
    List<Integer> lowerPriorityAgents;
    List<Integer> higherPriorityAgents;
    List<Integer> neighbores;
    Assignment agentView = new Assignment();
    List<Entry<Integer, Assignment>> nogoods = new LinkedList<>();
    int myValue;

    @Override
    public void start() {
        currentOrder = new HashMap<>();
        for (int i = 0; i < getNumberOfVariables(); i++) {
            currentOrder.put(i, (getNumberOfVariables() - 1 - i));
        }

        fixPriorities();
        neighbores = new LinkedList<>(getNeighbors());
        myValue = 0;
        send("ok?", getId(), myValue).toAll(neighboresWithLowerPriority());
    }

    @Override
    public void onIdleDetected() {
        finish(myValue);
    }

    @WhenReceived("ok?")
    public void handleOk(int xj, int dj) {
        agentView.assign(xj, dj);
        removeInconsistentNOGOODs();
        checkAgentView();
    }

    @Override
    protected Message beforeMessageProcessing(Message msg) {
        logIf(!msg.getName().equals("order"), "got " + msg);
        return super.beforeMessageProcessing(msg);
    }

    @WhenReceived("order")
    public void handleOrder(Map<Integer, Integer> receivedOrder) {
//        if (checkifMoreUpdated(receivedOrder)) {
//            currentOrder = receivedOrder;
//            removeInconsistentNOGOODs();
//            checkAgentView();
//        }
    }

    @WhenReceived("nogood")
    public void handleNogood(int xj, Assignment nogood) {
        int xk = checkIfContainsLowerPriority(nogood);
        if (xk != -1) {
            send("nogood", this.getId(), nogood).to(xk);
            send("ok?", this.getId(), myValue).to(xj);
        } else {
            agentView.assign(getId(), myValue);
            boolean cons = noGoodConsistent(nogood, agentView);
            agentView.unassign(getId());
            log("consistent = " + cons);
            if (cons) {
                nogoods.add(new AbstractMap.SimpleEntry<>(xj, nogood));


                LinkedList<Integer> xs = getNoNeighbores(nogood);
                for (Integer _xk : xs) {
                    send("add-neighbor", this.getId()).to(_xk);
                    int dk = nogood.getAssignment(_xk);
                    agentView.assign(_xk, dk);
                }
                myValue = -1;
                checkAgentView();
            } else {
                send("ok?", this.getId(), myValue).to(xj);
            }
        }
    }

    @WhenReceived("add-neighbor")
    public void handleAddNeighbor(int xj) {
        neighbores.add(xj);
    }

    private void checkAgentView() {
//        log("checking agent view: " + agentView);
        if (myValue == -1 || !agentView.isConsistentWith(getId(), myValue, getProblem())) {
            if (!selectConsistentValue()) {
                backtrack();
            } else {
                currentOrder = chooseNewOrder();
                send("ok?", this.getId(), myValue).toAll(neighbores);
                send("order", currentOrder).toAll(lowerPriorityAgents);
            }
        }
    }

    private void backtrack() {
        Assignment nogood = resolveInconsistentSubset();
        if (nogood == null) {
            finishWithNoSolution();
            return;
        }

        int xj = findLowestPriority(nogood);
        send("nogood", this.getId(), nogood).to(xj);
        agentView.unassign(xj);
        removeAllNogoodsContaining(xj);
        myValue = -1;
        checkAgentView();
    }

    private void removeAllNogoodsContaining(int xj) {
        List<Entry<Integer, Assignment>> toRemove = new LinkedList<>();
        for (Entry<Integer, Assignment> ng : nogoods) {
            if (ng.getValue().isAssigned(xj)) {
                toRemove.add(ng);
            }
        }

        while (!toRemove.isEmpty()) {
            nogoods.remove(toRemove.remove(0));
        }
    }

    @Override
    public void log(String what) {
        super.log(what);
    }

    private int findLowestPriority(Assignment nogood) {
        int minP = -1;
        int ans = -1;

        for (Integer av : nogood.assignedVariables()) {
            if (minP == -1 || minP > currentOrder.get(av)) {
                minP = currentOrder.get(av);
                ans = av;
            }
        }

        return ans;
    }

    private Assignment resolveInconsistentSubset() {
        for (int i = 1; i <= agentView.getNumberOfAssignedVariables(); i++) {
            Assignment noGood = findNoGoodOfSize(new Assignment(), i);
            if (noGood != null) {
                return noGood;
            }
        }

        return null;
    }

    private Assignment findNoGoodOfSize(Assignment noGood, int size) {
        if (size == noGood.getNumberOfAssignedVariables()) {
            if (findConsistentValue(noGood) == -1) {
                return noGood;
            }
        }

        if (noGood.getNumberOfAssignedVariables() > size) {
            return null;
        }

        for (Integer av : agentView.assignedVariables()) {
            if (!noGood.isAssigned(av)) {
                noGood.assign(av, agentView.getAssignment(av));
                final Assignment please = findNoGoodOfSize(noGood, size);
                if (please != null) {
                    return please;
                }

                noGood.unassign(av);
            }
        }

        return null;
    }

    private Map<Integer, Integer> chooseNewOrder() {
        return this.currentOrder;
    }

    private int findConsistentValue(Assignment from) {
        for (Integer vi : getDomain()) {
            if (from.isConsistentWith(getId(), vi, getProblem()) && isConsistentWithNoGoods(vi)) {
                return vi;
            }
        }

        return -1;
    }

    private boolean isConsistentWithNoGoods(int val) {
        for (Entry<Integer, Assignment> ng : nogoods) {
            if (!(ng.getValue().isAssigned(getId()) && ng.getValue().getAssignment(getId()) != val)) {
                return false;
            }
        }
        return true;
    }

    private boolean selectConsistentValue() {
        int temp = findConsistentValue(agentView);
        if (temp != -1) {
            myValue = temp;
//            log("new value selected: " + myValue + " in assignment " + agentView);
            return true;
        }

        return false;
    }

    private boolean checkIfAssignmentConsistent() {
        return agentView.isConsistent(getProblem());
    }

    private void removeInconsistentNOGOODs() {
        LinkedList<Entry<Integer, Assignment>> toRemove = new LinkedList<>();

        for (Entry<Integer, Assignment> ng : nogoods) {
            if (!noGoodConsistent(ng.getValue(), agentView)) {
                toRemove.add(ng);
                //nogoods.remove(i);
            }
        }

        nogoods.removeAll(toRemove);
    }

    public boolean noGoodConsistent(Assignment nogood, Assignment with) {
        log("checking nogood consistency: nogood: " + nogood + ", with: " + with);
        for (int var : higherPriorityAgents) {
            if ((nogood.isAssigned(var)) && ((!with.isAssigned(var)) || (!nogood.getAssignment(var).equals(with.getAssignment(var))))) {
                return false;
            }
        }

        return true;
    }

    private LinkedList<Integer> getNoNeighbores(Assignment nogood) {
        LinkedList<Integer> ret = new LinkedList<>(nogood.assignedVariables());
        ret.removeAll(neighbores);
        ret.remove((Integer) getId());
        return ret;
    }

    private boolean checkIfConsistent(Assignment nogood) {
        // TODO Auto-generated method stub
        return false;
    }

    private int checkIfContainsLowerPriority(Assignment nogood) {
        int myP = currentOrder.get(getId());
        int minPrior = -1;
        int ans = -1;
        for (int xk : nogood.assignedVariables()) {
            final Integer xkPrior = currentOrder.get(xk);
            if (xkPrior < myP) {
                if (minPrior == -1 || minPrior > xkPrior) {
                    ans = xk;
                    minPrior = xkPrior;
                }
            }
        }

        return ans;
    }

    private boolean checkifMoreUpdated(Map<Integer, Integer> receivedOrder) {
        // TODO Auto-generated method stub
        return false;
    }

    private void fixPriorities() {
        lowerPriorityAgents = new LinkedList<>();
        higherPriorityAgents = new LinkedList<>();

        int myPrior = currentOrder.get(getId());
        for (Entry<Integer, Integer> p : currentOrder.entrySet()) {
            if (p.getKey() != getId()) {
                if (p.getValue() > myPrior) {
                    higherPriorityAgents.add(p.getKey());
                } else {
                    lowerPriorityAgents.add(p.getKey());
                }
            }
        }
    }

    private List<Integer> neighboresWithLowerPriority() {
        LinkedList<Integer> ret = new LinkedList<>(neighbores);
        ret.retainAll(lowerPriorityAgents);
        return ret;
    }
}
