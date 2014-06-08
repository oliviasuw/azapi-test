/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.map.impl.wersdfawer;

import data.map.impl.wersdfawer.groupbounding.HasId;
import java.util.HashMap;

/**
 *
 * @author Shl
 */
public class AZVisVertex implements HasId {

    private double x;
    private double y;
    private String name;
    private HashMap<String, String> tags;

    public AZVisVertex(String name, double x, double y) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.tags = new HashMap<>();
    }

    AZVisVertex(String name, double x, double y, HashMap<String, String> params) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.tags = params;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagValue(String tagKey) {
        return tags.get(tagKey);
    }

    public String addTag(String tagKey, String tagValue) {
        return tags.put(tagKey, tagValue);
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    @Override
    public String getId() {
        return this.name;
    }

//    @Override
//    public boolean isHit(double x, double y) {
//        double dx = this.x - x;
//        double dy = this.y - y;
//        return dx * dx + dy * dy < HIT_ERROR;
//    }

}
