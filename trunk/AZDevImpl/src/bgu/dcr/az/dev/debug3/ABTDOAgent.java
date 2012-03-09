/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev.debug3;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;
import java.util.*;

@Algorithm(name = "ABTDO", useIdleDetector = true)
public class ABTDOAgent extends SimpleAgent {

    @Variable(name = "udo", defaultValue = "true", description = "use dynamic ordering")
    boolean udo = true;

    public static final int COUNTER = 1;
    public static final int PRIORITY = 0;
    int[][] currentOrder;
    List<Integer> lowerPriorityAgents;
    List<Integer> higherPriorityAgents;
    List<Integer> neighbores;
    Assignment agentView = new Assignment();
    List<Assignment> nogoods = new LinkedList<>();
    int myValue;

    @Override
    public void start() {

        if (isFirstAgent()) {
            if (udo) {
                System.out.println("ABT-DO Running");
            } else {
                System.out.println("ABT Running");
            }
        }

        currentOrder = new int[getNumberOfVariables()][2];
        for (int i = 0; i < currentOrder.length; i++) {
            currentOrder[i][PRIORITY] = getNumberOfVariables() - i - 1;
            currentOrder[i][COUNTER] = 0;
        }
        fixPriorities();
        neighbores = new LinkedList<>(getNeighbors());
        myValue = 0;
        //log("ok? => " + str(neighboresWithLowerPriority()));
//        send("ok?", getId(), myValue).toAll(neighboresWithLowerPriority());
        send("ok?", getId(), myValue).toAll(neighbores);
//        checkAgentView();
    }

    @Override
    public void onIdleDetected() {
//        System.out.println("Finish");
        finish(myValue);
    }

    @WhenReceived("ok?")
    public void handleOk(int xj, int dj) {
        agentView.assign(xj, dj);
        //log("now my agent view is " + agentView);
        removeInconsistentNOGOODs();
        checkAgentView();
    }

    @Override
    protected Message beforeMessageProcessing(Message msg) {
//        System.out.println("A" + getId() + " => " + msg + "(order: " + str(currentOrder) + ", agentView: " + agentView + ").");
        ////log("got " + msg);
        return super.beforeMessageProcessing(msg);
    }

    @WhenReceived("order")
    public void handleOrder(int[][] receivedOrder) {
        if (checkifMoreUpdated(receivedOrder)) {
            System.out.println("Took new Order");
            currentOrder = receivedOrder;
            fixPriorities();
            removeInconsistentNOGOODs();
            checkAgentView();
        }
    }

    @WhenReceived("nogood")
    public void handleNogood(int xj, Assignment nogood) {
        int xk = LowestPriorityAgent(nogood);
        panicIf(currentOrder[xk][PRIORITY]>currentOrder[getId()][PRIORITY], "I'm not in the nogood!!!!");
        if (xk != getId()) {
//            //log("nogood => " + xk + ", ok? => " + xj);
//            panicIf(currentOrder[xk][PRIORITY] >= currentOrder[getId()][PRIORITY], "not following order");
            send("nogood", this.getId(), nogood).to(xk);
            send("ok?", this.getId(), myValue).to(xj);
        } else {
            boolean cons = isConsistent(agentView, nogood, true);//checkIfnoGoodConsistent(nogood);
//            //log("consistent = " + cons + " nogood: " + nogood + " aview: " + agentView);
            if (cons) {
                nogood.assign(getId(), myValue);
                nogoods.add(nogood);
                LinkedList<Integer> xs = getNoNeighbores(nogood);
                for (Integer _xk : xs) {
                    neighbores.add(_xk);
                    //log("add-neighbor => " + _xk);
                    send("add-neighbor", this.getId()).to(_xk);
                    int dk = nogood.getAssignment(_xk);
                    agentView.assign(_xk, dk);
                }
//                myValue = -1;
                checkAgentView();
            } else {
//                //log("ok => " + xj);
                send("ok?", this.getId(), myValue).to(xj);
            }
        }
    }

