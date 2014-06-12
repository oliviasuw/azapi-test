/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map;

import com.bbn.openmap.util.quadtree.QuadTree;
import java.awt.Point;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Shl
 */
public class BoundingQueryTest {

    public static final int QUAD_TREE_BOUNDS = 100000;
    public static QuadTree tree = new QuadTree(0, QUAD_TREE_BOUNDS, QUAD_TREE_BOUNDS, 0, QUAD_TREE_BOUNDS);
    public static Random r = new Random();
//    public static LinkedList<Integer> ls = new LinkedList<>();
    public static HashMap<Integer, Point> locs = new HashMap<>();

    public static void main(String[] args) {
        int objects = 10000;
        int tests = 100;

        for (int i = 0; i < objects; i++) {
            int x = r.nextInt(QUAD_TREE_BOUNDS);
            int y = r.nextInt(QUAD_TREE_BOUNDS);
            Integer iz = new Integer(i);
            Point point = new Point(x, y);
            locs.put(iz, point);
            tree.put((float)x, (float)y, point);
        }

        long start = System.currentTimeMillis();
        for (int times = 0; times < tests; times++) {
            for (int i = 0; i < objects; i++) {
                Point loc = locs.get(i);
                Object obj = tree.remove((float)loc.x, (float)loc.y, loc);
                if (obj == null) {
                    System.out.println("no object?");
                }
                loc.x = loc.x++ % QUAD_TREE_BOUNDS;
                loc.y = loc.y++ % QUAD_TREE_BOUNDS;
                tree.put((float)loc.x, (float)loc.y, loc);
            }
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("elapsed time for remove and put: " + elapsed);
        
        start = System.currentTimeMillis();
        for (int times = 0; times < tests; times++) {
            QuadTree newTree = new QuadTree(0, QUAD_TREE_BOUNDS, QUAD_TREE_BOUNDS, 0, QUAD_TREE_BOUNDS);
            for (int i = 0; i < objects; i++) {
                Point loc = locs.get(i);
                Object obj = tree.remove((float)loc.x, (float)loc.y, loc);
                if (obj == null) {
                    System.out.println("no object?");
                }
                loc.x = loc.x++ % QUAD_TREE_BOUNDS;
                loc.y = loc.y++ % QUAD_TREE_BOUNDS;
                newTree.put((float)loc.x, (float)loc.y, loc);
            }
            tree = newTree;
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("elapsed time for re-build: " + elapsed);
        
        
//        int print = (Integer)tree.remove(ls.get(5000),y5000,new Integer(5000));
//        System.out.println("print: " + print);

    }
}
