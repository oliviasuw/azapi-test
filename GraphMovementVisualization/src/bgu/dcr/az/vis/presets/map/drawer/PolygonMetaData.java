/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vis.presets.map.drawer;

import bgu.dcr.az.vis.tools.StringPair;
import java.util.HashMap;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import resources.img.R;

/**
 *
 * @author Shl
 */
public class PolygonMetaData {

    private HashMap<StringPair, Paint> keyTocolors;
    private Paint defaultPaint;

    public PolygonMetaData() {
        init();
    }

    public HashMap<StringPair, Paint> getKeyTocolors() {
        return keyTocolors;
    }

    public Paint getDefaultPaint() {
        return defaultPaint;
    }

    private void init() {
        ImagePattern grassImagePattern = new ImagePattern(new Image(R.class.getResourceAsStream("grassTexture.jpg")), 0, 0, 100, 100, false);
        keyTocolors = new HashMap<>();
        keyTocolors.put(new StringPair("leisure", "park"), grassImagePattern);
        keyTocolors.put(new StringPair("landuse", "grass"), grassImagePattern);
        keyTocolors.put(new StringPair("landuse", "village_green"), grassImagePattern);
        keyTocolors.put(new StringPair("landuse", "meadow"), grassImagePattern);
        keyTocolors.put(new StringPair("leisure", "pitch"), grassImagePattern);
        keyTocolors.put(new StringPair("leisure", "garden"), grassImagePattern);
        keyTocolors.put(new StringPair("building", "yes"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "garages"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "house"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "roof"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "collapsed"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "university"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "residential"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "apartments"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "public"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "school"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "office"), Color.RED); //* means everything
        keyTocolors.put(new StringPair("building", "commercial"), Color.RED); //* means everything
        defaultPaint = Color.AQUA;
    }

}
