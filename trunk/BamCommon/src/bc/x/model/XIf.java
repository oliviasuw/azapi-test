/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.x.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nu.xom.Element;

import static bc.dsl.XNavDSL.*;
import static bc.dsl.JavaDSL.*;

/**
 *
 * @author BLutati
 */
public class XIf extends XObject {

    private boolean accepted;
    private List<AcceptionChangeListener> listeners;

    private XIf(Element metadata, XObject parent, XObject root) {
        super(metadata, "IF [" + attr(metadata, "param") + "]", parent, root);

        listeners = new LinkedList<AcceptionChangeListener>();

        final String param = attr(metadata, "param");
        final String value = attr(metadata, "value");

        if (param.isEmpty()) {
            throw new RuntimeException("cannot constract XIF - no param attribute");
        }

        /**
         * TODO: check if need to move it to listen on the root.. 
         */
        parent.addValueChangedListener(new ValueChangedHandler(param) {

            @Override
            public void onValueChanged(XObject sender, String oldVal, String newVal) {
                setAccepted(eq(newVal, value));
            }
        });
    }

    public static XIf create(Element metadata, XObject parent, XObject root){
        XIf i = new XIf(metadata, parent, root);
        for (XObject c : XCommand.generateFragments(metadata, i, root)){
            i.addChild(c);
        }

        return i;
    }

    public String getCheckedParameter(){
        return attr(getMetadata(), "param");
    }

    public String getCheckedValue(){
        return attr(getMetadata(), "value");
    }

    private void setAccepted(boolean b) {
        this.accepted = b;
        for (AcceptionChangeListener l : listeners){
            l.onAcceptionChanged(this, accepted);
        }
    }

    public void addAcceptionChangedListener(AcceptionChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public List<String> getValues() {
        LinkedList<String> values = new LinkedList<String>();
        if (!accepted) return values;

        for (XObject c : getChilds()){
            values.addAll(c.getValues());
        }

        return values;
    }

    @Override
    public void consumeValues(List<String> values) {
        if (isAccepted()) {
            for (XObject c : getChilds()){
                c.consumeValues(values);
            }
        }
    }

    public boolean isAccepted() {
        return accepted;
    }

    @Override
    public String getValueOf(String param) {
        String value = null;
        if (isAccepted()){
            for (XObject c : getChilds()) {
                if ((value = c.getValueOf(param)) != null) return value;
            }
        }

        return null;
    }

    @Override
    public Map<String, String> asValueMap() {
        HashMap<String, String> ret = new HashMap<String, String>();
        
        if (isAccepted()) {
            for (XObject c : getChilds()){
                ret.putAll(c.asValueMap());
            }
        }
        
        return ret;
    }

    public static interface AcceptionChangeListener {

        void onAcceptionChanged(XIf sender, boolean accepted);
    }
}
