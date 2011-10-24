/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

/**
 *
 * @author bennyl
 */
public class ValueNode extends Node {

    Object value;

    public ValueNode(Class cls, Object v, Node parent) {
        super(parent, v);
        value = v;
        putAtt(Att.VALUE_CLASS, cls);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    protected void _setValue(Object value) {
        this.value = value;
    }

}
