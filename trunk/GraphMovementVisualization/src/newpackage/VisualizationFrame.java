/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;

import data.events.api.SimulatorEvent;
import java.util.Collection;

/**
 *
 * @author Shl
 */
public interface VisualizationFrame {
    
   public Collection<VisualizationAction> getActions();
   
   public void addAction(VisualizationAction action);
   
}
