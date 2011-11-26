/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.visuals;

import javax.swing.Icon;

/**
 *
 * @author bennyl
 */
public class Visual {
    Object item;
    String text;
    String description;
    Icon icon;

    public Visual(Object item, String text, String description, Icon icon) {
        this.item = item;
        this.text = text;
        this.description = description;
        this.icon = icon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        
        if (obj instanceof Visual){
            Visual other = (Visual) obj;
            return other.item.equals(this.item);
        }else if (obj.getClass().equals(this.item.getClass())){
            return obj.equals(item);
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.item != null ? this.item.hashCode() : 0);
        return hash;
    }
    
}
