/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc.swing;

import bam.utils.ui.rnd.CheckTreeRenderer;
import bam.utils.ui.mvc.GenericTreeModel.Node;
import bam.utils.ui.mvc.GenericTreeModel.SimpleLeafNode;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author bennyl
 */
public class CheckTree extends JTree {

    public CheckTree() {
        setModel(new CheckTreeModel(new SimpleLeafNode("root", null)));
        setCellRenderer(new CheckTreeRenderer());

        addMouseListener(new MouseInputAdapter() {

            Object prevSel = null;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && prevSel != null && leafIsSelected() && prevSel.equals(getSelectionPath().getLastPathComponent())) {
                    final CheckTreeModel model = (CheckTreeModel) getModel();
                    model.setChecked((Node) prevSel, !model.isChecked((Node) prevSel));
                }

                prevSel = (getSelectionPath() != null ? getSelectionPath().getLastPathComponent() : null);
            }
        });

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) && leafIsSelected()) {
                    final CheckTreeModel model = (CheckTreeModel) getModel();
                    model.setChecked((Node) getSelectionPath().getLastPathComponent(), !model.isChecked((Node) getSelectionPath().getLastPathComponent()));
                }
            }

        });

    }
    private boolean leafIsSelected() {
        return getSelectionPath() != null && ((Node) getSelectionPath().getLastPathComponent()).isLeaf();
    }

    @Override
    public CheckTreeRenderer getCellRenderer() {
        if (!(super.getCellRenderer() instanceof CheckTreeRenderer)) {
            super.setCellRenderer(new CheckTreeRenderer());
        }
        return (CheckTreeRenderer) super.getCellRenderer();
    }
}
