/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.rnd;

import bam.utils.ui.mvc.GenericTreeModel.Node;
import bam.utils.ui.mvc.swing.CheckTreeModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author bennyl
 */
public class CheckTreeRenderer implements TreeCellRenderer {

    IconSupplier isup; 
    
    JCheckBox check;
    JLabel label;
    //Border selectedBorder;
    private Color unselectedColor = new Color(245,245,245); 
    private Color selectedColor = new Color(160,200,255);

    public CheckTreeRenderer() {
        check = new JCheckBox();
        label = new JLabel();
        
        check.setOpaque(true);
        label.setOpaque(true);

        check.setBackground(unselectedColor);
        label.setBackground(unselectedColor);
        
        //label.setHorizontalTextPosition(JLabel.CENTER);
        
    }

    public void setIconSupplier(IconSupplier isup) {
        this.isup = isup;
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JComponent choosen;
        if (leaf) {
            check.setText(" " + value.toString() + " ");
            check.setSelected(((CheckTreeModel) tree.getModel()).isChecked((Node) value));
            choosen = check;
        } else {
            if (isup != null) label.setIcon(isup.supply(value));
            label.setText(" " + value.toString() + " ");
            choosen = label;
        }
        
        if (selected) {
            choosen.setBackground(selectedColor);
        } else {
            choosen.setBackground(unselectedColor);
        }

        choosen.validate();
        choosen.doLayout();
        return choosen;
    }
}
