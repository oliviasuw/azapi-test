/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import static bc.dsl.SwingDSL.*;


/**
 *
 * @author bennyl
 */
public class JTextualComboBox extends JComboBox implements TextualComponent{

    @Override
    public void setValueText(String text) {
        select(this, text);
    }

    @Override
    public String getValueText() {
        return getSelectedItem().toString();
    }

    @Override
    public void addValueChangedListener(final ValueChangedListener l) {
        this.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                l.onChange(JTextualComboBox.this);
            }
        });
    }
    
}
