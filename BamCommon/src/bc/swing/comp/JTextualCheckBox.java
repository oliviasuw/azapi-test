/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bc.swing.comp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JCheckBox;

/**
 *
 * @author BLutati
 */
public class JTextualCheckBox extends JCheckBox implements TextualComponent{
    public static final String FALISE_LITERAL = "FALSE";
    public static final String TRUE_LITERAL = "TRUE";
    LinkedList<ValueChangedListener> listeners = new LinkedList<ValueChangedListener>();

    public JTextualCheckBox() {
        getModel().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (ValueChangedListener l : listeners) l.onChange(JTextualCheckBox.this);
            }
        });

        setBackground(Color.WHITE);
    }

    @Override
    public void setValueText(String text) {
        this.setSelected(text.toLowerCase().trim().equals("true"));
    }

    @Override
    public String getValueText() {
        return this.isSelected()? TRUE_LITERAL: FALISE_LITERAL;
    }

    @Override
    public void addValueChangedListener(ValueChangedListener l) {
        listeners.add(l);
    }

}
