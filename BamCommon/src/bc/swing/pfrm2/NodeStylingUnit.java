/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2;

import bc.dsl.ReflectionDSL;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public enum NodeStylingUnit {

    UNIT;
    public static final String SELECTOR_AND_SPLIT_PATTERN = "\\s*&\\s*";
    HashMap<Method, String[]> selectingDataMap = new HashMap<Method, String[]>();
    HashMap<String, List<Method>> selectorMap = new HashMap<String, List<Method>>();
    Object styling = null;

    private NodeStylingUnit() {
        loadStyleClass(new DefaultStyler());
    }

    public void loadStyleClass(Object st) {
        styling = st;
        Class stc = st.getClass();
        List<Method> methods = ReflectionDSL.getAllMethodsWithAnnotation(stc, Selector.class);

        for (Method m : methods) {
            Selector dano = m.getAnnotation(Selector.class);
            String[] sels = dano.value()[dano.value().length - 1].split(SELECTOR_AND_SPLIT_PATTERN);
            String sel = sels[0];
            for (int i = 1; i < sels.length; i++) {
                if (sel.startsWith("#")) {
                    break;
                }

                if (sels[i].startsWith("#")) {
                    sel = sels[i];
                    continue;
                }

                if (!sel.startsWith(".") && sels[i].startsWith(".")) {
                    sel = sels[i];
                    continue;
                }

                if (!sel.startsWith("%") && !sel.startsWith(".") && sels[i].startsWith("%")) {
                    sel = sels[i];
                    continue;
                }
            }

            selectingDataMap.put(m, dano.value());
            getSelectorMethods(sel, true).add(m);
        }
    }

    private void applyStylingMethods(List<Method> methods, Node n) {
        for (Method m : methods) {
            if (match(selectingDataMap.get(m), n)) {
                try {
                    System.err.println("applying " + m.getName() + " on " + n.getStringAtt(Att.ID));
                    m.invoke(styling, n);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(NodeStylingUnit.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(NodeStylingUnit.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(NodeStylingUnit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private List<Method> getSelectorMethods(String sel, boolean create) {
        List<Method> l = selectorMap.get(sel);
        if (l == null && create) {
            l = new LinkedList<Method>();
            selectorMap.put(sel, l);
        }

        if (l == null) {
            return Collections.emptyList();
        }

        return l;
    }

    public void styleNode(Node n) {
        n.lockAtts();
        styleNodeById(n);
        styleNodeByRole(n);
        styleNodeByViewClass(n);
        styleNodeByValueClass(n);
        n.unlockAtts();
    }

    private void styleNodeById(Node n) {
        String id = (String) n.getAtt(Att.ID);
        List<Method> methods = getSelectorMethods("#" + id, false);
        applyStylingMethods(methods, n);
    }

    private void styleNodeByRole(Node n) {
        String[] roles = (String[]) n.getAtt(Att.ROLES);
        for (String r : roles) {
            List<Method> methods = getSelectorMethods("." + r, false);
            applyStylingMethods(methods, n);
        }
    }

    private void styleNodeByValueClass(Node n) {
        Class c = (Class) n.getAtt(Att.VALUE_CLASS);
        Set<Class> classGraph = ReflectionDSL.getClassGraph(c);

        for (Class cc : classGraph) {
            List<Method> methods = getSelectorMethods(cc.getSimpleName(), false);
            applyStylingMethods(methods, n);
        }
    }

    private void styleNodeByViewClass(Node n) {
        Class c = (Class) n.getAtt(Att.VIEW_CLASS);
        if (c != null) {
            List<Method> methods = getSelectorMethods("%" + c.getSimpleName(), false);
            applyStylingMethods(methods, n);
        }
    }

    private boolean match(String[] selector, Node n) {
        return match(selector, selector.length - 1, n);
    }

    private boolean match(String[] selector, int idx, Node n) {
        if (idx == -1) {
            return true;
        } else if (n == null) {
            return false;
        } else {
            String[] selectorAnds = selector[idx].split(SELECTOR_AND_SPLIT_PATTERN);
            for (String s : selectorAnds) {
                String subs = s.substring(1);
                if (s.startsWith("#")) {
                    if (!n.getAtt(Att.ID).equals(subs)) {
                        return false;
                    }
                } else if (s.startsWith(".")) {
                    String[] roles = (String[]) n.getAtt(Att.ROLES);
                    boolean found = false;
                    for (String r : roles) {
                        if (r.equals(subs)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        return false;
                    }
                } else if (s.startsWith("%")) {
                    if (n.hasAtt(Att.VIEW_CLASS)) {
                        Class vc = (Class) n.getAtt(Att.VIEW_CLASS);
                        if (!vc.getSimpleName().equals(subs)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    Class vc = (Class) n.getAtt(Att.VALUE_CLASS);
                    if (!vc.getSimpleName().equals(s)) {
                        return false;
                    }
                }
            }

            return match(selector, idx - 1, n.getParent());
        }
    }
}
