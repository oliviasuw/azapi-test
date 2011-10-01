/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bam.utils.ui.mvc.swing;

import bam.utils.ui.rnd.CheckListRenderer;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author bennyl
 */
public class CheckList extends JList {

    private final CheckListRenderer renderer;

    public CheckList() {
        renderer = new CheckListRenderer();
        renderer.setUnselectedColor(getBackground());
        setCellRenderer(renderer);
        setModel(new CheckListModel());
        addMouseListener(new MouseInputAdapter() {

            int prevSel = -1;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && prevSel == getSelectedIndex()) {
                    final CheckListModel model = (CheckListModel) getModel();
                    model.setChecked(prevSel, !model.isChecked(prevSel));
                }

                prevSel = getSelectedIndex();
            }
        });

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    final CheckListModel model = (CheckListModel) getModel();
                    model.setChecked(getSelectedIndex(), !model.isChecked(getSelectedIndex()));
                }
            }
        });
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (renderer != null) {
            renderer.setUnselectedColor(bg);
        }
    }
}
