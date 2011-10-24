/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import bc.dsl.ReflectionDSL;
import bc.swing.pfrm.Action;
import bc.swing.pfrm.events.Event;
import bc.swing.pfrm.events.EventListener;
import bc.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

/**
 *
 * @author bennyl
 */
public abstract class Node {
    /*
     * ATTRIBUTE CHANGED EVENT
     */

    public static final String ACE_KEY = "KEY";
    public static final String ACE_VALUE = "VALUE";
    public static final String ATTRIBUTE_CHANGED_EVENT = "ATTRIBUTE CHANGED EVENT";
    /*
     * LEAF VALUE CHANGED EVENT
     */
    public static final String VALUE_CHANGED_EVENT = "VALUE CHANGED EVENT";
    public static final String VCE_NEW_VALUE = "NEW VALUE";
    /*
     * NODE DISPOSING EVENT
     */
    public static final String NDE_NODE = "NODE";
    public static final String NODE_DISPOSING_EVENT = "NODE DISPOSING EVENT";
    /*
     * FIELDS
     */
    private Map<Object, Node> children;
    private Map attributes;
    private Map<Object, List<EventListener>> attributeChangedListener;
    private List<EventListener> leafValueChangeListeners;
    private List<EventListener> disposeListeners;
    private Node parent;
    private WeakReference<NodeView> view = null;
    private Map<String, Action> actions;
    private Object target;
    private boolean lockAtts = false;
    private boolean childrenGenerated = false;

