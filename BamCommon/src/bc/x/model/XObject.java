/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.x.model;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import nu.xom.Element;

import static bc.dsl.JavaDSL.*;

/**
 *
 * @author BLutati
 */
public abstract class XObject {

    private Element metadata;
    private List<XObject> childs;
    private List<ValueChangedListener> listeners;
    private String value;
    private String name;
    private XObject root;
    private XObject parent;

    public XObject(Element metadata, String name, XObject parent, XObject root) {
        this.metadata = metadata;
        this.childs = new LinkedList<XObject>();
        this.listeners = new LinkedList<ValueChangedListener>();
        this.name = name;
        this.parent = parent;
        this.root = root;
    }

    public boolean setChildValue(String child, String newValue) {
        boolean setted;
        if (getName().equals(child)) {
            setValue(newValue);
            return true;
        } else {
            for (XObject c : getChilds()) {
                setted = c.setChildValue(child, newValue);
                if (setted) {
                    return true;
                }
            }
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public Element getMetadata() {
        return metadata;
    }

    protected String getValue() {
        return value;
    }

    /**
     * seeking down
     * @param name
     * @return
     */
    public String getValueOf(String name) {
        if (eq(this.getName(), name)) {
            return getValue();
        } else {
            for (XObject c : getChilds()) {
                final String val = c.getValueOf(name);
                if (val != null) {
                    return val;
                }
            }
            return null;
        }
    }

    /**
     * notifying up
     * @param value
     */
    protected void setValue(String value) {
        String old = this.value;
        this.value = value;
        for (ValueChangedListener l : listeners) {
            if (l.getRequestedNotifications().contains(this.getName())) {
                l.onValueChanged(this, old, value);
            }
        }
        if (parent != null) {
            parent.onChildValueChanged(this, old, value);
        }
    }

    public List<XObject> getChilds() {
        return childs;
    }

    /**
     * also modifying the parent of the child
     * @param xo
     */
    protected void addChild(XObject xo) {
        xo.parent = this;
        childs.add(xo);
    }

    public void addValueChangedListener(ValueChangedListener listener) {
        listeners.add(listener);
        for (XObject c : childs) {
            c.addValueChangedListener(listener);
        }
    }

    public boolean isLeaf() {
        return getChilds().isEmpty();
    }

    public void onChildValueChanged(XObject child, String oldValue, String newValue) {
        for (ValueChangedListener l : listeners) {
            if (l.getRequestedNotifications().contains(child.getName())) {
                l.onValueChanged(child, oldValue, newValue);
            }
        }
    }

    public XObject getRoot() {
        return root;
    }

    public abstract Map<String, String> asValueMap();
    
    public XObject getParent() {
        return parent;
    }

    public abstract List<String> getValues();

    public abstract void consumeValues(List<String> values);

    public void readValuesFromMap(Map map) {
        for (Object kv : map.entrySet()){
            Map.Entry ekv = (Entry) kv;
            setChildValue(ekv.getKey().toString(), ekv.getValue().toString());
        }
    }

    public static interface ValueChangedListener {

        List<String> getRequestedNotifications();

        void onValueChanged(XObject obj, String oldValue, String newValue);
    }

    public static abstract class ValueChangedHandler implements ValueChangedListener {

        List<String> request;

        public ValueChangedHandler(String... request) {
            this.request = new ArrayList<String>(request.length);
            this.request.addAll(Arrays.asList(request));
        }

        public List<String> getRequestedNotifications() {
            return request;
        }
    }
}
