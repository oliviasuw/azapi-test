/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.proc.impl.actions;

import bgu.dcr.az.vis.proc.api.Entity;
import bgu.dcr.az.vis.proc.api.VisualScene;
import bgu.dcr.az.vis.proc.impl.Location;
import javafx.animation.KeyValue;

/**
 *
 * @author Zovadi
 */
public class MoveAction extends SingleEntityAction {

    private final Location destination;
    private Location initialLocation;

    public MoveAction(long entityId, Location destination, double duration) {
        super(entityId, duration);
        this.destination = destination;
    }

    @Override
    protected void _init(VisualScene scene) {
        Entity entity = scene.getEntity(getEntityId());
        initialLocation = new Location(entity.locationProperty().get().getX(), entity.locationProperty().get().getY());
    }

    @Override
    protected void _tick(VisualScene scene, double duration) {
        if (!isFinished()) {
            Entity entity = scene.getEntity(getEntityId());
            double percentage = getCurrentTime() / getDuration();
            double sx = entity.locationProperty().get().getX();
            double sy = entity.locationProperty().get().getY();
            double ex = destination.getX();
            double ey = destination.getY();
            
            double tx = (ex - sx) * percentage + sx;
            double ty = (ey - sy) * percentage + sy;
            
            scene.addDelayedTransformation(new KeyValue(entity.locationProperty().get().xProperty(), tx));
            scene.addDelayedTransformation(new KeyValue(entity.locationProperty().get().yProperty(), ty));
        }
    }

    @Override
    public void execute(VisualScene scene) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
