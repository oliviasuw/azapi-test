/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import bgu.dcr.az.api.prob.Problem;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.HashMap;
import java.util.stream.IntStream;

/**
 *
 * @author Zovadi
 */
public class ProblemGraph {

    private final Graph<Vertex, Edge> problemGraph;

    public ProblemGraph(Problem p) {
        if (p.type().isAsymmetric()) {
            throw new UnsupportedOperationException("Only symmetric problems supported (will be extended later)");
        } else {
            if (p.type().isBinary()) {
                this.problemGraph = new UndirectedSparseGraph<>();
                initializeSymmetricBinaryProblemGraph(p);
            } else {
                throw new UnsupportedOperationException("Only binary constraints supported ny now");
            }
        }
    }

    private void initializeSymmetricBinaryProblemGraph(Problem p) {
        HashMap<Integer, VariableVertex> variableVertices = new HashMap<>();
        HashMap<Integer, AgentVertex> agentVertices = new HashMap<>();

        IntStream.range(0, p.getNumberOfAgents()).forEach(aid -> agentVertices.put(aid, new AgentVertex(aid, p)));
        agentVertices.values().forEach(problemGraph::addVertex);
        agentVertices.values().forEach(av -> {
            av.getControlledVariables().forEach(vv -> variableVertices.put(vv.getId(), vv));
        });
        variableVertices.values().forEach(problemGraph::addVertex);

        int[][] adep = new int[p.getNumberOfAgents()][p.getNumberOfAgents()];

        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < i; j++) {
                if (p.isConstrained(i, j)) {
                    VariableVertex u = variableVertices.get(i);
                    VariableVertex v = variableVertices.get(j);
                    problemGraph.addEdge(new Edge(u, v), u, v);
                    if (adep[u.getOwner().getId()][v.getOwner().getId()] == 0) {
                        problemGraph.addEdge(new Edge(u.getOwner(), v.getOwner()), u.getOwner(), v.getOwner());
                        adep[u.getOwner().getId()][v.getOwner().getId()] = adep[v.getOwner().getId()][u.getOwner().getId()] = 1;
                    }
                }
            }
        }
    }

    public Graph<Vertex, Edge> getProblemGraph() {
        return problemGraph;
    }

}
