/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExecutionStatisticalView.java
 *
 * Created on 18/08/2011, 10:41:40
 */
package bgu.csp.az.dev.pui.stat;

//import bc.dsl.PageDSL;
import bc.swing.pfrm.Page;
import bc.swing.pfrm.PageLayout;

/**
 *
 * @author bennyl
 */
public class StatisticsLayout extends javax.swing.JPanel implements PageLayout {
    public static final String EXPORT_TO_CSV_ACTION = "EXPORT TO CSV ACTION";

    public static final String ROUNDS_ROLE = "ROUNDS ROLE";
    public static final String COLLECTORS_ROLE = "COLLECTORS ROLE";
    public static final String CHART_ROLE = "CHART ROLE";
    public static final String TABLE_ROLE = "TABLE ROLE";

    private Page page;
    
    /** Creates new form ExecutionStatisticalView */
    public StatisticsLayout() {
        initComponents();
//        descriptionLabel.setBackgroundPainter(new RectanglePainter(new Color(245, 245, 245), null));
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

        chartsPanel = new javax.swing.JPanel();
        slaveGrapthPan1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        roundPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        collectorPan = new javax.swing.JPanel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        spacer = new javax.swing.JLabel();
        chartPan = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tablePan = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new java.awt.BorderLayout());

        chartsPanel.setBackground(new java.awt.Color(255, 255, 255));
        chartsPanel.setLayout(new java.awt.GridBagLayout());

        slaveGrapthPan1.setBackground(new java.awt.Color(255, 255, 255));
        slaveGrapthPan1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        slaveGrapthPan1.setPreferredSize(new java.awt.Dimension(250, 116));
        slaveGrapthPan1.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(245, 245, 245));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel3.setText("Analayzing Control");
        jPanel3.add(jLabel3);

        slaveGrapthPan1.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Round");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(roundPanel, gridBagConstraints);

        jLabel5.setText("Statistic");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        jPanel1.add(collectorPan, gridBagConstraints);

        jXHyperlink1.setText("Export To CSV");
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jXHyperlink1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(spacer, gridBagConstraints);

        slaveGrapthPan1.add(jPanel1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        chartsPanel.add(slaveGrapthPan1, gridBagConstraints);

        chartPan.setBackground(new java.awt.Color(255, 255, 255));
        chartPan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        chartPan.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(245, 245, 245));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel1.setText("Chart");
        jPanel2.add(jLabel1);

        chartPan.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 3);
        chartsPanel.add(chartPan, gridBagConstraints);

        tablePan.setBackground(new java.awt.Color(255, 255, 255));
        tablePan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        tablePan.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(245, 245, 245));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel4.setText("Data");
        jPanel4.add(jLabel4);

        tablePan.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        chartsPanel.add(tablePan, gridBagConstraints);

        add(chartsPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
        page.executeAction(EXPORT_TO_CSV_ACTION);
    }//GEN-LAST:event_jXHyperlink1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPan;
    private javax.swing.JPanel chartsPanel;
    private javax.swing.JPanel collectorPan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private javax.swing.JPanel roundPanel;
    private javax.swing.JPanel slaveGrapthPan1;
    private javax.swing.JLabel spacer;
    private javax.swing.JPanel tablePan;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setPage(Page page) {
        this.page = page;
//        PageDSL.fillByRole(page, roundPanel, ROUNDS_ROLE);
//        PageDSL.fillByRole(page, collectorPan, COLLECTORS_ROLE);
//        PageDSL.fillByRole(page, chartPan, CHART_ROLE);
//        PageDSL.fillByRole(page, tablePan, TABLE_ROLE);
//        PageDSL.fillByRole(model, chartSelectPan, GRAPHS_TREE_ROLE);
//        model.getParamsWithRole(GRAPHS_TREE_ROLE).get(0).addSelectionListner(new ChangeListener() {
//
//            @Override
//            public void onChange(BaseParamModel source, Object newValue, Object deltaHint) {
//                descriptionLabel.setText(source.getPage().getModel().provideParamValueDescription(source.getName(), newValue));
//            }
//        });
//        
//        PageDSL.insertToCenterByRole(model, masterGraphPan, MASTER_GRAPH_ROLE);
    }

    @Override
    public void onDispose() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