    @WhenReceived("add-neighbor")
    public void handleAddNeighbor(int xj) {
        neighbores.add(xj);
        //log("ok? => " + xj);
        send("ok?", getId(), myValue).to(xj);
    }

    private void checkAgentView() {
        //if my value not good anymore
        if (myValue == -1 || !isAgentViewConsistent()) {
            if (!selectConsistentValue()) {
                //cant find other value to assign
                //log("backtracking because " + agentView + " value " + myValue + ", nogoods: " + Objects.toString(nogoods));
                backtrack();
            } else {
//                found new value lets notify others


//                //log("order => " + str(lowerPriorityAgents) + "\ncurrentOrder" + str(currentOrder));
                //log("ok? => " + str(lowerPriorityAgents) + " order: " + str(currentOrder) + " to " + str(neighboresWithLowerPriority()));
//                System.out.println("A" + getId() + " send ok? " + myValue + " to all " + str(neighboresWithLowerPriority()) + ", order: " + str(currentOrder));
//                send("ok?", this.getId(), myValue).toAll(neighboresWithLowerPriority());
                send("ok?", this.getId(), myValue).toAll(neighbores);

                if (udo && getCurrentMessage().getName().equals("nogood") && lowerPriorityAgents.contains((Integer) getCurrentMessage().getSender())) {
                    chooseNewOrder();
                    send("order", (Object) currentOrder).toAll(lowerPriorityAgents);
                }
            }
        } else {
//            //log("myvalue is " + myValue + ", agentview is " + agentView);
        }

    }

    private void backtrack() {
        Assignment nogood = resolveInconsistentSubset();

        if (nogood.assignedVariables().isEmpty()) {
//            System.out.println("Finish with no solution");
            finishWithNoSolution();
            return;
        }

        int xj = findLowestPriority(nogood);
        int dj = nogood.getAssignment(xj);
//        nogood.unassign(xj);
        //log("sending nogood to " + xj + "nogood: " + nogood);
//        panicIf(currentOrder[xj][PRIORITY] >= currentOrder[getId()][PRIORITY], "not following order - myp = " + currentOrder[getId()][PRIORITY] + " hisp = " + currentOrder[xj][PRIORITY]);
        send("nogood", this.getId(), nogood).to(xj);
        agentView.unassign(xj);
        removeAllNogoodsContaining(xj, dj);
        myValue = -1;
        checkAgentView();
    }

    private int[] genLookup(int[][] order) {
        int[] lookup = new int[getNumberOfVariables()];
        for (int i = 0; i < order.length; i++) {
            lookup[order[i][PRIORITY]] = i;
        }
        return lookup;
    }

    private void removeAllNogoodsContaining(int xj, int dj) {
        List<Assignment> toRemove = new LinkedList<>();
        for (Assignment ng : nogoods) {
            if (ng.isAssigned(xj) && (ng.getAssignment(xj) == dj)) {
                toRemove.add(ng);
            }
        }

        nogoods.removeAll(toRemove);
    }

    @Override
    public void log(String what) {
//        System.out.println("Agent " + getId() + ": " + what);
        //super.//log(what);
    }

    private int findLowestPriority(Assignment nogood) {
        int minP = -1;
        int ans = -1;

        for (Integer av : nogood.assignedVariables()) {
            if (minP == -1 || minP > currentOrder[av][PRIORITY]) {
                minP = currentOrder[av][PRIORITY];
                ans = av;
            }
        }
        return ans;
    }

    private Assignment resolveInconsistentSubset() {
        Assignment unification = new Assignment();
        for (Assignment ng : nogoods) {
            for (Integer i : ng.assignedVariables()) {
                unification.assign(i, ng.getAssignment(i));
            }
        }

        unification.unassign(getId());
        return unification;
    }

