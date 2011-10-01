/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.rnd;

import bam.utils.SwingUtils;
import bam.utils.ui.mvc.GenericTreeModel;
import bam.utils.ui.mvc.GenericTreeModel.Node;
import bam.utils.ui.mvc.GenericTreeModel.SimpleLeafNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author bennyl
 */
public class GenericTreeCellRenderer implements TreeCellRenderer{

    public static final Border SELECTED_BORDER = new CompoundBorder(new LineBorder(new Color(205,210,220), 1), new EmptyBorder(2, 1, 1, 1));
    
    JLabel label;
    public GenericTreeCellRenderer() {
        label = new JLabel();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Node val = (Node) value;
        label.setText(val.toString());
        label.setIcon(val.getIcon());
        
        if (selected){
            label.setBorder(SELECTED_BORDER);
        }else {
            label.setBorder(null);
        }
        final Dimension dimension = new Dimension(label.getFontMetrics(label.getFont()).stringWidth(val.toString()) + 16 + 16, 16);
        label.setPreferredSize(dimension);
        label.setMinimumSize(dimension);
        
        return label;
    }
    
    //Test
    public static void main(String[] args){
        SwingUtils.configureSystemLookAndFeel();
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JTree tree = new JTree();
        GenericTreeModel model = new GenericTreeModel(new Node("Root", null) {

            @Override
            protected Node createChildNode(Object t) {
                final SimpleLeafNode n = new GenericTreeModel.SimpleLeafNode(t, this);
                n.setIcon(SwingUtils.icon("msgbox-logo"));
                return n;
            }

            @Override
            protected Object[] _getChilds() {
                return new Object[]{"C1231231", "C2", "C3", "C4"};
            }
        });
        
        tree.setModel(model);
        tree.setCellRenderer(new GenericTreeCellRenderer());
        panel.add(tree, BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        
    }
    
   
    
}
