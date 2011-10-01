/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.models;

import bc.swing.pfrm.Action;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author BLutati
 */
public class GenericTreeModel<T> implements TreeModel, TreeSelectionListener{
    Node<T> root;
    List<Action> actions = new LinkedList<Action>();
    List<TreeModelListener> listeners = new LinkedList<TreeModelListener>();
    T selectedItem;

    public GenericTreeModel(Node<T> root) {
        this.root = root;
    }

    public void setSelectedItem(T selectedItem) {
        this.selectedItem = selectedItem;
    }

    public T getSelectedItem() {
        return selectedItem;
    }
    
    public List<Action> getActions() {
        return actions;
    }
    
    public void addAction(Action action){
        actions.add(action);
    }

    protected void setRoot(Node<T> root){
        this.root = root;
        fireTreeStractureChanged();
    }

    public void fireTreeStractureChanged() {
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(new TreeModelEvent(root, new Object[]{root}));
        }
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        Node<T> nparent = (Node<T>) parent;
        return nparent.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        Node<T> nparent = (Node<T>) parent;
        return nparent.getChildren().size();
    }


    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Imutable Tree Change Attempt");
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Node<T> nparent = (Node<T>) parent;
        return nparent.getChildren().indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public void valueChanged(TreeSelectionEvent e) {
        selectedItem = ((Node<T>)e.getPath().getLastPathComponent()).getData();
    }
    
    public static abstract class Node<T> {
        private T data;
        private String string;
        private ImageIcon icon = null;
        private boolean selected;

        public Node(T data) {
            this.data = data;
        }

        public void setIcon(ImageIcon icon) {
            this.icon = icon;
        }

        public ImageIcon getIcon() {
            return icon;
        }
        
        public T getData() {
            return data;
        }

        public abstract List<Node<T>> getChildren();

        @Override
        public String toString() {
            return string == null? data.toString() : string;
        }

        public boolean isLeaf() {
            return getChildren().isEmpty();
        }


        void setString(String string) {
            this.string = string;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }
        
    }

    public static class LeafNode<T> extends Node<T>{

        public LeafNode(T data) {
            super(data);
        }

        @Override
        public List<Node<T>> getChildren() {
            return new LinkedList<Node<T>>();
        }

    }
}
