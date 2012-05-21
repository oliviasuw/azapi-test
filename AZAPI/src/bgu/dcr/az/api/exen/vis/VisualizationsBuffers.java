/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import bgu.dcr.az.api.exen.VisualExperiment;
import bgu.dcr.az.api.exen.mdef.Visualization;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class VisualizationsBuffers implements VisualizationFrameSynchronizer.FrameSyncListener{
    Map<Visualization, VisualizationBuffer> buffers = new IdentityHashMap<Visualization, VisualizationBuffer>(); //dangares but fast...
    Visualization[] visualizations;

    public VisualizationsBuffers(VisualExperiment vexp) {
        for (Visualization v : vexp.getVisualizations()){
            buffers.put(v, new VisualizationBuffer());
        }
        
        // for faster iteration..
        visualizations = vexp.getVisualizations().toArray(new Visualization[0]);
    }
        
    public VisualizationBuffer getBufferFor(Visualization vis){
        return buffers.get(vis);
    }

    @Override
    public void onFrameSync() {
        for (Visualization v : visualizations){
            buffers.get(v).buffer(v.sample());
        }
    }
    
}
