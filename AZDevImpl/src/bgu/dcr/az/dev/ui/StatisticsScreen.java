/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StatisticsScreen.java
 *
 * Created on 24/11/2011, 16:25:21
 */
package bgu.dcr.az.dev.ui;

import bc.swing.models.DataExtractor;
import bc.swing.models.GenericTableModel;
import bc.ui.swing.charts.LineChart;
import bc.ui.swing.listeners.SelectionListener;
import bc.ui.swing.lists.OptionList;
import bc.ui.swing.visuals.Visual;
import bgu.dcr.az.api.infra.Experiment;
import bgu.dcr.az.api.infra.Round;
import bgu.dcr.az.api.infra.VariableMetadata;
import bgu.dcr.az.api.infra.stat.StatisticCollector;
import bgu.dcr.az.api.infra.stat.vmod.LineVisualModel;
import bgu.dcr.az.impl.db.DatabaseUnit;
import java.awt.BorderLayout;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JFrame;

/**
 *
 * @author bennyl
 */
public class StatisticsScreen extends javax.swing.JPanel {

    private OptionList availableStatisticsList;
    private OptionList roundsList;
    private StatisticCollector selectedCollector = null;
    private Round selectedRound = null;

    /** Creates new form StatisticsScreen */
    public StatisticsScreen() {
        initComponents();
        //STAT
        availableStatisticsList = new OptionList();
        statScroll.setViewportView(availableStatisticsList);

        //ROUND
        roundsList = new OptionList();
        roundScroll.setViewportView(roundsList);

        statScroll.getViewport().setOpaque(false);
        varscrolls.getViewport().setOpaque(false);
        roundScroll.getViewport().setOpaque(false);

        //TESTING 
//        availableStatisticsList.add("stat 1");
//        availableStatisticsList.add("stat 2");
//        
//        roundsList.add("stat 1");
//        roundsList.add("stat 2");
//        
//        vars.add(new VariableMetadata("test1", "this is test 1", 5, Integer.class));
//        vars.add(new VariableMetadata("test2", "this is test 2", 5, Integer.class));
//        vars.add(new VariableMetadata("test3", "this is test 3", 5, Integer.class));

    }

    public void setModel(Experiment exp) {
        availableStatisticsList.getSelectionListeners().addListener(new SelectionListener() {

            @Override
            public void onSelectionChanged(Object source, List selectedItems) {
                varsDataPan.unSetData();
                selectedCollector = null;

                if (!selectedItems.isEmpty()) {
                    StatisticCollector sc = (StatisticCollector) ((Visual) selectedItems.get(0)).getItem();
                    selectedCollector = sc;
                    if (sc.provideExpectedVariables().length > 0) {
                        vars.setModel(sc.provideExpectedVariables());
                        varsDataPan.setData(varscrolls);
                    }
                }

            }
        });

        roundsList.getSelectionListeners().addListener(new SelectionListener() {

            @Override
            public void onSelectionChanged(Object source, List selectedItems) {
                availableStatisticsList.clear();
                selectedRound = null;

                if (!selectedItems.isEmpty()) {

                    selectedRound = ((Round) ((Visual) selectedItems.get(0)).getItem());
                    availableStatisticsList.setItems(Visual.adapt(selectedRound.getRegisteredStatisticCollectors(), new Visual.VisualGen() {

                        @Override
                        public Visual gen(Object it) {
                            StatisticCollector sc = (StatisticCollector) it;
                            return new Visual(it, sc.getName(), "", null);
                        }
                    }));
                }
            }
        });

        roundsList.setItems(Visual.adapt(exp.getRounds(), new Visual.VisualGen() {

            @Override
            public Visual gen(Object it) {
                Round r = (Round) it;
                return new Visual(it, r.getName(), "", null);
            }
        }));

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setContentPane(new StatisticsScreen());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
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

        resultsPan = new javax.swing.JSplitPane();
        chartResultPan = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        resultList = new bc.ui.swing.tables.ScrolleableStripeTable();
        varscrolls = new javax.swing.JScrollPane();
        vars = new bc.ui.swing.configurable.VariablesEditor();
        jPanel12 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        roundScroll = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        statScroll = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        varsDataPan = new bc.ui.swing.useful.DataPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        resultDataPan = new bc.ui.swing.useful.DataPanel();

        resultsPan.setBorder(null);
        resultsPan.setResizeWeight(0.6);

        chartResultPan.setLayout(new java.awt.BorderLayout());
        resultsPan.setLeftComponent(chartResultPan);

        jPanel14.setBackground(new java.awt.Color(153, 153, 153));
        jPanel14.setMinimumSize(new java.awt.Dimension(150, 10));
        jPanel14.setPreferredSize(new java.awt.Dimension(150, 10));
        jPanel14.setLayout(new java.awt.BorderLayout());

        resultList.setBackground(new java.awt.Color(255, 102, 102));
        resultList.setForeground(new java.awt.Color(218, 236, 255));
        resultList.setEvenRowColor(new java.awt.Color(173, 173, 173));
        resultList.setOddRowColor(new java.awt.Color(153, 153, 153));
        jPanel14.add(resultList, java.awt.BorderLayout.CENTER);

        resultsPan.setRightComponent(jPanel14);

        varscrolls.setBorder(null);
        varscrolls.setOpaque(false);
        varscrolls.setViewportView(vars);

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        jPanel12.setBackground(new java.awt.Color(120, 120, 120));
        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Configuration");
        jPanel12.add(jLabel9);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel12, gridBagConstraints);

