/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JLinkButton.java
 *
 * Created on 03/07/2011, 00:53:04
 */
package bc.swing.comp;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.net.URI;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;

/**
 *
 * @author bennyl
 */
public class JLinkButton extends JGradientPanel {

    /** Creates new form JLinkButton */
    public JLinkButton() {
        initComponents();
    }

    @Override
    public void setBackground(Color bg) {
        this.setBorder(new LineBorder(bg.darker(), 1, false));
        super.setBackground(bg);
    }

    public void setVerticalTextPosition(int textPosition) {
        btn.setVerticalTextPosition(textPosition);
    }

    public void setVerticalAlignment(int alignment) {
        btn.setVerticalAlignment(alignment);
    }

    public void setText(String text) {
        btn.setText(text);
    }

    public void setSelectedIcon(Icon selectedIcon) {
        btn.setSelectedIcon(selectedIcon);
    }

    public void setSelected(boolean b) {
        btn.setSelected(b);
    }

    public void setRolloverSelectedIcon(Icon rolloverSelectedIcon) {
        btn.setRolloverSelectedIcon(rolloverSelectedIcon);
    }

    public void setRolloverIcon(Icon rolloverIcon) {
        btn.setRolloverIcon(rolloverIcon);
    }

    public void setRolloverEnabled(boolean b) {
        btn.setRolloverEnabled(b);
    }

    public void setPressedIcon(Icon pressedIcon) {
        btn.setPressedIcon(pressedIcon);
    }

    public void setMultiClickThreshhold(long threshhold) {
        btn.setMultiClickThreshhold(threshhold);
    }

    public void setMnemonic(char mnemonic) {
        btn.setMnemonic(mnemonic);
    }

    public void setMnemonic(int mnemonic) {
        btn.setMnemonic(mnemonic);
    }

    public void setIconTextGap(int iconTextGap) {
        btn.setIconTextGap(iconTextGap);
    }

    public void setIcon(Icon defaultIcon) {
        btn.setIcon(defaultIcon);
    }

    public void setHorizontalTextPosition(int textPosition) {
        btn.setHorizontalTextPosition(textPosition);
    }

    public void setHorizontalAlignment(int alignment) {
        btn.setHorizontalAlignment(alignment);
    }

    public void setHideActionText(boolean hideActionText) {
        btn.setHideActionText(hideActionText);
    }

    public void setFocusPainted(boolean b) {
        btn.setFocusPainted(b);
    }

    @Override
    public void setForeground(Color fg) {
        if (btn != null) { //the look and feel tries to change it for some reason before the constractor is finished
            btn.setClickedColor(fg);
            btn.setUnclickedColor(fg);
            btn.setForeground(fg);
        }

    }

    public void setEnabled(boolean b) {
        btn.setEnabled(b);
    }

    public void setDisplayedMnemonicIndex(int index) throws IllegalArgumentException {
        btn.setDisplayedMnemonicIndex(index);
    }

    public void setDisabledSelectedIcon(Icon disabledSelectedIcon) {
        btn.setDisabledSelectedIcon(disabledSelectedIcon);
    }

    public void setDisabledIcon(Icon disabledIcon) {
        btn.setDisabledIcon(disabledIcon);
    }

    public void setContentAreaFilled(boolean b) {
        btn.setContentAreaFilled(b);
    }

    public void setBorderPainted(boolean b) {
        btn.setBorderPainted(b);
    }

    public void setActionCommand(String actionCommand) {
        btn.setActionCommand(actionCommand);
    }

    public void setAction(Action a) {
        btn.setAction(a);
    }

    public void removeItemListener(ItemListener l) {
        btn.removeItemListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        btn.removeChangeListener(l);
    }

    public void removeActionListener(ActionListener l) {
        btn.removeActionListener(l);
    }

    public boolean isSelected() {
        return btn.isSelected();
    }

    public boolean isRolloverEnabled() {
        return btn.isRolloverEnabled();
    }

    public boolean isFocusPainted() {
        return btn.isFocusPainted();
    }

    public boolean isContentAreaFilled() {
        return btn.isContentAreaFilled();
    }

