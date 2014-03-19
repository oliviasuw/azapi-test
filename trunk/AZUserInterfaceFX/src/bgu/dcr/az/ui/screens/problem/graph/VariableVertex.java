/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Zovadi
 */
public class VariableVertex extends Vertex {

    private final static double CIRCLE_RADIUS = 15;
    private final static double BORDER_SIZE = 2;
    private final Font LABEL_FONT = Font.getDefault();
    private final Color OUTER_COLOR = Color.BLACK;
    private final Color INNER_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = Color.RED;

    private final AgentVertex owner;

    public VariableVertex(int id, AgentVertex owner) {
        super(id, VertexType.Variable);
        this.owner = owner;
    }

    public AgentVertex getOwner() {
        return owner;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(OUTER_COLOR);
        gc.fillOval(getX() - CIRCLE_RADIUS, getY() - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);

        gc.setFill(INNER_COLOR);
        gc.fillOval(getX() - CIRCLE_RADIUS + BORDER_SIZE, getY() - CIRCLE_RADIUS + BORDER_SIZE, (CIRCLE_RADIUS - BORDER_SIZE) * 2, (CIRCLE_RADIUS - BORDER_SIZE) * 2);

        gc.setFill(TEXT_COLOR);
        gc.setFont(LABEL_FONT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("" + getId(), getX(), getY());
    }

}
