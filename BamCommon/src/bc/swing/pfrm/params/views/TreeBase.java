/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.dsl.JavaDSL;
import bc.swing.dnd.ObjectTransferHandler;
import bc.swing.models.GenericTreeModel;
import bc.swing.models.GenericTreeModel.Node;
import bc.swing.pfrm.Action;
import bc.swing.pfrm.ano.ViewHints.DND;
import bc.swing.pfrm.params.ParamModel;
import bc.swing.renderers.DefaultTreeRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 *
 * @author bennyl
 */
public class TreeBase extends JTree {

    JPopupMenu pmenu;

    public TreeBase() {
        this.pmenu = new JPopupMenu();
        this.setRowHeight(20);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        this.setCellRenderer(new DefaultTreeRenderer());

        configureKeys();
        configureMouse();
    }

    private void configureMouse() {
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) {
                    return;
                }

                TreePath p = getClosestPathForLocation(e.getX(), e.getY());
                if (p != null) {
                    setSelectionPath(p);
                    pmenu.show(TreeBase.this, e.getX(), e.getY());
                }
            }
        });
    }

    private void configureKeys() {
        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == e.VK_F5) {
                    getModel().fireTreeStractureChanged();
                }
            }
        });
    }

    @Override
    public GenericTreeModel getModel() {
        return JavaDSL.<GenericTreeModel<File>>cast(super.getModel());
    }

    public void setModel(final ParamModel model) {
        this.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                if (TreeBase.this.getSelectionPath() != null) {
                    model.fireSelectionChanged(((Node) TreeBase.this.getSelectionPath().getLastPathComponent()).getData());

                } else {
                    model.fireSelectionChanged(null);
                }
            }
        });

        pmenu.removeAll();
        for (final Action a : model.getActions()) {
            pmenu.add(new AbstractAction(a.getName(), a.getIcon()) {

                public void actionPerformed(ActionEvent e) {
                    a.execute();
                }
            });
        }

        if (!model.getViewHints().dnd().equals(DND.UNDEF)) {
            defineDND(model);
        }

    }

    public void reflectChanges(ParamModel to) {
        //
    }

    public void onChange(ParamModel source, Object newValue, Object deltaHint) {
        //
    }

    private void defineDND(ParamModel model) {
        if (model.getViewHints().dnd().equals(DND.DRAG)) {
            this.setTransferHandler(new ObjectTransferHandler(ObjectTransferHandler.DragSupport.COPY, false, true) {

                @Override
                protected void exportDone(Object data, JComponent source, int action) {
                }

                @Override
                public boolean importData(Object data, TransferSupport info) {
                    return false;
                }

                @Override
                public Object exportData(JComponent c) {
                    return ((Node) getSelectionPath().getLastPathComponent()).getData();
                }
            });
            this.setDragEnabled(true);
        }
    }

}
