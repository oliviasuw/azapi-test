/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.views;

import bc.dsl.JavaDSL;
import bc.swing.dnd.ObjectTransferHandler;
import bc.swing.models.GenericTreeModel;
import bc.swing.models.GenericTreeModel.Node;
import bc.swing.pfrm.Action;
import bc.swing.pfrm.ano.ViewHints.DND;
import bc.swing.pfrm.BaseParamModel;
import bc.swing.pfrm.DeltaHint;
import bc.swing.pfrm.FieldParamModel.ChangeListener;
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
    BaseParamModel pmodel;
    protected boolean extractValueFromNodeOnSelection = true;

    public TreeBase() {
        this.pmenu = new JPopupMenu();
        this.setRowHeight(20);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        this.setCellRenderer(new DefaultTreeRenderer());

        configureKeys();
        configureMouse();
    }

    protected void setExtractValueFromNodeOnSelection(boolean extractValueFromNodeOnSelection) {
        this.extractValueFromNodeOnSelection = extractValueFromNodeOnSelection;
    }
    
    private void configureMouse() {
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() % 2 == 0) {
                    pmodel.executeDefaultAction();
                }

                if (e.getButton() == MouseEvent.BUTTON3) { //POPUP SHOULD BE VIEWED
                    TreePath p = getClosestPathForLocation(e.getX(), e.getY());
                    if (p != null) {
                        setSelectionPath(p);
                        pmenu.show(TreeBase.this, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void configureKeys() {
        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                int kc = e.getKeyCode();
                switch (kc) {
                    case KeyEvent.VK_F5:
                        getModel().fireTreeStractureChanged();
                        break;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pmodel.executeDefaultAction();
                }
            }
        });
    }

    @Override
    public GenericTreeModel getModel() {
        return JavaDSL.<GenericTreeModel<File>>cast(super.getModel());
    }

    public void setParam(final BaseParamModel param) {
        pmodel = param;
        this.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                if (TreeBase.this.getSelectionPath() != null) {
                    if (extractValueFromNodeOnSelection){
                        param.fireSelectionChanged(((Node) TreeBase.this.getSelectionPath().getLastPathComponent()).getData());
                    }else {
                        param.fireSelectionChanged(((Node) TreeBase.this.getSelectionPath().getLastPathComponent()));
                    }

                } else {
                    param.fireSelectionChanged(null);
                }
            }
        });

        pmenu.removeAll();
        for (final Action a : param.getActions()) {
            pmenu.add(new AbstractAction(a.getName(), a.getIcon()) {

                public void actionPerformed(ActionEvent e) {
                    a.execute();
                }
            });
        }

        if (!param.getViewHints().dnd().equals(DND.UNDEF)) {
            defineDND(param);
        }

        param.addChangeListener(new ChangeListener() {

            public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
                if (deltaHint != null && deltaHint instanceof DeltaHint) {
                    DeltaHint dh = (DeltaHint) deltaHint;
                    switch (dh.type) {
                        case DeltaHint.ONE_ITEM_ADDED_TYPE:
                            if (dh.item instanceof Node) {
                                getModel().fireNodeAdded((Node) dh.item);
                            } else {
                                throw new UnsupportedOperationException();
                            }
                            break;
                        case DeltaHint.ONE_ITEM_CHANGED_TYPE:
                            if (dh.item instanceof Node) {
                                getModel().fireNodeChanged((Node) dh.item);
                            } else {
                                throw new UnsupportedOperationException();
                            }
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                }
            }
        });

    }

    public void reflectChangesToParam(BaseParamModel to) {
        //
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
        //
    }

    private void defineDND(final BaseParamModel model) {
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
                    return model.dragFilter(((Node) getSelectionPath().getLastPathComponent()).getData());
                }
            });
            this.setDragEnabled(true);
        }
    }
}
