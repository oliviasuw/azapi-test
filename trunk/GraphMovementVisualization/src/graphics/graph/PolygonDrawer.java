/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphics.graph;

import bgu.dcr.az.vis.tools.StringPair;
import data.map.impl.wersdfawer.AZVisVertex;
import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import resources.img.R;

/**
 *
 * @author Shl
 */
public class PolygonDrawer implements PolygonImageDrawer {
    
    private final HashMap<StringPair, Paint> keyTocolors;
    private final Paint defaultPaint;
    private static final int MAX_BUILDING_HEIGHT = 100; 

    public PolygonDrawer() {
        keyTocolors = new HashMap<>();
        defaultPaint = Color.RED;
        init();
    }
    
    private void init() {
        ImagePattern grassImagePattern = new ImagePattern(new Image(R.class.getResourceAsStream("grassTexture.jpg")), 0, 0, 100, 100, false);
        
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
        
        Paint defaultColor = Color.AQUA;

    }
    
    @Override
    public boolean canDraw(GraphPolygon polygon) {
        HashMap<String, String> params = (HashMap<String, String>) polygon.getParams();
        for (StringPair key : keyTocolors.keySet()) {
            String get = params.get(key.getFirst());
            if (get != null && get.equals(key.getSecond())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas, GraphData graphData, GraphPolygon polygon, double scale) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();
        gc.save();
        Iterator<String> it = polygon.getNodes().iterator();
        String node = it.next();
        AZVisVertex source = (AZVisVertex) graphData.getData(node);
        //if want to extend to more params per polygon, need to change this
        Map.Entry<String, String> entry = polygon.getParams().entrySet().iterator().next();
        StringPair keyVal = new StringPair(entry.getKey(),entry.getValue());
        Paint get = keyTocolors.get(keyVal);
        if (get==null) {
            System.out.println("unsupported polygon " + keyVal.getFirst() + "=" + keyVal.getSecond());
            get = defaultPaint;
        }
        gc.setFill(get);
        gc.beginPath();
        
        gc.moveTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);
//        double[] xs = new double[polygon.getNodes().size()];
//        double[] ys = new double[polygon.getNodes().size()];

//        xs[0] = (source.getX() - tx) * scale;
//        ys[0] = (source.getY() - ty) * scale;
        int i = 1;
        while (it.hasNext()) {
            node = it.next();
            source = (AZVisVertex) graphData.getData(node);
            gc.lineTo((source.getX() - tx) * scale, (source.getY() - ty) * scale);

//            xs[i] = (source.getX() - tx) * scale;
//            ys[i] = (source.getY() - ty) * scale;
//            i++;
        }
        gc.closePath();
        gc.fill();
//        gc.fillPolygon(xs, ys, polygon.getNodes().size());

        gc.restore();
    }
    
}