        jPanel1.setBackground(new java.awt.Color(245, 245, 245));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(120, 120, 120), 3));
        jPanel1.setPreferredSize(new java.awt.Dimension(102, 180));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBackground(new java.awt.Color(245, 245, 245));
        jPanel2.setMinimumSize(new java.awt.Dimension(180, 10));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel6.setFont(new java.awt.Font("Consolas", 1, 12));
        jLabel6.setText("Select Round To Analayze");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel6, gridBagConstraints);

        roundScroll.setBorder(null);
        roundScroll.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel2.add(roundScroll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(38, 180));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/lineWithNumber1.png"))); // NOI18N
        jPanel3.add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3, new java.awt.GridBagConstraints());

        jPanel4.setBackground(new java.awt.Color(232, 232, 232));
        jPanel4.setMinimumSize(new java.awt.Dimension(250, 180));
        jPanel4.setPreferredSize(new java.awt.Dimension(240, 30));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel7.setFont(new java.awt.Font("Consolas", 1, 12));
        jLabel7.setText("Select Statistic");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jLabel7, gridBagConstraints);

        statScroll.setBorder(null);
        statScroll.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel4.add(statScroll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel4, gridBagConstraints);

        jPanel5.setMinimumSize(new java.awt.Dimension(38, 180));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/lineWithNumber2.png"))); // NOI18N
        jPanel5.add(jLabel2, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel5, new java.awt.GridBagConstraints());

        jPanel6.setBackground(new java.awt.Color(220, 220, 220));
        jPanel6.setMinimumSize(new java.awt.Dimension(194, 180));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel8.setFont(new java.awt.Font("Consolas", 1, 12));
        jLabel8.setText("Configure Analyzer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel8, gridBagConstraints);

        varsDataPan.setNoDataText("No Configuration Needed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel6.add(varsDataPan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel6, gridBagConstraints);

        jPanel8.setMinimumSize(new java.awt.Dimension(38, 180));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/lineWithNumber3.png"))); // NOI18N
        jPanel8.add(jLabel3, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel8, new java.awt.GridBagConstraints());

        jPanel7.setBackground(new java.awt.Color(210, 210, 210));
        jPanel7.setMinimumSize(new java.awt.Dimension(55, 180));
        jPanel7.setLayout(new java.awt.GridBagLayout());

        jXHyperlink1.setForeground(new java.awt.Color(0, 153, 255));
        jXHyperlink1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/statistics-collection-view.png"))); // NOI18N
        jXHyperlink1.setText("Analyze");
        jXHyperlink1.setClickedColor(new java.awt.Color(0, 102, 204));
        jXHyperlink1.setFont(new java.awt.Font("Consolas", 1, 12));
        jXHyperlink1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink1.setUnclickedColor(new java.awt.Color(0, 102, 204));
        jXHyperlink1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel7.add(jXHyperlink1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel7, gridBagConstraints);

        jPanel9.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanel9, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);

        jPanel10.setBackground(new java.awt.Color(245, 245, 245));
        jPanel10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 3, 3, 3, new java.awt.Color(120, 120, 120)));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel11.setBackground(new java.awt.Color(120, 120, 120));
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Analyzing Results");
        jPanel11.add(jLabel4);

        jPanel10.add(jPanel11, java.awt.BorderLayout.PAGE_START);
        jPanel10.add(resultDataPan, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel10, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
        //TEST IF ROUND IS READY:
        if (!DatabaseUnit.UNIT.isSignaled(selectedRound)){
            MessageDialog.showFail("cannot produce statistics for this round", "the round has not been analyzed yet\n"
                    + "either it was not started yet or it is in the process of analyzing\n"
                    + "please try again later.");
            return;
        }
        
        //Assign Variables
        if (selectedCollector == null) {
            System.out.println("no statistic collector selected - TODO IN MSGBOX");
            return;
        }
        Map<String, Object> v = vars.getConfiguration();
        
        if (v == null) { //cannot produce configuration
            return;
        }
        
        VariableMetadata.assign(selectedCollector, v);
        chartResultPan.removeAll();
        LineChart chart = new LineChart();
        final LineVisualModel model = (LineVisualModel) selectedCollector.analyze(DatabaseUnit.UNIT.createDatabase(), selectedRound);
        chart.setModel(model);
        chartResultPan.add(chart, BorderLayout.CENTER);
        resultDataPan.setData(resultsPan);
        GenericTableModel tableModel = new GenericTableModel(new DataExtractor(model.getxAxisName(), model.getyAxisName()) {

            @Override
            public Object getData(String dataName, Object from) {
                Entry<Double, Double> e = (Entry<Double, Double>) from;
                if (model.getxAxisName().equals(dataName)) {
                    return "" + String.format("%.2f", e.getKey());
                } else {
                    return "" + String.format("%.2f", e.getValue());
                }
            }
        });
        
        tableModel.setInnerList(new LinkedList(model.getValues().entrySet()));
        resultList.setModel(tableModel);
        revalidate();
        repaint();
    }//GEN-LAST:event_jXHyperlink1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartResultPan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private bc.ui.swing.useful.DataPanel resultDataPan;
    private bc.ui.swing.tables.ScrolleableStripeTable resultList;
    private javax.swing.JSplitPane resultsPan;
    private javax.swing.JScrollPane roundScroll;
    private javax.swing.JScrollPane statScroll;
    private bc.ui.swing.configurable.VariablesEditor vars;
    private bc.ui.swing.useful.DataPanel varsDataPan;
    private javax.swing.JScrollPane varscrolls;
    // End of variables declaration//GEN-END:variables
}
