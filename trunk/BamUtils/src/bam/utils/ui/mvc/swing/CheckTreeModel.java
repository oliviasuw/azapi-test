/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc.swing;

import static bam.utils.JavaUtils.*;
import bam.utils.ui.mvc.GenericListModel;
import bam.utils.ui.mvc.GenericTreeModel;
import bam.utils.ui.mvc.GenericTreeModel.Node;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class CheckTreeModel<T> extends GenericTreeModel<T>{

    HashSet<Node<T>> checked;
    List<CheckListener<T>> listeners;

    public CheckTreeModel(Node<T> root) {
        super(root);
        checked = new HashSet<Node<T>>();
        listeners = new LinkedList<CheckListener<T>>();
    }

    public List<T> getCheckedItems() {
        return map(checked, new Fn1<Node<T>, T>() {

            @Override
            public T invoke(Node<T> arg) {
                return arg.getValue();
            }
        });
    }

    public boolean isChecked(Node<T> item) {
        return checked.contains(item);
    }

    public int getNumberOfCheckedItems() {
        return checked.size();
    }

    public void setChecked(T item, boolean check){
        setChecked(new SimpleLeafNode<T>(item, null), check);
    }
    
    public void setChecked(Node<T> item, boolean check) {
        if (check) {
            checked.add(item);
            fireItemChecked(item);
        } else {
            checked.remove(item);
            fireItemUnchecked(item);
        }
    }

    public void addCheckedListener(CheckListener<T> listener) {
        this.listeners.add(listener);
    }

    private void fireItemChecked(Node<T> item) {
        for (CheckListener<T> l : listeners) l.onCheckChanged(this, item.getValue(), true);
        fireNodeChanged(item);
    }

    private void fireItemUnchecked(Node<T> item) {
        for (CheckListener<T> l : listeners) l.onCheckChanged(this, item.getValue(), false);
        fireNodeChanged(item);
    }

    public static interface CheckListener<T> {

        public void onCheckChanged(CheckTreeModel<T> source, T item, boolean checked);
    }
}
