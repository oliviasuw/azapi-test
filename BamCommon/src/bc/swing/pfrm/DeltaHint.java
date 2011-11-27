/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

/**
 *
 * @author bennyl
 */
public class DeltaHint {

    public static final int LAST_ITEM_ADDED_TYPE = 0;
    public static final int ONE_ITEM_REOMVED_TYPE = 1;
    public static final int ONE_ITEM_CHANGED_TYPE = 2;
    public static final int ONE_ITEM_ADDED_TYPE = 3;
    public static final int NO_HINT = 4;
    
    public int type;
    public Object item;

    public DeltaHint(int type) {
        this.type = type;
    }

    public DeltaHint(int type, Object item) {
        this.type = type;
        this.item = item;
    }

    public static DeltaHint oneItemRemoved(Object item) {
        return new DeltaHint(ONE_ITEM_REOMVED_TYPE, item);
    }

    public static DeltaHint oneItemChanged(Object item) {
        return new DeltaHint(ONE_ITEM_CHANGED_TYPE, item);
    }

    public static DeltaHint oneItemAdded(Object item) {
        return new DeltaHint(ONE_ITEM_ADDED_TYPE, item);
    }

    public static DeltaHint lastItemAdded() {
        return new DeltaHint(LAST_ITEM_ADDED_TYPE);
    }
    
    public static DeltaHint noHint(){
        return new DeltaHint(NO_HINT);
    }
}
