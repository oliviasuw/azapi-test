/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.comp;

import bc.swing.pfrm.Action;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import org.jdesktop.swingx.JXHyperlink;

/**
 *
 * @author BLutati
 */
public class JActionView extends JXHyperlink{
    private Action action;
    private List<Listener> listeners;

    public JActionView(Action action) {
        this(action, Color.WHITE);
    }

    public JActionView(final Action action, Color linkColor) {
        this.setClickedColor(linkColor);
        this.setForeground(linkColor);
        this.setUnclickedColor(linkColor);

        this.action = action;
        this.setIcon(action.getIcon());
        this.setText(action.getName());

        this.listeners = new LinkedList<Listener>();

        this.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                action.execute();
            }
        });
    }

    public Action getActionModel() {
        return action;
    }

    public void addListener(Listener l){
        listeners.add(l);
    }

    public static interface Listener{
        void onAction(Action action);
    }

}
