/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import bc.dsl.ReflectionDSL;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.Action;
import bc.swing.pfrm.ano.OnEvent;
import bc.swing.pfrm.events.Event;
import bc.swing.pfrm.events.EventListener;
import bc.swing.pfrm.units.EventBusUnit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class Controller {

    ValueNode node = null;

    public Controller() {
        scanDeclaredGlobalEvents();
    }

    protected Node generateNewNode() {
        node = new ValueNode(getClass(), this, null);
        scanDeclaredChildren();
        scanDeclaredAttributes();
        scanDeclaredActions();
        configureGeneratedNode(node);
        return node;
    }

    public ValueNode getNode() {
        if (node == null) {
            generateNewNode();
        }

        return node;
    }

    public boolean isNodeGenerated() {
        return node != null;
    }

    public void disposeNode() {
        node.dispose();
        node = null;
    }

    private void callNodeConfigurator(Method m, Node cnode) {
        Method configurator = ReflectionDSL.methodByName(getClass(), "cfg" + m.getName().substring(3));
        if (configurator != null) {
            try {
                configurator.invoke(this, cnode);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void configureAttributes(Node n, NodeDef dano) {
        NodeDef.Actions.setAttributes(dano, n);
    }

    private Action generateAction(ActionDef dano, final Method m) {
        return new Action(dano.name(), SwingDSL.resIcon(dano.icon())) {

            @Override
            public void execute() {
                try {
                    m.invoke(Controller.this);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }

    private void scanDeclaredGlobalEvents() {
        List<Method> emtds = ReflectionDSL.getAllMethodsWithAnnotation(getClass(), OnEvent.class);

        for (final Method mtd : emtds) {
            final OnEvent dano = mtd.getAnnotation(OnEvent.class);
            EventBusUnit.UNIT.register(dano.name(), new EventListener() {

                public void onEvent(Event e) {
                    try {
                        if (dano.extract().length == 0) {
                            mtd.invoke(Controller.this, e);
                        } else {
                            Object[] data = new Object[dano.extract().length];
                            for (int i = 0; i < data.length; i++) {
                                data[i] = e.getField(dano.extract()[i]);
                            }
                            mtd.invoke(Controller.this, data);
                        }
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }

    private void scanDeclaredChildren() {
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(getClass(), NodeDef.class);
        for (Method m : methods) {
            NodeDef dano = m.getAnnotation(NodeDef.class);
            Node cnode = null;
            if (Controller.class.isAssignableFrom(m.getReturnType())) {
                cnode = getControllerFromMethod(m).getNode();
                cnode.setParent(node);
                addChildren(cnode, dano);
            } else {
                Method setter = null;
                if (m.getName().startsWith("get")) {
                    setter = ReflectionDSL.methodByName(getClass(), "set" + m.getName().substring(3));
                }
                cnode = new MethodNode(m, setter, this, node);
                addChildren(cnode, dano);
            }

            callNodeConfigurator(m, cnode);
        }
        findAndCreateItemExpanders();
    }

    @Override
    protected void finalize() throws Throwable {
        if (isNodeGenerated()) {
            disposeNode();
        }
    }

    private Controller getControllerFromMethod(Method m) {
        try {
            return (Controller) m.invoke(this);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void addChildren(Node child, NodeDef dano) {
        configureAttributes(child, dano);
        NodeStylingUnit.UNIT.styleNode(child);
        node.putChild(dano.id(), child);
    }

    private void scanDeclaredAttributes() {
        NodeDef dano = getClass().getAnnotation(NodeDef.class);
        configureAttributes(node, dano);
    }

    private void scanDeclaredActions() {
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(getClass(), ActionDef.class);
        for (final Method m : methods) {
            ActionDef dano = m.getAnnotation(ActionDef.class);

            if (dano.forId().isEmpty()) {
                node.addAction(generateAction(dano, m));
            } else {
                node.getChild(dano.forId()).addAction(generateAction(dano, m));
            }
        }
    }

    /**
     * filter dragged material
     * if a parameter is configured to be able to drag from then maybe you dont want the
     * actual content to be dragged
     * if you want that when dragging some content from the parameter other content will get dragged
     * then this is the function to use 
     * @param nodeChild
     * @param dragged
     * @return
     */
    public Object dragFilter(String nodeChild, Object dragged) {
        return dragged;
    }

    /**
     * read dragFilter
     * @param param
     * @param dropped
     * @return
     */
    public Object dropFilter(String nodeChild, Object dropped) {
        return dropped;
    }

    public void configureGeneratedNode(Node n) {
    }

    private NodeExpander generateChildProvider(final Method childProvider) {
        return new NodeExpander() {

            public List<Node> getChildren(Node n) {
                try {
                    return (List<Node>) childProvider.invoke(this, n);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }

                return Collections.emptyList();
            }
        };
    }

    private void findAndCreateItemExpanders() {
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(getClass(), ItemExpander.class);
        for (final Method expander : methods){
            final ItemExpander dano = expander.getAnnotation(ItemExpander.class);
            final Method editor = searchInserterFor(dano.forId());
            ItemEditor iano = (editor == null? null: editor.getAnnotation(ItemEditor.class));
            final Set<String> editeableItems = new HashSet<String>();
            if (iano != null){
                editeableItems.addAll(Arrays.asList(iano.columns()));
            }
            
            node.getChild(dano.forId()).putAtt(Att.ITEM_EXPANDER_DEF, dano);
            node.getChild(dano.forId()).putAtt(Att.ITEM_EDITOR_DEF, iano);
            
            NodeExpander iexp = new NodeExpander() {

                public List<Node> getChildren(final Node n) {
                    List<Node> nodes = new LinkedList<Node>();
                    for (final String column : dano.columns()){
                        Node nc = new Node(n, Controller.this) {

                            @Override
                            public Object getValue() {
                                try {
                                    return expander.invoke(Controller.this, column, n.getValue());
                                } catch (IllegalAccessException ex) {
                                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IllegalArgumentException ex) {
                                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (InvocationTargetException ex) {
                                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                return "???";
                            }

                            @Override
                            protected void _setValue(Object value) {
                                if (!getBooleanAtt(Att.READ_ONLY)){
                                    try {
                                        editor.invoke(Controller.this, column, n.getValue(), value);
                                    } catch (IllegalAccessException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (IllegalArgumentException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (InvocationTargetException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        };
                        
                        nc.putAtt(Att.ID, column);
                        nc.putAtt(Att.ROLES, new String[0]);
                        nc.putAtt(Att.READ_ONLY, !editeableItems.contains(column));
                        nodes.add(nc);
                    }
                    
                    return nodes;
                }
            };
            
            node.getChild(dano.forId()).putAtt(Att.ITEM_EXPANDER, iexp);
        }
    }

    private Method searchInserterFor(String forId) {
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(getClass(), ItemEditor.class);
        for (Method method : methods){
            if (method.getAnnotation(ItemEditor.class).forId().equals(forId)){
                return method;
            }
        }
        
        return null;
    }
}