    private void chooseNewOrderRandomly() {

//        //log("order before choosing " + str(currentOrder));
        Integer[] lowers = lowerPriorityAgents.toArray(new Integer[0]);
        lowers = shuffle(lowers);

        for (int i = 0; i < lowers.length; i++) {
            currentOrder[lowers[i]][PRIORITY] = i;
            currentOrder[lowers[i]][COUNTER] = 0;
        }

        currentOrder[getId()][COUNTER]++;
//        //log("new order " + str(currentOrder));

        Set<Integer> allCheck = new HashSet<>();
        for (int i = 0; i < currentOrder.length; i++) {
            panicIf(allCheck.contains(currentOrder[i][PRIORITY]), "two variables with the same priority: " + currentOrder[i][PRIORITY]);
            allCheck.add(currentOrder[i][PRIORITY]);
        }
    }

    private void swapOrder(int i, int j) {
        int tp = currentOrder[i][PRIORITY];
        currentOrder[i][PRIORITY] = currentOrder[j][PRIORITY];
        currentOrder[j][PRIORITY] = tp;
    }

    private void chooseNewOrder() {
        int sender = getCurrentMessage().getSender();
        int[] lookup = genLookup(currentOrder);

        Integer[] lowers = lowerPriorityAgents.toArray(new Integer[0]);
        for (int i = 0; i < lowers.length; i++) {
            currentOrder[lowers[i]][COUNTER] = 0;
        }

        currentOrder[getId()][COUNTER]++;

        while (currentOrder[sender][PRIORITY] != currentOrder[getId()][PRIORITY] - 1) {
//    try{
            swapOrder(sender, lookup[currentOrder[sender][PRIORITY] + 1]);
//    }catch(Exception ex){
//        System.out.println("HERE");
//    }
        }
//        
//        Integer[] lowers = lowerPriorityAgents.toArray(new Integer[0]);
//        lowers = shuffle(lowers);
//        
//        for (int i = 0; i < lowers.length; i++) {
//            currentOrder[lowers[i]][PRIORITY] = i;
//            currentOrder[lowers[i]][COUNTER] = 0;
//        }
//
//        currentOrder[getId()][COUNTER]++;
////        //log("new order " + str(currentOrder));
//
//        Set<Integer> allCheck = new HashSet<>();
//        for (int i = 0; i < currentOrder.length; i++) {
//            panicIf(allCheck.contains(currentOrder[i][PRIORITY]), "two variables with the same priority: " + currentOrder[i][PRIORITY]);
//            allCheck.add(currentOrder[i][PRIORITY]);
//        }
    }

    private int findConsistentValue(Assignment from) {
        LinkedList<Integer> dom = new LinkedList<>(getDomain());

        for (Assignment ng : nogoods) {
            if (dom.isEmpty()) {
                break;
            }
            dom.remove((Integer) ng.getAssignment(getId()));
        }

        panicIf(from.isAssigned(getId()), "assignment contains me!");
        for (Integer vi : dom) {
            boolean good = true;
            for (Integer j : from.assignedVariables()) {
                if (getConstraintCost(getId(), vi, j, from.getAssignment(j)) != 0) {
                    nogoods.add(new Assignment(getId(), vi, j, from.getAssignment(j)));
                    good = false;
                }
            }
            if (good) {
                return vi;
            }
        }

        return -1;
    }

    private boolean isConsistentWithNoGoods(int val) {


        for (Assignment ng : nogoods) {
            panicIf(!ng.isAssigned(getId()), "i should be in any of the nogoods");
            if (ng.getAssignment(getId()) == val) {
                return false;
            }
        }
        return true;
    }

    private boolean selectConsistentValue() {
        Assignment highPriorAV = agentView.deepCopy();
        for (Integer i : lowerPriorityAgents) {
            highPriorAV.unassign(i);
        }
        int temp = findConsistentValue(highPriorAV);
        //log("trying to select a value to assign found: " + temp + ""
//                + "\nand the agent view is " + agentView + ""
//               + "\nand my no goods are " + str(nogoods));

        if (temp != -1) {
            myValue = temp;
            //log("new value selected: " + myValue + " in assignment " + agentView);
            return true;
        }

        return false;
    }

