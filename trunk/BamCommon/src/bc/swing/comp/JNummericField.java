/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.comp;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import static bc.dsl.JavaDSL.*;

/**
 *
 * @author bennyl
 */
public class JNummericField extends JHinteableTextField {

    private int max = Integer.MAX_VALUE;
    private int min = 0;

    public JNummericField() {
        addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                setSelectionStart(0);
                setSelectionEnd(getText().length());
            }

            @Override
            public void focusLost(FocusEvent e) {
                doValidate();
            }
        });

    }

    private void doValidate() throws NumberFormatException {
        if (nummeric(getText())) {
            Integer num = Integer.parseInt(getText());
            if (!between(num, min, max)) {
                setText("" + max);
            }
        } else {
            setText("" + min);
        }
    }
    
    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public void setValueText(String text) {
        setText(text);
        doValidate();
    }

    @Override
    public String getValueText() {
        return getText();
    }
}
