/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import bgu.dcr.az.api.prob.Problem;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Zovadi
 */
public class AgentVertex extends Vertex {

    private final Set<VariableVertex> controlledVariables;

    public AgentVertex(int id, Problem p) {
        super(id, VertexType.Agent);
        controlledVariables = new HashSet<>();

        for (int v : p.getVariablesOwnedByAgent(id)) {
            controlledVariables.add(new VariableVertex(v, this));
        }
    }

    public Set<VariableVertex> getControlledVariables() {
        return controlledVariables;
    }

    @Override
    public void draw(GraphicsContext gc) {
    }
}
