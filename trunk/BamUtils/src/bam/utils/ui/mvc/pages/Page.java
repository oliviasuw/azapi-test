/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc.pages;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public abstract class Page {
    String name;
    ImageIcon icon;

    public Page(String name, ImageIcon icon) {
        this.name = name;
        this.icon = icon;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
    
    public abstract JPanel getView();
    
    public abstract void disposeView();
}