    /**
     * @param value 
     */
    public Node(Node parent, Object target) {
        children = new LinkedHashMap<Object, Node>();
        attributes = new HashMap();
        attributeChangedListener = new HashMap<Object, List<EventListener>>();
        leafValueChangeListeners = new LinkedList<EventListener>();
        disposeListeners = new LinkedList<EventListener>();
        actions = new LinkedHashMap<String, Action>();
        this.parent = parent;
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void lockAtts() {
        lockAtts = true;
    }

    public void unlockAtts() {
        lockAtts = false;
    }

    public List<Node> childrenList() {

        tryGenerateChildren();

        Collection<Node> vls = children.values();
        if (vls instanceof List) {
            return (List<Node>) vls;
        }
        return new LinkedList<Node>(vls);
    }

    private void tryGenerateChildren() {
        if (!childrenGenerated && children.isEmpty()) {
            if (!tryCreateChildrenByChildProvider()) {
                tryCreateChildrenByItemChildProvider();
            }
        }

        childrenGenerated = true;
    }

    public List<Action> actionsList() {
        Collection<Action> vls = actions.values();
        if (vls instanceof List) {
            return (List<Action>) vls;
        }
        return new LinkedList<Action>(vls);
    }

    public void addAction(Action action) {
        actions.put(action.getName(), action);
    }

    public void execute(String actionName) {
        if (actions.containsKey(actionName)) {
            actions.get(actionName).execute();
        } else {
            System.err.println("execute " + actionName + " called on " + getAtt(Att.ID) + ", this object dont support this action.");
        }
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public List<EventListener> leafValueChangeListeners() {
        return leafValueChangeListeners;
    }

    public List<EventListener> disposeListeners() {
        return disposeListeners;
    }

    public abstract Object getValue();

    public void setValue(Object value) {
        _setValue(value);
        fireLeafValueChanged(value);
    }

    protected abstract void _setValue(Object value);

    public void dispose() {
        fireNodeDisposing();
    }

    public Node getChild(Object key) {
        tryGenerateChildren();
        return children.get(key);
    }

    public void putChild(Object key, Node child) {
        children.put(key, child);
    }

    public void putAtt(Object key, Object value) {
        if (!lockAtts || !hasAtt(key)) {
//            System.out.println("Putting Att: " + key + " => " + value + " to " + getId());
            attributes.put(key, value);
            fireAttributeChanged(key, value);
        }
    }

    public void putAttIfNotExists(Object key, Object value) {
        if (!hasAtt(value)) {
            putAtt(key, value);
        }
    }

    public <T> T getAtt(Object key, T defaultValue) {
        return getAtt(key, (Class<T>) defaultValue.getClass(), defaultValue);
    }

    public <T> T getAtt(Object key, Class<T> rett, T defaultValue) {
        Object t = getAtt(key);
        if (t == null) {
            return defaultValue;
        }

        if (rett.isAssignableFrom(t.getClass())) {
            return (T) t;
        }

        if (t instanceof String) {
            Method vofm = ReflectionDSL.methodByNameAndNArgs(rett, "valueOf", 1);
            if (vofm != null && Modifier.isStatic(vofm.getModifiers())) {
                try {
                    return (T) vofm.invoke(null, t);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (defaultValue == null) { //notifinig to the poor user...
            System.err.println("Node requested to get attribute " + key + " as calss " + rett.getSimpleName() + " but its real type is " + t.getClass().getSimpleName() + ", returning null");
        }

        return defaultValue;
    }

    public String getStringAtt(Object key) {
        return getAtt(key, String.class, null);
    }

    public Integer getIntegerAtt(Object key) {
        return getAtt(key, Integer.class, null);
    }

    public Boolean getBooleanAtt(Object key) {
        return getAtt(key, Boolean.class, null);
    }

    public Class getClassAtt(Object key) {
        return getAtt(key, Class.class, null);
    }

    public Class getClassAtt(Object key, Class defaultValue) {
        return getAtt(key, Class.class, defaultValue);
    }

    public Boolean getBooleanAtt(Object key, Boolean defaultValue) {
        return getAtt(key, Boolean.class, defaultValue);
    }

    public String getStringAtt(Object key, String defaultValue) {
        return getAtt(key, String.class, defaultValue);
    }

    public Integer getIntegerAtt(Object key, int defaultValue) {
        return getAtt(key, Integer.class, defaultValue);
    }

    /**
     * return att value -> if recursive set to true and no such att found in the current node 
     * then searching in the parent node.
     * @param key
     * @param recursive
     * @return 
     */
    public Object getAtt(Object key, boolean recursive) {
        Object ret = attributes.get(key);
        if (recursive && ret == null && parent != null) {
            ret = parent.getAtt(key, recursive);
        }

        return ret;
    }
    
    /**
     * same as calling getAtt(key, false);
     * @param key
     * @return 
     */
    public Object getAtt(Object key){
        return getAtt(key, false);
    }

    public Set attributeKeys() {
        return attributes.keySet();
    }

    public Set childrenKeys() {
        tryGenerateChildren();
        return children.keySet();
    }

    private void addChildrenViaProvider(final NodeExpander cprov) {
        List<Node> clds = cprov.getChildren(this);
        for (Node c : clds) {
            putChild(c.getAtt(Att.ID), c);
        }
    }

    public String getId() {
        return getStringAtt(Att.ID);
    }

    private List<EventListener> getAttrListeners(Object key) {
        List<EventListener> l = attributeChangedListener.get(key);
        if (l == null) {
            l = new LinkedList<EventListener>();
            attributeChangedListener.put(key, l);
        }

        return l;
    }

    private void fireAttributeChanged(Object key, Object value) {
        Event e = new Event(ATTRIBUTE_CHANGED_EVENT);
        e.setAllFields(ACE_KEY, key, ACE_VALUE, value);
        for (EventListener attl : getAttrListeners(key)) {
            attl.onEvent(e);
        }
    }

    private void fireLeafValueChanged(Object value) {
        Event e = new Event(VALUE_CHANGED_EVENT);
        e.setAllFields(VCE_NEW_VALUE, value);
        for (EventListener attl : leafValueChangeListeners) {
            attl.onEvent(e);
        }
    }

    private void fireNodeDisposing() {
        Event e = new Event(NODE_DISPOSING_EVENT);
        e.setAllFields(NDE_NODE, this);
        for (EventListener attl : leafValueChangeListeners) {
            attl.onEvent(e);
        }
    }

    public boolean hasAtt(Object attr) {
        return attributes.containsKey(attr);
    }

    public boolean missAttr(Object attr) {
        return !attributes.containsKey(attr);
    }

    private JComponent generateView() {
        if (hasAtt(Att.VIEW_CLASS)) {
            try {
                Class<? extends NodeView> nvc = (Class<? extends NodeView>) getAtt(Att.VIEW_CLASS);
                final NodeView newInstance = nvc.newInstance();
                view = new WeakReference<NodeView>(newInstance);
                newInstance.setNode(this);
                return (JComponent) newInstance;
            } catch (Exception ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, "while trying to generate view for " + getAtt(Att.ID), ex);
            }
        }

        return null;
    }

    public JComponent getView() {
        return getView(true);
    }

    public JComponent getView(boolean create) {
        JComponent ret = null;
        if (view == null) {
            if (create) {
                ret = generateView();
            } else {
                ret = null;
            }
        } else {
            NodeView v = view.get();
            if (v == null) {
                if (create) {
                    ret = generateView();
                } else {
                    ret = null;
                }
            } else {
                ret = (JComponent) v;
            }
        }

        if (ret == null) {
            System.err.println("Cannot create view for " + getStringAtt(Att.ID));
        }

        return ret;
    }

    public void syncFromView() {
        NodeView v = (NodeView) getView(false);
        if (v != null) {
            v.syncFromView(this);
        }
    }

    public void syncToView() {
        NodeView v = (NodeView) getView(false);
        if (v != null) {
            v.syncToView(this);
        }
    }

    public Object dropFilter(Object data) {
        if (getTarget() instanceof Controller) {
            return ((Controller) getTarget()).dropFilter(getStringAtt(Att.ID), data);
        } else {
            return data;
        }
    }

    public Object dragFilter(Object data) {
        if (getTarget() instanceof Controller) {
            return ((Controller) getTarget()).dragFilter(getStringAtt(Att.ID), data);
        } else {
            return data;
        }
    }

    public Node generateChild(Object value) {
        Node n = new ValueNode(value.getClass(), value, this);
        return n;
    }

    private boolean tryCreateChildrenByChildProvider() {
        if (!hasAtt(Att.EXPANDER)) {
            return false;
        }
        final NodeExpander cprov = getAtt(Att.EXPANDER, NodeExpander.class, null);
//        System.out.println("" + getId() + " node generating childs from direct expander");
        addChildrenViaProvider(cprov);
        return true;

    }

    private boolean tryCreateChildrenByItemChildProvider() {
        if (hasParent()) {
            if (!getParent().hasAtt(Att.ITEM_EXPANDER)) {
                return false;
            }

            final NodeExpander cprov = getParent().getAtt(Att.ITEM_EXPANDER, NodeExpander.class, null);
            addChildrenViaProvider(cprov);
            return true;
        }

        return false;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public void writeToXML() {
        Element root = new Element("ROOT");
        Node n = this;
        while (n.hasParent()) {
            n = n.getParent();
        }
        n.writeToXML(root);
        Document doc = new Document(root);
        try {
            FileUtils.persistText(new File("."), "stracture.xml", doc.toXML());
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeToXML(Element root) {


        try {
            root.setLocalName(getClass().getSimpleName());
        } catch (Exception ex) {
            root.setLocalName("ANONYMUS-NODE");
        }
        for (Object att : attributes.entrySet()) {
            Map.Entry ee = (Map.Entry) att;
            try {
                root.addAttribute(new Attribute(("" + ee.getKey()).replace(" ", "-"), "" + ee.getValue()));
            } catch (Exception ex) {
                System.out.println("Cannot write Att: " + ee);
            }
        }

        root.appendChild("" + getValue());

        for (Node child : childrenList()) {
            Element c = new Element("CHILD");
            child.writeToXML(c);
            root.appendChild(c);
        }
    }
}
