/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * XMenuStepListRenderer.java
 *
 * Created on 06/07/2011, 14:05:26
 */
package bc.swing.pfrm2.view;

import bc.swing.renderers.*;
import bc.dsl.SwingDSL;
import bc.swing.pfrm.BaseParamModel;
import bc.swing.pfrm2.Att;
import bc.swing.pfrm2.Node;
import bc.swing.pfrm2.NodeDescriptionProvider;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author bennyl
 */
public class DefaultListRenderer extends javax.swing.JPanel implements ListCellRenderer {

    public static final Color EVEN_LINE_COLOR = Color.white;
    public static final Color ODD_LINE_COLOR = new Color(245,245,255);
    public static final Color SELECTED_LINE_COLOR = new Color(140,180,255);//new Color(185,208,255);
    public static final Color FONT_COLOR = new Color(51,51,51);
    public static final Color SELECTED_FONT_COLOR = new Color(255,255,120);
    public static final Color DESCRIPTION_FONT_COLOR = new Color(156,191,255);
    public static final Color SELECTED_DESCRIPTION_FONT_COLOR = Color.white;
    
    Node model;
    
    /** Creates new form XMenuStepListRenderer */
    public DefaultListRenderer() {
        initComponents();
    }

    public void setModel(Node model) {
        this.model = model;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbl = new javax.swing.JLabel();
        desc = new org.jdesktop.swingx.JXLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        lbl.setFont(new java.awt.Font("Tahoma", 1, 11));
        lbl.setForeground(new java.awt.Color(102, 102, 102));
        lbl.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(lbl, gridBagConstraints);

        desc.setBackground(new java.awt.Color(255, 204, 0));
        desc.setForeground(new java.awt.Color(156, 191, 255));
        desc.setText("jXLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(desc, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXLabel desc;
    private javax.swing.JLabel lbl;
    // End of variables declaration//GEN-END:variables

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        Node val = (Node) value;
        
        lbl.setIcon(SwingDSL.resIcon(val.getStringAtt(Att.ICON)));
        lbl.setText(val.getValue().toString());
        Dimension ps = this.getPreferredSize();
        //this.setPreferredSize(new Dimension(list.getSize().width - 5, ps.height));
        
        NodeDescriptionProvider desp = val.getAtt(Att.DESCRIPTION_PROVIDER, NodeDescriptionProvider.class, null); 
        if (desp!=null){
            desc.setText(desp.describe(val));
        }else {
            desc.setText("");
        }
        if (index % 2 == 0) {
            setBackground(EVEN_LINE_COLOR);
        }else{
            setBackground(ODD_LINE_COLOR);
        }
        
        if (isSelected){
            setBackground(SELECTED_LINE_COLOR);
            lbl.setForeground(SELECTED_FONT_COLOR);
            desc.setForeground(SELECTED_DESCRIPTION_FONT_COLOR);
        }else{
            lbl.setForeground(FONT_COLOR);
            desc.setForeground(DESCRIPTION_FONT_COLOR);
        }
        
        revalidate();
        return this;
    }
}
