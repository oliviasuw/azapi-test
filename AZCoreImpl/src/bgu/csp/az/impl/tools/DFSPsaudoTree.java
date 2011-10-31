/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.tools;

import java.util.Set;
import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Mailer;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.tools.NesteableTool;
import bgu.csp.az.api.tools.PsaudoTree;
import bgu.csp.az.api.tools.RootSelectionAlgorithm;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class DFSPsaudoTree{} //TODO FIX IT!!!
//
///**
// *
// * @author bennyl
// */
//public class DFSPsaudoTree extends NesteableTool implements PsaudoTree {
//
//    private static final int COLOR_BLACK = 0;
//    private static final int COLOR_WHITE = 1;
//    private static final int COLOR_GRAY = 2;
//    
//    List<Integer> children;
//    List<Integer> pchildren;
//    Integer parent;
//    ArrayList<Integer> pparents;
//    Set<Integer> seperator;
//    
//    private Agent callingAgent;
//    private RootSelectionAlgorithm lsa;
//    private int color = COLOR_WHITE;
//    private List<Integer> neighbors;
//    private boolean[] dones;
//    private int root = 0;
//    private int depth = 0;
//    private List<Integer> descendants;
//    private List<Integer> pParentsDepths;
//    private PlatformOps mpops;
//
//    public DFSPsaudoTree() {
//        children = new LinkedList<Integer>();
//        pchildren = new LinkedList<Integer>();
//        parent = -1;
//        pparents = new  ArrayList<Integer>();
//        seperator = new HashSet<Integer>();
//        descendants = new LinkedList<Integer>();
//        pParentsDepths = new LinkedList<Integer>();
//    }
//
//    @Override
//    public void log(String what) {
//        System.out.println("Tree for agent " + callingAgent.getId() + ": " + what);
//    }
//
//    @Override
//    public List<Integer> getChildren() {
//        return children;
//    }
//
//    @Override
//    public List<Integer> getPsaudoChildren() {
//        return pchildren;
//    }
//
//    @Override
//    public Integer getParent() {
//        return parent;
//    }
//
//    @Override
//    public List<Integer> getPsaudoParents() {
//        return pparents;
//    }
//
//    @Override
//    public void calculate(Agent a) {
//        calculate(a, new RootSelectionAlgorithm.Default());
//    }
//
//    @Override
//    public void calculate(Agent a, RootSelectionAlgorithm lsa) {
//        callingAgent = a;
//        this.lsa = lsa;
//        start();
//    }
//
//    @Override
//    public void start() {
//        PlatformOps tpops = Agent.PlatformOperationsExtractor.extract(callingAgent);
//        this.mpops = Agent.PlatformOperationsExtractor.extract(this);
//        mpops.setExecution(tpops.getExecution());
//        mpops.setId(callingAgent.getId() + callingAgent.getNumberOfVariables());
//        
//        mpops.getExecution().fire("pseudo-tree-agent-created", "id", "" + callingAgent.getId());
//        
//        final Mailer mailer = mpops.getExecution().getMailer();
//        mailer.register(this);
//
//        for (int i = getNumberOfVariables(); i < getNumberOfVariables() * 2; i++) {
//            try {
//                mailer.waitFor(i);
//            } catch (InterruptedException ex) {
//                panic(ex);
//            }
//        }
//
//        dones = new boolean[getNumberOfVariables()];
//        for (int i = 0; i < dones.length; i++) {
//            dones[i] = false;
//        }
//        root = lsa.select(callingAgent);
//
//        if (callingAgent.getId() == root) {
//            dfsVisit();
//        }
//
//        try {
//            startMailLoop();
//            descendants.remove((Integer) callingAgent.getId());
//        } catch (InterruptedException ex) {
//            panic(ex);
//        }
//    }
//
//    private void startMailLoop() throws InterruptedException {
//        while (!isFinished()/*finish*/ && !Thread.currentThread().isInterrupted()) {
//            processNextMessage();
//        }
//        log("unregistering!");
//        mpops.getExecution().getMailer().unRegister(getId());
//    }
//
//    private void dfsVisit() {
//        color = COLOR_GRAY;
//        neighbors = getProblem().getNeighbors(callingAgent.getId());
//        if (parent != null) {
//            neighbors.remove(parent);
//        }
//        neighbors.removeAll(pparents);
//        visitNextNeighbor();
//    }
//
//    private void visitNextNeighbor() {
//        if (neighbors.size() > 0) {
//            send("VISIT", depth+1).to(neighbors.get(0) + getNumberOfVariables());
//        } else {
//            noMoreNeighbors();
//        }
//    }
//
//    @WhenReceived("VISIT")
//    public void handleVisit(int pDepth) {
//        final int p = getCurrentMessage().getSender();
//        final int sendingAgent = p - getNumberOfVariables();
//
//        if (color == COLOR_WHITE) {
//            parent = sendingAgent;
//            depth = pDepth;
//            seperator.add(parent);
//            dfsVisit();
//        } else if (color == COLOR_BLACK) {
//            insertPseudoParent(sendingAgent,depth);
//            seperator.add(sendingAgent);
//            send("SET_PSAUDO_CHILD", descendants).to(p);
//        } else {
//            send("REFUSE_VISIT").to(p);
//        }
//    }
//
//    @WhenReceived("SET_CHILD")
//    public void handleSetChild(Set<Integer> childSeperator, LinkedList<Integer> childDescendants) {
//
//        final Integer node = getCurrentMessage().getSender() - getNumberOfVariables();
//        children.add(node);
//        
//        mpops.getExecution().fire("pseudo-tree-connection", "type", "normal", "parent", ""+callingAgent.getId(), "child", ""+node);
//        
//        seperator.addAll(childSeperator);
//        seperator.remove(callingAgent.getId());
//        
//        descendants.remove(node);
//        descendants.removeAll(childDescendants);
//        
//        descendants.add(node);
//        descendants.addAll(childDescendants);
//    }
//
//    @WhenReceived("SET_PSAUDO_CHILD")
//    public void handleSetPsaudoChild(LinkedList<Integer> childDescendants) {
//        final Integer node = getCurrentMessage().getSender() - getNumberOfVariables();
//        pchildren.add(node);
//        
//        mpops.getExecution().fire("pseudo-tree-connection", "type", "pseudo", "parent", ""+callingAgent.getId(), "child", ""+node);
//        
//        descendants.remove(node);
//        descendants.removeAll(childDescendants);
//        
//        descendants.add(node);
//        descendants.addAll(childDescendants);
//        handleDone();
//    }
//
//    @WhenReceived("DONE")
//    public void handleDone() {
//        if (!isRoot() && getCurrentMessage().getSender() - getNumberOfVariables() == root && !dones[callingAgent.getId()]) {
//            panic(new NotConnectivityGraphException("The recived problem not represents a conetivity graph"));
//        }
//
//        final Integer node = getCurrentMessage().getSender() - getNumberOfVariables();
//        dones[getCurrentMessage().getSender() - getNumberOfVariables()] = true;
//        if (allDone()) {
//            finish();//finish = true;
//        } else if (color == COLOR_GRAY && neighbors.get(0) == node) {
//            neighbors.remove(node);
//            visitNextNeighbor();
//        }
//    }
//
//    @WhenReceived("REFUSE_VISIT")
//    public void handleRefuseVisit() {
//        final Integer node = getCurrentMessage().getSender() - getNumberOfVariables();
//        neighbors.remove(node);
//        visitNextNeighbor();
//    }
//
//    private void noMoreNeighbors() {
//        color = COLOR_BLACK;
//        dones[getId() - getNumberOfVariables()] = true;
//        if (parent >= 0) { // not root
//            send("SET_CHILD", seperator, descendants).to(parent + getNumberOfVariables());
//        }
//        send("DONE").toAll(range(getNumberOfVariables(), getNumberOfVariables() * 2 - 1));
//    }
//
//    @Override
//    protected SimpleMessage beforeMessageProcessing(SimpleMessage msg) {
//        log("handling message: " + msg.getName() + " from " + (msg.getSender() - getNumberOfVariables()));
//        return msg;
//    }
//
//    @Override
//    public String toString() {
//        return "Vertex: " + callingAgent.getId() + "\r\n"
//                + "Children: " + str(children) + "\r\n"
//                + "Psaudo Childen: " + str(pchildren) + "\r\n"
//                + "Parent: " + parent + "\r\n"
//                + "Psaudo Parents: " + str(pparents) + "\r\n";
//    }
//
//    private boolean allDone() {
//        for (boolean b : dones) {
//            if (!b) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public boolean isRoot() {
//        return callingAgent.getId() == root;
//    }
//
//    @Override
//    public boolean isLeaf() {
//        return getChildren().isEmpty();
//    }
//
//    @Override
//    public Set<Integer> getSeperator() {
//        return seperator;
//    }
//    
//    @Override
//    public int getDepth(){
//        return depth;
//    }
//    
//    @Override
//    public List<Integer> getDescendants(){
//        return descendants;
//    }
//
//    @Override
//    public List<Integer> getPseudoParentDepths() {
//        return pParentsDepths;
//    }
//
//    
//    public void insertPseudoParent(int agentIdx, int depth) {
//        int index = findInsertionIdx(depth);
//        pParentsDepths.add(index,depth);
//        pparents.add(index,agentIdx);
//    }
//
//	public int findInsertionIdx(int depth) {
//		if(pParentsDepths.isEmpty())
//			return 0;
//		//else binary search this vector:
//		int low=0,high=pParentsDepths.size();
//		while(high > low && pParentsDepths.get(low) < depth){
//			int mid = low + (high-low)/2;
//			if(pParentsDepths.get(mid) < depth)
//				low = mid+1;
//			else
//				high = mid;
//		}
//		return low;
//	}
//
//    @Override
//    protected SimpleAgent createNestedAgent() {
//        return new DFSTreeComputingAgent();
//    }
//
//    public class DFSTreeComputingAgent extends SimpleAgent{
//
//        @Override
//        public void start() {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//        
//    }
//
//
//        
//}
