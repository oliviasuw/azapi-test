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
//        ImagePattern grassImagePattern = new ImagePattern(new Image(R.class.getResourceAsStream("grassTexture.jpg")), 0, 0, 100, 100, false);
        Color grassImagePattern = Color.rgb(197, 240, 197);
        Color buildingColor = Color.rgb(190, 173, 173);
        keyTocolors = new HashMap<>();
        keyTocolors.put(new StringPair("leisure", "park"), grassImagePattern);
        keyTocolors.put(new StringPair("landuse", "grass"), grassImagePattern);
        keyTocolors.put(new StringPair("landuse", "village_green"), grassImagePattern);
        keyTocolors.put(new StringPair("landuse", "meadow"), grassImagePattern);
        keyTocolors.put(new StringPair("leisure", "pitch"), grassImagePattern);
        keyTocolors.put(new StringPair("leisure", "garden"), grassImagePattern);
        keyTocolors.put(new StringPair("building", "yes"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "garages"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "house"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "roof"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "collapsed"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "university"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "residential"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "apartments"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "public"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "school"), Color.rgb(240, 240, 216)); //* means everything
//        keyTocolors.put(new StringPair("amenity", "school"), Color.rgb(240, 240, 216)); //* means everything
        keyTocolors.put(new StringPair("building", "office"), buildingColor); //* means everything
        keyTocolors.put(new StringPair("building", "commercial"), buildingColor); //* means everything
        defaultPaint = Color.BURLYWOOD;
    }

}
