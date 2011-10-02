/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.swing.comp;

/**
 *
 * @author bennyl
 */
public interface TextualComponent {
    void setValueText(String text);
    String getValueText();
    void addValueChangedListener(ValueChangedListener l);
    
    public static interface ValueChangedListener{
        public void onChange(TextualComponent source);
    }
}
