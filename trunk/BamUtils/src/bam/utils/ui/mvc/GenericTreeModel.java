/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author bennyl
 */
public class GenericTreeModel<T> implements TreeModel {

    List<TreeModelListener> listeners;
    Node<T> root;

    public GenericTreeModel(Node<T> root) {
        listeners = new LinkedList<TreeModelListener>();
        this.root = root;
    }

    @Override
    public Node<T> getRoot() {
        return root;
    }

    public TreePath calculatePathToNode(Node<T> node) {
        LinkedList<Node<T>> ll = new LinkedList<Node<T>>();

        while (node != null) {
            ll.addFirst(node);
            node = node.getParent();
        }

        if (ll.isEmpty()) {
            ll.add(root);
        }

        return new TreePath(ll.toArray());
    }

    public void fireTreeStracturedChanged() {
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(new TreeModelEvent(this, new Object[]{root}));
        }
    }

    public void fireNodeChanged(Node<T> node) {
        final TreePath path = calculatePathToNode(node);
        for (TreeModelListener l : listeners) {
            l.treeNodesChanged(new TreeModelEvent(this, path));
        }
    }

    public void fireNodeAdded(Node<T> node) {
        final TreePath path = calculatePathToNode(node);
        if (path.getPath()[0].equals(root)) {
            fireTreeStracturedChanged();
        } else {
            for (TreeModelListener l : listeners) {
                l.treeNodesInserted(new TreeModelEvent(this, path));
            }
        }
    }

    public void fireNodeRemoved(Node<T> node) {
        final TreePath path = calculatePathToNode(node);
        if (path.getPath()[0].equals(root)) {
            fireTreeStracturedChanged();
        } else {
            for (TreeModelListener l : listeners) {
                l.treeNodesRemoved(new TreeModelEvent(this, path));
            }
        }
    }

    public T getNodeValue() {
        return root.getValue();
    }

    @Override
    public Object getChild(Object parent, int index) {
        return asNode(parent).getChilds().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return asNode(parent).getChilds().size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return asNode(node).getChilds().isEmpty();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        for (TreeModelListener l : listeners) {
            l.treeNodesChanged(new TreeModelEvent(this, path));
        }
    }

    public void setRoot(Node<T> root) {
        this.root = root;
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(new TreeModelEvent(this, new Object[]{root}));
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return asNode(parent).getChilds().indexOf(child);
    }

    private Node<T> asNode(Object o) {
        return (Node<T>) o;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public static abstract class Node<T> {

        T value;
        Node<T> parent;
        String name;
        int tag;
        ImageIcon icon;

        public Node(T value, Node<T> parent) {
            this.value = value;
            this.parent = parent;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }
        
        public ImageIcon getIcon() {
            return icon;
        }

        public void setIcon(ImageIcon icon) {
            this.icon = icon;
        }
        
        public int getTag(){
            return tag;
        }

        public void setTag(int tag){
            this.tag = tag;
        }
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }

        public Node<T> getParent() {
            return parent;
        }

        public T getValue() {
            return value;
        }

        protected abstract Node<T> createChildNode(T t);

        protected abstract T[] _getChilds();

        public List<Node<T>> getChilds() {
            LinkedList<Node<T>> childs = new LinkedList<Node<T>>();
            for (T t : _getChilds()) {
                Node<T> ndt = createChildNode(t);
                childs.add(ndt);
            }

            return childs;
        }

        public Node<T> getChildFor(T item) {
            for (Node<T> c : getChilds()) {
                if (c.getValue().equals(item)) {
                    return c;
                }
            }

            return null;
        }

        @Override
        public String toString() {
            if (name != null) {
                return name;
            }
            return value.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Node && getValue().equals(((Node) obj).getValue());
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }

        public boolean isLeaf() {
            return getChilds().isEmpty();
        }
    }
    
    public static class SimpleLeafNode<T> extends Node<T> {

        Class ct;

        public SimpleLeafNode(T value, Node<T> parent) {
            super(value, parent);
            ct = value.getClass();
        }

        @Override
        protected Node<T> createChildNode(T t) {
            return null;
        }

        @Override
        protected T[] _getChilds() {
            return (T[]) Array.newInstance(ct, 0);
        }
    }
}
