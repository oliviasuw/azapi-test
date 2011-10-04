/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.comp;

import java.awt.Color;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.LineBorder;
import org.jdesktop.swingx.JXHyperlink;
import static bc.dsl.SwingDSL.*;

/**
 *
 * @author bennyl
 */
public class JRepeatPanel extends JStackPanel {

    public static final ImageIcon ADD_ICON = resIcon(JRepeatPanel.class, "img/plus-button.png");
    public static final ImageIcon REMOVE_ICON = resIcon(JRepeatPanel.class, "img/minus-button.png");
    private JXHyperlink addl;
    Model model;
    HashMap<JComponent, JSeparator> seps = new HashMap<JComponent, JSeparator>();

    public void setModel(Model model) {
        this.model = model;
        model.view = this;

        for (Object k : model.getKeys()) {
            addComponent(model.getChild(k));
        }
    }

    public JRepeatPanel() {
        addl = new JXHyperlink();
        addl.setIcon(ADD_ICON);
        addl.setText("add...");
        pushAddButton();
        eol(false);

        initAddButton();
    }

    private void initAddButton() {
        addl.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (model != null) {
                    model.onNewChildRequested();

                }
            }
        });
    }

    public void addComponent(JComponent added) {
        JStackPanel outer = new JStackPanel();
        final JXHyperlink removeButton = new JXHyperlink();
        removeButton.setIcon(REMOVE_ICON);
        outer.push(removeButton);
        outer.push(added, outer.expand(outer.constraint()));
        push(outer);
        eol();
        initRemoveButton(removeButton, added);
        revalidate();
        repaint();
        pushDownAddButton();
    }

    private void initRemoveButton(JXHyperlink removeButton, final JComponent added) {
        final JSeparator sep = lastSeperator;
        seps.put(added, sep);
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (model != null) {
                    model.removeChild(model.childToKey(added));
                }
            }
        });
    }

    private void pushDownAddButton() {
        remove(addl);
        pushAddButton();
        eol(false);

    }

    private void pushAddButton() {
        push(addl, expand(constraint()));
    }

    private void onChildAdded(JComponent comp) {
        addComponent(comp);
    }

    private void onChildLastRemoved(JComponent comp) {
        remove(comp.getParent());
        remove(seps.remove(comp));
        revalidate();
        repaint();
    }

    //-------------------------------------------------------------------
    public static abstract class Model {

        HashMap<Object, JComponent> comps = new HashMap<Object, JComponent>();
        JRepeatPanel view;
        private int min = -1;

        public void setView(JRepeatPanel view) {
            this.view = view;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getChildCount() {
            return comps.size();
        }

        public void addChild(Object key, JComponent comp) {
            comps.put(key, comp);
            view.onChildAdded(comp);
        }

        public void removeChild(Object key) {
            if (min >= 0 && comps.size() - 1 < min) {
                msgbox("cannot compleate operation", "cannot remove - there should be at least " + min + " repetitions.");
            } else {
                JComponent comp = comps.remove(key);
                view.onChildLastRemoved(comp);
                onLastChildRemoved(key);
            }
        }

        public JComponent getChild(Object key) {
            return comps.get(key);
        }

        public Object childToKey(JComponent child) {
            for (Entry<Object, JComponent> e : comps.entrySet()) {
                if (e.getValue().equals(child)) {
                    return e.getKey();
                }
            }

            return null;
        }

        public Set getKeys() {
            return comps.keySet();
        }

        /**
         * request the model to add a new child..
         * @return
         */
        public abstract void onNewChildRequested();

        public abstract void onLastChildRemoved(Object key);
    }
}
