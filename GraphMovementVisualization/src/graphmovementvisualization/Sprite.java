/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphmovementvisualization;

import java.io.InputStream;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.scene.image.Image;

/**
 *
 * @author Shl
 */
public class Sprite  {
    
    private Image image;
    private Location location;
    DoubleProperty x;
    DoubleProperty y;

    public Sprite() {
        InputStream resourceAsStream = getClass().getResourceAsStream("circlesprite.png");
        image = new Image(resourceAsStream);
        location = new Location();
    }

    public DoubleProperty getX() {
        return x;
    }

    public void setX(DoubleProperty x) {
        this.x = x;
    }

    public DoubleProperty getY() {
        return y;
    }

    public void setY(DoubleProperty y) {
        this.y = y;
    }

    

    public Image getImage() {
        return image;
    }
    
    

    
    
}
