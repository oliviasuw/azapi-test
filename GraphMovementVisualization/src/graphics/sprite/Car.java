/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphics.sprite;

import javafx.animation.KeyFrame;

/**
 *
 * @author Shl
 */
public class Car extends Sprite {

    public Car(long index) {
        super(index);
    }
    
    public KeyFrame move(double x, double y) {
        return animator.move(x, y);
    }
    
    
}
