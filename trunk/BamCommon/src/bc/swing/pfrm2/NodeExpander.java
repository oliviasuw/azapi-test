/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public interface NodeExpander {

    List<Node> getChildren(Node n);

    public static class IterableNodeChildProvider implements NodeExpander {

        public List<Node> getChildren(Node n) {
            if (Iterable.class.isAssignableFrom(n.getClassAtt(Att.VALUE_CLASS))) {
                List<Node> ret = new LinkedList<Node>();
                Iterable iter = (Iterable) n.getValue();
                for (Object i : iter){
                    Node t = new ValueNode(i.getClass(), i, n);
                    t.putAtt(Att.ID, i);
                    ret.add(t);
                }
                
                return ret;
            }
            
            return Collections.emptyList();
        }
    }
}
