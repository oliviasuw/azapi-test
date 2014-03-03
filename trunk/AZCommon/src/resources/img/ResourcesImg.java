/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.img;

import javafx.scene.image.Image;

/**
 *
 * @author User
 */
public class ResourcesImg {

    public static Image png(String img) {
        return new Image(ResourcesImg.class.getResourceAsStream(img + ".png"));
    }
}
