/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;

/**
 *
 * @author Administrator
 */
public interface Visualization<STATE> {
    public Class<? extends VisualizationDrawer> getViewType();
    public void initialize(Execution ex, VisualizationFrameBuffer<STATE> buffer);
}