    private void removeInconsistentNOGOODs() {
        LinkedList<Assignment> toRemove = new LinkedList<>();
        for (Assignment ng : nogoods) {
            if (ng.getNumberOfAssignedVariables() > 1 && !isConsistent(agentView, ng, false)) {
                toRemove.add(ng);
            }
        }

        nogoods.removeAll(toRemove);
        //log("no goods: " + str(toRemove) + " removed.");
    }

    private LinkedList<Integer> getNoNeighbores(Assignment nogood) {
        LinkedList<Integer> ret = new LinkedList<>(nogood.assignedVariables());
        ret.removeAll(neighbores);
        ret.remove((Integer) getId());
        return ret;
    }

    private int LowestPriorityAgent(Assignment nogood) {
        int ans = -1;
        int minP = -1;
        for (int agt : nogood.assignedVariables()) {
            int hisP = currentOrder[agt][PRIORITY]; 
            if (ans == -1 || minP > hisP ) {
                ans = agt;
                minP = hisP;
            }
        }
        return ans;
    }

    private void fixPriorities() {
        lowerPriorityAgents = new LinkedList<>();
        higherPriorityAgents = new LinkedList<>();

        int myPrior = currentOrder[getId()][PRIORITY];
        for (int i = 0; i < currentOrder.length; i++) {
            if (i != getId()) {
                if (currentOrder[i][PRIORITY] > myPrior) {
                    higherPriorityAgents.add(i);
                } else {
                    lowerPriorityAgents.add(i);
                }
            }
        }
    }

    private List<Integer> neighboresWithLowerPriority() {
        LinkedList<Integer> ret = new LinkedList<>(neighbores);
        ret.retainAll(lowerPriorityAgents);
        return ret;
    }

    private boolean isConsistent(Assignment agentView, Assignment nogood, boolean checkMe) {
        panicIf(agentView.isAssigned(getId()), "Inna Was Wrong! - im in the agent view");
        if (checkMe && nogood.getAssignment(getId()) != myValue) {
            return false;
        }
        Assignment higherPriorAV = agentView.deepCopy();
        for (Integer i : lowerPriorityAgents) {
            higherPriorAV.unassign(i);
        }
        for (Integer vi : higherPriorAV.assignedVariables()) {
            if (nogood.isAssigned(vi) && nogood.getAssignment(vi) != higherPriorAV.getAssignment(vi)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAgentViewConsistent() {
//        Assignment higherPriorAV = agentView.deepCopy();
//        for (Integer i : lowerPriorityAgents) {
//            higherPriorAV.unassign(i);
//        }
        return (isConsistentWithNoGoods(myValue) && agentView.isConsistentWith(getId(), myValue, getProblem()));
    }

    private boolean checkifMoreUpdated(int[][] receivedOrder) {
        int[] myLookup = genLookup(currentOrder);
        int[] hisLookup = genLookup(receivedOrder);


        for (int i = 0; i < currentOrder.length; i++) {
            int oMy = myLookup[getNumberOfVariables() - i - 1];
            int oHis = hisLookup[getNumberOfVariables() - i - 1];
            if (currentOrder[oMy][COUNTER] == receivedOrder[oHis][COUNTER] && oMy == oHis) {
                continue;
            }
//            if (oMy != oHis) return false;
//            System.out.println("myOrder is " + str(currentOrder) + ", hisOrder is " + str(receivedOrder) 
//                                         + "\noMy is " + oMy + ", oHis is " + oHis);
            
            if (currentOrder[oMy][COUNTER] < receivedOrder[oHis][COUNTER]) {
                return true;
            }

            if (currentOrder[oMy][COUNTER] == receivedOrder[oHis][COUNTER] && oHis < oMy) {
                return true;
            }

            return false;
        }
        return panic("same order time stamp exactly!");
    }
}
