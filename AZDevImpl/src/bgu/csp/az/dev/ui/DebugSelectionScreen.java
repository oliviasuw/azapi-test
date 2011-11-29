/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StatusScreen.java
 *
 * Created on 24/11/2011, 16:25:11
 */
package bgu.csp.az.dev.ui;

import javax.swing.plaf.metal.MetalProgressBarUI;

/**
 *
 * @author bennyl
 */
public class DebugSelectionScreen extends javax.swing.JPanel {

    /** Creates new form StatusScreen */
    public DebugSelectionScreen() {
        initComponents();
        this.problemView.setDebugProblemButtonPanVisibility(true);
        this.problemView.setFaildProblemPanVisibility(true);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        deleteSelected = new javax.swing.JButton();
        deleteAll = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        stripeList1 = new bc.ui.swing.lists.StripeList();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        problemView = new bgu.csp.az.dev.ui.RoundView();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(120, 120, 120));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel11.setBackground(new java.awt.Color(120, 120, 120));
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        deleteSelected.setBackground(null);
        deleteSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/delete-selected.png"))); // NOI18N
        deleteSelected.setToolTipText("Delete the selected problems from the list");
        deleteSelected.setMinimumSize(new java.awt.Dimension(25, 25));
        deleteSelected.setOpaque(false);
        deleteSelected.setPreferredSize(new java.awt.Dimension(25, 25));
        jPanel11.add(deleteSelected);

        deleteAll.setBackground(null);
        deleteAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/delete-all.png"))); // NOI18N
        deleteAll.setToolTipText("Delete all problems but the last from the list");
        deleteAll.setMinimumSize(new java.awt.Dimension(25, 25));
        deleteAll.setOpaque(false);
        deleteAll.setPreferredSize(new java.awt.Dimension(25, 25));
        jPanel11.add(deleteAll);

        jLabel4.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Select one of the failed sessions to debug");
        jPanel11.add(jLabel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(jPanel11, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        stripeList1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 3, new java.awt.Color(153, 153, 153)));
        stripeList1.setMinimumSize(new java.awt.Dimension(200, 24));
        stripeList1.setOddBackColor(new java.awt.Color(230, 230, 230));
        stripeList1.setOddForeColor(new java.awt.Color(61, 61, 61));
        stripeList1.setPreferredSize(new java.awt.Dimension(200, 194));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(stripeList1, gridBagConstraints);

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setViewportView(problemView);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        jPanel1.add(jPanel3, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteAll;
    private javax.swing.JButton deleteSelected;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private bgu.csp.az.dev.ui.RoundView problemView;
    private bc.ui.swing.lists.StripeList stripeList1;
    // End of variables declaration//GEN-END:variables
}
