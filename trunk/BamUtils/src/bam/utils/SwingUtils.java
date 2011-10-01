/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils;

import com.sun.java.swing.plaf.motif.MotifTabbedPaneUI;
import java.awt.Color;
import java.awt.Insets;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author bennyl
 */
public class SwingUtils {

    private static HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
    private static final String ICONS_LOCATION = "bam/utils/icons";

    public static ImageIcon resIcon(String path) {
        try {
            if (!icons.containsKey(path)) {
                if (path.endsWith(".png")) {
                    icons.put(path, new ImageIcon(ClassLoader.getSystemClassLoader().getResource(path)));
                } else {
                    icons.put(path, new ImageIcon(ClassLoader.getSystemClassLoader().getResource("resources/img/" + path + ".png")));
                }
            }

            return icons.get(path);
        } catch (Exception ex) {
            return null;
        }
    }

    public static ImageIcon icon(String name) {
        return resIcon(ICONS_LOCATION + "/" + name + ".png");
    }

    public static void changeTableRenderer(JTable table, TableCellRenderer cellRenderer, TableCellRenderer headerRenderer) {
        table.setDefaultRenderer(Object.class, cellRenderer);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    public static void configureSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        };
    }

    public static void msgbox(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE, icon("msgbox-logo"));
    }

    public static void errbox(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE, icon("errbox-logo"));
    }

    public static void fill(JComboBox resbox, Class enumClass) {
        DefaultComboBoxModel cboxm = new DefaultComboBoxModel();

        EnumSet es = EnumSet.allOf(enumClass);
        for (Object e : es) {
            cboxm.addElement(e);
        }

        resbox.setModel(cboxm);
    }

    public static void fill(JComboBox cbox, List data) {
        DefaultComboBoxModel cboxm = new DefaultComboBoxModel();


        for (Object e : data) {
            cboxm.addElement(e);
        }

        cbox.setModel(cboxm);
    }

    public static void makeBlackTab(JTabbedPane tab) {
        tab.setUI(new BasicTabbedPaneUI() {

            @Override
            protected void installDefaults() {
                super.installDefaults();
                lightHighlight = new Color(51, 51, 51);
                shadow = new Color(131, 131, 131);
                focus = Color.WHITE;
                this.highlight = Color.white;
                this.tabInsets = new Insets(3, 3, 3, 3);
                this.contentBorderInsets = new Insets(1, 0, 0, 0);
                this.selectedTabPadInsets = new Insets(0, 0, 0, 0);
                this.tabAreaInsets = new Insets(0, 0, 0, 0);
                this.tabPane.setBackground(new Color(101, 101, 101));
                this.tabPane.setForeground(Color.white);
            }
        });
    }
}
