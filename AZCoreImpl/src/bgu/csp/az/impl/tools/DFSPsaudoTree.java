/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.csp.az.impl.tools;

import java.util.Set;
import bgu.csp.az.api.Agent;
import bgu.csp.az.api.Message;
import bgu.csp.az.api.agt.SimpleAgent;
import bgu.csp.az.api.ano.WhenReceived;
import bgu.csp.az.api.tools.NesteableTool;
import bgu.csp.az.api.tools.PsaudoTree;
import bgu.csp.az.api.tools.RootSelectionAlgorithm;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class DFSPsaudoTree extends NesteableTool implements PsaudoTree {

    private static final int COLOR_BLACK = 0;
    private static final int COLOR_WHITE = 1;
    private static final int COLOR_GRAY = 2;
    private List<Integer> children;
    private List<Integer> pchildren;
    private Integer parent;
    private int root = 0;
    private int vertexId;
    private List<Integer> pparents;
    private Set<Integer> seperator;
    private RootSelectionAlgorithm lsa;
    private int depth = 0;
    private List<Integer> descendants;
    private List<Integer> pParentsDepths;

    public DFSPsaudoTree() {
        children = new LinkedList<Integer>();
        pchildren = new LinkedList<Integer>();
        parent = -1;
        pparents = new LinkedList<Integer>();
        seperator = new HashSet<Integer>();
        descendants = new LinkedList<Integer>();
        pParentsDepths = new LinkedList<Integer>();
    }

    @Override
    public List<Integer> getChildren() {
        return children;
    }

    @Override
    public List<Integer> getPsaudoChildren() {
        return pchildren;
    }

    @Override
    public Integer getParent() {
        return parent;
    }

    @Override
    public List<Integer> getPsaudoParents() {
        return pparents;
    }

    @Override
    public String toString() {
        return "Vertex: " + vertexId + "\r\n"
                + "Children: " + Agent.str(children) + "\r\n"
                + "Psaudo Childen: " + Agent.str(pchildren) + "\r\n"
                + "Parent: " + parent + "\r\n"
                + "Psaudo Parents: " + Agent.str(pparents) + "\r\n";
    }

    @Override
    public boolean isRoot() {
        return vertexId == root;
    }

    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    @Override
    public Set<Integer> getSeperator() {
        return seperator;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public List<Integer> getDescendants() {
        return descendants;
    }

    @Override
    public List<Integer> getPseudoParentDepths() {
        return pParentsDepths;
    }

    @Override
    protected SimpleAgent createNestedAgent() {
        return new DFSTreeComputingAgent();
    }

    public class DFSTreeComputingAgent extends SimpleAgent {

        private int color = COLOR_WHITE;
        private List<Integer> neighbors;
        private boolean[] dones;

        @Override
        public void start() {
              
//            log("starting dfs");
            vertexId = this.getId();
            dones = new boolean[getNumberOfVariables()];
            for (int i = 0; i < dones.length; i++) {
                dones[i] = false;
            }
            root = 0;//lsa.select(callingAgent);

            if (this.getId() == root) {
//                log("starting dfsVisit");
                dfsVisit();
            }
        }

        @Override
        public void handleTermination() {
//            log("terminating");
            super.handleTermination();
            descendants.remove(this.getId());
        }

        private void dfsVisit() {
            color = COLOR_GRAY;
            neighbors = getProblem().getNeighbors(this.getId());
            if (parent != null) {
                neighbors.remove(parent);
            }
            neighbors.removeAll(pparents);
//            log("starting visitNext2");
            visitNextNeighbor();
        }

        private void visitNextNeighbor() {
//            log("in visitNext");
            if (neighbors.size() > 0) {
//                log("sending VISIT to" + neighbors.get(0));
                send("VISIT", depth + 1).to(neighbors.get(0));
            } else {
//                log("starting noMoreNeighbors");
                noMoreNeighbors();
            }
        }

        @Override
        protected Message beforeMessageProcessing(Message msg) {
        
//            log("got: " + msg);
            return super.beforeMessageProcessing(msg);
        }

        
        
        private void noMoreNeighbors() {
            color = COLOR_BLACK;
            dones[getId()] = true;
            if (parent >= 0) { // not root
                send("SET_CHILD", seperator, descendants).to(parent);
//                log("sending SET_CHILD to parent");
            }
            send("DONE").toAll(range(0, getProblem().getNumberOfVariables()-1));
//            log("sending DONE to "+ range(0, getProblem().getNumberOfVariables()-1).toString());
        }

        @WhenReceived("VISIT")
        public void handleVisit(int pDepth) {
            final int sendingAgent = getCurrentMessage().getSender();
//            log("visiting");
            if (color == COLOR_WHITE) {
                parent = sendingAgent;
                depth = pDepth;
                seperator.add(parent);
//                log("starting DFSVisit");
                dfsVisit();
            } else if (color == COLOR_BLACK) {
                insertPseudoParent(sendingAgent, depth);
                seperator.add(sendingAgent);
                send("SET_PSAUDO_CHILD", descendants).to(sendingAgent);
//                log("sending SET_PSAUDO_CHILD to "+ sendingAgent);
            } else {
                send("REFUSE_VISIT").to(sendingAgent);
//                log("sending REFUSE_VISIT to "+ sendingAgent);
            }
        }

        @WhenReceived("DONE")
        public void handleDone() {
//            log("handling DONE");
            if (!isRoot() && getCurrentMessage().getSender() == root && !dones[this.getId()]) {
                panic(new NotConnectivityGraphException("The recived problem not represents a conetivity graph"));
            }

            final Integer node = getCurrentMessage().getSender();
            dones[getCurrentMessage().getSender()] = true;
            if (allDone()) {
//                log("finishing");
                finish();//finish = true;
            } else if (color == COLOR_GRAY && neighbors.get(0) == node) {
                neighbors.remove(node);
//                log("starting visitNext1");
                visitNextNeighbor();
            }
        }

        @WhenReceived("SET_CHILD")
        public void handleSetChild(Set<Integer> childSeperator, LinkedList<Integer> childDescendants) {
//            log ("starting setChild");
            final Integer node = getCurrentMessage().getSender();
            children.add(node);

            seperator.addAll(childSeperator);
            seperator.remove(this.getId());

            descendants.remove(node);
            descendants.removeAll(childDescendants);

            descendants.add(node);
            descendants.addAll(childDescendants);
        }

        @WhenReceived("SET_PSAUDO_CHILD")
        public void handleSetPsaudoChild(LinkedList<Integer> childDescendants) {
//            log("handling SET_PSAUDO_CHILD");
            final Integer node = getCurrentMessage().getSender();
            pchildren.add(node);

            descendants.remove(node);
            descendants.removeAll(childDescendants);

            descendants.add(node);
            descendants.addAll(childDescendants);
            handleDone();
//            log("End handling SET_PSAUDO_CHILD");
        }

        @WhenReceived("REFUSE_VISIT")
        public void handleRefuseVisit() {
//            log("handling REFUSE_VISIT");
            final Integer node = getCurrentMessage().getSender();
            neighbors.remove(node);
            visitNextNeighbor();
//            log("End handling REFUSE_VISIT");
        }

        private boolean allDone() {
            for (boolean b : dones) {
                if (!b) {
                    return false;
                }
            }
            return true;
        }

        public void insertPseudoParent(int agentIdx, int depth) {
//            log("inserting pp");
            int index = findInsertionIdx(depth);
            pParentsDepths.add(index, depth);
            pparents.add(index, agentIdx);
        }

        public int findInsertionIdx(int depth) {
            if (pParentsDepths.isEmpty()) {
                return 0;
            }
            //else binary search this vector:
            int low = 0, high = pParentsDepths.size();
            while (high > low && pParentsDepths.get(low) < depth) {
                int mid = low + (high - low) / 2;
                if (pParentsDepths.get(mid) < depth) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
            }
            return low;
        }
    }
}
