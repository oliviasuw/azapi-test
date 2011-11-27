/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm;

import javax.swing.Icon;

/**
 *
 * @author bennyl
 */
public class Visual2 {
    Object item;
    private String text = ""; 
    private Icon icon = null;

    public Visual2(Object item) {
        this.item = item;
    }

    public Object getItem() {
        return item;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }
    
}
