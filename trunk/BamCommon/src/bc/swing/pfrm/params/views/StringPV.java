/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.pfrm.params.views;

import bc.swing.pfrm.params.ParamModel;
import bc.swing.pfrm.params.ParamView;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author bennyl
 */
public class StringPV extends JTextField implements ParamView{
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

    public void setModel(ParamModel model) {
        String v = (String) model.getValue();
        setText(v);
    }

    public void reflectChanges(ParamModel to) {
        to.setValue(getText());
    }

    public void onChange(ParamModel source, Object newValue, Object deltaHint) {
        setText(newValue.toString());
    }
    
}
