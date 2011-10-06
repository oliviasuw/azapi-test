/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.views;

import bc.swing.pfrm.BaseParamModel;
import bc.swing.pfrm.Action;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import bc.swing.pfrm.params.views.ext.IconProvider;
import bc.swing.pfrm.params.views.ext.SimpleTreeRenderer;
import bc.swing.models.GenericTreeModel;
import bc.swing.models.GenericTreeModel.Node;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;
import bc.dsl.JavaDSL;
import bc.swing.dnd.ObjectTransferHandler;
import bc.swing.pfrm.ano.ViewHints.DND;
import bc.swing.pfrm.ParamView;
import java.awt.event.FocusAdapter;
import java.awt.event.MouseListener;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionListener;
import static bc.dsl.JavaDSL.*;
import static bc.dsl.SwingDSL.*;

/**
 *
 * @author BLutati
 */
public class FileTreePV extends JTree implements ParamView {

    JPopupMenu pmenu;
    private GenericTreeModel<File> ftm;

    public FileTreePV() {
        this.pmenu = new JPopupMenu();
        this.setModel(new GenericTreeModel(new FileNode(new File("."), null)));
        this.setRowHeight(20);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        this.setCellRenderer(new SimpleTreeRenderer(new IconProvider() {

            @Override
            public ImageIcon getIcon(Object item) {
                return resIcon("file");
            }
        }));

        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == e.VK_F5) {
                    getModel().fireTreeStractureChanged();
                }
            }
        });

        this.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != e.BUTTON3) {
                    return;
                }

                TreePath p = getClosestPathForLocation(e.getX(), e.getY());
                if (p != null) {
                    setSelectionPath(p);
                    pmenu.show(FileTreePV.this, e.getX(), e.getY());
                }
            }

            public void mousePressed(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseReleased(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });

    }

    @Override
    public GenericTreeModel<File> getModel() {
        return JavaDSL.<GenericTreeModel<File>>cast(super.getModel());
    }

    public void setParam(final BaseParamModel model) {

        File f = (File) model.getValue();
        ftm = new GenericTreeModel<File>(new FileNode(f, null));
        this.setModel(ftm);

        this.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                if (FileTreePV.this.getSelectionPath() != null){
                model.fireSelectionChanged(((Node)FileTreePV.this.getSelectionPath().getLastPathComponent()).getData());
                    
                }else {
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

        /*this.addTreeSelectionListener(ftm);*/
    }

    public void reflectChangesToParam(BaseParamModel to) {
        //
    }

    public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
        //
    }

    private void defineDND(BaseParamModel model) {
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
                    return ((Node)getSelectionPath().getLastPathComponent()).getData();
                }

            });
            this.setDragEnabled(true);
        }
    }

    public static class FileNode extends Node<File> {

        public FileNode(File data, Node parent) {
            super(data, parent);
        }

        @Override
        public boolean isLeaf() {
            return getData().isFile();
        }

        @Override
        public String toString() {
            return getData().getName();
        }

        @Override
        public List<Node<File>> getChildren() {
            final File[] files = getData().listFiles();

            if (files == null) {
                return new LinkedList<Node<File>>();
            }

            return map(files, new Fn1<Node<File>, File>() {

                @Override
                public Node<File> invoke(File arg) {
                    return new FileNode(arg, FileNode.this);
                }
            });
        }
    }
}
