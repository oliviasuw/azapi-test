/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

/**
 *
 * @author bennyl
 */
public class ListChangeDeltaHint {
    public static final int LAST_ITEM_ADDED_TYPE = 0;
    public static final int ONE_ITEM_REOMVED_TYPE = 1;
    public static final int ONE_ITEM_CHANGED_TYPE = 2;
    
    public int type;
    public Object item;

    public ListChangeDeltaHint(int type) {
        this.type = type;
    }

    public ListChangeDeltaHint(int type, Object item) {
        this.type = type;
        this.item = item;
    }

    public static ListChangeDeltaHint oneItemRemoved(Object item){
        return new ListChangeDeltaHint(ONE_ITEM_REOMVED_TYPE, item);
    }

    public static ListChangeDeltaHint oneItemChanged(Object item){
        return new ListChangeDeltaHint(ONE_ITEM_CHANGED_TYPE, item);
    }

    public static ListChangeDeltaHint lastItemAdded(){
        return new ListChangeDeltaHint(LAST_ITEM_ADDED_TYPE);
    }

}
