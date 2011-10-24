/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import bc.swing.pfrm2.view.ListNodeView;
import bc.swing.pfrm2.view.StringNodeView;
import java.awt.Color;

/**
 *
 * @author bennyl
 */
public class DefaultStyler {

    public static final NodeExpander DEFAULT_ITERABLE_CHILDREN_PROVIDER =
            new NodeExpander.IterableNodeChildProvider();
    
    @Selector("String")
    public void styleString(Node n) {
        n.putAtt(Att.VIEW_CLASS, StringNodeView.class);
    }
    
    @Selector("List")
    public void styleList(Node n) {
        n.putAtt(Att.VIEW_CLASS, ListNodeView.class);
        styleIterable(n);
    }

    @Selector("%StringNodeView")
    public void styleTestString(Node n) {
        n.putAtt(Att.FOREGROUND_COLOR, Color.red);
    }
    
    @Selector("Iterable")
    public void styleIterable(Node n){
        n.putAtt(Att.EXPANDER, DEFAULT_ITERABLE_CHILDREN_PROVIDER);
    }
    
}
