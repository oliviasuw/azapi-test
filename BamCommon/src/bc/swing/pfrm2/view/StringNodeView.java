/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm2.view;

import bc.swing.pfrm2.Att;
import bc.swing.pfrm2.Node;
import bc.swing.pfrm2.NodeView;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author bennyl
 */
public class StringNodeView extends JTextField implements NodeView {

    private String hint;
    private Color hintColor = Color.gray.brighter();
    JLabel hintField = new JLabel();

    @Override
    public void paint(Graphics g) {

        boolean hinting = false;

        if (hint != null && getText().isEmpty()) {
            hinting = true;
        }

        if (hinting) {
            super.paintComponent(g);
            g.setColor(hintColor);
            g.setFont(getFont());
            g.drawString(hint, 3, 15);

        } else {
            super.paintComponent(g);
        }

        super.paintBorder(g);
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public void setNode(Node node) {
        syncToView(node);
        style(node);
    }

    public void syncFromView(Node c) {
        c.setValue(getText());
    }

    public void syncToView(Node c) {
        if (c.getValue() == null) {
            setText("");
        } else {
            setText("" + c.getValue());
        }
    }

    private void style(Node node) {
        setForeground(node.getAtt(Att.FOREGROUND_COLOR, Color.black));
    }
}
