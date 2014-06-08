/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.player.impl.CanvasLayer;
import bgu.dcr.az.vis.player.impl.entities.DefinedSizeSpriteBasedEntity;
import bgu.dcr.az.vis.presets.map.GroupScale;
import bgu.dcr.az.vis.tools.Convertor;
import bgu.dcr.az.vis.tools.Location;
import data.map.impl.wersdfawer.groupbounding.GroupBoundingQuery;
import java.util.Comparator;
import java.util.Vector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author Shl
 */
public class SpriteDrawer extends GroupDrawer {

    private static final int MAX_SPRITE_HEIGHT = 100;

    public SpriteDrawer(DrawerInterface drawer) {
        super(drawer);
    }

    @Override
    public void _draw(String group, String subgroup) {
        GroupBoundingQuery boundingQuery = drawer.getQuery();
        Location viewPortLocation = drawer.getViewPortLocation();

        GroupScale groupScale = (GroupScale) drawer.getQuery().getMetaData(group, GroupScale.class);
        CanvasLayer canvasLayer = (CanvasLayer) drawer.getQuery().getMetaData(group, CanvasLayer.class);
        Canvas canvas = canvasLayer.getCanvas();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double viewPortWidth = drawer.getViewPortWidth();
        double viewPortHeight = drawer.getViewPortHeight();
        double scale = drawer.getScale();

        double epsilonH = boundingQuery.getEpsilon(group, subgroup)[1];
        double epsilonW = boundingQuery.getEpsilon(group, subgroup)[0];
        double wXs = Convertor.viewToWorld(viewPortLocation.getX(), scale) - epsilonW;
        double wXt = Convertor.viewToWorld(viewPortLocation.getX() + viewPortWidth, scale) + epsilonW;
        double wYs = Convertor.viewToWorld(viewPortLocation.getY(), scale) - epsilonH;
        double wYt = Convertor.viewToWorld(viewPortLocation.getY() + viewPortHeight, scale) + epsilonH;
        
        Vector entities = (Vector) boundingQuery.get(group, subgroup, wXs, wXt, wYs, wYt);
        
        if (!boundingQuery.isMoveable(group, subgroup)) {
            entities.sort(new Comparator() {

                @Override
                public int compare(Object o1, Object o2) {
                    DefinedSizeSpriteBasedEntity entity1 = (DefinedSizeSpriteBasedEntity) o1;
                    DefinedSizeSpriteBasedEntity entity2 = (DefinedSizeSpriteBasedEntity) o2;
                    return (entity1.getLocation().getY() > entity2.getLocation().getY()) ? 1 : -1;
                }
            });
        }
        
        double tx = drawer.getViewPortLocation().getX();
        double ty = drawer.getViewPortLocation().getY();

        for (Object obj : entities) {
            DefinedSizeSpriteBasedEntity entity = (DefinedSizeSpriteBasedEntity) obj;
            double cX = entity.getLocation().getX();
            double cY = entity.getLocation().getY();
            double newH = entity.getRealHeight();
            double newW = entity.getRealWidth();
            
            double tscale = scale;
            if (groupScale != null) {
                tscale *= groupScale.getCurrentScale(scale);
            }
            newH *= tscale;
            newW *= tscale;
            
//            if (newH > MAX_SPRITE_HEIGHT) {
//                newW = newW / newH * MAX_SPRITE_HEIGHT;
//                newH = MAX_SPRITE_HEIGHT;
//            }

            gc.save();
            gc.translate(Convertor.worldToView(cX, scale) - tx, Convertor.worldToView(cY, scale) - ty);
            gc.translate( -(newW / 2.0), -(newH / 2.0));
            gc.rotate(entity.getRotation());
//            gc.rotate(canvasLayer.getRotation());
//            gc.scale(canvasLayer.getScale(), canvasLayer.getScale());
//            gc.translate(- ((newW / scale) / 2.0), - ((newH / scale) / 2.0));
            gc.drawImage(entity.getImage(), 0, 0, newW, newH);
            gc.restore();

//            gc.drawImage(entity.getImage(), (centerX - tx - ((newW / scale) / 2.0)) * scale, (centerY - ty - ((newH / scale) / 2.0)) * scale, newW, newH);
        }
    }

}
