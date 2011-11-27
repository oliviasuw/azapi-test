/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.scan;

/**
 *
 * @author bennyl
 */
public class ItemBox<T> implements Box<T> {

    T item;

    public ItemBox(T item) {
        this.item = item;
    }
    
    public T get() {
        return item;
    }

    public boolean set(T val) {
        this.item = val;
        return true;
    }

    public boolean isReadOnly() {
        return false;
    }

    public Class getType() {
        return item.getClass();
    }
    
}