    public boolean isBorderPainted() {
        return btn.isBorderPainted();
    }

    public int getVerticalTextPosition() {
        return btn.getVerticalTextPosition();
    }

    public int getVerticalAlignment() {
        return btn.getVerticalAlignment();
    }

    public String getText() {
        return btn.getText();
    }

    public Object[] getSelectedObjects() {
        return btn.getSelectedObjects();
    }

    public Icon getSelectedIcon() {
        return btn.getSelectedIcon();
    }

    public Icon getRolloverSelectedIcon() {
        return btn.getRolloverSelectedIcon();
    }

    public Icon getRolloverIcon() {
        return btn.getRolloverIcon();
    }

    public Icon getPressedIcon() {
        return btn.getPressedIcon();
    }

    public long getMultiClickThreshhold() {
        return btn.getMultiClickThreshhold();
    }

    public ButtonModel getModel() {
        return btn.getModel();
    }

    public int getMnemonic() {
        return btn.getMnemonic();
    }

    public Insets getMargin() {
        return btn.getMargin();
    }

    public ItemListener[] getItemListeners() {
        return btn.getItemListeners();
    }

    public int getIconTextGap() {
        return btn.getIconTextGap();
    }

    public Icon getIcon() {
        return btn.getIcon();
    }

    public int getHorizontalTextPosition() {
        return btn.getHorizontalTextPosition();
    }

    public int getHorizontalAlignment() {
        return btn.getHorizontalAlignment();
    }

    public boolean getHideActionText() {
        return btn.getHideActionText();
    }

    public int getDisplayedMnemonicIndex() {
        return btn.getDisplayedMnemonicIndex();
    }

    public Icon getDisabledSelectedIcon() {
        return btn.getDisabledSelectedIcon();
    }

    public Icon getDisabledIcon() {
        return btn.getDisabledIcon();
    }

    public ChangeListener[] getChangeListeners() {
        return btn.getChangeListeners();
    }

    public ActionListener[] getActionListeners() {
        return btn.getActionListeners();
    }

    public String getActionCommand() {
        return btn.getActionCommand();
    }

    public Action getAction() {
        return btn.getAction();
    }

    public void doClick(int pressTime) {
        btn.doClick(pressTime);
    }

    public void doClick() {
        btn.doClick();
    }

    public void addItemListener(ItemListener l) {
        btn.addItemListener(l);
    }

    public void addChangeListener(ChangeListener l) {
        btn.addChangeListener(l);
    }

    public void addActionListener(ActionListener l) {
        btn.addActionListener(l);
    }

    public void setDefaultCapable(boolean defaultCapable) {
        btn.setDefaultCapable(defaultCapable);
    }

    public void removeNotify() {
        btn.removeNotify();
    }

    public boolean isDefaultCapable() {
        return btn.isDefaultCapable();
    }

    public boolean isDefaultButton() {
        return btn.isDefaultButton();
    }

    public AccessibleContext getAccessibleContext() {
        return btn.getAccessibleContext();
    }

    public void setUnclickedColor(Color color) {
        btn.setUnclickedColor(color);
    }

    public void setURI(URI uri) {
        btn.setURI(uri);
    }

    public void setOverrulesActionOnClick(boolean overrule) {
        btn.setOverrulesActionOnClick(overrule);
    }

    public void setClickedColor(Color color) {
        btn.setClickedColor(color);
    }

    public void setClicked(boolean clicked) {
        btn.setClicked(clicked);
    }

    public boolean isClicked() {
        return btn.isClicked();
    }

    public Color getUnclickedColor() {
        return btn.getUnclickedColor();
    }

    public boolean getOverrulesActionOnClick() {
        return btn.getOverrulesActionOnClick();
    }

    public Color getClickedColor() {
        return btn.getClickedColor();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn = new org.jdesktop.swingx.JXHyperlink();

        btn.setForeground(new java.awt.Color(255, 255, 255));
        btn.setText("Button");
        btn.setClickedColor(new java.awt.Color(255, 255, 255));
        btn.setUnclickedColor(new java.awt.Color(255, 255, 255));
        add(btn);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXHyperlink btn;
    // End of variables declaration//GEN-END:variables
}
