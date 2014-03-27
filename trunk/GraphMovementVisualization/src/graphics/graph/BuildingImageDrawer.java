/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

import data.map.impl.wersdfawer.GraphData;
import data.map.impl.wersdfawer.GraphPolygon;
import java.util.HashMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import resources.img.R;

/**
 * @author Shlomi
 */
public class BuildingImageDrawer implements PolygonImageDrawer {
    
    private final String key = "building";
    private final HashMap<String, Image> images;
    private final Image defaultImage;
    private static final int MAX_BUILDING_HEIGHT = 100; 
    
    public BuildingImageDrawer() {
        images = new HashMap<>();

//        images.put("yes", new Image(R.class.getResourceAsStream("building")) );
//
        images.put("university", new Image(R.class.getResourceAsStream("university.png")));
        images.put("school", new Image(R.class.getResourceAsStream("university.png")));
        images.put("office", new Image(R.class.getResourceAsStream("office.png")));
        


        defaultImage = new Image(R.class.getResourceAsStream("building.png"));
    }
    
    
    @Override
    public boolean canDraw(GraphPolygon polygon) {
        HashMap<String, String> params = (HashMap<String, String>) polygon.getParams();
        return params.get(key) != null;
    }
    
    @Override
    public void draw(Canvas canvas, GraphData graphData, GraphPolygon polygon, double scale) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double tx = canvas.getTranslateX();
        double ty = canvas.getTranslateY();
        
        if (polygon.getCenter()==null) {
            polygon.setCenter(graphData);
        }
        double centerX = polygon.getCenter().x;
        double centerY = polygon.getCenter().y;
        
        Image image = images.get(polygon.getParams().get(key));
        if (image==null) {
            image = defaultImage;
        }
        double subScale = Math.sqrt(Math.abs(polygon.getArea()));
//        System.out.println(subScale);
//        gc.drawImage(image, (centerX -tx -(image.getWidth() / 2.0)) * scale, (centerY -ty -(image.getHeight() / 2.0)) * scale, image.getWidth()*scale, image.getHeight()*scale);
        double newW = scale*subScale;
        double newH = (image.getHeight() / image.getWidth()) * newW;
        if (newH > MAX_BUILDING_HEIGHT) {
            newW = newW/newH * MAX_BUILDING_HEIGHT;
            newH = MAX_BUILDING_HEIGHT;
        }
        gc.drawImage(image, (centerX -tx -(subScale / 2.0)) * scale, (centerY -ty -2*((newH/scale) / 2.0)) * scale, newW, newH);
    }

    
    

}
