/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Zovadi
 */
public class Edge {

    public final static Color EDGE_COLOR = Color.RED;

    private final Vertex u;
    private final Vertex v;
    private final EdgeType type;

    public Edge(Vertex u, Vertex v) {
        this.u = u;
        this.v = v;

        switch (u.getType()) {
            case Agent:
                if (Vertex.VertexType.Agent.equals(v.getType())) {
                    this.type = EdgeType.AgentAgent;
                } else {
                    throw new UnsupportedOperationException("Only Agent-to-Agent and Variable-to-Variable edges supported");
                }
                break;
            case Variable:
                if (Vertex.VertexType.Variable.equals(v.getType())) {
                    this.type = EdgeType.VariableVariable;
                } else {
                    throw new UnsupportedOperationException("Only Agent-to-Agent and Variable-to-Variable edges supported");
                }
                break;
            case Link:
                switch (v.getType()) {
                    case Agent:
                        this.type = EdgeType.AgentAgent;
                        break;
                    case Variable:
                        this.type = EdgeType.VariableVariable;
                        break;
                    case Link:
                        throw new UnsupportedOperationException("Only Agent-to-Agent and Variable-to-Variable edges supported");
                    default:
                        throw new UnsupportedOperationException("Only Agent-to-Agent and Variable-to-Variable edges supported");
                }
            default:
                throw new UnsupportedOperationException("Only Agent-to-Agent and Variable-to-Variable edges supported");
        }
    }

    public void draw(GraphicsContext gc) {
        if (!Edge.EdgeType.VariableVariable.equals(getType())) {
            return;
        }

        gc.setStroke(EDGE_COLOR);
        gc.strokeLine(u.getX(), u.getY(), v.getX(), v.getY());
    }

    public Vertex getU() {
        return u;
    }

    public Vertex getV() {
        return v;
    }

    public EdgeType getType() {
        return type;
    }

    public static enum EdgeType {

        VariableVariable, AgentAgent
    }
}
