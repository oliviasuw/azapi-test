/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.exen.Execution;
import java.util.List;

/**
 *
 * @author Administrator
 */
public interface Visualization<STATE> {
    public Class<? extends VisualizationDrawer> getViewType();
    public List<STATE> sample();
    public void initialize(Execution ex);
}
