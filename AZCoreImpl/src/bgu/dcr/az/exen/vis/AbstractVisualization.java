/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.vis;

import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;

/**
 *
 * @author Administrator
 */
public abstract class AbstractVisualization<STATE> implements Visualization<STATE> {

    private Class<? extends VisualizationDrawer> viewType;

    public AbstractVisualization(Class<? extends VisualizationDrawer> viewType) {
        this.viewType = viewType;
    }

    @Override
    public Class<? extends VisualizationDrawer> getViewType() {
        return viewType;
    }
}
