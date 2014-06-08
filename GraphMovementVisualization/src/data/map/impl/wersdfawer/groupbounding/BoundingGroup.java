/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer.groupbounding;

import bgu.dcr.az.vis.player.api.Layer;
import com.bbn.openmap.util.quadtree.QuadTree;
import static data.map.impl.wersdfawer.GraphData.QUAD_TREE_BOUNDS;
import data.map.impl.wersdfawer.GraphPolygon;
import java.util.Collection;
import java.util.Vector;

/**
 *
 * @author Shl
 */
public class BoundingGroup {

    private QuadTree objects;
    private final boolean moveable;

    private double maxWidth;
    private double maxHeight;
    private String subGroup;


    public BoundingGroup(String subGroup, boolean moveable) {
        this.subGroup = subGroup;
        this.moveable = moveable;
//        this.objects = new QuadTree(0, QUAD_TREE_BOUNDS, QUAD_TREE_BOUNDS, 0, QUAD_TREE_BOUNDS);
        this.objects = new QuadTree(QUAD_TREE_BOUNDS, 0, QUAD_TREE_BOUNDS, 0, QUAD_TREE_BOUNDS);
    }

    public boolean add(double x, double y, double width, double height, Object obj) {
        if (height > maxHeight) {
            maxHeight = height;
        }
        if (width > maxWidth) {
            maxWidth = width;
        }
        return objects.put((float) y, (float) x, obj);
    }

    public String getSubGroup() {
        return subGroup;
    }

    public Vector get(double left, double right, double up, double down) {
        return this.objects.get((float) down, (float) left, (float) up, (float) right);
    }
    
    public Object remove(double x, double y, Object obj) {
        return this.objects.remove((float)y, (float)x, obj);
    }

    /**
     * returns array with array[0]==epsilon height and array[1]==epsilon width,
     * meaning the maximal width/height of an object in this group divided by 2.
     * requesting objects within a bounding box calculated with the addition of
     * epsilons, guarantees that a picture will not be cut in the middle.
     *
     * @return
     */
    public double[] getEpsilon() {
        return new double[]{maxWidth / 2, maxHeight / 2};
    }

    public boolean isMoveable() {
        return moveable;
    }
    
    

}
