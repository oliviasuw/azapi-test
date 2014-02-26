/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.vis.proc.impl.test;

import bgu.dcr.az.vis.proc.api.Entity;
import bgu.dcr.az.vis.proc.api.Layer;
import bgu.dcr.az.vis.proc.api.VisualScene;
import bgu.dcr.az.vis.proc.impl.CanvasLayer;
import bgu.dcr.az.vis.proc.impl.entities.SpriteBasedEntity;
import java.util.Arrays;
import java.util.Collection;
import resources.img.R;

/**
 *
 * @author Zovadi
 */
public class TrafficLoadVisualScene implements VisualScene {
    private final CanvasLayer layer;
    private final SpriteBasedEntity entity;

    public TrafficLoadVisualScene() {
        this.layer = new CanvasLayer();
        this.entity = new SpriteBasedEntity(0, 0, R.class.getResourceAsStream("car.jpg"));
    }
    
    
    @Override
    public Layer getLayer(long id) {
        return id == 0 ? layer : null;
    }

    @Override
    public Collection<Layer> getLayers() {
        return Arrays.asList(layer);
    }

    @Override
    public Entity getEntity(long id) {
        return id == 0 ? entity : null;
    }

    @Override
    public Collection<Entity> getEntities() {
        return Arrays.asList(entity);
    }

}
