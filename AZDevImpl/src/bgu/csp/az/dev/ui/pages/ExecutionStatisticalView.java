/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExecutionStatisticalView.java
 *
 * Created on 18/08/2011, 10:41:40
 */
package bgu.csp.az.dev.ui.pages;

import bam.utils.ui.mvc.pages.PagePart;
import static bam.utils.SwingUtils.*;
import bam.utils.ui.mvc.GenericTreeModel.Node;
import bam.utils.ui.mvc.GenericMapModel;
import bam.utils.ui.mvc.GenericMapModel.MapChangeListener;
import bam.utils.ui.mvc.swing.CheckTree;
import bam.utils.ui.mvc.swing.CheckTreeModel;
import bam.utils.ui.rnd.IconSupplier;
import bgu.csp.az.dev.Round;
import bgu.csp.az.dev.ui.statistics.StatisticalPagePart;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import org.jdesktop.swingx.painter.RectanglePainter;

/**
 *
 * @author bennyl
 */
public class ExecutionStatisticalView extends javax.swing.JPanel implements CheckTreeModel.CheckListener<PagePart> {

    /** Creates new form ExecutionStatisticalView */
    public ExecutionStatisticalView() {
        initComponents();
        descriptionLabel.setBackgroundPainter(new RectanglePainter(new Color(245, 245, 245), null));
    }

    public void setModel(final ExecutionStatisticalPage model) {
        final CheckTreeModel tmodel = createTreeModel(model);
        tmodel.addCheckedListener(this);
        model.getPagePartsModel().addListener(createPagePartListener(tmodel));
        tree.setModel(tmodel);
        ((CheckTree)tree).getCellRenderer().setIconSupplier(new IconSupplier() {

            @Override
            public ImageIcon supply(Object item) {
                Node n = (Node) item;
                Object v = n.getValue();
                if (v instanceof String){
                    return resIcon("resources/img/clipboard-invoice.png");
                } else {
                    return resIcon("resources/img/round.png");
                }
            }
        });
    }

    private MapChangeListener<Round, List<StatisticalPagePart>> createPagePartListener(final CheckTreeModel tmodel) {
        return new MapChangeListener<Round, List<StatisticalPagePart>>() {

            @Override
            public void onItemAdded(GenericMapModel<Round, List<StatisticalPagePart>> source, Round key, List<StatisticalPagePart> value) {
                tmodel.fireNodeAdded(tmodel.getRoot().getChildFor(key));
            }

            @Override
            public void onItemRemoved(GenericMapModel<Round, List<StatisticalPagePart>> source, Round key, List<StatisticalPagePart> value) {
                tmodel.fireNodeRemoved(((Node) (tmodel.getRoot().getChilds().get(0))).getChildFor(key));
            }

            @Override
            public void onCleared(GenericMapModel<Round, List<StatisticalPagePart>> source) {
                tmodel.fireTreeStracturedChanged();
            }

            @Override
            public void onItemsAdded(GenericMapModel<Round, List<StatisticalPagePart>> source, Map<? extends Round, ? extends List<StatisticalPagePart>> items) {
                tmodel.fireTreeStracturedChanged();
            }

            @Override
            public void onItemChanged(GenericMapModel<Round, List<StatisticalPagePart>> source, Round key) {
                tmodel.fireNodeChanged(((Node) (tmodel.getRoot().getChilds().get(0))).getChildFor(key));
            }
        };
    }

    private CheckTreeModel createTreeModel(final ExecutionStatisticalPage model) {
        final StatisticsNode statisticsNode = new StatisticsNode("Collected Statistics", null, model);
        return new CheckTreeModel(statisticsNode);
    }

    @Override
    public void onCheckChanged(CheckTreeModel<PagePart> source, PagePart item, boolean checked) {
        if (checked && source.getNumberOfCheckedItems() > 4) {
            msgbox("cannot compleate operation", "only 4 items can be selected at once.");
            source.setChecked(item, false);
            return;
        }

        if (checked) {
            chartsPanel.add(item.getView());
            chartsPanel.validate();
            chartsPanel.repaint();
        } else {
            chartsPanel.remove(item.getView());
            chartsPanel.validate();
            chartsPanel.repaint();
        }

        if (source.getNumberOfCheckedItems() > 0 && nographLabel.isVisible()) {
            nographLabel.setVisible(false);
            chartsPanel.remove(nographLabel);
            chartsPanel.validate();
        } else if (source.getNumberOfCheckedItems() == 0 && !nographLabel.isVisible()) {
            chartsPanel.add(nographLabel);
            nographLabel.setVisible(true);
            chartsPanel.validate();
        }

    }

    public static class StatisticsNode extends Node {

        ExecutionStatisticalPage model;

        public StatisticsNode(Object value, Node parent, ExecutionStatisticalPage model) {
            super(value, parent);
            this.model = model;
        }

        @Override
        protected Object[] _getChilds() {
            if (getValue() instanceof String) {
                return model.getPagePartsModel().keySet().toArray();
            } else if (getValue() instanceof Round) {
                return model.getPagePartsModel().get(getValue()).toArray();
            } else {
                return new Object[0];
            }
        }

        @Override
        protected Node createChildNode(Object t) {
            return new StatisticsNode(t, this, model);
        }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new CheckTree();
        chartsPanel = new javax.swing.JPanel();
        nographLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        descriptionLabel = new org.jdesktop.swingx.JXLabel();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 5, 0, 0, new java.awt.Color(245, 245, 245)));

        tree.setBackground(new java.awt.Color(245, 245, 245));
        tree.setRowHeight(19);
        tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(tree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        chartsPanel.setBackground(new java.awt.Color(255, 255, 255));
        chartsPanel.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

        nographLabel.setFont(new java.awt.Font("Tahoma", 0, 18));
        nographLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nographLabel.setText("No Graphs To Show - Select one from the List");
        nographLabel.setMaximumSize(new java.awt.Dimension(9999999, 22));
        nographLabel.setPreferredSize(new java.awt.Dimension(99999, 22));
        chartsPanel.add(nographLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel1.add(chartsPanel, gridBagConstraints);

        jLabel2.setBackground(new java.awt.Color(245, 245, 245));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/information-frame.png"))); // NOI18N
        jLabel2.setText("Description");
        jLabel2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 5, 0, 0, new java.awt.Color(245, 245, 245)));
        jLabel2.setMaximumSize(new java.awt.Dimension(200, 16));
        jLabel2.setMinimumSize(new java.awt.Dimension(200, 24));
        jLabel2.setOpaque(true);
        jLabel2.setPreferredSize(new java.awt.Dimension(200, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 15;
        jPanel1.add(jLabel2, gridBagConstraints);

        descriptionLabel.setBackground(new java.awt.Color(245, 245, 245));
        descriptionLabel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 10, 0, 5, new java.awt.Color(245, 245, 245)));
        descriptionLabel.setForeground(new java.awt.Color(51, 102, 255));
        descriptionLabel.setText("Description Here");
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setMaximumSize(new java.awt.Dimension(200, 0));
        descriptionLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        jPanel1.add(descriptionLabel, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void treeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeValueChanged
        final Object value = ((Node) evt.getPath().getLastPathComponent()).getValue();
        if (value instanceof PagePart) {
            descriptionLabel.setText(((PagePart)value).getDescription());
        }else{
            descriptionLabel.setText("No description for this item");
        }
    }//GEN-LAST:event_treeValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartsPanel;
    private org.jdesktop.swingx.JXLabel descriptionLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nographLabel;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
