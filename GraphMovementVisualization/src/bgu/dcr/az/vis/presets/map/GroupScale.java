/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map;

/**
 *
 * @author Shl
 */
public abstract class GroupScale {

    private double groupScale = 1;
    private double changeFactor = 10;
    private double minWorldScale = 0.2;
    private double maxWorldScale = 1;

    /**
     * default values.
     */
    public GroupScale() {
    }

    /**
     * creates a new GroupScale object with a given change factor. the change
     * factor effects the scale calculation given a general world scale. e.g:
     * for a change factor of 2, if the world scale is 0.5, then scale will be
     * 2*(1/0.5) = 4;
     *
     * @param groupScale
     * @param changeFactor
     * @param minWorldScale
     * @param maxWorldScale
     */
    public GroupScale(double groupScale, double changeFactor, double minWorldScale, double maxWorldScale) {
        this.groupScale = groupScale;
        this.changeFactor = changeFactor;
        this.minWorldScale = minWorldScale;
        this.maxWorldScale = maxWorldScale;
    }

    public double getGroupScale() {
        return groupScale;
    }

    public void setGroupScale(double groupScale) {
        this.groupScale = groupScale;
    }

    public double getChangeFactor() {
        return changeFactor;
    }

    public void setChangeFactor(double changeFactor) {
        this.changeFactor = changeFactor;
    }

    public double getMinWorldScale() {
        return minWorldScale;
    }

    public void setMinWorldScale(double minWorldScale) {
        this.minWorldScale = minWorldScale;
    }

    public double getMaxWorldScale() {
        return maxWorldScale;
    }

    public void setMaxWorldScale(double maxWorldScale) {
        this.maxWorldScale = maxWorldScale;
    }

    public double getCurrentScale(double worldScale) {
        if (worldScale > minWorldScale && worldScale < maxWorldScale) {
            return changeFactor * (1 / worldScale);
        } else {
            return 1;
        }
    }

    public abstract double getCurrentScale(double worldScale, String subGroup);

//    /**
//     * a simple group scale, meaning that all subgroup in group have the same scale.
//     */
//    public class SimpleGroupScale extends GroupScale {
//
//        @Override
//        public double getCurrentScale(double worldScale, String subGroup) {
//            return super.getCurrentScale(worldScale);
//        }
//
//    }
//
//    public class GraphGroupScale extends GroupScale {
//
//        @Override
//        public double getCurrentScale(double worldScale, String subGroup) {
//            if (subGroup.contains("EDGES")) {
//                return super.getCurrentScale(worldScale);
//            }
//            else {
//                return 1;
//            }
//        }
//
//    }

}
