/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.vis;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exen.vis.VisualExecutionRunner;
import bgu.dcr.az.api.exen.vis.VisualizationBuffer;
import bgu.dcr.az.api.exen.vis.VisualizationFrameSynchronizer;

/**
 *
 * @author Administrator
 */
public class VisualExecutionRunnerImpl implements VisualExecutionRunner {

    private Execution execution;
    private Visualization visualization = null;
    private VisualizationBuffer vbuf = null;
    private VisualizationFrameSynchronizer fsync = new VisualizationFrameSynchronizer();

    public VisualExecutionRunnerImpl(Execution execution) {
        this.execution = execution;
    }

    @Override
    public Execution getRunningExecution() {
        return execution;
    }

    public void setVisualization(Visualization vis) {
        visualization = vis;
    }

    @Override
    public boolean isFinished() {
        return execution.isFinished();
    }

    @Override
    public void stop() {
        execution.stop();
    }

    @Override
    public void run() {
        try {
            vbuf = new VisualizationBuffer();
            execution.setVisualizationFrameSynchronizer(this.fsync);
            this.fsync.addFrameSyncListener(this);
            this.fsync.setExecution(execution);
            visualization.initialize(execution);
            execution.run();
            this.fsync.fireFrameSync(); //final frame..
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Visualization getLoadedVisualization() {
        return this.visualization;
    }

    @Override
    public VisualizationBuffer getLoadedVisualizationBuffer() {
        return vbuf;
    }

    @Override
    public void onFrameSync() {
        vbuf.buffer(visualization.sample());
    }
}
