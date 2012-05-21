/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exen.vis.VisualizationFrameSynchronizer;
import bgu.dcr.az.api.exen.vis.VisualizationsBuffers;
import java.util.List;

/**
 *
 * @author Administrator
 */
public interface VisualExperiment extends Experiment{
    public List<Visualization> getVisualizations(); 
    public VisualizationsBuffers getVisualizationBuffers();
    public VisualizationFrameSynchronizer getVisualizationFrameSynchronizer();
}
