/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import bc.swing.pfrm.scan.Adapter;
import bc.swing.pfrm.scan.Box;
import java.util.List;

/**
 *
 * @author bennyl
 */
public interface ViewAdapter<IN,OUT> extends Adapter<IN, OUT>{
    
    Object getSelectedItem();
    List<Object> getSelectedItems();
}
