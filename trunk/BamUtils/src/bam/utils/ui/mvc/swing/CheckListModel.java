/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc.swing;

import bam.utils.ui.mvc.GenericListModel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class CheckListModel<T> extends GenericListModel<T> {

    HashSet<Integer> checked;
    List<CheckListener<T>> listeners;

    public CheckListModel() {
        checked = new HashSet<Integer>();
        listeners = new LinkedList<CheckListener<T>>();
    }

    public List<T> getCheckedItems() {
        List<T> list = new LinkedList<T>();
        for (Integer c : checked) {
            list.add(getElementAt(c));
        }
        return list;
    }

    public boolean isChecked(T item) {
        return checked.contains(model.indexOf(item));
    }

    public boolean isChecked(int row) {
        return checked.contains(row);
    }

    public int getNumberOfCheckedItems() {
        return checked.size();
    }

    public void setChecked(int row, boolean check) {
        if (check) {
            checked.add(row);
            fireItemChecked(row);
        } else {
            checked.remove(row);
            fireItemUnchecked(row);
        }
    }

    public void setChecked(T item, boolean check) {
        setChecked(model.indexOf(item), check);
    }

    public void addCheckedListener(CheckListener<T> listener) {
        this.listeners.add(listener);
    }

    private void fireItemChecked(int row) {
        for (CheckListener<T> l : listeners) l.onCheckChanged(this, row, true);
        fireContentsChanged(this, row, row+1);
    }

    private void fireItemUnchecked(int row) {
        for (CheckListener<T> l : listeners) l.onCheckChanged(this, row, false);
        fireContentsChanged(this, row, row+1);
    }

    public void addAll(List<T> counters) {
        model.addAll(counters);
        fireIntervalAdded(this, model.size() - counters.size(), model.size());
    }

    public static interface CheckListener<T> {

        public void onCheckChanged(CheckListModel<T> source, int line, boolean checked);
    }
}
