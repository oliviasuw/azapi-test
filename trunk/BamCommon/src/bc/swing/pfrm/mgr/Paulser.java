/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.mgr;

import java.awt.event.ActionListener;

/**
 *
 * @author bennyl
 */
public enum Paulser {
    INSTANCE;
    
    long nextPaulse = -1;
    
    
    public static abstract class PaulsRequest{
        long time;

        public PaulsRequest(long time) {
            this.time = time;
        }
        
        
    }
}
