/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.vis.Canvas;
import bgu.dcr.az.api.exen.vis.ExecutionSnapshot;
import bgu.dcr.az.api.exen.vis.ExecutionTime;

/**
 *
 * @author Administrator
 */
public interface Visualizator {
    void initialize(Execution ex, Canvas canvas);
    void update(ExecutionSnapshot snapshot, ExecutionTime time);
    void draw(Canvas canvas, ExecutionTime time);
}
