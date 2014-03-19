/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.ui.screens.problem.graph;

import java.util.Objects;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Zovadi
 */
public abstract class Vertex {

    private final int id;
    private double x;
    private double y;
    private final VertexType type;

    public Vertex(int id, VertexType type) {
        this(id, Math.random() * 500, Math.random() * 500, type);
    }

    public Vertex(int id, double x, double y, VertexType type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public VertexType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        hash = 29 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vertex other = (Vertex) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    public abstract void draw(GraphicsContext gc);

    public static enum VertexType {

        Agent, Variable, Link
    }
}
