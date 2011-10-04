/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.comp;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 *
 * @author bennyl
 */
public class JStackPanel extends JPanel {

    public static final Color SEPERATOR_COLOR = new Color(225, 230, 245);
    private int columns = 0;
    private int line = 0;
    private int column = 0;
    protected JSeparator lastSeperator;

    public JStackPanel() {
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.WHITE);
    }

    public GridBagConstraints constraint() {
        GridBagConstraints ret = new GridBagConstraints(column, line, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0);
        return ret;
    }

    public GridBagConstraints expand(GridBagConstraints gbc) {
        gbc.weightx = 1.0;
        return gbc;
    }

    public GridBagConstraints fullRow(GridBagConstraints gbc) {
        gbc.gridwidth = columns;
        return gbc;
    }

    public void push(JComponent comp, GridBagConstraints constraints) {
        add(comp, constraints);
        column++;
    }

    public void eol() {
        eol(true);
    }

    public void eol(boolean seperator) {
        columns = Math.max(column, columns);
        column = 0;
        line++;
        if (seperator) {
            addSeperator();
        }
    }

    public JSeparator addSeperator() {
        final JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setForeground(SEPERATOR_COLOR);
        add(sep, fullRow(constraint()));
        line++;
        lastSeperator = sep;
        return sep;
    }

    public void push(JComponent cn) {
        push(cn, constraint());
    }

    public void seal() {
        JLabel spacer = new JLabel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.gridy = line;
        add(spacer, gbc);
    }

    public GridBagConstraints takeColumns(GridBagConstraints constraint, int i) {
        constraint.gridwidth = i;
        return constraint;
    }

    public GridBagConstraints lefty(GridBagConstraints con) {
        con.anchor = GridBagConstraints.WEST;
        return con;
    }
}
