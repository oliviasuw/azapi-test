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

import bc.ui.swing.visuals.Visual;
import bgu.csp.az.api.infra.Experiment;
import bgu.csp.az.api.infra.Round;
import javax.swing.plaf.metal.MetalProgressBarUI;

/**
 *
 * @author bennyl
 */
public class StatusScreen extends javax.swing.JPanel {

    /** Creates new form StatusScreen */
    public StatusScreen() {
        initComponents();
        execProgress.setUI(new MetalProgressBarUI());
    }

    void setModel(Experiment experiment) {
        roundList.setItems(Visual.adapt(experiment.getRounds(), new Visual.VisualGen() {

            @Override
            public Visual gen(Object it) {
                Round r = (Round) it;
                return new Visual(it, r.getName(), "", null);
            }
        }));
        
        
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

        roundDataScroll = new javax.swing.JScrollPane();
        roundView1 = new bgu.csp.az.dev.ui.RoundView();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        execProgress = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        roundList = new bc.ui.swing.lists.StripeList();
        roundData = new bc.ui.swing.useful.DataPanel();

        roundDataScroll.setBorder(null);
        roundDataScroll.setViewportView(roundView1);

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/monitor.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Consolas", 1, 14));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Execution 7 of 16, 20 sec");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel2.add(execProgress, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setBackground(new java.awt.Color(120, 120, 120));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel11.setBackground(new java.awt.Color(120, 120, 120));
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Executing Rounds");
        jPanel11.add(jLabel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(jPanel11, gridBagConstraints);

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        roundList.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 3, new java.awt.Color(153, 153, 153)));
        roundList.setMinimumSize(new java.awt.Dimension(200, 24));
        roundList.setOddBackColor(new java.awt.Color(230, 230, 230));
        roundList.setOddForeColor(new java.awt.Color(61, 61, 61));
        roundList.setPreferredSize(new java.awt.Dimension(200, 194));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(roundList, gridBagConstraints);

        roundData.setNoDataForeColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel3.add(roundData, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        jPanel1.add(jPanel3, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar execProgress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private bc.ui.swing.useful.DataPanel roundData;
    private javax.swing.JScrollPane roundDataScroll;
    private bc.ui.swing.lists.StripeList roundList;
    private bgu.csp.az.dev.ui.RoundView roundView1;
    // End of variables declaration//GEN-END:variables
}
