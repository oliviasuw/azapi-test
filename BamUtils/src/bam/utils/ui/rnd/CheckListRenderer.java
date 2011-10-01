/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.rnd;

import bam.utils.ui.mvc.swing.CheckListModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author bennyl
 */
public class CheckListRenderer extends JCheckBox implements ListCellRenderer{

    private  Color selectedColor = new Color(180, 220, 250);;
    private  Color unselectedColor = Color.white;

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setUnselectedColor(Color unselectedColor) {
        this.unselectedColor = unselectedColor;
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected){
            setBackground(selectedColor);
        }else {
            setBackground(unselectedColor);
        }
        
        setSelected(((CheckListModel) list.getModel()).isChecked(index));
        
        setText(value.toString());
        
        return this;
    }
    
}
