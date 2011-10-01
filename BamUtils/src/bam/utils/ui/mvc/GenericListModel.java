/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author bennyl
 */
public class GenericListModel<T> extends AbstractListModel {

    protected LinkedList<T> model;
    
    public GenericListModel() {
        model = new LinkedList<T>();
    }
    
    public GenericListModel(Collection<T> copy) {
        model = new LinkedList<T>(copy);
        fireContentsChanged(this, 0, copy.size());
    }
    
    @Override
    public int getSize() {
        return model.size();
    }

    @Override
    public T getElementAt(int index) {
        return model.get(index);
    }
    
    public void addLast(T item){
        model.addLast(item);
        fireIntervalAdded(this, getSize()-1, getSize());
    }
    
    public List<T> getInternalList(){
        return model;
    }

    public void fillWith(List<T> all) {
        model.addAll(all);
        fireIntervalAdded(this, model.size() - all.size(), model.size());
    }
}
