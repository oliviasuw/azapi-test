/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import bc.swing.pfrm.events.EventListener;

/**
 *
 * @author bennyl
 */
public interface NodeView {
    public void setNode(Node node);
    public void syncFromView(Node c);
    public void syncToView(Node c);
}
