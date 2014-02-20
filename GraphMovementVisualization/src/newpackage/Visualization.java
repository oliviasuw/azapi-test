/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;

import com.esotericsoftware.kryo.Kryo;
import java.util.Collection;

/**
 *
 * @author Shl
 */
public interface Visualization {
    
    public VisualizationFrame update(Frame frame);
    
    public void setAnimator(Animator animator);
    
    public void visualize(VisualizationFrame visFrame);
    
    public void initialize(Kryo kryo);
    
}
