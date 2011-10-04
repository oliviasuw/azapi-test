/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.comp;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author bennyl
 */
public class JHinteableTextField extends JTextField implements TextualComponent{
    private String hint;
    private Color hintColor = Color.gray.brighter();
    JLabel hintField = new JLabel();


    @Override
    public void paint(Graphics g) {

        boolean hinting = false;

        if (hint != null && getText().isEmpty()){
            hinting = true;
        }

        if (hinting){
            super.paintComponent(g);
            g.setColor(hintColor);
            g.setFont(getFont());
            g.drawString(hint, 3, 15);

        }else {
            super.paintComponent(g);
        }

        super.paintBorder(g);

    }



    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public void setValueText(String text) {
        setText(text);
    }

    @Override
    public String getValueText() {
        return getText();
    }

    @Override
    public void addValueChangedListener(final ValueChangedListener l) {
        this.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                l.onChange(JHinteableTextField.this);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                l.onChange(JHinteableTextField.this);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                l.onChange(JHinteableTextField.this);
            }
        });
    }
    
}
