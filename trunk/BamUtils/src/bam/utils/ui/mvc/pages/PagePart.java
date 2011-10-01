/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc.pages;

import javax.swing.JPanel;

/**
 *
 * @author bennyl
 */
public abstract class PagePart{
    private String name;
    private String description;

    public PagePart(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public abstract JPanel getView();
    
    public abstract void disposeView();

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
    
}
